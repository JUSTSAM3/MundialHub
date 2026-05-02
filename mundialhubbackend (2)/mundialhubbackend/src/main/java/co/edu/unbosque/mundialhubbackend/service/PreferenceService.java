package co.edu.unbosque.mundialhubbackend.service;

import co.edu.unbosque.mundialhubbackend.dto.TeamDTO;
import co.edu.unbosque.mundialhubbackend.dto.StadiumDTO;
import co.edu.unbosque.mundialhubbackend.model.Stadium;
import co.edu.unbosque.mundialhubbackend.model.Team;
import co.edu.unbosque.mundialhubbackend.model.User;
import co.edu.unbosque.mundialhubbackend.repository.StadiumRepository;
import co.edu.unbosque.mundialhubbackend.repository.TeamRepository;
import co.edu.unbosque.mundialhubbackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PreferenceService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private StadiumRepository stadiumRepository;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Devuelve la lista completa de equipos favoritos del usuario. Retorna lista
	 * vacía si el usuario no existe.
	 */
	public List<TeamDTO> getFavoriteTeams(String username) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return new ArrayList<>();
		return user.getFavoriteTeams().stream().map(team -> modelMapper.map(team, TeamDTO.class))
				.collect(Collectors.toList());
	}

	/**
	 * Reemplaza la lista completa de equipos favoritos por los IDs recibidos. Los
	 * IDs que no existan en base de datos se omiten silenciosamente. Códigos: 0 =
	 * OK, 1 = usuario no encontrado.
	 */
	public int updateFavoriteTeams(String username, List<Long> ids) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		List<Team> nuevos = new ArrayList<>();
		for (Long id : ids) {
			teamRepository.findById(id).ifPresent(nuevos::add);
		}

		user.getFavoriteTeams().clear();
		user.getFavoriteTeams().addAll(nuevos);
		userRepository.save(user);
		return 0;
	}

	/**
	 * Agrega a los favoritos todos los equipos de la lista de IDs recibida. Los que
	 * ya estén en favoritos o no existan se omiten sin lanzar error. Códigos: 0 =
	 * OK, 1 = usuario no encontrado.
	 */
	public int addTeamsToFavorites(String username, List<Long> ids) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		for (Long id : ids) {
			teamRepository.findById(id).ifPresent(team -> {
				if (!user.getFavoriteTeams().contains(team)) {
					user.getFavoriteTeams().add(team);
				}
			});
		}

		userRepository.save(user);
		return 0;
	}

	/**
	 * Elimina de los favoritos todos los equipos cuyos IDs se reciben. Los IDs que
	 * no estuvieran en la lista se ignoran. Códigos: 0 = OK, 1 = usuario no
	 * encontrado.
	 */
	public int removeTeamsFromFavorites(String username, List<Long> ids) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		user.getFavoriteTeams().removeIf(team -> ids.contains(team.getId()));
		userRepository.save(user);
		return 0;
	}

	/**
	 * Devuelve la lista completa de estadios favoritos del usuario. Retorna lista
	 * vacía si el usuario no existe.
	 */
	public List<StadiumDTO> getFavoriteStadiums(String username) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return new ArrayList<>();
		return user.getFavoriteStadiums().stream().map(stadium -> modelMapper.map(stadium, StadiumDTO.class))
				.collect(Collectors.toList());
	}

	/**
	 * Reemplaza la lista completa de estadios favoritos por los IDs recibidos. Los
	 * IDs que no existan en base de datos se omiten silenciosamente. Códigos: 0 =
	 * OK, 1 = usuario no encontrado.
	 */
	public int updateFavoriteStadiums(String username, List<Long> ids) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		List<Stadium> nuevos = new ArrayList<>();
		for (Long id : ids) {
			stadiumRepository.findById(id).ifPresent(nuevos::add);
		}

		user.getFavoriteStadiums().clear();
		user.getFavoriteStadiums().addAll(nuevos);
		userRepository.save(user);
		return 0;
	}

	/**
	 * Agrega a los favoritos todos los estadios de la lista de IDs recibida. Los
	 * que ya estén en favoritos o no existan se omiten sin lanzar error. Códigos: 0
	 * = OK, 1 = usuario no encontrado.
	 */
	public int addStadiumsToFavorites(String username, List<Long> ids) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		for (Long id : ids) {
			stadiumRepository.findById(id).ifPresent(stadium -> {
				if (!user.getFavoriteStadiums().contains(stadium)) {
					user.getFavoriteStadiums().add(stadium);
				}
			});
		}

		userRepository.save(user);
		return 0;
	}

	/**
	 * Elimina de los favoritos todos los estadios cuyos IDs se reciben. Los IDs que
	 * no estuvieran en la lista se ignoran. Códigos: 0 = OK, 1 = usuario no
	 * encontrado.
	 */
	public int removeStadiumsFromFavorites(String username, List<Long> ids) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		user.getFavoriteStadiums().removeIf(stadium -> ids.contains(stadium.getId()));
		userRepository.save(user);
		return 0;
	}
}