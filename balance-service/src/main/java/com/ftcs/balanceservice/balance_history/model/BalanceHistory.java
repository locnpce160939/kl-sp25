package com.ftcs.balanceservice.balance_history.model;

import com.ftcs.balanceservice.balance_history.constant.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BalanceHistory", schema = "dbo")
public class BalanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BalanceHistoryId", nullable = false)
    private Long balanceHistoryId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "Amount", nullable = false)
    private Double amount;

    @Column(name = "referenceId", nullable = false)
    private Long referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TransactionType", nullable = false)
    private TransactionType transactionType;

    @Column(name = "PreviousBalance")
    private Double previousBalance;

    @Column(name = "CurrentBalance")
    private Double currentBalance;

    @Column(name = "Description", length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "TransactionDate", updatable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }
}