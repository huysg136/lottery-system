package com.lottery.controller;

import com.lottery.model.Ticket;
import com.lottery.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletResponse;
import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import com.lottery.service.PdfExportService;
import com.lottery.service.DrawService;
import com.lottery.repository.LotteryTypeRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TicketRepository ticketRepository;
    private final PdfExportService pdfExportService;
    private final DrawService drawService;
    private final LotteryTypeRepository lotteryTypeRepository;

    @GetMapping("/dashboard")
    public String dashboard(
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long gameId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String status,
            Model model) {
        
        List<Ticket> allTickets = ticketRepository.findAll();

        if (gameId != null) {
            allTickets = allTickets.stream()
                    .filter(t -> t.getLotteryType().getId().equals(gameId))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            if ("PENDING".equalsIgnoreCase(status)) {
                allTickets = allTickets.stream().filter(t -> t.getPublishedDate() == null).collect(Collectors.toList());
            } else if ("WON".equalsIgnoreCase(status)) {
                allTickets = allTickets.stream().filter(t -> t.getPublishedDate() != null && t.getWinAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
            } else if ("LOST".equalsIgnoreCase(status)) {
                allTickets = allTickets.stream().filter(t -> t.getPublishedDate() != null && t.getWinAmount().compareTo(BigDecimal.ZERO) == 0).collect(Collectors.toList());
            }
        }

        model.addAttribute("tickets", allTickets);
        model.addAttribute("lotteryTypes", lotteryTypeRepository.findAll());
        model.addAttribute("selectedGameId", gameId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("activePage", "admin-dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/export/pdf/{id}")
    public void exportSingleTicketPDF(@org.springframework.web.bind.annotation.PathVariable Long id, HttpServletResponse response) throws IOException, DocumentException {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
            return;
        }

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=ticket_" + ticket.getId() + ".pdf";
        response.setHeader(headerKey, headerValue);

        pdfExportService.exportTicketsToPdfStream(ticket.getUser(), List.of(ticket), response.getOutputStream());
    }

    @PostMapping("/draw")
    public String conductDraw(RedirectAttributes redirectAttributes) {
        Map<String, Integer> results = drawService.conductDraw();
        int winners = results.get("winners");
        int checked = results.get("checked");
        
        if (checked == 0) {
            redirectAttributes.addFlashAttribute("success", "No pending tickets to draw.");
        } else {
            redirectAttributes.addFlashAttribute("success", "Draw completed! Checked " + checked + " ticket(s) and found " + winners + " winner(s). Prizes have been awarded.");
        }
        
        return "redirect:/admin/dashboard";
    }
}
