package com.ftcs.authservice.features.account;

import com.ftcs.authservice.features.account.contacts.RoleType;
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

    @Column(name = "Status", length = 20)
    private String status;

    @Column(name = "Balance", nullable = false)
    private Double balance;

    @Column(name = "Notes", length = 255)
    private String notes;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}