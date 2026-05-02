package co.edu.unbosque.mundialhubbackend.service;

import co.edu.unbosque.mundialhubbackend.dto.CommunityDTO;
import co.edu.unbosque.mundialhubbackend.dto.CommunityInviteDTO;
import co.edu.unbosque.mundialhubbackend.dto.CommunityMemberDTO;
import co.edu.unbosque.mundialhubbackend.model.*;
import co.edu.unbosque.mundialhubbackend.model.CommunityMember.CommunityRole;
import co.edu.unbosque.mundialhubbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommunityService {

	// URL base del frontend para construir el enlace de invitación
	private String revisar;
//	@Value("${app.frontend.url:https://mundialhub.app}")
//	private String frontendUrl;

	// Tiempo de vida del enlace de invitación por defecto (7 días)
	private static final int DEFAULT_EXPIRE_DAYS = 7;

	@Autowired
	private CommunityRepository communityRepository;
	@Autowired
	private CommunityMemberRepository communityMemberRepository;
	@Autowired
	private CommunityInviteRepository communityInviteRepository;
	@Autowired
	private UserRepository userRepository;

	// ─── Crear comunidad ─────────────────────────────────────────────────────

	/**
	 * Crea una nueva comunidad y agrega al creador como OWNER. Retorna: 0 = OK | 1
	 * = usuario no encontrado
	 */
	public int createCommunity(String username, String name, String description) {
		User owner = userRepository.findByUsername(username).orElse(null);
		if (owner == null)
			return 1;

		Community community = new Community();
		community.setName(name);
		community.setDescription(description);
		community.setOwner(owner);
		community.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
		communityRepository.save(community);

		// El creador entra automáticamente como OWNER
		CommunityMember member = new CommunityMember();
		member.setCommunity(community);
		member.setUser(owner);
		member.setRole(CommunityRole.OWNER);
		member.setJoinedAt(LocalDateTime.now(ZoneOffset.UTC));
		communityMemberRepository.save(member);

		return 0;
	}

	// ─── Generar enlace de invitación ────────────────────────────────────────

	/**
	 * Genera un enlace de invitación para la comunidad. Solo el OWNER puede generar
	 * invitaciones.
	 *
	 * @param maxUses    null = ilimitado | entero positivo = cupo máximo
	 * @param expireDays días hasta que el enlace vence (null = DEFAULT_EXPIRE_DAYS)
	 *
	 *                   Retorna null en caso de error. El caller distingue el caso
	 *                   por los códigos: 0 = OK → dto contiene el enlace 1 = no
	 *                   encontrado 2 = sin permiso (no es OWNER)
	 */
	public Object[] generateInvite(String username, Long communityId, Integer maxUses, Integer expireDays) {

		Community community = communityRepository.findById(communityId).orElse(null);
		if (community == null)
			return new Object[] { 1, null };

		// Solo el OWNER puede generar invitaciones
		boolean isOwner = communityMemberRepository.existsByCommunityIdAndUserUsernameAndRole(communityId, username,
				CommunityRole.OWNER);
		if (!isOwner)
			return new Object[] { 2, null };

		User creator = userRepository.findByUsername(username).orElse(null);

		int days = (expireDays != null && expireDays > 0) ? expireDays : DEFAULT_EXPIRE_DAYS;

		CommunityInvite invite = new CommunityInvite();
		invite.setCommunity(community);
		invite.setCreatedBy(creator);
		invite.setToken(UUID.randomUUID().toString());
		invite.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
		invite.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusDays(days));
		invite.setMaxUses(maxUses); // null = ilimitado
		invite.setCurrentUses(0);
		invite.setActive(true);
		communityInviteRepository.save(invite);

		return new Object[] { 0, toInviteDTO(invite) };
	}

	// ─── Unirse por enlace ───────────────────────────────────────────────────

	/**
	 * Procesa el token de un enlace de invitación y agrega al usuario a la
	 * comunidad.
	 *
	 * Retorna: 0 = OK 1 = usuario no encontrado 2 = token inválido o inexistente 3
	 * = enlace vencido 4 = enlace agotado (maxUses alcanzado) 5 = ya es miembro
	 */
	public int joinByToken(String username, String token) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		CommunityInvite invite = communityInviteRepository.findByToken(token).orElse(null);

		if (invite == null || !invite.isActive())
			return 2;

		// Verificar vencimiento
		if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
			invite.setActive(false);
			communityInviteRepository.save(invite);
			return 3;
		}

		// Verificar cupo
		if (invite.getMaxUses() != null && invite.getCurrentUses() >= invite.getMaxUses()) {
			invite.setActive(false);
			communityInviteRepository.save(invite);
			return 4;
		}

		Community community = invite.getCommunity();

		// Ya es miembro
		if (communityMemberRepository.existsByCommunityIdAndUserUsername(community.getId(), username))
			return 5;

		// Agregar miembro
		CommunityMember member = new CommunityMember();
		member.setCommunity(community);
		member.setUser(user);
		member.setRole(CommunityRole.MEMBER);
		member.setJoinedAt(LocalDateTime.now(ZoneOffset.UTC));
		communityMemberRepository.save(member);

		// Incrementar contador de usos y desactivar si llegó al límite
		invite.setCurrentUses(invite.getCurrentUses() + 1);
		if (invite.getMaxUses() != null && invite.getCurrentUses() >= invite.getMaxUses()) {
			invite.setActive(false);
		}
		communityInviteRepository.save(invite);

		return 0;
	}

	// ─── Revocar enlace ──────────────────────────────────────────────────────

	/**
	 * Desactiva manualmente un enlace de invitación. Solo el OWNER puede revocarlo.
	 * Retorna: 0 = OK | 1 = no encontrado | 2 = sin permiso
	 */
	public int revokeInvite(String username, Long inviteId) {
		CommunityInvite invite = communityInviteRepository.findById(inviteId).orElse(null);
		if (invite == null)
			return 1;

		boolean isOwner = communityMemberRepository.existsByCommunityIdAndUserUsernameAndRole(
				invite.getCommunity().getId(), username, CommunityRole.OWNER);
		if (!isOwner)
			return 2;

		invite.setActive(false);
		communityInviteRepository.save(invite);
		return 0;
	}

	// ─── Expulsar miembro ────────────────────────────────────────────────────

	/**
	 * El OWNER puede expulsar a cualquier MEMBER de su comunidad. Retorna: 0 = OK |
	 * 1 = comunidad no encontrada | 2 = sin permiso 3 = miembro no encontrado | 4 =
	 * no se puede expulsar al dueño
	 */
	public int removeMember(String ownerUsername, Long communityId, String targetUsername) {
		Community community = communityRepository.findById(communityId).orElse(null);
		if (community == null)
			return 1;

		boolean isOwner = communityMemberRepository.existsByCommunityIdAndUserUsernameAndRole(communityId,
				ownerUsername, CommunityRole.OWNER);
		if (!isOwner)
			return 2;

		if (!communityMemberRepository.existsByCommunityIdAndUserUsername(communityId, targetUsername))
			return 3;

		// El OWNER no puede expulsarse a sí mismo
		if (ownerUsername.equals(targetUsername))
			return 4;

		communityMemberRepository.deleteByCommunityIdAndUserUsername(communityId, targetUsername);
		return 0;
	}

	// ─── Abandonar comunidad ─────────────────────────────────────────────────

	/**
	 * Un miembro abandona voluntariamente la comunidad. El OWNER no puede
	 * abandonarla; primero debe transferir la titularidad o eliminarla. Retorna: 0
	 * = OK | 1 = no es miembro | 2 = el dueño no puede abandonar
	 */
	public int leaveCommunity(String username, Long communityId) {
		CommunityMember member = communityMemberRepository.findByCommunityIdAndUserUsername(communityId, username)
				.orElse(null);
		if (member == null)
			return 1;
		if (member.getRole() == CommunityRole.OWNER)
			return 2;

		communityMemberRepository.deleteByCommunityIdAndUserUsername(communityId, username);
		return 0;
	}

	// ─── Consultas ───────────────────────────────────────────────────────────

	public List<CommunityDTO> getCommunitiesForUser(String username) {
		return communityRepository.findCommunitiesByParticipant(username).stream().map(this::toCommunityDTO)
				.collect(Collectors.toList());
	}

	public CommunityDTO getCommunityById(Long communityId) {
		Community c = communityRepository.findById(communityId).orElse(null);
		if (c == null)
			return null;
		return toCommunityDTOWithMembers(c);
	}

	public List<CommunityInviteDTO> getActiveInvites(String username, Long communityId) {
		boolean isOwner = communityMemberRepository.existsByCommunityIdAndUserUsernameAndRole(communityId, username,
				CommunityRole.OWNER);
		if (!isOwner)
			return List.of();

		return communityInviteRepository.findByCommunityIdAndActiveTrue(communityId).stream().map(this::toInviteDTO)
				.collect(Collectors.toList());
	}

	// ─── Validación usada por PollService ────────────────────────────────────

	/**
	 * Verifica si un usuario es miembro activo de una comunidad. Se llama desde
	 * PollService antes de dejar unirse a una polla.
	 */
	public boolean isMember(Long communityId, String username) {
		return communityMemberRepository.existsByCommunityIdAndUserUsername(communityId, username);
	}

	// ─── Job: limpiar invitaciones vencidas ──────────────────────────────────

	/**
	 * Se ejecuta cada hora y desactiva automáticamente los enlaces vencidos.
	 */
	@Scheduled(fixedDelay = 3_600_000)
	public void deactivateExpiredInvites() {
		List<CommunityInvite> expired = communityInviteRepository
				.findByActiveTrueAndExpiresAtBefore(LocalDateTime.now(ZoneOffset.UTC));
		expired.forEach(inv -> {
			inv.setActive(false);
			communityInviteRepository.save(inv);
		});
		if (!expired.isEmpty()) {
			System.out.println("[CommunityService] Invitaciones vencidas desactivadas: " + expired.size());
		}
	}

	// ─── Conversión Entidad → DTO ─────────────────────────────────────────────

	private CommunityDTO toCommunityDTO(Community c) {
		CommunityDTO dto = new CommunityDTO();
		dto.setId(c.getId());
		dto.setName(c.getName());
		dto.setDescription(c.getDescription());
		dto.setOwnerUsername(c.getOwner().getUsername());
		dto.setMemberCount(c.getMembers() != null ? c.getMembers().size() : 0);
		dto.setPollCount(c.getPolls() != null ? c.getPolls().size() : 0);
		dto.setCreatedAt(c.getCreatedAt());
		return dto;
	}

	private CommunityDTO toCommunityDTOWithMembers(Community c) {
		CommunityDTO dto = toCommunityDTO(c);
		List<CommunityMember> members = communityMemberRepository.findByCommunityIdOrderByJoinedAtAsc(c.getId());
		dto.setMembers(members.stream().map(this::toMemberDTO).collect(Collectors.toList()));
		return dto;
	}

	private CommunityMemberDTO toMemberDTO(CommunityMember m) {
		CommunityMemberDTO dto = new CommunityMemberDTO();
		dto.setId(m.getId());
		dto.setUserId(m.getUser().getId());
		dto.setUsername(m.getUser().getUsername());
		dto.setName(m.getUser().getName());
		dto.setRole(m.getRole());
		dto.setJoinedAt(m.getJoinedAt());
		return dto;
	}

	public CommunityInviteDTO toInviteDTO(CommunityInvite inv) {
		CommunityInviteDTO dto = new CommunityInviteDTO();
		dto.setId(inv.getId());
		dto.setCommunityId(inv.getCommunity().getId());
		dto.setCommunityName(inv.getCommunity().getName());
		dto.setToken(inv.getToken());
//		dto.setInviteUrl(frontendUrl + "/communities/join/" + inv.getToken());
		dto.setInviteUrl(inv.getToken());
		dto.setCreatedByUsername(inv.getCreatedBy().getUsername());
		dto.setMaxUses(inv.getMaxUses());
		dto.setCurrentUses(inv.getCurrentUses());
		dto.setActive(inv.isActive());
		dto.setCreatedAt(inv.getCreatedAt());
		dto.setExpiresAt(inv.getExpiresAt());
		return dto;
	}
}