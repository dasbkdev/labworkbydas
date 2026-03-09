package controller;

import model.Candidate;
import service.CandidateService;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CandidateController {
    private final CandidateService candidateService = new CandidateService();

    public void showCandidates(OutputStream out) throws Exception {
        List<Candidate> candidates = candidateService.getAllCandidates();

        StringBuilder html = new StringBuilder();

        html.append("""
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Candidates</title>
                    <link rel="stylesheet" href="/css/candidates.css">
                </head>
                <body>
                <div class="container">
                    <div class="header">
                        <h1>Voting Service</h1>
                        <div class="nav-links">
                            <a href="/candidates">Candidates</a>
                            <a href="/votes">Votes</a>
                        </div>
                    </div>
                    <div class="cards">
                """);

        for (Candidate candidate : candidates) {
            String imageFile = candidate.getPhoto();
            String imagePath = "/images/" + imageFile;

            html.append("""
                    <div class="card">
                    """);

            html.append("<img class=\"candidate-image\" src=\"")
                    .append(imagePath)
                    .append("\" alt=\"")
                    .append(escapeHtml(candidate.getName()))
                    .append("\">");

            html.append("<h2>")
                    .append(escapeHtml(candidate.getName()))
                    .append("</h2>");

            html.append("<p>Votes: ")
                    .append(candidate.getVotes())
                    .append("</p>");

            html.append("<form method=\"post\" action=\"/vote\">")
                    .append("<input type=\"hidden\" name=\"id\" value=\"")
                    .append(candidate.getId())
                    .append("\">")
                    .append("<button type=\"submit\">Vote</button>")
                    .append("</form>");

            html.append("</div>");
        }

        html.append("""
                    </div>
                </div>
                </body>
                </html>
                """);

        byte[] bodyBytes = html.toString().getBytes(StandardCharsets.UTF_8);

        out.write(("HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "\r\n").getBytes(StandardCharsets.UTF_8));

        out.write(bodyBytes);
        out.flush();
    }

    public void showVotesStub(OutputStream out) throws Exception {
        String body = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Votes</title>
                    <link rel="stylesheet" href="/css/candidates.css">
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Votes</h1>
                            <div class="nav-links">
                                <a href="/candidates">Candidates</a>
                                <a href="/votes">Votes</a>
                            </div>
                        </div>
                        <div class="card single-card">
                            <p>This page will be implemented on the next step.</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

        sendHtml(out, body);
    }

    public void showThankYouStub(OutputStream out) throws Exception {
        String body = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Thank you</title>
                    <link rel="stylesheet" href="/css/candidates.css">
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Thank you</h1>
                            <div class="nav-links">
                                <a href="/candidates">Candidates</a>
                                <a href="/votes">Votes</a>
                            </div>
                        </div>
                        <div class="card single-card">
                            <p>This page will be implemented on the next step.</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

        sendHtml(out, body);
    }

    public void sendCss(OutputStream out) throws Exception {
        Path cssPath = Path.of("data/candidates.css");
        byte[] cssBytes = Files.readAllBytes(cssPath);

        out.write(("HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/css; charset=UTF-8\r\n" +
                "Content-Length: " + cssBytes.length + "\r\n" +
                "\r\n").getBytes(StandardCharsets.UTF_8));

        out.write(cssBytes);
        out.flush();
    }

    public void sendImage(OutputStream out, String fileName) throws Exception {
        Path imagePath = Path.of("data", fileName);

        if (!Files.exists(imagePath)) {
            String body = "Image not found";
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

            out.write(("HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: text/plain; charset=UTF-8\r\n" +
                    "Content-Length: " + bytes.length + "\r\n" +
                    "\r\n").getBytes(StandardCharsets.UTF_8));

            out.write(bytes);
            out.flush();
            return;
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);

        out.write(("HTTP/1.1 200 OK\r\n" +
                "Content-Type: image/jpeg\r\n" +
                "Content-Length: " + imageBytes.length + "\r\n" +
                "\r\n").getBytes(StandardCharsets.UTF_8));

        out.write(imageBytes);
        out.flush();
    }

    private void sendHtml(OutputStream out, String body) throws Exception {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        out.write(("HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + bytes.length + "\r\n" +
                "\r\n").getBytes(StandardCharsets.UTF_8));

        out.write(bytes);
        out.flush();
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}