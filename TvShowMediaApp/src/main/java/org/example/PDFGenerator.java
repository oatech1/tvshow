package org.example;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.List;

public class PDFGenerator {

    public static void generatePDF(List<TVShow> tvShowList, String filePath) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph("TV Show List"));

            for (TVShow tvShow : tvShowList) {
                document.add(new Paragraph(tvShow.getTitle() + " (Season " + tvShow.getSeason() + "): " +
                        "Rating - " + tvShow.getRating() + ", Release Date - " + tvShow.getReleaseDate()));
            }

            System.out.println("PDF generated successfully!");
        } catch (DocumentException | java.io.IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
