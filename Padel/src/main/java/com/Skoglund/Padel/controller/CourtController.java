package com.Skoglund.Padel.controller;

import com.Skoglund.Padel.dto.CourtDTO;
import com.Skoglund.Padel.service.CourtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courts")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CourtDTO>> getAllCourts() {
        return ResponseEntity.ok(courtService.getAllCourts());
    }

    @GetMapping("/{courtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtDTO> getCourtById(@PathVariable Long courtId) {
        return ResponseEntity.ok(courtService.getCourtById(courtId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtDTO> createCourt(@RequestBody CourtDTO dto) {
        CourtDTO created = courtService.createCourt(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{courtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtDTO> updateCourt(@PathVariable Long courtId,
                                                @RequestBody CourtDTO dto) {
        return ResponseEntity.ok(courtService.updateCourt(courtId, dto));
    }

    @DeleteMapping("/{courtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourt(@PathVariable Long courtId) {
        courtService.deleteCourt(courtId);
        return ResponseEntity.noContent().build();
    }
}