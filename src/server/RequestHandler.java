package server;

import controller.CandidateController;
import controller.VoteController;
import service.VoteService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            int contentLength = 0;
            String cookieHeader = "";

            while ((line = in.readLine()) != null && !line.isEmpty()) {
                String lower = line.toLowerCase();
                if (lower.startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
                }
                if (lower.startsWith("cookie:")) {
                    cookieHeader = line.substring(line.indexOf(":") + 1).trim();
                }
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
            Map<String, String> cookies = parseCookies(cookieHeader);

            String userId = cookies.get("userId");
            if (userId == null || userId.isBlank()) {
                userId = UUID.randomUUID().toString();
            }

            CandidateController candidateController = new CandidateController();
            VoteController voteController = new VoteController();
            VoteService voteService = new VoteService();

            if ("GET".equalsIgnoreCase(method) && (path.equals("/") || path.equals("/candidates"))) {
                candidateController.showCandidates(out);
                return;
            }

            if ("POST".equalsIgnoreCase(method) && path.equals("/vote")) {
                char[] bodyChars = new char[contentLength];
                int read = in.read(bodyChars);
                String body = read > 0 ? new String(bodyChars, 0, read) : "";
                Map<String, String> bodyParams = parseParams(body);
                voteController.handleVote(out, bodyParams, userId);
                return;
            }

            if ("GET".equalsIgnoreCase(method) && path.equals("/thankyou")) {
                Integer candidateId = voteService.getVotedCandidateId(userId);
                candidateController.showThankYou(out, userId, candidateId);
                return;
            }

            if ("GET".equalsIgnoreCase(method) && path.equals("/votes")) {
                candidateController.showVotes(out);
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

    private Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return cookies;
        }

        String[] pairs = cookieHeader.split(";");

        for (String pair : pairs) {
            String[] kv = pair.trim().split("=", 2);
            if (kv.length == 2) {
                cookies.put(kv[0].trim(), kv[1].trim());
            }
        }

        return cookies;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private void send404(OutputStream out) throws Exception {
        String body = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>404</title>
                    <link rel="stylesheet" href="/css/candidates.css">
                </head>
                <body>
                    <div class="page-shell">
                        <div class="single-box">
                            <h1>404</h1>
                            <p>Page not found</p>
                            <div class="nav-actions">
                                <a class="nav-button" href="/candidates">Back to candidates</a>
                            </div>
                        </div>
                    </div>
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