package controller;

import model.Candidate;
import service.CandidateService;
import service.VoteService;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class VoteController {
    private final VoteService voteService = new VoteService();
    private final CandidateService candidateService = new CandidateService();

    public void handleVote(OutputStream out, Map<String, String> bodyParams, String userId) throws Exception {
        String idValue = bodyParams.get("id");

        if (idValue == null || idValue.isBlank()) {
            new CandidateController().showCandidateNotFound(out);
            return;
        }

        int candidateId;

        try {
            candidateId = Integer.parseInt(idValue);
        } catch (Exception e) {
            new CandidateController().showCandidateNotFound(out);
            return;
        }

        Candidate candidate = candidateService.getCandidateById(candidateId);

        if (candidate == null) {
            new CandidateController().showCandidateNotFound(out);
            return;
        }

        if (!voteService.hasUserVoted(userId)) {
            candidateService.addVote(candidateId);
            voteService.saveVote(userId, candidateId);
        }

        redirectToThankYou(out, userId);
    }

    private void redirectToThankYou(OutputStream out, String userId) throws Exception {
        out.write(("HTTP/1.1 303 See Other\r\n" +
                "Set-Cookie: userId=" + userId + "; Path=/\r\n" +
                "Location: /thankyou\r\n" +
                "\r\n").getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}