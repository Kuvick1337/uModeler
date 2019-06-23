package at.jku.ce.umodeler.controller;

import at.jku.ce.umodeler.online.Constants;
import at.jku.ce.umodeler.online.Utils;
import at.jku.ce.umodeler.online.mxBase64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ControllerUtil {

    /**
     * Encodes the passed String as UTF-8 using an algorithm that's compatible
     * with JavaScript's <code>encodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s       The String to be encoded
     * @param charset the character set to base the encoding on
     * @return the encoded String
     */
    private static String encodeURIComponent(String s, String charset) {
        if (s == null) {
            return null;
        } else {
            String result;

            try {
                result = URLEncoder.encode(s, charset).replaceAll("\\+", "%20")
                        .replaceAll("\\%21", "!").replaceAll("\\%27", "'")
                        .replaceAll("\\%28", "(").replaceAll("\\%29", ")")
                        .replaceAll("\\%7E", "~");
            } catch (UnsupportedEncodingException e) {
                // This exception should never occur
                result = s;
            }

            return result;
        }
    }

    /**
     * validates the name of the given data by giving a default name if not present and performing URL-decoding
     *
     * @param filename
     * @return
     */
    private static String validateFilename(String filename) {
        // Only limited characters allowed
        try {
            filename = URLDecoder.decode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore unsupported encoding
        }

        filename = filename.replaceAll("[\\/:;*?\"<>|]", "");

        if (filename.length() == 0) {
            filename = "export.xml";
        } else if (!filename.toLowerCase().endsWith(".svg") &&
                !filename.toLowerCase().endsWith(".html") &&
                !filename.toLowerCase().endsWith(".xml") &&
                !filename.toLowerCase().endsWith(".png") &&
                !filename.toLowerCase().endsWith(".jpg") &&
                !filename.toLowerCase().endsWith(".pdf") &&
                !filename.toLowerCase().endsWith(".vsdx") &&
                !filename.toLowerCase().endsWith(".txt")) {
            filename = filename + ".xml";
        }

        filename = encodeURIComponent(filename, "UTF-8");

        return filename;
    }

    /**
     * handles the request on save/export to create a response containing the data requested by the user
     * e.g. graph export as XML or SVG
     * Taken from the draw.io backend.
     *
     * @param request
     * @param response
     * @return the xml
     * @throws IOException
     */
    public static String handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getContentLength() < Constants.MAX_REQUEST_SIZE) {
            String mime = request.getParameter("mime");
            String filename = request.getParameter("filename");
            byte[] data = null;

            // Data in data param is base64 encoded and deflated
            String enc = request.getParameter("data");
            String xml = null;

            try {
                if (enc != null && enc.length() > 0) {
                    // NOTE: Simulate is used on client-side so the value is double-encoded
                    xml = Utils.inflate(mxBase64.decode(URLDecoder
                            .decode(enc, Utils.CHARSET_FOR_URL_ENCODING)
                            .getBytes()));
                } else {
                    xml = request.getParameter("xml");
                }

                // Decoding is optional (no plain text values allowed here so %3C means encoded)
                if (xml != null && xml.startsWith("%3C")) {
                    xml = URLDecoder.decode(xml,
                            Utils.CHARSET_FOR_URL_ENCODING);
                }

                String binary = request.getParameter("binary");

                if (binary != null && binary.equals("1") && xml != null
                        && (mime != null || filename != null)) {
                    response.setStatus(HttpServletResponse.SC_OK);

                    if (filename != null) {
                        filename = validateFilename(filename);

                        response.setContentType("application/x-unknown");
                        response.setHeader("Content-Disposition",
                                "attachment; filename=\"" + filename
                                        + "\"; filename*=UTF-8''" + filename);
                    } else if (mime != null) {
                        response.setContentType(mime);
                    }

                    response.getOutputStream()
                            .write(mxBase64.decodeFast(URLDecoder.decode(xml,
                                    Utils.CHARSET_FOR_URL_ENCODING)));
                } else if (xml != null) {
                    data = xml.getBytes(Utils.CHARSET_FOR_URL_ENCODING);

                    String format = request.getParameter("format");

                    if (format == null) {
                        format = "xml";
                    }

                    if (filename != null && filename.length() > 0
                            && !filename.toLowerCase().endsWith(".svg")
                            && !filename.toLowerCase().endsWith(".html")
                            && !filename.toLowerCase().endsWith(".png")
                            && !filename.toLowerCase().endsWith("." + format)) {
                        filename += "." + format;
                    }

                    response.setStatus(HttpServletResponse.SC_OK);

                    if (filename != null) {
                        filename = validateFilename(filename);

                        if (mime != null) {
                            response.setContentType(mime);
                        } else {
                            response.setContentType("application/x-unknown");
                        }

                        response.setHeader("Content-Disposition",
                                "attachment; filename=\"" + filename
                                        + "\"; filename*=UTF-8''" + filename);
                    } else if (mime.equals("image/svg+xml")) {
                        response.setContentType("image/svg+xml");
                    } else {
                        // Required to avoid download of file
                        response.setContentType("text/plain");
                    }

                    OutputStream out = response.getOutputStream();
                    out.write(data);
                    out.close();
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }

            } catch (OutOfMemoryError e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return xml;
        } else {
            response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
        }
        return null;
    }
}
