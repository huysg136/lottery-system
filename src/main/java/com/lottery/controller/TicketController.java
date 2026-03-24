package com.lottery.controller;

import com.lottery.model.*;
import com.lottery.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import com.lottery.repository.LotteryTypeRepository;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final UserService userService;
    private final TicketService ticketService;
    private final CartService cartService;
    private final PdfExportService pdfExportService;
    private final LotteryTypeRepository lotteryTypeRepository;

    @GetMapping("/history")
    public String history(
            @RequestParam(required = false) Long gameId,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails, Model model) {
        
        User user = userService.findByUsername(userDetails.getUsername());
        List<Ticket> tickets = ticketService.getTicketHistory(user);

        if (gameId != null) {
            tickets = tickets.stream()
                    .filter(t -> t.getLotteryType().getId().equals(gameId))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            if ("PENDING".equalsIgnoreCase(status)) {
                tickets = tickets.stream().filter(t -> t.getPublishedDate() == null).collect(Collectors.toList());
            } else if ("WON".equalsIgnoreCase(status)) {
                tickets = tickets.stream().filter(t -> t.getPublishedDate() != null && t.getWinAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
            } else if ("LOST".equalsIgnoreCase(status)) {
                tickets = tickets.stream().filter(t -> t.getPublishedDate() != null && t.getWinAmount().compareTo(BigDecimal.ZERO) == 0).collect(Collectors.toList());
            }
        }

        BigDecimal totalSpent = tickets.stream()
                .map(Ticket::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("tickets", tickets);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("cartCount", cartService.getCartCount(user));
        model.addAttribute("lotteryTypes", lotteryTypeRepository.findAll());
        model.addAttribute("activePage", "history");
        
        // Add current filters back to the model so the UI can select them
        model.addAttribute("selectedGameId", gameId);
        model.addAttribute("selectedStatus", status);
        
        return "ticket/history";
    }

    @GetMapping("/export/{id}")
    public void exportTicketPdf(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) throws IOException, DocumentException {
        User user = userService.findByUsername(userDetails.getUsername());
        Ticket ticket = ticketService.findById(id);

        if (!ticket.getUser().getId().equals(user.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to view this ticket");
            return;
        }

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=ticket_" + ticket.getId() + ".pdf";
        response.setHeader(headerKey, headerValue);

        pdfExportService.exportTicketsToPdfStream(user, List.of(ticket), response.getOutputStream());
    }
}
