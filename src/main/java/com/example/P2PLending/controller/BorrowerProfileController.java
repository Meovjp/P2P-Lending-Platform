package com.example.P2PLending.controller;

import com.example.P2PLending.dto.BorrowerProfileDto;
import com.example.P2PLending.service.BorrowerProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class BorrowerProfileController {

    private final BorrowerProfileService profileService;

    @PostMapping
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<BorrowerProfileDto> createOrUpdateProfile(@Valid @RequestBody BorrowerProfileDto profileDto, Principal principal) {
        String username = principal.getName();
        BorrowerProfileDto updatedProfile = profileService.createOrUpdateProfile(username, profileDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<BorrowerProfileDto> getMyProfile(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(profileService.getProfileByUsername(username));
    }
}