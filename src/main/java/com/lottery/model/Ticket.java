package com.lottery.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lottery_type_id", nullable = false)
    private LotteryType lotteryType;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "date_of_selling")
    @Builder.Default
    private LocalDateTime dateOfSelling = LocalDateTime.now();

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TicketNumber> ticketNumbers;

    public int getTicketCount() {
        return ticketNumbers != null ? ticketNumbers.size() : 0;
    }
}
