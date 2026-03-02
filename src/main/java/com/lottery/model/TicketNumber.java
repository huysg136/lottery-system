package com.lottery.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ticket_numbers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "main_numbers", nullable = false, length = 50)
    private String mainNumbers; // VD: "5,12,23,45,67"

    @Column(name = "bonus_number", nullable = false)
    private int bonusNumber;

    // Helper method
    public String[] getMainNumbersArray() {
        return mainNumbers.split(",");
    }

    public String getDisplayNumbers() {
        return mainNumbers + " | " + bonusNumber;
    }
}
