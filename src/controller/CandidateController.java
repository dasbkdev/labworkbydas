package controller;

import model.Candidate;
import model.VoteResult;
import service.CandidateService;
import util.TemplateUtils;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CandidateController {
    private final CandidateService candidateService = new CandidateService();

    public void showCandidates(OutputStream out) throws Exception {
        List<Candidate> candidates = candidateService.getAllCandidates();
        String template = TemplateUtils.readFile("data/candidates.html");

        StringBuilder cards = new StringBuilder();

        for (Candidate candidate : candidates) {
            cards.append("""
                    <div class="candidate-card">
                    """);
            cards.append("<img class=\"candidate-image\" src=\"/images/")
                    .append(TemplateUtils.escapeHtml(candidate.getPhoto()))
                    .append("\" alt=\"")
                    .append(TemplateUtils.escapeHtml(candidate.getName()))
                    .append("\">");

            cards.append("<div class=\"candidate-content\">");
            cards.append("<h2>").append(TemplateUtils.escapeHtml(candidate.getName())).append("</h2>");
            cards.append("<p class=\"vote-count\">Votes: ").append(candidate.getVotes()).append("</p>");
            cards.append("<form method=\"post\" action=\"/vote\">");
            cards.append("<input type=\"hidden\" name=\"id\" value=\"").append(candidate.getId()).append("\">");
            cards.append("<button type=\"submit\" class=\"vote-button\">Vote</button>");
            cards.append("</form>");
            cards.append("</div>");
            cards.append("</div>");
        }

        String body = template.replace("{{candidates}}", cards.toString());
        sendHtml(out, body);
    }

    public void showThankYou(OutputStream out, String userId, Integer candidateId) throws Exception {
        String template = TemplateUtils.readFile("data/thankyou.html");

        if (candidateId == null) {
            showCandidateNotFound(out);
            return;
        }

        Candidate candidate = candidateService.getCandidateById(candidateId);

        if (candidate == null) {
            showCandidateNotFound(out);
            return;
        }

        double percent = candidateService.getPercentForCandidate(candidate);

        String body = template
                .replace("{{candidateName}}", TemplateUtils.escapeHtml(candidate.getName()))
                .replace("{{candidateVotes}}", String.valueOf(candidate.getVotes()))
                .replace("{{candidatePercent}}", String.format("%.2f", percent));

        sendHtml(out, body);
    }

    public void showVotes(OutputStream out) throws Exception {
        String template = TemplateUtils.readFile("data/votes.html");
        List<VoteResult> results = candidateService.getSortedVoteResults();

        StringBuilder rows = new StringBuilder();

        for (VoteResult result : results) {
            rows.append("<tr>");
            rows.append("<td>").append(TemplateUtils.escapeHtml(result.getCandidate().getName())).append("</td>");
            rows.append("<td>").append(String.format("%.2f%%", result.getPercent())).append("</td>");
            rows.append("</tr>");
        }

        String body = template.replace("{{voteRows}}", rows.toString());
        sendHtml(out, body);
    }

    public void showCandidateNotFound(OutputStream out) throws Exception {
        String body = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Candidate not found</title>
                    <link rel="stylesheet" href="/css/candidates.css">
                </head>
                <body>
                    <div class="page-shell">
                        <div class="single-box">
                            <h1>Candidate not found</h1>
                            <p>The selected candidate does not exist.</p>
                            <div class="nav-actions">
                                <a class="nav-button" href="/candidates">Back to candidates</a>
                                <a class="nav-button secondary" href="/votes">View results</a>
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
            imagePath = Path.of("data", "anon.jpeg");
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
}