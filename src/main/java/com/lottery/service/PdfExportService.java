package com.lottery.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lottery.model.Ticket;
import com.lottery.model.TicketNumber;
import com.lottery.model.User;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportService {

    public void exportTicketsToPdf(User user, List<Ticket> tickets, String filePath) throws DocumentException, IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            exportTicketsToPdfStream(user, tickets, fos);
        }
    }

    public void exportTicketsToPdfStream(User user, List<Ticket> tickets, java.io.OutputStream outputStream) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        // Title
        Paragraph title = new Paragraph("Lottery Ticket Purchase Receipt", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // User Info
        document.add(new Paragraph("User Information:", boldFont));
        document.add(new Paragraph("Name: " + user.getFirstName() + " " + user.getLastName(), regularFont));
        document.add(new Paragraph("Email: " + user.getEmail(), regularFont));
        document.add(new Paragraph("Username: " + user.getUsername(), regularFont));
        document.add(new Paragraph(" ")); // Spacer

        // Tickets Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        
        // Table Headers
        String[] headers = {"Ticket ID", "Game", "Date", "Numbers"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Table Data
        for (Ticket ticket : tickets) {
            for (TicketNumber number : ticket.getTicketNumbers()) {
                table.addCell(new Phrase(String.valueOf(ticket.getId()), regularFont));
                table.addCell(new Phrase(ticket.getLotteryType().getName(), regularFont));
                table.addCell(new Phrase(ticket.getDateOfSelling().format(formatter), regularFont));
                
                String nums = number.getMainNumbers() + " [Bonus: " + number.getBonusNumber() + "]";
                table.addCell(new Phrase(nums, regularFont));
            }
        }

        document.add(table);
        
        Paragraph footer = new Paragraph("Thank you for your purchase. Good luck!", regularFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();
    }
}
