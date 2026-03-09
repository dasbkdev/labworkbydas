package repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Candidate;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CandidateRepository {
    private final List<Candidate> candidates = new ArrayList<>();

    public CandidateRepository() {
        loadCandidates();
    }

    public List<Candidate> findAll() {
        return new ArrayList<>(candidates);
    }

    public Candidate findById(int id) {
        for (Candidate candidate : candidates) {
            if (candidate.getId() == id) {
                return candidate;
            }
        }
        return null;
    }

    private void loadCandidates() {
        try (Reader reader = new FileReader("data/candidates.json")) {

            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(reader);

            if (root.isJsonArray()) {
                JsonArray array = root.getAsJsonArray();
                for (JsonElement element : array) {
                    if (element.isJsonObject()) {
                        candidates.add(parseCandidate(element.getAsJsonObject()));
                    }
                }
            } else if (root.isJsonObject()) {
                JsonObject object = root.getAsJsonObject();
                if (object.has("candidates") && object.get("candidates").isJsonArray()) {
                    JsonArray array = object.getAsJsonArray("candidates");
                    for (JsonElement element : array) {
                        if (element.isJsonObject()) {
                            candidates.add(parseCandidate(element.getAsJsonObject()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read candidates.json", e);
        }
    }

    private Candidate parseCandidate(JsonObject object) {
        int id = getInt(object, "id", 0);
        String name = getString(object, "name", "Unknown");
        String photo = getString(object, "photo", "");

        if (photo.isBlank()) {
            photo = getString(object, "image", "");
        }
        if (photo.isBlank()) {
            photo = getString(object, "img", "");
        }

        int votes = getInt(object, "votes", 0);

        return new Candidate(id, name, photo, votes);
    }

    private String getString(JsonObject object, String key, String defaultValue) {
        if (object.has(key) && !object.get(key).isJsonNull()) {
            return object.get(key).getAsString();
        }
        return defaultValue;
    }

    private int getInt(JsonObject object, String key, int defaultValue) {
        if (object.has(key) && !object.get(key).isJsonNull()) {
            return object.get(key).getAsInt();
        }
        return defaultValue;
    }
}