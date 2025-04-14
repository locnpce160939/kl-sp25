package com.ftcs.rank_setting;

import com.ftcs.authservice.features.account.contacts.Rank;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RankSetting", schema = "dbo")
public class RankSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NameRank", nullable = false, length = 100)
    private Rank nameRank;

    @Column(name = "MinPoint", nullable = false)
    private Integer minPoint;

    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "UpdateDate")
    private LocalDateTime updateDate;
}