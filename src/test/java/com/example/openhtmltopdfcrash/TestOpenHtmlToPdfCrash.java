package com.example.openhtmltopdfcrash;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestOpenHtmlToPdfCrash {

    @Test
    public void reproduce(final @TempDir Path directory) throws IOException {
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
        pdfBuilder.run();
        try (final OutputStream file = new FileOutputStream(directory.resolve("test.pdf").toFile())) {
            outputStream.writeTo(file);
        }
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

