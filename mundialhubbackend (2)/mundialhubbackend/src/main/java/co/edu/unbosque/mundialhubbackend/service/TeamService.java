package co.edu.unbosque.mundialhubbackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.mundialhubbackend.dto.TeamDTO;
import co.edu.unbosque.mundialhubbackend.model.Team;
import co.edu.unbosque.mundialhubbackend.repository.TeamRepository;

@Service
public class TeamService {
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private ModelMapper modelMapper;

	public int create(TeamDTO dto) {
		if (teamRepository.existsByName(dto.getName()))
			return 2;
		try {
			teamRepository.save(modelMapper.map(dto, Team.class));
			return 0;
		} catch (Exception e) {
			return 1;
		}
	}

	public List<TeamDTO> getAll() {
		return teamRepository.findAll().stream().map(t -> modelMapper.map(t, TeamDTO.class))
				.collect(Collectors.toList());
	}

	public TeamDTO getByName(String name) {
		return teamRepository.findByName(name).map(t -> modelMapper.map(t, TeamDTO.class)).orElse(null);
	}

	public int updateByName(String name, TeamDTO dto) {
		return teamRepository.findByName(name).map(team -> {
			team.setFlagUrl(dto.getFlagUrl());
			team.setGroupName(dto.getGroupName());
			teamRepository.save(team);
			return 0;
		}).orElse(2);
	}

	public int deleteByName(String name) {
		if (!teamRepository.existsByName(name))
			return 2;
		try {
			teamRepository.deleteByName(name);
			return 0;
		} catch (Exception e) {
			return 1;
		}
	}
}