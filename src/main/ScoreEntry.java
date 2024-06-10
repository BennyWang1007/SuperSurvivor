package main;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ScoreEntry implements Serializable {
    private String name;
    private int score;
    private LocalDateTime timestamp;

    public ScoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
        this.timestamp = LocalDateTime.now();
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Name: " + name + ", Score: " + score + ", Timestamp: " + timestamp;
    }
}
