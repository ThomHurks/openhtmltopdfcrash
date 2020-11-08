package com.example.openhtmltopdfcrash;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OpenHtmlToPdfCrash {

    @Test
    public void reproduce() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PdfRendererBuilder pdfBuilder = new PdfRendererBuilder();
        pdfBuilder.useFastMode();
        pdfBuilder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_2_A);
        pdfBuilder.useFont(getFontAsFSSupplier("NotoSans-Regular.ttf"),
                "Noto Sans", 400, BaseRendererBuilder.FontStyle.NORMAL, true);
        pdfBuilder.useFont(getFontAsFSSupplier("NotoSansSymbols-Regular.ttf"),
                "Noto Sans Symbols", 400, BaseRendererBuilder.FontStyle.NORMAL, true);
        final String html = new String(Files.readAllBytes(Paths.get(new ClassPathResource("crash.html").getURL().getPath())));
        pdfBuilder.withHtmlContent(html, null);
        pdfBuilder.toStream(outputStream);
        final NullPointerException ex = assertThrows(NullPointerException.class, pdfBuilder::run);
        assertEquals("org.apache.pdfbox.cos.COSArray.add(COSArray.java:62)", ex.getStackTrace()[0].toString());
    }

    private static FSSupplier<InputStream> getFontAsFSSupplier(final String fontFile) {
        return () -> {
            try {
                return new ClassPathResource(fontFile).getInputStream();
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }
}

