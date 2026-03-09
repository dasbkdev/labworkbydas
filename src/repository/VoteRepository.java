package repository;

import java.util.HashMap;
import java.util.Map;

public class VoteRepository {
    private static final VoteRepository INSTANCE = new VoteRepository();

    private final Map<String, Integer> userVotes = new HashMap<>();

    private VoteRepository() {
    }

    public static VoteRepository getInstance() {
        return INSTANCE;
    }

    public boolean hasUserVoted(String userId) {
        return userVotes.containsKey(userId);
    }

    public void saveVote(String userId, int candidateId) {
        userVotes.put(userId, candidateId);
    }

    public Integer findVotedCandidateId(String userId) {
        return userVotes.get(userId);
    }
}