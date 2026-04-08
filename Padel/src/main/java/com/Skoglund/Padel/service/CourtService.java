package com.Skoglund.Padel.service;

import com.Skoglund.Padel.dto.CourtDTO;
import com.Skoglund.Padel.entity.Court;
import com.Skoglund.Padel.repository.CourtRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourtService {

    private static final Logger logger = LoggerFactory.getLogger(CourtService.class);

    private final CourtRepository courtRepository;

    public CourtService(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public List<CourtDTO> getAllCourts() {
        return courtRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CourtDTO getCourtById(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court not found with id: " + id));
        return toDTO(court);
    }

    @Transactional
    public CourtDTO createCourt(CourtDTO dto) {
        Court court = new Court();
        court.setName(dto.getName());
        court.setSurface(dto.getSurface());
        court.setIndoors(dto.isIndoors());
        court.setPricePerHourSek(dto.getPricePerHourSek());
        Court saved = courtRepository.save(court);
        logger.info("admin created court {} (id={})", saved.getName(), saved.getId());
        return toDTO(saved);
    }

    @Transactional
    public CourtDTO updateCourt(Long id, CourtDTO dto) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court not found with id: " + id));
        court.setName(dto.getName());
        court.setSurface(dto.getSurface());
        court.setIndoors(dto.isIndoors());
        court.setPricePerHourSek(dto.getPricePerHourSek());
        Court saved = courtRepository.save(court);
        logger.info("admin updated court {} (id={})", saved.getName(), saved.getId());
        return toDTO(saved);
    }

    @Transactional
    public void deleteCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court not found with id: " + id));
        courtRepository.delete(court);
        logger.info("admin deleted court {} (id={})", court.getName(), id);
    }

    public CourtDTO toDTO(Court court) {
        CourtDTO dto = new CourtDTO();
        dto.setId(court.getId());
        dto.setName(court.getName());
        dto.setSurface(court.getSurface());
        dto.setIndoors(court.isIndoors());
        dto.setPricePerHourSek(court.getPricePerHourSek());
        return dto;
    }
}