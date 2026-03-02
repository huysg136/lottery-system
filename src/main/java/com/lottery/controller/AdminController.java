package com.lottery.controller;

import com.lottery.model.Ticket;
import com.lottery.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TicketRepository ticketRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Ticket> allTickets = ticketRepository.findAll();
        model.addAttribute("tickets", allTickets);
        model.addAttribute("activePage", "admin-dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=lottery_tickets_report.pdf";
        response.setHeader(headerKey, headerValue);

        List<Ticket> tickets = ticketRepository.findAll();

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Lottery Tickets Sales Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1f, 3f, 2f, 3f, 1.5f, 2.5f, 1.5f});

        addTableHeader(table);
        addRows(table, tickets);

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"ID", "User", "Game", "Numbers", "Price ($)", "Selling Date", "Published Date"};
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addRows(PdfPTable table, List<Ticket> tickets) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        for (Ticket t : tickets) {
            table.addCell(new Phrase(String.valueOf(t.getId()), dataFont));
            table.addCell(new Phrase(t.getUser().getFirstName() + " " + t.getUser().getLastName() + "\n" + t.getUser().getEmail(), dataFont));
            table.addCell(new Phrase(t.getLotteryType().getName(), dataFont));
            
            StringBuilder nums = new StringBuilder();
            t.getTicketNumbers().forEach(tn -> {
                nums.append(tn.getMainNumbers()).append(" [").append(tn.getBonusNumber()).append("]\n");
            });
            table.addCell(new Phrase(nums.toString(), dataFont));
            
            table.addCell(new Phrase(String.format("$%.2f", t.getTotalAmount()), dataFont));
            table.addCell(new Phrase(t.getDateOfSelling() != null ? t.getDateOfSelling().format(formatter) : "", dataFont));
            table.addCell(new Phrase(t.getPublishedDate() != null ? t.getPublishedDate().format(formatter) : "Pending", dataFont));
        }
    }
}
