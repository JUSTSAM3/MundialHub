package co.edu.unbosque.mundialhubbackend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.unbosque.mundialhubbackend.dto.StadiumDTO;
import co.edu.unbosque.mundialhubbackend.model.Stadium;
import co.edu.unbosque.mundialhubbackend.repository.StadiumRepository;

@Service
public class StadiumService {

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private ModelMapper modelMapper;

    public int create(StadiumDTO dto) {
        if (stadiumRepository.existsByName(dto.getName())) return 2;
        try {
            stadiumRepository.save(modelMapper.map(dto, Stadium.class));
            return 0;
        } catch (Exception e) { return 1; }
    }

    public List<StadiumDTO> getAll() {
        return stadiumRepository.findAll().stream()
                .map(s -> modelMapper.map(s, StadiumDTO.class))
                .collect(Collectors.toList());
    }

    public StadiumDTO getByName(String name) {
        return stadiumRepository.findByName(name)
                .map(s -> modelMapper.map(s, StadiumDTO.class))
                .orElse(null);
    }

    public int updateByName(String name, StadiumDTO dto) {
        return stadiumRepository.findByName(name).map(stadium -> {
            stadium.setCity(dto.getCity());
            stadium.setCapacity(dto.getCapacity());
            stadiumRepository.save(stadium);
            return 0;
        }).orElse(2);
    }

    @Transactional
    public int deleteByName(String name) {
        if (!stadiumRepository.existsByName(name)) return 2;
        try {
            stadiumRepository.deleteByName(name);
            return 0;
        } catch (Exception e) { return 1; }
    }
}