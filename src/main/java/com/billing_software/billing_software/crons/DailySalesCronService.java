package com.billing_software.billing_software.crons;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.models.financialDocument.FinancialDocument;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.models.organization.Outlet;
import com.billing_software.billing_software.repositories.FinancialDocumentRepository;
import com.billing_software.billing_software.utils.commons.EmailService;


@Service
public class DailySalesCronService {

        @Autowired
        private FinancialDocumentRepository financialDocumentRepository;

        @Autowired
        private EmailService emailService;

        @Autowired
        private OrganizationInterface organizationInterface;

        // Scheduled to run every day at 23:59 (11:59 PM)
        // @Scheduled(cron = "0 59 23 * * *")
        // public void sendDailySalesReport() {
        //         try {
        //                 // Get today's date
        //                 LocalDate today = LocalDate.now();

        //                 // Prepare the start and end date-times for today
        //                 LocalDateTime startDateTime = today.atStartOfDay();
        //                 LocalDateTime endDateTime = today.atTime(23, 59, 59);

        //                 // Fetch financial documents for the day
        //                 List<FinancialDocument> documentList = financialDocumentRepository
        //                                 .findAllByDocumentDateRange(startDateTime, endDateTime)
        //                                 .collectList()
        //                                 .blockOptional()
        //                                 .orElse(List.of()); // Default to empty list if none found

        //                 if (documentList != null && documentList.size() > 0) {

        //                         Organization orgData = organizationInterface.get(documentList.get(0).getOrgId());
        //                         Outlet outletData = orgData.getOutlets().get(0);
        //                         // Prepare email subject and body
        //                         String subject = String.format("Daily Sales Report for %s",
        //                                         startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        //                         StringBuilder body = new StringBuilder();
        //                         body.append("<h1>Sales Report</h1>");
        //                         body.append("<p>Below are the sales details for ")
        //                                         .append(startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE))
        //                                         .append(":</p>");

        //                         // Generate sales report in HTML table format
        //                         if (documentList.isEmpty()) {
        //                                 body.append("<p>No sales recorded for today.</p>");
        //                         } else {
        //                                 body.append("<table style='border-collapse: collapse; width: 100%;'>")
        //                                                 .append("<tr style='background-color: #f2f2f2;'>")
        //                                                 .append("<th style='border: 1px solid #dddddd; text-align: left; padding: 8px;'>Document Type</th>")
        //                                                 .append("<th style='border: 1px solid #dddddd; text-align: left; padding: 8px;'>Document Number</th>")
        //                                                 .append("<th style='border: 1px solid #dddddd; text-align: left; padding: 8px;'>Date</th>")
        //                                                 .append("<th style='border: 1px solid #dddddd; text-align: left; padding: 8px;'>Customer Name</th>")
        //                                                 .append("<th style='border: 1px solid #dddddd; text-align: left; padding: 8px;'>Total Amount</th>")
        //                                                 .append("<th style='border: 1px solid #dddddd; text-align: left; padding: 8px;'>Status</th>")
        //                                                 .append("</tr>");

        //                                 for (FinancialDocument document : documentList) {
        //                                         body.append("<tr>")
        //                                                         .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
        //                                                         .append(document.getDocumentType()).append("</td>")
        //                                                         .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
        //                                                         .append(document.getDocumentNumber()).append("</td>")
        //                                                         .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
        //                                                         .append(document.getDocumentDate()).append("</td>")
        //                                                         .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
        //                                                         .append(document.getCustomerName()).append("</td>")
        //                                                         .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
        //                                                         .append(document.getTotalAmount()).append("</td>")
        //                                                         .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
        //                                                         .append(document.getStatus()).append("</td>")
        //                                                         .append("</tr>");
        //                                 }

        //                                 body.append("</table>");
        //                         }

        //                         // Send email with the generated content
        //                         try {
        //                                 emailService.sendEmail(
        //                                                 outletData.getSMTPConfig().get("username"), // Replace with your
        //                                                                                             // sender
        //                                                                                             // email
        //                                                 outletData.getSMTPConfig().get("password"), // Use the subject
        //                                                                                             // constructed above
        //                                                 orgData.getEmail(), // Replace with recipient email
        //                                                 subject,
        //                                                 body.toString(), // Convert StringBuilder to String
        //                                                 null // Replace with actual email credentials if needed
        //                                 );
        //                         } catch (Exception e) {
        //                                 // Log the error or handle it accordingly
        //                                 e.printStackTrace();
        //                         }
        //                 }
        //         } catch (Exception e) {
        //                 e.printStackTrace();
        //         }
        // }

}
