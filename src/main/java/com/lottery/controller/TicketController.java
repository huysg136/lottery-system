package com.lottery.controller;

import com.lottery.model.*;
import com.lottery.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final UserService userService;
    private final TicketService ticketService;
    private final CartService cartService;

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Ticket> tickets = ticketService.getTicketHistory(user);
        java.math.BigDecimal totalSpent = tickets.stream()
                .map(Ticket::getTotalAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        model.addAttribute("tickets", tickets);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("cartCount", cartService.getCartCount(user));
        model.addAttribute("activePage", "history");
        return "ticket/history";
    }
}
