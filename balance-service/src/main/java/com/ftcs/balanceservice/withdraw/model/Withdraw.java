package com.ftcs.balanceservice.withdraw.model;
import com.ftcs.balanceservice.withdraw.constant.BankName;
import com.ftcs.balanceservice.withdraw.constant.WithdrawStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Withdraw", schema = "dbo")
public class Withdraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WithdrawId", nullable = false)
    private Long withdrawId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "Amount", nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20, nullable = false)
    private WithdrawStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "BankName", length = 50, nullable = false)
    private BankName bankName;

    @Column(name = "BankAccountNumber", length = 50, nullable = false)
    private String bankAccountNumber;

    @CreationTimestamp
    @Column(name = "RequestDate", updatable = false)
    private LocalDateTime requestDate;

    @UpdateTimestamp
    @Column(name = "ProcessedDate")
    private LocalDateTime processedDate;

    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (status == WithdrawStatus.APPROVED || status == WithdrawStatus.REJECTED) {
            processedDate = LocalDateTime.now();
        }
    }
}
