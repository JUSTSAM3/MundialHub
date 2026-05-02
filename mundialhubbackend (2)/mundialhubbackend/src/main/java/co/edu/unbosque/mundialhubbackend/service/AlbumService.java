package co.edu.unbosque.mundialhubbackend.service;

import co.edu.unbosque.mundialhubbackend.dto.StickerDTO;
import co.edu.unbosque.mundialhubbackend.dto.StickerExchangeDTO;
import co.edu.unbosque.mundialhubbackend.dto.StickerPackageDTO;
import co.edu.unbosque.mundialhubbackend.dto.UserStickerDTO;
import co.edu.unbosque.mundialhubbackend.model.*;
import co.edu.unbosque.mundialhubbackend.model.StickerExchange.ExchangeStatus;
import co.edu.unbosque.mundialhubbackend.model.StickerPackage.PackageSource;
import co.edu.unbosque.mundialhubbackend.model.StickerPackage.PackageStatus;
import co.edu.unbosque.mundialhubbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlbumService {

	// Láminas por paquete
	private int stickerPerPackage = 5;

	// Límite de paquetes por tipo de acción por día (RNF-16)
	private int maxPackagePerDay = 3;

	// Límite de intercambios completados por día (RNF-16)
	private int maxExchangePerDay = 10;

	// Horas de vida de una propuesta de intercambio antes de expirar
	private int exchangeLifeTime = 48;

	// Probabilidades de rareza al abrir un paquete
	private Map<Sticker.StickerRarity, Double> probabilities = Map.of(Sticker.StickerRarity.COMMON, 0.60,
			Sticker.StickerRarity.RARE, 0.30, Sticker.StickerRarity.EPIC, 0.09, Sticker.StickerRarity.LEGEND, 0.01);

	@Autowired
	private StickerRepository stickerRepository;
	@Autowired
	private UserStickerRepository userStickerRepository;
	@Autowired
	private StickerPackageRepository packageRepository;
	@Autowired
	private StickerExchangeRepository exchangeRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PromotionalCodeRepository promoRepository;

	// Reemplazar promoRepository.saveRedemption(redemption) por:
	@Autowired
	private PromoRedemptionRepository promoRedemptionRepository;
	// ...
	// ─── HU-21 Ver álbum ─────────────────────────────────────────────────────

	/**
	 * Devuelve el álbum del usuario. Si section viene null → álbum completo. Si
	 * section tiene valor → solo esa sección.
	 */
	public List<UserStickerDTO> getAlbum(String username, String section) {
		List<UserSticker> stickers = (section != null && !section.isBlank())
				? userStickerRepository.findByUserAndSection(username, section.trim())
				: userStickerRepository.findByUserUsernameOrderByStickerAlbumNumberAsc(username);

		return stickers.stream().map(this::toUserStickerDTO).collect(Collectors.toList());
	}

	/**
	 * Devuelve las secciones disponibles del álbum con el conteo de láminas que el
	 * usuario tiene y el total por sección.
	 */
	public Map<String, Object> getAlbumSections(String username) {
		List<String> allSections = stickerRepository.findDistinctSections();
		List<String> missing = stickerRepository.findMissingStickersForUser(username).stream().map(Sticker::getSection)
				.distinct().collect(Collectors.toList());

		long totalUnique = stickerRepository.findByActiveTrueOrderByAlbumNumberAsc().size();
		long userUnique = userStickerRepository.countUniqueByUser(username);

		Map<String, Object> summary = new LinkedHashMap<>();
		summary.put("sections", allSections);
		summary.put("totalStickers", totalUnique);
		summary.put("collectedStickers", userUnique);
		summary.put("completionPercent", totalUnique > 0 ? (userUnique * 100.0 / totalUnique) : 0.0);
		summary.put("sectionsWithGaps", missing);
		return summary;
	}

	// ─── HU-24 Ver repetidas ──────────────────────────────────────────────────

	public List<UserStickerDTO> getDuplicates(String username) {
		return userStickerRepository.findDuplicatesByUser(username).stream().map(this::toUserStickerDTO)
				.collect(Collectors.toList());
	}

	// ─── HU-22 Recibir paquetes por actividad ────────────────────────────────

	/**
	 * Entrega un paquete de láminas por actividad del usuario. Respeta el límite de
	 * 3 paquetes por tipo de acción por día (RNF-16).
	 *
	 * Retorna: 0 = OK | 1 = usuario no encontrado | 2 = límite diario alcanzado
	 */
	public int awardActivityPackage(String username, PackageSource source) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		LocalDateTime startOfDay = LocalDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay();

		long todayCount = packageRepository.countByUserAndSourceToday(username, source, startOfDay);

		if (todayCount >= maxPackagePerDay)
			return 2;

		createPackage(user, source);
		return 0;
	}

	/**
	 * Entrega un paquete especial al ganador de una polla (HU-20). Este tipo no
	 * tiene límite diario — es un premio único. Este método resuelve el TODO
	 * pendiente en PollService.
	 */
	public int awardWinnerPackage(String username) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;
		createPackage(user, PackageSource.POLL_WINNER);
		return 0;
	}

	// ─── HU-26 Canjear código promocional ────────────────────────────────────

	/**
	 * Valida el código promocional y entrega el paquete correspondiente.
	 *
	 * Retorna: 0 = OK | 1 = usuario no encontrado | 2 = código inválido 3 = código
	 * vencido | 4 = código agotado | 5 = ya fue canjeado por este usuario
	 */
	public int redeemPromoCode(String username, String code) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		PromotionalCode promo = promoRepository.findByCodeAndActiveTrue(code.toUpperCase().trim()).orElse(null);
		if (promo == null)
			return 2;

		if (promo.getExpiresAt() != null && promo.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
			promo.setActive(false);
			promoRepository.save(promo);
			return 3;
		}

		if (promo.getMaxUses() != null && promo.getCurrentUses() >= promo.getMaxUses()) {
			promo.setActive(false);
			promoRepository.save(promo);
			return 4;
		}

		if (promoRepository.hasUserRedeemed(promo.getId(), username))
			return 5;

		// Registrar canje
		PromoRedemption redemption = new PromoRedemption();
		redemption.setCode(promo);
		redemption.setUser(user);
		redemption.setRedeemedAt(LocalDateTime.now(ZoneOffset.UTC));
		promoRedemptionRepository.save(redemption);

		promo.setCurrentUses(promo.getCurrentUses() + 1);
		if (promo.getMaxUses() != null && promo.getCurrentUses() >= promo.getMaxUses()) {
			promo.setActive(false);
		}
		promoRepository.save(promo);

		createPackage(user, PackageSource.PROMO_CODE);

		return 0;
	}

	// ─── HU-23 Abrir paquete ──────────────────────────────────────────────────

	/**
	 * Abre un paquete pendiente y asigna las láminas al álbum del usuario. Las
	 * nuevas van al álbum; las que ya tiene aumentan su quantity (repetidas).
	 *
	 * Retorna: 0 = OK | 1 = paquete no encontrado | 2 = no es del usuario 3 = ya
	 * estaba abierto
	 */
	public Object[] openPackage(String username, Long packageId) {
		StickerPackage pkg = packageRepository.findById(packageId).orElse(null);
		if (pkg == null)
			return new Object[] { 1, null };

		if (!pkg.getUser().getUsername().equals(username))
			return new Object[] { 2, null };

		if (pkg.getStatus() == PackageStatus.OPENED)
			return new Object[] { 3, null };

		// Sortear las láminas según las probabilidades de rareza
		List<Sticker> revealed = drawStickers(pkg.getStickerCount());

		// Asignar cada lámina al álbum del usuario
		for (Sticker sticker : revealed) {
			Optional<UserSticker> existing = userStickerRepository.findByUserUsernameAndStickerId(username,
					sticker.getId());

			if (existing.isPresent()) {
				UserSticker us = existing.get();
				us.setQuantity(us.getQuantity() + 1);
				userStickerRepository.save(us);
			} else {
				UserSticker us = new UserSticker();
				us.setUser(pkg.getUser());
				us.setSticker(sticker);
				us.setQuantity(1);
				us.setOfferedForExchange(0);
				us.setFirstObtainedAt(LocalDateTime.now(ZoneOffset.UTC));
				userStickerRepository.save(us);
			}
		}

		pkg.setRevealedStickers(revealed);
		pkg.setStatus(PackageStatus.OPENED);
		pkg.setOpenedAt(LocalDateTime.now(ZoneOffset.UTC));
		packageRepository.save(pkg);

		StickerPackageDTO dto = toPackageDTO(pkg);
		dto.setRevealedStickers(revealed.stream().map(this::toStickerDTO).collect(Collectors.toList()));

		return new Object[] { 0, dto };
	}

	// ─── HU-25 Proponer intercambio ───────────────────────────────────────────

	/**
	 * El proponente ofrece una de sus repetidas a cambio de una lámina del
	 * receptor. Ambas partes deben tener las láminas correspondientes.
	 *
	 * Retorna: 0 = OK | 1 = usuario no encontrado | 2 = receptor no encontrado 3 =
	 * el proponente no tiene esa lámina como repetida 4 = el receptor no tiene la
	 * lámina solicitada 5 = límite diario de intercambios alcanzado (RNF-16) 6 = no
	 * se puede intercambiar con uno mismo
	 */
	public int proposeExchange(String proposerUsername, String receiverUsername, Long offeredStickerId,
			Long requestedStickerId) {

		if (proposerUsername.equals(receiverUsername))
			return 6;

		User proposer = userRepository.findByUsername(proposerUsername).orElse(null);
		if (proposer == null)
			return 1;

		User receiver = userRepository.findByUsername(receiverUsername).orElse(null);
		if (receiver == null)
			return 2;

		// El proponente debe tener esa lámina como repetida (quantity > 1)
		UserSticker proposerSticker = userStickerRepository
				.findByUserUsernameAndStickerId(proposerUsername, offeredStickerId).orElse(null);
		if (proposerSticker == null || proposerSticker.getQuantity() <= 1)
			return 3;

		// El receptor debe tener la lámina que se le solicita como repetida
		UserSticker receiverSticker = userStickerRepository
				.findByUserUsernameAndStickerId(receiverUsername, requestedStickerId).orElse(null);
		if (receiverSticker == null || receiverSticker.getQuantity() <= 1)
			return 4;

		// Verificar límite diario del proponente
		LocalDateTime startOfDay = LocalDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay();
		long todayExchanges = exchangeRepository.countCompletedExchangesToday(proposerUsername, startOfDay);
		if (todayExchanges >= maxExchangePerDay)
			return 5;

		Sticker offered = stickerRepository.findById(offeredStickerId).orElse(null);
		Sticker requested = stickerRepository.findById(requestedStickerId).orElse(null);

		StickerExchange exchange = new StickerExchange();
		exchange.setProposer(proposer);
		exchange.setReceiver(receiver);
		exchange.setOfferedSticker(offered);
		exchange.setRequestedSticker(requested);
		exchange.setStatus(ExchangeStatus.PENDING);
		exchange.setProposedAt(LocalDateTime.now(ZoneOffset.UTC));
		exchange.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusHours(exchangeLifeTime));
		exchangeRepository.save(exchange);

		return 0;
	}

	/**
	 * El receptor acepta o rechaza una propuesta de intercambio. Si acepta, se
	 * transfieren las láminas en el mismo instante.
	 *
	 * Retorna: 0 = OK | 1 = intercambio no encontrado | 2 = no es el receptor 3 =
	 * ya fue respondido | 4 = intercambio vencido 5 = límite diario alcanzado | 6 =
	 * condiciones ya no válidas
	 */
	public int respondExchange(String receiverUsername, Long exchangeId, boolean accepted) {
		StickerExchange exchange = exchangeRepository.findById(exchangeId).orElse(null);
		if (exchange == null)
			return 1;

		if (!exchange.getReceiver().getUsername().equals(receiverUsername))
			return 2;

		if (exchange.getStatus() != ExchangeStatus.PENDING)
			return 3;

		if (exchange.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
			exchange.setStatus(ExchangeStatus.EXPIRED);
			exchangeRepository.save(exchange);
			return 4;
		}

		if (!accepted) {
			exchange.setStatus(ExchangeStatus.REJECTED);
			exchange.setRespondedAt(LocalDateTime.now(ZoneOffset.UTC));
			exchangeRepository.save(exchange);
			return 0;
		}

		// Verificar límite diario del receptor al aceptar
		LocalDateTime startOfDay = LocalDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay();
		long todayExchanges = exchangeRepository.countCompletedExchangesToday(receiverUsername, startOfDay);
		if (todayExchanges >= maxExchangePerDay)
			return 5;

		// Verificar que ambas partes aún tienen las láminas correspondientes
		String proposerUsername = exchange.getProposer().getUsername();
		Long offeredId = exchange.getOfferedSticker().getId();
		Long requestedId = exchange.getRequestedSticker().getId();

		UserSticker fromProposer = userStickerRepository.findByUserUsernameAndStickerId(proposerUsername, offeredId)
				.orElse(null);
		UserSticker fromReceiver = userStickerRepository.findByUserUsernameAndStickerId(receiverUsername, requestedId)
				.orElse(null);

		if (fromProposer == null || fromProposer.getQuantity() <= 1 || fromReceiver == null
				|| fromReceiver.getQuantity() <= 1)
			return 6;

		// Ejecutar la transferencia
		transferSticker(fromProposer, exchange.getReceiver());
		transferSticker(fromReceiver, exchange.getProposer());

		exchange.setStatus(ExchangeStatus.ACCEPTED);
		exchange.setRespondedAt(LocalDateTime.now(ZoneOffset.UTC));
		exchangeRepository.save(exchange);

		return 0;
	}

	/**
	 * El proponente cancela una propuesta que aún no fue respondida.
	 *
	 * Retorna: 0 = OK | 1 = no encontrado | 2 = no es el proponente | 3 = ya fue
	 * respondido
	 */
	public int cancelExchange(String proposerUsername, Long exchangeId) {
		StickerExchange exchange = exchangeRepository.findById(exchangeId).orElse(null);
		if (exchange == null)
			return 1;

		if (!exchange.getProposer().getUsername().equals(proposerUsername))
			return 2;

		if (exchange.getStatus() != ExchangeStatus.PENDING)
			return 3;

		exchange.setStatus(ExchangeStatus.CANCELLED);
		exchange.setRespondedAt(LocalDateTime.now(ZoneOffset.UTC));
		exchangeRepository.save(exchange);
		return 0;
	}

	// ─── Consultas de intercambios ────────────────────────────────────────────

	public List<StickerExchangeDTO> getIncomingExchanges(String username) {
		return exchangeRepository.findByReceiverUsernameAndStatusOrderByProposedAtDesc(username, ExchangeStatus.PENDING)
				.stream().map(this::toExchangeDTO).collect(Collectors.toList());
	}

	public List<StickerExchangeDTO> getMyExchangeHistory(String username) {
		return exchangeRepository.findAllByParticipant(username).stream().map(this::toExchangeDTO)
				.collect(Collectors.toList());
	}

	// ─── Consulta de paquetes ─────────────────────────────────────────────────

	public List<StickerPackageDTO> getPendingPackages(String username) {
		return packageRepository.findByUserUsernameAndStatusOrderByAwardedAtDesc(username, PackageStatus.PENDING)
				.stream().map(this::toPackageDTO).collect(Collectors.toList());
	}

	// ─── Job: expirar intercambios vencidos ───────────────────────────────────

	/**
	 * Corre cada hora. Marca como EXPIRED los intercambios que llevan más de 48 h
	 * sin respuesta.
	 */
	@Scheduled(fixedDelay = 3_600_000)
	public void expireStaleExchanges() {
		List<StickerExchange> stale = exchangeRepository.findByStatusAndExpiresAtBefore(ExchangeStatus.PENDING,
				LocalDateTime.now(ZoneOffset.UTC));
		stale.forEach(e -> {
			e.setStatus(ExchangeStatus.EXPIRED);
			exchangeRepository.save(e);
		});
		if (!stale.isEmpty()) {
			System.out.println("[AlbumService] Intercambios expirados: " + stale.size());
		}
	}

	// ─── Privados ─────────────────────────────────────────────────────────────

	/**
	 * Crea y persiste un paquete de láminas para el usuario.
	 */
	private void createPackage(User user, PackageSource source) {
		StickerPackage pkg = new StickerPackage();
		pkg.setUser(user);
		pkg.setSource(source);
		pkg.setStatus(PackageStatus.PENDING);
		pkg.setStickerCount(stickerPerPackage);
		pkg.setAwardedAt(LocalDateTime.now(ZoneOffset.UTC));
		packageRepository.save(pkg);
	}

	/**
	 * Sortea N láminas aplicando las probabilidades de rareza definidas. Usa un
	 * sistema de pesos acumulados (Cumulative Distribution Function).
	 */
	private List<Sticker> drawStickers(int count) {
		Random rnd = new Random();
		List<Sticker> result = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			double roll = rnd.nextDouble();
			Sticker.StickerRarity rarity = pickRarity(roll);

			List<Sticker> pool = stickerRepository.findByRarityAndActiveTrue(rarity);
			if (!pool.isEmpty()) {
				result.add(pool.get(rnd.nextInt(pool.size())));
			}
		}
		return result;
	}

	/**
	 * Selecciona la rareza según la tirada y los pesos configurados.
	 */
	private Sticker.StickerRarity pickRarity(double roll) {
		double cumulative = 0;
		for (Map.Entry<Sticker.StickerRarity, Double> entry : probabilities.entrySet()) {
			cumulative += entry.getValue();
			if (roll < cumulative)
				return entry.getKey();
		}
		return Sticker.StickerRarity.COMMON;
	}

	/**
	 * Transfiere una lámina del origen al destino. Reduce quantity en el origen y
	 * aumenta (o crea) en el destino.
	 */
	private void transferSticker(UserSticker source, User destination) {
		source.setQuantity(source.getQuantity() - 1);
		userStickerRepository.save(source);

		Optional<UserSticker> destEntry = userStickerRepository
				.findByUserUsernameAndStickerId(destination.getUsername(), source.getSticker().getId());

		if (destEntry.isPresent()) {
			UserSticker dest = destEntry.get();
			dest.setQuantity(dest.getQuantity() + 1);
			userStickerRepository.save(dest);
		} else {
			UserSticker dest = new UserSticker();
			dest.setUser(destination);
			dest.setSticker(source.getSticker());
			dest.setQuantity(1);
			dest.setOfferedForExchange(0);
			dest.setFirstObtainedAt(LocalDateTime.now(ZoneOffset.UTC));
			userStickerRepository.save(dest);
		}
	}

	// ─── Conversión Entidad → DTO ─────────────────────────────────────────────

	private StickerDTO toStickerDTO(Sticker s) {
		StickerDTO dto = new StickerDTO();
		dto.setId(s.getId());
		dto.setName(s.getName());
		dto.setSection(s.getSection());
		dto.setRarity(s.getRarity());
		dto.setCategory(s.getCategory());
		dto.setImageUrl(s.getImageUrl());
		dto.setDescription(s.getDescription());
		dto.setAlbumNumber(s.getAlbumNumber());
		return dto;
	}

	private UserStickerDTO toUserStickerDTO(UserSticker us) {
		UserStickerDTO dto = new UserStickerDTO();
		dto.setId(us.getId());
		dto.setSticker(toStickerDTO(us.getSticker()));
		dto.setQuantity(us.getQuantity());
		dto.setOfferedForExchange(us.getOfferedForExchange());
		dto.setHasDuplicates(us.getQuantity() > 1);
		dto.setInAlbum(us.getQuantity() >= 1);
		dto.setFirstObtainedAt(us.getFirstObtainedAt());
		return dto;
	}

	private StickerPackageDTO toPackageDTO(StickerPackage pkg) {
		StickerPackageDTO dto = new StickerPackageDTO();
		dto.setId(pkg.getId());
		dto.setSource(pkg.getSource());
		dto.setStatus(pkg.getStatus());
		dto.setStickerCount(pkg.getStickerCount());
		dto.setAwardedAt(pkg.getAwardedAt());
		dto.setOpenedAt(pkg.getOpenedAt());
		return dto;
	}

	private StickerExchangeDTO toExchangeDTO(StickerExchange e) {
		StickerExchangeDTO dto = new StickerExchangeDTO();
		dto.setId(e.getId());
		dto.setProposerUsername(e.getProposer().getUsername());
		dto.setReceiverUsername(e.getReceiver().getUsername());
		dto.setOfferedSticker(toStickerDTO(e.getOfferedSticker()));
		dto.setRequestedSticker(toStickerDTO(e.getRequestedSticker()));
		dto.setStatus(e.getStatus());
		dto.setProposedAt(e.getProposedAt());
		dto.setRespondedAt(e.getRespondedAt());
		dto.setExpiresAt(e.getExpiresAt());
		return dto;
	}
}