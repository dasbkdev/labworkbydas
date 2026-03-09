package server;

import controller.CandidateController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    public void handle() throws Exception {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                OutputStream out = socket.getOutputStream()
        ) {
            String requestLine = in.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
            }

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String fullPath = parts[1];

            String path = fullPath;
            String queryString = "";

            int queryIndex = fullPath.indexOf('?');
            if (queryIndex >= 0) {
                path = fullPath.substring(0, queryIndex);
                queryString = fullPath.substring(queryIndex + 1);
            }

            Map<String, String> queryParams = parseParams(queryString);

            CandidateController candidateController = new CandidateController();

            if ("GET".equalsIgnoreCase(method) && (path.equals("/") || path.equals("/candidates"))) {
                candidateController.showCandidates(out);
                return;
            }

            if ("GET".equalsIgnoreCase(method) && path.equals("/votes")) {
                candidateController.showVotesStub(out);
                return;
            }

            if ("GET".equalsIgnoreCase(method) && path.equals("/thankyou")) {
                candidateController.showThankYouStub(out);
                return;
            }

            if ("GET".equalsIgnoreCase(method) && path.equals("/css/candidates.css")) {
                candidateController.sendCss(out);
                return;
            }

            if ("GET".equalsIgnoreCase(method) && path.startsWith("/images/")) {
                String fileName = path.substring("/images/".length());
                candidateController.sendImage(out, fileName);
                return;
            }

            send404(out);
        } finally {
            socket.close();
        }
    }

    private Map<String, String> parseParams(String source) {
        Map<String, String> params = new HashMap<>();

        if (source == null || source.isBlank()) {
            return params;
        }

        String[] pairs = source.split("&");

        for (String pair : pairs) {
            if (pair.isBlank()) {
                continue;
            }

            String[] kv = pair.split("=", 2);
            String key = decode(kv[0]);
            String value = kv.length > 1 ? decode(kv[1]) : "";
            params.put(key, value);
        }

        return params;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private void send404(OutputStream out) throws Exception {
        String body = """
                <html>
                <head><meta charset="UTF-8"><title>404</title></head>
                <body>
                    <h1>404</h1>
                    <p>Page not found</p>
                </body>
                </html>
                """;

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        out.write(("HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + bytes.length + "\r\n" +
                "\r\n").getBytes(StandardCharsets.UTF_8));

        out.write(bytes);
        out.flush();
    }
}