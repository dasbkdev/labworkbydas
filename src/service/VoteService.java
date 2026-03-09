package service;

import repository.VoteRepository;

public class VoteService {
    private final VoteRepository voteRepository = new VoteRepository();

    public void saveVote(String userKey, int candidateId) {
        voteRepository.saveVote(userKey, candidateId);
    }

    public Integer getVoteByUser(String userKey) {
        return voteRepository.findVoteByUser(userKey);
    }

    public boolean hasUserVoted(String userKey) {
        return voteRepository.hasUserVoted(userKey);
    }
}