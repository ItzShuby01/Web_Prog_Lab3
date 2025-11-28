package web.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;


// Handles all raw JDBC interactions with the PostgreSQL database.

@ApplicationScoped
public class DataBaseManager {

    // My psql studs database credentials
    private static final String URL =  System.getenv("DB_URL"); //"jdbc:postgresql://pg:5432/studs"; // "jdbc:postgresql://localhost:5555/studs"; // Port 5555 for the tunnel
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD =  System.getenv("DB_PASSWORD");

    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS results (
            id SERIAL PRIMARY KEY,
            x DOUBLE PRECISION NOT NULL,
            y DOUBLE PRECISION NOT NULL,
            r DOUBLE PRECISION NOT NULL,
            hit BOOLEAN NOT NULL,
            timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
            exec_time BIGINT NOT NULL
        )
        """;

    private static final String INSERT_SQL = "INSERT INTO results (x, y, r, hit, timestamp, exec_time) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT x, y, r, hit, timestamp, exec_time FROM results ORDER BY id DESC";

    public DataBaseManager() {
        // Initializes the database table if it doesn't exist upon creation of the DAO
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            System.err.println("Database initialization error. Check DB connection and credentials: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {

        if (USER == null || PASSWORD == null) {
            System.err.println("Database credentials (DB_USER or DB_PASSWORD) are missing from environment variables.");
            // Throwing a dedicated exception might be better in a production app
            throw new SQLException("Missing database credentials in environment.");
        }

        // Ensures the driver is loaded and returns a new connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void saveResult(CalculationResult result) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setDouble(1, result.getX());
            stmt.setDouble(2, result.getY());
            stmt.setDouble(3, result.getR());
            stmt.setBoolean(4, result.isHit());
            stmt.setTimestamp(5, Timestamp.valueOf(result.getTimestamp()));
            stmt.setLong(6, result.getExecutionTimeNanos());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving result: " + e.getMessage());
        }
    }


    // Deletes all records from the results table.
    public void clearAllResults() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM results")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting results table: " + e.getMessage());
        }
    }

    public List<CalculationResult> loadAllResults() {
        List<CalculationResult> results = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                CalculationResult res = new CalculationResult();
                res.setX(rs.getDouble("x"));
                res.setY(rs.getDouble("y"));
                res.setR(rs.getDouble("r"));
                res.setHit(rs.getBoolean("hit"));
                res.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                res.setExecutionTimeNanos(rs.getLong("exec_time"));
                results.add(res);
            }

        } catch (SQLException e) {
            System.err.println("Error loading results. Check if the table exists: " + e.getMessage());
        }
        return results;
    }
}