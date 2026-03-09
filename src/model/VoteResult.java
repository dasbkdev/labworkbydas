package model;

public class VoteResult {
    private final Candidate candidate;
    private final double percent;

    public VoteResult(Candidate candidate, double percent) {
        this.candidate = candidate;
        this.percent = percent;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public double getPercent() {
        return percent;
    }
}