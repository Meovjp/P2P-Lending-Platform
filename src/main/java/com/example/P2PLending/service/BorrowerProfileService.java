package com.example.P2PLending.service;

import com.example.P2PLending.dto.BorrowerProfileDto;
import com.example.P2PLending.entity.BorrowerProfile;
import com.example.P2PLending.entity.User;
import com.example.P2PLending.repository.BorrowerProfileRepository;
import com.example.P2PLending.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BorrowerProfileService {

    private final BorrowerProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional
    public BorrowerProfileDto createOrUpdateProfile(String username, BorrowerProfileDto profileDto) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        BorrowerProfile profile = profileRepository.findById(user.getUserId())
                .orElse(new BorrowerProfile());

        // 1. Cập nhật thông tin cơ bản
        profile.setUser(user);
        profile.setFullName(profileDto.getFullName());
        profile.setDateOfBirth(profileDto.getDateOfBirth());
        profile.setMonthlyIncome(profileDto.getMonthlyIncome());
        profile.setDtiRatio(profileDto.getDtiRatio());

        // 2. CHỈ TÍNH Risk Category (Theo Yêu cầu 1)
        calculateRiskCategory(profile);

        // 3. RESET Lãi suất (Theo Yêu cầu 2)
        // Điều này buộc Admin phải xem xét và gán lại lãi suất
        profile.setInterestRate(null);

        BorrowerProfile savedProfile = profileRepository.save(profile);
        return mapToDto(savedProfile);
    }

    @Transactional(readOnly = true)
    public BorrowerProfileDto getProfileByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        BorrowerProfile profile = profileRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Borrower profile not found"));
        
        return mapToDto(profile);
    }

    /**
     * Logic nghiệp vụ: Chỉ tính toán điểm rủi ro và hạng (A, B, C).
     * KHÔNG gán lãi suất.
     */
    private void calculateRiskCategory(BorrowerProfile profile) {
        int score = 650; // Điểm cơ bản
        double dti = profile.getDtiRatio();
        double income = profile.getMonthlyIncome().doubleValue();

        if (dti > 0.4) score -= 100;
        else if (dti < 0.2) score += 50;

        if (income < 10000000) score -= 50;
        else if (income > 30000000) score += 50;
        
        profile.setRiskScore(Math.max(300, Math.min(850, score)));

        if (score > 720) {
            profile.setRiskCategory("A");
        } else if (score > 650) {
            profile.setRiskCategory("B");
        } else {
            profile.setRiskCategory("C");
        }
    }

    private BorrowerProfileDto mapToDto(BorrowerProfile profile) {
        BorrowerProfileDto dto = new BorrowerProfileDto();
        dto.setUserId(profile.getId());
        dto.setUsername(profile.getUser().getUsername());
        dto.setFullName(profile.getFullName());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setMonthlyIncome(profile.getMonthlyIncome());
        dto.setDtiRatio(profile.getDtiRatio());
        dto.setRiskScore(profile.getRiskScore());
        dto.setRiskCategory(profile.getRiskCategory());
        dto.setInterestRate(profile.getInterestRate()); // Sẽ là null nếu chưa đc gán
        return dto;
    }
}