import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:nonogram.db";

    private Connection connection;

    public Database() {
        connect();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database.");
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String playersTable = """
            CREATE TABLE IF NOT EXISTS players (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE
            )
        """;

        String scoresTable = """
            CREATE TABLE IF NOT EXISTS scores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_id INTEGER NOT NULL,
                gameName TEXT NOT NULL,
                score INTEGER NOT NULL,
                FOREIGN KEY(player_id) REFERENCES players(id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(playersTable);
            stmt.execute(scoresTable);
        }
    }

    public int getOrCreatePlayer(String username) throws SQLException {
        Integer playerId = findPlayerId(username);

        if (playerId != null) {
            return playerId;
        }

        return createPlayer(username);
    }

    private Integer findPlayerId(String username) throws SQLException {
        String sql = "SELECT id FROM players WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return null;
    }

    private int createPlayer(String username) throws SQLException {
        String sql = "INSERT INTO players(username) VALUES(?)";

        try (PreparedStatement stmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to create player.");
    }

    public void saveScore(int playerId, String gameName, int score) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO scores(player_id,gameName,score) VALUES(?,?,?)");
        ps.setInt(1, playerId);
        ps.setString(2, gameName);
        ps.setInt(3, score);
        ps.executeUpdate();
    }



    public void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("Failed to close database connection.");
            e.printStackTrace();
        }
    }
}