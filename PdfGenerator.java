package com.example.medilab.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.medilab.models.LabReport;
import com.example.medilab.models.Payment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Native Android PdfDocument generator for MEDILAB.
 * Generates high-quality PDF lab reports and payment invoices.
 */
public class PdfGenerator {

    private static final String TAG = "PdfGenerator";

    /**
     * Generates a PDF report file on device storage.
     */
    public static File generateLabReportPdf(Context context, LabReport report) {
        if (report == null) return null;

        int pageWidth = 595;
        int pageHeight = 842;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        int margin = 36;
        int usableWidth = pageWidth - (margin * 2);
        int currentY = margin + 10;

        // 1. HEADER BRANDING (MEDILAB)
        paint.setColor(Color.rgb(0, 102, 138)); // Medical Teal
        RectF headerBanner = new RectF(margin, currentY, margin + usableWidth, currentY + 65);
        canvas.drawRoundRect(headerBanner, 8, 8, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(22);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("MEDILAB CLINICAL DIAGNOSTICS", margin + 18, currentY + 32, paint);

        paint.setTextSize(10);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("NABL ACCREDITED PATHOLOGY & BIOCHEMICAL LABORATORY", margin + 18, currentY + 48, paint);

        currentY += 80;

        // 2. PATIENT & REPORT METADATA SECTION
        paint.setColor(Color.rgb(245, 247, 250));
        RectF metaBox = new RectF(margin, currentY, margin + usableWidth, currentY + 90);
        canvas.drawRoundRect(metaBox, 6, 6, paint);

        paint.setColor(Color.rgb(207, 216, 220));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawRoundRect(metaBox, 6, 6, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.rgb(30, 41, 59));
        paint.setTextSize(11);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        int col1X = margin + 16;
        int col2X = margin + 280;
        int metaLineY = currentY + 24;

        canvas.drawText("Patient ID: #" + report.getPatientId(), col1X, metaLineY, paint);
        canvas.drawText("Report ID: #" + report.getReportId(), col2X, metaLineY, paint);

        metaLineY += 20;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Patient Name: " + safe(report.getPatientName()), col1X, metaLineY, paint);
        canvas.drawText("Test Date: " + safe(report.getTestDate()), col2X, metaLineY, paint);

        metaLineY += 20;
        String genderAge = safe(report.getPatientGender()) + " / " + report.getPatientAge() + " Yrs";
        canvas.drawText("Gender / Age: " + genderAge, col1X, metaLineY, paint);
        canvas.drawText("Blood Group: " + safe(report.getPatientBloodGroup()), col2X, metaLineY, paint);

        currentY += 105;

        // 3. INVESTIGATION HEADER
        paint.setColor(Color.rgb(10, 37, 64)); // Navy Primary
        paint.setTextSize(14);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DIAGNOSTIC TEST FINDINGS", margin, currentY + 12, paint);

        currentY += 24;

        // Table Header
        paint.setColor(Color.rgb(238, 242, 246));
        RectF tableHeader = new RectF(margin, currentY, margin + usableWidth, currentY + 26);
        canvas.drawRect(tableHeader, paint);

        paint.setColor(Color.rgb(10, 37, 64));
        paint.setTextSize(10);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("TEST INVESTIGATION", margin + 10, currentY + 17, paint);
        canvas.drawText("CATEGORY", margin + 220, currentY + 17, paint);
        canvas.drawText("REFERENCE NORMAL RANGE", margin + 340, currentY + 17, paint);

        currentY += 26;

        // Table Row
        paint.setColor(Color.WHITE);
        RectF tableRow = new RectF(margin, currentY, margin + usableWidth, currentY + 36);
        canvas.drawRect(tableRow, paint);

        paint.setColor(Color.rgb(226, 232, 240));
        canvas.drawLine(margin, currentY + 36, margin + usableWidth, currentY + 36, paint);

        paint.setColor(Color.rgb(15, 23, 42));
        paint.setTextSize(11);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(safe(report.getTestName()), margin + 10, currentY + 22, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(safe(report.getTestCategory()), margin + 220, currentY + 22, paint);

        paint.setTextSize(9);
        canvas.drawText(safe(report.getNormalRange()), margin + 340, currentY + 22, paint);

        currentY += 50;

        // 4. OBSERVED RESULTS & VALUES
        paint.setColor(Color.rgb(10, 37, 64));
        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("OBSERVED FINDINGS & QUANTITATIVE VALUES:", margin, currentY, paint);

        currentY += 14;

        paint.setColor(Color.rgb(248, 250, 252));
        RectF resultBox = new RectF(margin, currentY, margin + usableWidth, currentY + 95);
        canvas.drawRoundRect(resultBox, 6, 6, paint);

        paint.setColor(Color.rgb(203, 213, 225));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(resultBox, 6, 6, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.rgb(15, 23, 42));
        paint.setTextSize(11);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        drawWrappedText(canvas, paint, safe(report.getResult()), margin + 14, currentY + 22, usableWidth - 28, 16);

        currentY += 115;

        // 5. DOCTOR / PATHOLOGIST REMARKS
        paint.setColor(Color.rgb(10, 37, 64));
        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DOCTOR / CLINICAL PATHOLOGIST REMARKS:", margin, currentY, paint);

        currentY += 14;

        paint.setColor(Color.rgb(248, 250, 252));
        RectF remarksBox = new RectF(margin, currentY, margin + usableWidth, currentY + 70);
        canvas.drawRoundRect(remarksBox, 6, 6, paint);

        paint.setColor(Color.rgb(203, 213, 225));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(remarksBox, 6, 6, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.rgb(71, 85, 105));
        paint.setTextSize(10);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        String remarks = report.getRemarks() != null && !report.getRemarks().isEmpty()
                ? report.getRemarks() : "Results correlated clinically. No critical variations observed.";
        drawWrappedText(canvas, paint, remarks, margin + 14, currentY + 20, usableWidth - 28, 15);

        // 6. FOOTER SIGNATURES & VERIFICATION STAMP
        int footerY = pageHeight - 90;
        paint.setColor(Color.rgb(203, 213, 225));
        canvas.drawLine(margin, footerY, margin + usableWidth, footerY, paint);

        paint.setColor(Color.rgb(100, 116, 139));
        paint.setTextSize(8);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Electronic Signature Verified • Authorized by Chief Medical Biochemist", margin, footerY + 16, paint);
        canvas.drawText("MEDILAB Healthcare & Diagnostics Ltd. • 24/7 Helpline: 1800-MEDILAB", margin, footerY + 30, paint);

        paint.setColor(Color.rgb(0, 102, 138));
        paint.setTextSize(9);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("VERIFIED BY MEDILAB", margin + usableWidth - 120, footerY + 20, paint);

        document.finishPage(page);

        // Save PDF to cache or external files directory
        File pdfDir = new File(context.getExternalFilesDir(null), "Reports");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        String fileName = "MEDILAB_Report_" + report.getReportId() + "_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(pdfDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            document.writeTo(fos);
            document.close();
            return pdfFile;
        } catch (IOException e) {
            Log.e(TAG, "Error saving PDF", e);
            document.close();
            return null;
        }
    }

    /**
     * Generates a formal PDF invoice for patient booking payments.
     */
    public static File generatePaymentInvoicePdf(Context context, Payment payment, String patientName, String testName, String appointmentDate) {
        if (payment == null) return null;

        int pageWidth = 595;
        int pageHeight = 842;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        int margin = 36;
        int usableWidth = pageWidth - (margin * 2);
        int currentY = margin + 10;

        // Header
        paint.setColor(Color.rgb(10, 37, 64)); // Navy
        RectF headerBanner = new RectF(margin, currentY, margin + usableWidth, currentY + 65);
        canvas.drawRoundRect(headerBanner, 8, 8, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("MEDILAB PAYMENT INVOICE", margin + 18, currentY + 32, paint);

        paint.setTextSize(10);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("TAX INVOICE / OFFICIAL PAYMENT RECEIPT", margin + 18, currentY + 48, paint);

        currentY += 80;

        // Invoice metadata
        paint.setColor(Color.rgb(245, 247, 250));
        RectF metaBox = new RectF(margin, currentY, margin + usableWidth, currentY + 80);
        canvas.drawRoundRect(metaBox, 6, 6, paint);

        paint.setColor(Color.rgb(30, 41, 59));
        paint.setTextSize(11);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Transaction ID: " + payment.getTransactionId(), margin + 16, currentY + 26, paint);
        canvas.drawText("Payment Date: " + payment.getPaymentDate(), margin + 280, currentY + 26, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Customer / Patient: " + safe(patientName), margin + 16, currentY + 48, paint);
        canvas.drawText("Payment Method: " + payment.getPaymentMethod(), margin + 280, currentY + 48, paint);

        currentY += 95;

        // Item Table
        paint.setColor(Color.rgb(238, 242, 246));
        RectF tableHeader = new RectF(margin, currentY, margin + usableWidth, currentY + 26);
        canvas.drawRect(tableHeader, paint);

        paint.setColor(Color.rgb(10, 37, 64));
        paint.setTextSize(10);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("ITEM DESCRIPTION", margin + 10, currentY + 17, paint);
        canvas.drawText("SCHEDULE DATE", margin + 300, currentY + 17, paint);
        canvas.drawText("AMOUNT (INR)", margin + 420, currentY + 17, paint);

        currentY += 26;

        paint.setColor(Color.WHITE);
        RectF tableRow = new RectF(margin, currentY, margin + usableWidth, currentY + 36);
        canvas.drawRect(tableRow, paint);

        paint.setColor(Color.rgb(15, 23, 42));
        paint.setTextSize(11);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(safe(testName), margin + 10, currentY + 22, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(safe(appointmentDate), margin + 300, currentY + 22, paint);
        canvas.drawText(String.format(Locale.getDefault(), "₹ %.2f", payment.getAmount()), margin + 420, currentY + 22, paint);

        currentY += 50;

        // Total
        paint.setColor(Color.rgb(0, 102, 138));
        paint.setTextSize(14);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Total Paid: " + String.format(Locale.getDefault(), "₹ %.2f", payment.getAmount()), margin + 340, currentY, paint);

        // Status Badge
        currentY += 30;
        paint.setColor(payment.getStatus().equalsIgnoreCase("Paid") ? Color.rgb(38, 166, 154) : Color.rgb(255, 152, 0));
        RectF badge = new RectF(margin, currentY, margin + 100, currentY + 24);
        canvas.drawRoundRect(badge, 4, 4, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(11);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(payment.getStatus().toUpperCase(Locale.getDefault()), margin + 25, currentY + 16, paint);

        document.finishPage(page);

        File pdfDir = new File(context.getExternalFilesDir(null), "Invoices");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        String fileName = "MEDILAB_Invoice_" + payment.getTransactionId() + ".pdf";
        File pdfFile = new File(pdfDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            document.writeTo(fos);
            document.close();
            return pdfFile;
        } catch (IOException e) {
            Log.e(TAG, "Error saving invoice PDF", e);
            document.close();
            return null;
        }
    }

    private static String safe(String str) {
        return str != null ? str : "";
    }

    private static int drawWrappedText(Canvas canvas, Paint paint, String text, int x, int startY, int maxWidth, int lineHeight) {
        int currentY = startY;
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (paint.measureText(testLine) < maxWidth) {
                currentLine.append(currentLine.length() == 0 ? "" : " ").append(word);
            } else {
                canvas.drawText(currentLine.toString(), x, currentY, paint);
                currentY += lineHeight;
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) {
            canvas.drawText(currentLine.toString(), x, currentY, paint);
            currentY += lineHeight;
        }
        return currentY;
    }

    public static void openPdfFile(Context context, File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String authority = context.getPackageName() + ".provider";
            Uri uri = FileProvider.getUriForFile(context, authority, pdfFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent chooser = Intent.createChooser(intent, "Open PDF Document");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
        } catch (Exception e) {
            Log.e(TAG, "Error opening PDF", e);
            Toast.makeText(context, "PDF saved: " + pdfFile.getName(), Toast.LENGTH_LONG).show();
        }
    }
}
