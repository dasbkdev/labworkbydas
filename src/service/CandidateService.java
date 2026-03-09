package service;

import model.Candidate;
import model.VoteResult;
import repository.CandidateRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CandidateService {
    private final CandidateRepository repository = CandidateRepository.getInstance();

    public List<Candidate> getAllCandidates() {
        return repository.findAll();
    }

    public Candidate getCandidateById(int id) {
        return repository.findById(id);
    }

    public void addVote(int candidateId) {
        repository.incrementVote(candidateId);
    }

    public int getTotalVotes() {
        int total = 0;
        for (Candidate candidate : repository.findAll()) {
            total += candidate.getVotes();
        }
        return total;
    }

    public double getPercentForCandidate(Candidate candidate) {
        int totalVotes = getTotalVotes();
        if (totalVotes == 0) {
            return 0.0;
        }
        return (candidate.getVotes() * 100.0) / totalVotes;
    }

    public List<VoteResult> getSortedVoteResults() {
        List<Candidate> candidates = new ArrayList<>(repository.findAll());
        candidates.sort(Comparator.comparingInt(Candidate::getVotes).reversed());

        List<VoteResult> results = new ArrayList<>();
        int totalVotes = getTotalVotes();

        for (Candidate candidate : candidates) {
            double percent = totalVotes == 0 ? 0.0 : (candidate.getVotes() * 100.0) / totalVotes;
            results.add(new VoteResult(candidate, percent));
        }

        return results;
    }
}