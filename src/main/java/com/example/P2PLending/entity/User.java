package com.example.P2PLending.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.JoinColumn;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails { // Implement UserDetails
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId; 

    @Column(name="user_name",nullable = false, unique = true)
    private String userName; 

    @Column(name="password_hash",nullable = false)
    private String passwordHash;// Sẽ lưu password đã hash

    @Column(nullable = false, unique = true)
    private String email; 

    private boolean enabled = true; 

    // Mối quan hệ N-N với Role
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
    	    name = "user_role", // (1) Đổi cho nhất quán
    	    joinColumns = @JoinColumn(name = "user_id"), // (2) Sửa lại
    	    inverseJoinColumns = @JoinColumn(name = "role_id") // (3) Sửa lại
    	)
    private Set<Role> roles; 
    
    /**
     * Liên kết 1-1 với BorrowerProfile (phía ngược lại)
     * mappedBy="user" chỉ ra rằng 'BorrowerProfile' là chủ sở hữu của mối quan hệ này.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Tôi là cha, hãy hiển thị con (profile) của tôi
    private BorrowerProfile borrowerProfile;
    
 // --- [FIX 3] THÊM TRƯỜNG SỐ DƯ VÍ ---
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO; // Mặc định là 0

    // --- Các phương thức của UserDetails ---
    // Spring Security sẽ dùng các phương thức này

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Chuyển Set<Role> thành Set<GrantedAuthority>
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());
        
    }

    @Override
    public String getPassword() {
        return this.passwordHash; // Trả về password đã hash
    }

    @Override
    public String getUsername() {
        return this.userName; // Trả về username
    }

    // Các phương thức khác (tạm thời để 'true')
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}