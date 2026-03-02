package com.lottery.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lottery_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotteryType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "main_count", nullable = false)
    private int mainCount; // Số lượng số chính cần chọn

    @Column(name = "main_max", nullable = false)
    private int mainMax; // Giá trị max số chính

    @Column(name = "bonus_max", nullable = false)
    private int bonusMax; // Giá trị max số bonus

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal price = new BigDecimal("2.00");

    @Column(name = "jackpot_amount", length = 50)
    private String jackpotAmount;

    @Column(name = "next_draw_date", length = 50)
    private String nextDrawDate;
}
