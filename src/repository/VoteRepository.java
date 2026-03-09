package repository;

import java.util.HashMap;
import java.util.Map;

public class VoteRepository {
    private final Map<String, Integer> userVotes = new HashMap<>();

    public void saveVote(String userKey, int candidateId) {
        userVotes.put(userKey, candidateId);
    }

    public Integer findVoteByUser(String userKey) {
        return userVotes.get(userKey);
    }

    public boolean hasUserVoted(String userKey) {
        return userVotes.containsKey(userKey);
    }
}