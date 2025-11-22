package web.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// POJO (Plain Old Java Object) representing a single calculation result.
// Used for storing in the database and displaying in the results table.

public class CalculationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double x;
    private Double y;
    private Double r;
    private boolean hit;
    private long executionTimeNanos;
    private LocalDateTime timestamp;


    // Formatter for display purposes (Static to save memory)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CalculationResult() {
        this.timestamp = LocalDateTime.now();
    }

    public CalculationResult(Double x, Double y, Double r, boolean hit, long executionTimeNanos) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.timestamp = LocalDateTime.now();
        this.executionTimeNanos = executionTimeNanos;
    }


    // Business Logic Getters

    public String getHitStatus() {
        return hit ? "Hit" : "Miss";
    }

    public String getFormattedTimestamp() {
        if (timestamp == null) {
            return "N/A";
        }
        return timestamp.format(FORMATTER);
    }

    public String getFormattedExecutionTime() {
        // Convert nanoseconds to milliseconds for readability
        return String.format("%.6f ms", executionTimeNanos / 1_000_000.0);
    }


    // Getters and Setters (Required for persistence and JSF)

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Double getR() { return r; }
    public void setR(Double r) { this.r = r; }

    public boolean isHit() { return hit; }
    public void setHit(boolean hit) { this.hit = hit; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public long getExecutionTimeNanos() { return executionTimeNanos; }
    public void setExecutionTimeNanos(long executionTimeNanos) { this.executionTimeNanos = executionTimeNanos; }
}