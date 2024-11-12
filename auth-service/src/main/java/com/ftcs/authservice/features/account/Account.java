package com.ftcs.authservice.features.account;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

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

    @Column(name = "Password", length = 50)
    private String password;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "Phone", length = 20)
    private String phone;

    @Column(name = "Role", length = 50)
    private String role;

    @Column(name = "ProfilePicture", length = 255)
    private String profilePicture;

    @Column(name = "LastLogin")
    private Instant lastLogin;

    @CreationTimestamp
    @Column(name = "CreateAt", updatable = false)
    private Instant createAt;

    @UpdateTimestamp
    @Column(name = "UpdateAt")
    private Instant updateAt;
}