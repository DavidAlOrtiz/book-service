package com.dva.bookstore.book_service.validator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.dva.bookstore.book_service.exception.InvalidFileException;

@Component
public class PdfValidator {

    public static final int MAX_BYTES = 10 * 1024 * 1024; // 10 MB del PDF original
    public static final int MAX_BASE64_CHARS = ((MAX_BYTES + 2) / 3) * 4; // 13,981,016
    private static final byte[] PDF_MAGIC = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    public void validate(String base64) {
        if (base64.length() > MAX_BASE64_CHARS) {
            throw new InvalidFileException("PDF must not exceed 10 MB");
        }

        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            throw new InvalidFileException("pdfBase64 is not valid Base64 (send it without the 'data:' prefix)");
        }

        if (bytes.length > MAX_BYTES) {
            throw new InvalidFileException("PDF must not exceed 10 MB");
        }
        if (bytes.length < PDF_MAGIC.length || !startsWithPdfMagic(bytes)) {
            throw new InvalidFileException("File is not a valid PDF");
        }
    }

    private boolean startsWithPdfMagic(byte[] bytes) {
        for (int i = 0; i < PDF_MAGIC.length; i++) {
            if (bytes[i] != PDF_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }
}