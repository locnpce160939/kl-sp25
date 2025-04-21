package com.ftcs.authservice.features.account;

import com.ftcs.authservice.features.account.contacts.Rank;
import com.ftcs.authservice.features.account.contacts.RoleType;
import com.ftcs.authservice.features.account.contacts.StatusAccount;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Account", schema = "dbo")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "Username", length = 50)
    private String username;

    @Column(name = "FullName", length = 500)
    private String fullName;

    @Column(name = "Password", length = 50)
    private String password;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "Phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "Role", length = 50)
    private RoleType role;

    @Column(name = "ProfilePicture", length = 255)
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20)
    private StatusAccount status;

    @Column(name = "Balance", nullable = false)
    private Double balance;

    @Column(name = "Notes", length = 255)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "Ranking", length = 50, nullable = false)
    private Rank ranking;

    @Column(name = "LoyaltyPoints", nullable = false)
    private Integer loyaltyPoints;

    @Column(name = "RedeemablePoints", nullable = false)
    private Integer redeemablePoints; // Điểm có thể đổi voucher

    @Column(name = "LastLogin")
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "CreateAt", updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "UpdateAt")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        loyaltyPoints = 0;
        ranking = Rank.BRONZE; // Mặc định hạng ban đầu là Bronze
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}