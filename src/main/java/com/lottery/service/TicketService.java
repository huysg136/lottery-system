package com.lottery.service;

import com.lottery.model.*;
import com.lottery.repository.TicketRepository;
import com.lottery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public List<Ticket> getTicketHistory(User user) {
        return ticketRepository.findByUserOrderByDateOfSellingDesc(user);
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
    }

    @Transactional
    public List<Ticket> checkout(User user, List<CartItem> cartItems) {
        BigDecimal totalCost = cartItems.stream()
                .map(item -> item.getLotteryType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new IllegalStateException("Insufficient balance to complete the purchase.");
        }

        user.setBalance(user.getBalance().subtract(totalCost));
        userRepository.save(user);

        List<Ticket> tickets = new ArrayList<>();

        for (CartItem item : cartItems) {
            TicketNumber tn = TicketNumber.builder()
                    .mainNumbers(item.getMainNumbers())
                    .bonusNumber(item.getBonusNumber())
                    .build();

            // For simplicity, we assume publishedDate will be manually updated by Admin later.
            // Right now we just set it as null (will be drawn later)
            Ticket ticket = Ticket.builder()
                    .user(user)
                    .lotteryType(item.getLotteryType())
                    .totalAmount(item.getLotteryType().getPrice())
                    .ticketNumbers(new ArrayList<>(List.of(tn)))
                    .dateOfSelling(LocalDateTime.now())
                    // admin will update publishedDate when drawing the results
                    .build();

            tn.setTicket(ticket);
            tickets.add(ticketRepository.save(ticket));
        }

        return tickets;
    }
}
