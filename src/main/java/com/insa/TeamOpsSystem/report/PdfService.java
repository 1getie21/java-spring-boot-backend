package com.insa.TeamOpsSystem.report;

import com.insa.TeamOpsSystem.CheckList.CheckList;
import com.insa.TeamOpsSystem.CheckList.CheckListRepository;
import com.insa.TeamOpsSystem.FTraffic.FTrafficRepository;
import com.insa.TeamOpsSystem.FTraffic.Ftraffics;
import com.insa.TeamOpsSystem.exceptions.AlreadyExistException;
import com.insa.TeamOpsSystem.failedTraffics.FailedTrafficRepository;
import com.insa.TeamOpsSystem.failedTraffics.FailedTraffics;
import com.insa.TeamOpsSystem.request.Request;
import com.insa.TeamOpsSystem.request.RequestRepository;
import com.insa.TeamOpsSystem.sixmonthchekelist.SixMCList;
import com.insa.TeamOpsSystem.sixmonthchekelist.SixMCListRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor

public class PdfService {
    private final FTrafficRepository trafficRepository;
    private final RequestRepository requestRepository;
    private final CheckListRepository checkListRepository;
    private final FailedTrafficRepository failedTrafficRepository;
    private final SixMCListRepository sixMCListRepository;

    public ByteArrayInputStream generatePdf(String userName) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img.png";
            Paragraph date = new Paragraph("Date: All")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1);
            Image img = new Image(ImageDataFactory.create(imagePath));
            img.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img);
            document.add(date);
            Table table = new Table(new float[]{1, 3, 3, 3, 3, 3, 3});
            table.setWidth(UnitValue.createPercentValue(100));
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);

            table.addCell("No");
            table.addCell("Site");
            table.addCell("Time");
            table.addCell("Total Time Traffic");
            table.addCell("CreatedAt");
            table.addCell("CreatedBy");
            table.addCell("Remark");

            // Fetch all traffic data
//            List<Ftraffics> ftraffics = trafficRepository.findAllBySitesDeletedIsFalseAndCreatedBy(userName);
//            int index = 1;

            List<Ftraffics> ftraffics;
            if (userName.equals("ROLE_ADMIN")) {
                ftraffics = trafficRepository.findAllBySitesDeletedIsFalse();
            } else {
                ftraffics = trafficRepository.findAllBySitesDeletedIsFalseAndCreatedBy(userName);
            }
            int index = 1;
            // Add rows
            for (Ftraffics traffics : ftraffics) {
                table.addCell(String.valueOf(index++));
                table.addCell(traffics.getSites() != null ? traffics.getSites().getName() : "");
                table.addCell(traffics.getTrafficTimeName() != null ? traffics.getTrafficTimeName() : "");
                table.addCell(traffics.getTimeValues() != null ? traffics.getTimeValues() : "");
                table.addCell(traffics.getCreatedAt() != null ? traffics.getCreatedAt().toString() : "");;
                table.addCell(traffics.getCreatedBy() != null ? traffics.getCreatedBy() : "");
                table.addCell(traffics.getRemark() != null ? traffics.getRemark() : "");
            }

            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(table);
            document.add(tableDiv);

            // Close document
            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    public ByteArrayInputStream generatePdfByDateRange(LocalDate from, LocalDate to,    String userName) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img.png";

            Image img = new Image(ImageDataFactory.create(imagePath));
            img.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img);

            // Add date (for specified date range)
            Paragraph date = new Paragraph("Date: " + from.toString() + " - " + to.toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1); // This will add an underline with a thickness of 1.5 points

            document.add(date);

            // Add table for traffic data
            Table trafficTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3});
            trafficTable.setWidth(UnitValue.createPercentValue(100));
            trafficTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            trafficTable.addCell("No");
            trafficTable.addCell("Site");
            trafficTable.addCell("Time");
            trafficTable.addCell("Total Time Traffic");
            trafficTable.addCell("CreatedAt");
            trafficTable.addCell("CreatedBy");
            trafficTable.addCell("Remark");

            // Fetch traffic data within specified date range
//            List<Ftraffics> ftraffics = trafficRepository.findAllByCreatedAtBetweenAndSitesDeletedIsFalseAndCreatedBy(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), token);
//            int index = 1;  // Index counter

            List<Ftraffics> ftraffics;
            if (userName.equals("ROLE_ADMIN")) {
                ftraffics = trafficRepository.findAllByCreatedAtBetweenAndSitesDeletedIsFalse(
                        from.atStartOfDay(), to.plusDays(1).atStartOfDay());
            } else {
                ftraffics = trafficRepository.findAllByCreatedAtBetweenAndSitesDeletedIsFalseAndCreatedBy(
                        from.atStartOfDay(), to.plusDays(1).atStartOfDay(), userName);
            }
            int index = 1;  // Index counter
            // Add rows
            for (Ftraffics traffics : ftraffics) {
                trafficTable.addCell(String.valueOf(index++));
                trafficTable.addCell(traffics.getSites() != null ? traffics.getSites().getName() : "");
                trafficTable.addCell(traffics.getTrafficTimeName() != null ? traffics.getTrafficTimeName() : "");
                trafficTable.addCell(traffics.getTimeValues() != null ? traffics.getTimeValues() : "");
                trafficTable.addCell(traffics.getCreatedAt() != null ? traffics.getCreatedAt().toString() : "");;
                trafficTable.addCell(traffics.getCreatedBy() != null ? traffics.getCreatedBy() : "");
                trafficTable.addCell(traffics.getRemark() != null ? traffics.getRemark() : "");
            }
            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(trafficTable);
            document.add(tableDiv);

            // Close document
            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }
    public ByteArrayInputStream generatePdfByTrafficTimeName(String trafficTimeName, String userName) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img.png";

            Image img = new Image(ImageDataFactory.create(imagePath));
            img.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img);

            Paragraph date = new Paragraph(trafficTimeName + " Reports")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1);
            document.add(date);

            // Add table for traffic data
            Table trafficTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3});
            trafficTable.setWidth(UnitValue.createPercentValue(100));
            trafficTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            trafficTable.addCell("No");
            trafficTable.addCell("Site");
            trafficTable.addCell("Time");
            trafficTable.addCell("Total Time Traffic");
            trafficTable.addCell("CreatedAt");
            trafficTable.addCell("CreatedBy");
            trafficTable.addCell("Remark");

            //List<Ftraffics> ftraffics = trafficRepository.findAllByTrafficTimeNameAndCreatedByAndSitesDeletedIsFalse(trafficTimeName, userName);

            List<Ftraffics> ftraffics;
            if (userName.equals("ROLE_ADMIN")) {
                //please use this condition
                ftraffics = trafficRepository.findAllBySitesDeletedIsFalse();
            } else {
                ftraffics = trafficRepository.findAllByTrafficTimeNameAndCreatedByAndSitesDeletedIsFalse(trafficTimeName, userName);;

            }
            int index = 1;  // Index counter

            // Add rows
            for (Ftraffics traffics : ftraffics) {
                trafficTable.addCell(String.valueOf(index++));
                trafficTable.addCell(traffics.getSites() != null ? traffics.getSites().getName() : "");
                trafficTable.addCell(traffics.getTrafficTimeName() != null ? traffics.getTrafficTimeName() : "");
                trafficTable.addCell(traffics.getTimeValues() != null ? traffics.getTimeValues() : "");
                trafficTable.addCell(traffics.getCreatedAt() != null ? traffics.getCreatedAt().toString() : "");;
                trafficTable.addCell(traffics.getCreatedBy() != null ? traffics.getCreatedBy() : "");
                trafficTable.addCell(traffics.getRemark() != null ? traffics.getRemark() : "");
            }
            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(trafficTable);
            document.add(tableDiv);

            // Close document
            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    public ByteArrayInputStream generatePdfReportByRequesters(String createdBy) {
        try{  ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Initialize PDF writer
        PdfWriter writer = new PdfWriter(out);

        // Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdf);
            document.setMargins(20, 20, 20, 20);
        // Add image
        String imagePath = "\\\\10.10.10.204\\home\\img3.png";
        Image img3 = new Image(ImageDataFactory.create(imagePath));
        img3.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(img3);
            // Add date (initially for "Date: All")
            Paragraph title = new Paragraph("All Traffic Control Service Request Reports")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1); // This will add an underline with a thickness of 1.5 points

            document.add(title);

            // Add table for traffic data
            Table requestTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3, 3, 3});
            requestTable.setWidth(UnitValue.createPercentValue(100));
            requestTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            requestTable.addCell("No");
           // requestTable.addCell("Created At");
            requestTable.addCell("Created By");
            requestTable.addCell("Phone");
            requestTable.addCell("Email");
            requestTable.addCell("Requester");
            requestTable.addCell("Organization");
            requestTable.addCell("Categories");
            requestTable.addCell("Contact");
            requestTable.addCell("Description");
           // requestTable.addCell("Priority");
//            List<Request> requestIterable = requestRepository.findAllByCreatedBy(createdBy);
//            // Add rows

            List<Request> requestss;
            if (createdBy.equals("ROLE_ADMIN")) {
                requestss = requestRepository.findAll();
            }
            else {
                requestss = requestRepository.findAllByCreatedBy(createdBy);
            }

            int index = 1;

            for (Request request : requestss) {
                requestTable.addCell(String.valueOf(index++));
               // requestTable.addCell(request.getCreatedAt() != null ? request.getCreatedAt().toString() : "");
                requestTable.addCell(request.getCreatedBy() != null ? request.getCreatedBy() : "");
                requestTable.addCell(request.getPhone() != null ? request.getPhone() : "");
                requestTable.addCell(request.getEmail() != null ? request.getEmail() : "");
                requestTable.addCell(request.getRequester() != null ? request.getRequester() : "");
                requestTable.addCell(request.getOrganization() != null ? request.getOrganization() : "");
                requestTable.addCell(request.getCategories() != null ? request.getCategories() : "");
                requestTable.addCell(request.getContact() != null ? request.getContact() : "");
                requestTable.addCell(request.getDescription() != null ? request.getDescription() : "");
               // requestTable.addCell(request.getPriority() != null ? request.getPriority() : "");
            }

            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(requestTable);
            document.add(tableDiv);

            // Close document
            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    public ByteArrayInputStream generatePdfRequestsByDateRange(LocalDate from, LocalDate to, String username) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img3.png";
            Image img3 = new Image(ImageDataFactory.create(imagePath));
            img3.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img3);
            // Add date (for specified date range)
            Paragraph date = new Paragraph("Date: " + from.toString() + " - " + to.toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1); // This will add an underline with a thickness of 1.5 points

            document.add(date);
            // Add table for requests data
            Table requestTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3, 3, 3});
            requestTable.setWidth(UnitValue.createPercentValue(100));
            requestTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            requestTable.addCell("No");
            requestTable.addCell("Created By");
            requestTable.addCell("Phone");
            requestTable.addCell("Email");
            requestTable.addCell("Requester");
            requestTable.addCell("Organization");
            requestTable.addCell("Categories");
            requestTable.addCell("Contact");
            requestTable.addCell("Description");

//            List<Request> requests = requestRepository.findAllByCreatedAtBetweenAndCreatedBy(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), username);
//            int index = 1; // Reset index counter

            List<Request> requests;
            if (username.equals("ROLE_ADMIN")) {

                requests = requestRepository.findAllByCreatedAtBetween(from.atStartOfDay(),
                        to.plusDays(1).atStartOfDay());
            } else {
                requests = requestRepository.findAllByCreatedAtBetweenAndCreatedBy(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), username);
            }
            int index = 1; // Reset index counter

// Add request data rows
            for (Request request : requests) {
                requestTable.addCell(String.valueOf(index++));
                requestTable.addCell(request.getCreatedBy() != null ? request.getCreatedBy() : "");
                requestTable.addCell(request.getPhone() != null ? request.getPhone() : "");
                requestTable.addCell(request.getEmail() != null ? request.getEmail() : "");
                requestTable.addCell(request.getRequester() != null ? request.getRequester() : "");
                requestTable.addCell(request.getOrganization() != null ? request.getOrganization() : "");
                requestTable.addCell(request.getCategories() != null ? request.getCategories() : "");
                requestTable.addCell(request.getContact() != null ? request.getContact() : "");
                requestTable.addCell(request.getDescription() != null ? request.getDescription() : "");
            }


            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(requestTable);
            document.add(tableDiv);

            // Close document


            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    //Check List
    public ByteArrayInputStream generatePdfByCheckLists(String createdBy) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img1.png";
            Image img1 = new Image(ImageDataFactory.create(imagePath));
            img1.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img1);

            // Add title for the report
            Paragraph title = new Paragraph("All Traffic Processing Capacity Reports")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1)
                    .setFontSize(16);
            document.add(title);

            // Add table headers for checklist reports
            Table checklistTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3, 3});
            checklistTable.setWidth(UnitValue.createPercentValue(100));
            checklistTable.setHorizontalAlignment(HorizontalAlignment.CENTER);


            checklistTable.addCell("No");
            checklistTable.addCell("Sites");
            checklistTable.addCell("Link Type");
            checklistTable.addCell("Download");
            checklistTable.addCell("Upload");
            checklistTable.addCell("Created By");
            checklistTable.addCell("Nbp Total");
            checklistTable.addCell("Avg NBP");

            //List<CheckList> checkLists = checkListRepository.findAllByCreatedByAndSitesDeletedIsFalse(createdBy);

            List<CheckList> checkLists;
            if (createdBy.equals("ROLE_ADMIN")) {
                checkLists = checkListRepository.findAllBySitesDeletedIsFalse();
            } else {
                checkLists = checkListRepository.findAllByCreatedByAndSitesDeletedIsFalse(createdBy);
            }

            int index = 1;
            for (CheckList checklist : checkLists) {
                checklistTable.addCell(String.valueOf(index++));
                checklistTable.addCell(checklist.getSites() != null ? checklist.getSites().getName() : "");
                checklistTable.addCell(checklist.getLinkType() != null ? checklist.getLinkType() : "");
                checklistTable.addCell(checklist.getDownload() != null ? checklist.getDownload() : "");
                checklistTable.addCell(checklist.getUpload() != null ? checklist.getUpload() : "");
                checklistTable.addCell(checklist.getCreatedBy() != null ? checklist.getCreatedBy() : "");
                checklistTable.addCell(String.valueOf(checklist.getNbpTotal()));
                checklistTable.addCell(checklist.getAvgNBP() != null ? checklist.getAvgNBP() : "");
            }
            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(checklistTable);
            document.add(tableDiv);

            // Close the document
            document.close();

            // Return the PDF as a ByteArrayInputStream
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    public ByteArrayInputStream generatePdfChecklistByDateRange(LocalDate from, LocalDate to, String username) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img1.png";

            Image img1 = new Image(ImageDataFactory.create(imagePath));
            img1.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img1);

            // Add date (for specified date range)
            Paragraph date = new Paragraph("Date: " + from.toString() + " - " + to.toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1); // This will add an underline with a thickness of 1.5 points
            document.add(date);

            // Add table for checklist data
            Table checklistTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3, 3});
            checklistTable.setWidth(UnitValue.createPercentValue(100));
            checklistTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            checklistTable.addCell("No");
            checklistTable.addCell("Sites");
            checklistTable.addCell("Link Type");
            checklistTable.addCell("Download");
            checklistTable.addCell("Upload");
            checklistTable.addCell("Created By");
            checklistTable.addCell("Nbp Total");
            checklistTable.addCell("Avg NBP");

            // Fetch checklist data within specified date range
           // List<CheckList> checkLists = checkListRepository.findAllByCreatedAtBetweenAndCreatedByAndSitesDeletedIsFalse(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), username);

            List<CheckList> checkLists;
            if (username.equals("ROLE_ADMIN")) {
                //please use this condition
                checkLists = checkListRepository.findAllByCreatedAtBetween(from.atStartOfDay(),
                        to.plusDays(1).atStartOfDay());
            } else {
                checkLists = checkListRepository.findAllByCreatedAtBetweenAndCreatedByAndSitesDeletedIsFalse(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), username);

            }

            // Add rows to the table
            int index = 1;
            for (CheckList checklist : checkLists) {
                checklistTable.addCell(String.valueOf(index++));
                checklistTable.addCell(checklist.getSites() != null ? checklist.getSites().getName() : "");
                checklistTable.addCell(checklist.getLinkType() != null ? checklist.getLinkType() : "");
                checklistTable.addCell(checklist.getDownload() != null ? checklist.getDownload() : "");
                checklistTable.addCell(checklist.getUpload() != null ? checklist.getUpload() : "");
                checklistTable.addCell(checklist.getCreatedBy() != null ? checklist.getCreatedBy() : "");
                checklistTable.addCell(String.valueOf(checklist.getNbpTotal()));
                checklistTable.addCell(checklist.getAvgNBP() != null ? checklist.getAvgNBP() : "");
            }

            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(checklistTable);
            document.add(tableDiv);

            // Close document

            document.close();
            // Return the PDF as a ByteArrayInputStream
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    //failed traffic
    public ByteArrayInputStream generatePdfByFailedTraffics(String createdBy) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img.png";

            Image img = new Image(ImageDataFactory.create(imagePath));
            img.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img);

            // Add title for the report
            Paragraph title = new Paragraph("Failed Traffic Reports")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1)
                    .setFontSize(16);
            document.add(title);

            // Add table headers for failed traffic reports
            Table failedTrafficTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3, 3, 3, 3});
            failedTrafficTable.setWidth(UnitValue.createPercentValue(100));
            failedTrafficTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            failedTrafficTable.addCell("No");
            failedTrafficTable.addCell("Sites");
            failedTrafficTable.addCell("Failed Link Type");
            failedTrafficTable.addCell("Created At");
            failedTrafficTable.addCell("Created By");
            failedTrafficTable.addCell("Reported To");
            failedTrafficTable.addCell("Fixed At");
            failedTrafficTable.addCell("Failed At");
            failedTrafficTable.addCell("Failure Length");
            failedTrafficTable.addCell("Failed Reason");

           // List<FailedTraffics> failedTraffics = failedTrafficRepository.findAllByCreatedByAndSitesDeletedIsFalse(createdBy);

            List<FailedTraffics> failedTraffics;

            if (createdBy.equals("ROLE_ADMIN")) {
                failedTraffics = failedTrafficRepository.findAllBySitesDeletedIsFalse();
            } else {
                failedTraffics = failedTrafficRepository.findAllByCreatedByAndSitesDeletedIsFalse(createdBy);
            }
            int index = 1;
            for (FailedTraffics failedTraffic : failedTraffics) {
                failedTrafficTable.addCell(String.valueOf(index++));
                failedTrafficTable.addCell(failedTraffic.getSites() != null ? failedTraffic.getSites().getName() : "");
                failedTrafficTable.addCell(failedTraffic.getFailedLinkType() != null ? failedTraffic.getFailedLinkType() : "");
                failedTrafficTable.addCell(failedTraffic.getCreatedAt() != null ? failedTraffic.getCreatedAt().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getCreatedBy() != null ? failedTraffic.getCreatedBy() : "");
                failedTrafficTable.addCell(failedTraffic.getReportedTo() != null ? failedTraffic.getReportedTo().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getFixedAt() != null ? failedTraffic.getFixedAt().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getDisConnectedAt() != null ? failedTraffic.getDisConnectedAt().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getFailureLength() != null ? failedTraffic.getFailureLength().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getFailedReason() != null ? failedTraffic.getFailedReason() : "");
            }

            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(failedTrafficTable);
            document.add(tableDiv);

            // Close the document
            document.close();

            // Return the PDF as a ByteArrayInputStream
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    //Date Range
    public ByteArrayInputStream generatePdfFailedTrafficByDateRange(LocalDate from, LocalDate to, String username) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img.png";

            Image img = new Image(ImageDataFactory.create(imagePath));
            img.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img);

            // Add title for the report
            Paragraph date = new Paragraph("Date: " + from.toString() + " - " + to.toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1); // This will add an underline with a thickness of 1.5 points
            document.add(date);

            // Add table headers for failed traffic reports
            Table failedTrafficTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3, 3, 3, 3});
            failedTrafficTable.setWidth(UnitValue.createPercentValue(100));
            failedTrafficTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            //table.addCell("No");
            failedTrafficTable.addCell("No");
            failedTrafficTable.addCell("Sites");
            failedTrafficTable.addCell("Failed Link Type");
            failedTrafficTable.addCell("Created At");
            failedTrafficTable.addCell("Created By");
            failedTrafficTable.addCell("Reported To");
            failedTrafficTable.addCell("Fixed At");
            failedTrafficTable.addCell("Failed At");
            failedTrafficTable.addCell("Failure Length");
            failedTrafficTable.addCell("Failed Reason");

            //List<FailedTraffics> failedTraffics = failedTrafficRepository.findAllByCreatedAtBetweenAndCreatedByAndSitesDeletedIsFalse(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), username);

            List<FailedTraffics> failedTraffics;
            if (username.equals("ROLE_ADMIN")) {
                //please use this condition
                failedTraffics = (List<FailedTraffics>) failedTrafficRepository.findAllByCreatedAtBetween(from.atStartOfDay(),
                        to.plusDays(1).atStartOfDay());
            } else {
                failedTraffics = failedTrafficRepository.findAllByCreatedAtBetweenAndCreatedByAndSitesDeletedIsFalse(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), username);

            }
            int index = 1;
            for (FailedTraffics failedTraffic : failedTraffics) {
                failedTrafficTable.addCell(String.valueOf(index++));
                failedTrafficTable.addCell(failedTraffic.getSites() != null ? failedTraffic.getSites().getName() : "");
                failedTrafficTable.addCell(failedTraffic.getFailedLinkType() != null ? failedTraffic.getFailedLinkType() : "");
                failedTrafficTable.addCell(failedTraffic.getCreatedAt() != null ? failedTraffic.getCreatedAt().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getCreatedBy() != null ? failedTraffic.getCreatedBy() : "");
                failedTrafficTable.addCell(failedTraffic.getReportedTo() != null ? failedTraffic.getReportedTo().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getFixedAt() != null ? failedTraffic.getFixedAt().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getDisConnectedAt() != null ? failedTraffic.getDisConnectedAt().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getFailureLength() != null ? failedTraffic.getFailureLength().toString() : "");
                failedTrafficTable.addCell(failedTraffic.getFailedReason() != null ? failedTraffic.getFailedReason() : "");
            }

            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(failedTrafficTable);
            document.add(tableDiv);

            // Close the document
            document.close();

            // Return the PDF as a ByteArrayInputStream
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    // SixMCList
    public ByteArrayInputStream generatePdfBySixCList(String createdBy) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img2.png";
            //System.out.println("Image Path: " + imagePath);
            Image img2 = new Image(ImageDataFactory.create(imagePath));
            img2.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img2);

            // Add title for the report
            Paragraph title = new Paragraph("All Six Month Security System Managment Reports")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1)
                    .setFontSize(16);
            document.add(title);


            // Add table headers for Six M List
            Table sixMListTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3}); // Update column widths as needed
            sixMListTable.setWidth(UnitValue.createPercentValue(100));
            sixMListTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            sixMListTable.addCell("No");
            sixMListTable.addCell("Sites");
            sixMListTable.addCell("Data Center");
            sixMListTable.addCell("Fiber");
            sixMListTable.addCell("Rack");
            sixMListTable.addCell("OPD");
            sixMListTable.addCell("Switch");
            sixMListTable.addCell("T9140");
            sixMListTable.addCell("Server");
            sixMListTable.addCell("Routine");
            sixMListTable.addCell("CreatedAt");
            sixMListTable.addCell("CreatedBy");

            //List<SixMCList> sixMList = sixMCListRepository.findAllByCreatedByAndSitesDeletedIsFalse(createdBy);

            List<SixMCList> sixMList;
            if (createdBy.equals("ROLE_ADMIN")) {
                sixMList = sixMCListRepository.findAllBySitesDeletedIsFalse();
            } else {
                sixMList = sixMCListRepository.findAllByCreatedByAndSitesDeletedIsFalse(createdBy);
            }
            int index = 1;
            for (SixMCList item : sixMList) {
                sixMListTable.addCell(String.valueOf(index++));
                sixMListTable.addCell(item.getSites() != null ? item.getSites().getName() : "");
                sixMListTable.addCell(item.getDatacenter() != null ? item.getDatacenter() : "");
                sixMListTable.addCell(item.getFiber() != null ? item.getFiber() : "");
                sixMListTable.addCell(item.getRack() != null ? item.getRack() : "");
                sixMListTable.addCell(item.getOpd() != null ? item.getOpd() : "");
                sixMListTable.addCell(item.getSwitch() != null ? item.getSwitch() : "");
                sixMListTable.addCell(item.getT9140() != null ? item.getT9140() : "");
                sixMListTable.addCell(item.getServer() != null ? item.getServer() : "");
                sixMListTable.addCell(item.getRoutine() != null ? item.getRoutine() : "");
                sixMListTable.addCell(item.getCreatedAt() != null ? item.getCreatedAt().toString() : "");;
                sixMListTable.addCell(item.getCreatedBy() != null ? item.getCreatedBy() : "");

            }

            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(sixMListTable);
            document.add(tableDiv);

            // Close the document
            document.close();

            // Return the PDF as a ByteArrayInputStream
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    // Date Range
    public ByteArrayInputStream generatePdfSixCListByByDateRange(LocalDate from, LocalDate to, String username) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            // Add image
            String imagePath = "\\\\10.10.10.204\\home\\img2.png";

            Image img2 = new Image(ImageDataFactory.create(imagePath));
            img2.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img2);

            // Add title for the report
            Paragraph date = new Paragraph("Date: " + from.toString() + " - " + to.toString())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline(1.5f, -1)
                    .setFontSize(16);
            document.add(date);

            // Add table headers for Six M List
            Table sixMListTable = new Table(new float[]{1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3}); // Update column widths as needed
            sixMListTable.setWidth(UnitValue.createPercentValue(100));
            sixMListTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            sixMListTable.addCell("No");
            sixMListTable.addCell("Sites");
            sixMListTable.addCell("Data Center");
            sixMListTable.addCell("Fiber");
            sixMListTable.addCell("Rack");
            sixMListTable.addCell("OPD");
            sixMListTable.addCell("Switch");
            sixMListTable.addCell("T9140");
            sixMListTable.addCell("Server");
            sixMListTable.addCell("Routine");
            sixMListTable.addCell("CreatedAt");
            sixMListTable.addCell("CreatedBy");


            //List<SixMCList> sixMList = sixMCListRepository.findAllByCreatedAtBetweenAndCreatedByAndSitesDeletedIsFalse(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), username);

            List<SixMCList> sixMList;
            if (username.equals("ROLE_ADMIN")) {

                sixMList = sixMCListRepository.findAllByCreatedAtBetween(from.atStartOfDay(),
                        to.plusDays(1).atStartOfDay());
            } else {
                sixMList = sixMCListRepository.findAllByCreatedAtBetweenAndCreatedByAndSitesDeletedIsFalse(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), username);

            }
            int index = 1;
            for (SixMCList item : sixMList) {
                sixMListTable.addCell(String.valueOf(index++));
                sixMListTable.addCell(item.getSites() != null ? item.getSites().getName() : "");
                sixMListTable.addCell(item.getDatacenter() != null ? item.getDatacenter() : "");
                sixMListTable.addCell(item.getFiber() != null ? item.getFiber() : "");
                sixMListTable.addCell(item.getRack() != null ? item.getRack() : "");
                sixMListTable.addCell(item.getOpd() != null ? item.getOpd() : "");
                sixMListTable.addCell(item.getSwitch() != null ? item.getSwitch() : "");
                sixMListTable.addCell(item.getT9140() != null ? item.getT9140() : "");
                sixMListTable.addCell(item.getServer() != null ? item.getServer() : "");
                sixMListTable.addCell(item.getRoutine() != null ? item.getRoutine() : "");
                sixMListTable.addCell(item.getCreatedAt() != null ? item.getCreatedAt().toString() : "");;
                sixMListTable.addCell(item.getCreatedBy() != null ? item.getCreatedBy() : "");
            }

            // Add table to document
            Div tableDiv = new Div();
            tableDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableDiv.add(sixMListTable);
            document.add(tableDiv);

            // Close the document
            document.close();

            // Return the PDF as a ByteArrayInputStream
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }
}


