package com.lottery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lottery_type_id", nullable = false)
    private LotteryType lotteryType;

    @Column(name = "main_numbers", nullable = false, length = 50)
    private String mainNumbers; // VD: "5,12,23,45,67"

    @Column(name = "bonus_number", nullable = false)
    private int bonusNumber;

    @Column(name = "added_at")
    @Builder.Default
    private LocalDateTime addedAt = LocalDateTime.now();

    // Helper method to get numbers as array
    public String[] getMainNumbersArray() {
        return mainNumbers.split(",");
    }

    public String getDisplayNumbers() {
        return mainNumbers + " | " + bonusNumber;
    }
}
