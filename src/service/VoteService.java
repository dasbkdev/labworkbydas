package service;

import repository.VoteRepository;

public class VoteService {
    private final VoteRepository repository = VoteRepository.getInstance();

    public boolean hasUserVoted(String userId) {
        return repository.hasUserVoted(userId);
    }

    public void saveVote(String userId, int candidateId) {
        repository.saveVote(userId, candidateId);
    }

    public Integer getVotedCandidateId(String userId) {
        return repository.findVotedCandidateId(userId);
    }
}