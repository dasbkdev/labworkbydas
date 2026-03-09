package service;

import model.Candidate;
import repository.CandidateRepository;

import java.util.List;

public class CandidateService {
    private final CandidateRepository candidateRepository = new CandidateRepository();

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public Candidate getCandidateById(int id) {
        return candidateRepository.findById(id);
    }
}