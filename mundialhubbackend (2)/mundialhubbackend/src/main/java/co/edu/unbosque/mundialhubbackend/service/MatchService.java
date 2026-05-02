package co.edu.unbosque.mundialhubbackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.mundialhubbackend.dto.MatchDTO;
import co.edu.unbosque.mundialhubbackend.model.Match;
import co.edu.unbosque.mundialhubbackend.model.Stadium;
import co.edu.unbosque.mundialhubbackend.model.Team;
import co.edu.unbosque.mundialhubbackend.repository.MatchRepository;
import co.edu.unbosque.mundialhubbackend.repository.StadiumRepository;
import co.edu.unbosque.mundialhubbackend.repository.TeamRepository;
import co.edu.unbosque.mundialhubbackend.util.MatchCreateRequest;
import co.edu.unbosque.mundialhubbackend.util.MatchResultRequest;

@Service
public class MatchService {

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private StadiumRepository stadiumRepository;

	@Autowired
	private ModelMapper modelMapper;

	public int createMatch(MatchCreateRequest request) {
		Team homeTeam = teamRepository.findById(request.getHomeTeamId()).orElse(null);
		Team awayTeam = teamRepository.findById(request.getAwayTeamId()).orElse(null);
		Stadium stadium = stadiumRepository.findById(request.getStadiumId()).orElse(null);

		if (homeTeam == null || awayTeam == null || stadium == null) {
			return 1; // Error: Entidades relacionadas no encontradas
		}

		try {
			Match match = new Match();
			match.setHomeTeam(homeTeam);
			match.setAwayTeam(awayTeam);
			match.setStadium(stadium);
			match.setMatchDate(request.getMatchDate());
			match.setStatus(Match.MatchStatus.SCHEDULED); // Inicia programado

			matchRepository.save(match);
			return 0; // Éxito
		} catch (Exception e) {
			return 2; // Error general
		}
	}

	// Listar todos los partidos (Para usuarios)
	public List<MatchDTO> getAllMatches() {
		return matchRepository.findAll().stream().map(m -> modelMapper.map(m, MatchDTO.class))
				.collect(Collectors.toList());
	}

	// Registrar resultado (Solo ADMIN/OPERATOR)
	public int updateMatchResult(MatchResultRequest request) {
		Match match = matchRepository.findById(request.getId()).orElse(null);
		if (match == null)
			return 1; // No encontrado

		if (request.getHomeScore() == null || request.getAwayScore() == null) {
			return 3; // Faltan datos
		}

		try {
			match.setHomeScore(request.getHomeScore());
			match.setAwayScore(request.getAwayScore());
			match.setStatus(Match.MatchStatus.FINISHED); // Al registrar resultado, se finaliza

			matchRepository.save(match);
			return 0; // Éxito
		} catch (Exception e) {
			return 2; // Error
		}
	}
}