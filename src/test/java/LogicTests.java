import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogicTests {
    static int total = 0;
    static int fail = 0;

    static void check(String name, boolean ok) {
        total++;
        System.out.println((ok ? "OK    " : "FAIL  ") + name);
        if (!ok) fail++;
    }

    public static void main(String[] args) throws SQLException {
        // ----- Nonogram -----
        NonogrammLogic n = new NonogrammLogic(5, 3);
        check("#1  Nonogram: setDiff", n.setDiff("Easy") == 5 && n.setDiff("Normal") == 10 && n.setDiff("Hard") == 15);
        check("#2  Nonogram: health (5x5 = 3, 10x10 = 4, 15x15 = 5)",
                new NonogrammLogic(5, 3).getHealth() == 3
                        && new NonogrammLogic(10, 3).getHealth() == 4
                        && new NonogrammLogic(15, 3).getHealth() == 5);
        check("#3  Nonogram: clue, mixed row (3 1)",
                n.buildCount(new boolean[]{true, true, true, false, true}).equals("3 1"));
        check("#4  Nonogram: clue, empty row (0)",
                n.buildCount(new boolean[5]).equals("0"));
        check("#5  Nonogram: clue, full row (5)",
                n.buildCount(new boolean[]{true, true, true, true, true}).equals("5"));
        check("#6  Nonogram: clue, blocks at both ends (1 1)",
                n.buildCount(new boolean[]{true, false, false, false, true}).equals("1 1"));
        check("#7  Nonogram: clue, alternating cells (1 1 1)",
                n.buildCount(new boolean[]{true, false, true, false, true}).equals("1 1 1"));

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                n.revealCell(i, j);
        check("#8  Nonogram: won after revealing every cell", n.isWon());
        int health = n.getHealth();
        n.revealCell(0, 0);
        check("#9  Nonogram: revealing a cell again costs no health", n.getHealth() == health);
        check("#10 Nonogram: score (3 health, 60 s = 680)", new NonogrammLogic(5, 3).calculateScore(60) == 680);

        // ----- Sudoku -----
        SodokuLogic s = new SodokuLogic(1, 3);
        check("#11 Sudoku: setDiff and health (Easy = 5, Normal = 4, Hard = 3)",
                s.setDiff("Easy") == 1 && s.setDiff("Normal") == 2 && s.setDiff("Hard") == 3
                        && new SodokuLogic(1, 3).getHealth() == 5
                        && new SodokuLogic(2, 3).getHealth() == 4
                        && new SodokuLogic(3, 3).getHealth() == 3);

        // 10 generated puzzles per level, each must have exactly one solution
        // (checked by a solver that is independent of the generator)
        int unique = 0;
        for (int level = 1; level <= 3; level++) {
            for (int k = 0; k < 10; k++) {
                SodokuLogic p = new SodokuLogic(level, 3);
                int[][] grid = new int[9][9];
                for (int i = 0; i < 9; i++)
                    for (int j = 0; j < 9; j++)
                        grid[i][j] = p.isFilled(i, j) ? p.getValue(i, j) : 0;
                if (countSolutions(grid, 2) == 1) unique++;
            }
        }
        check("#12 Sudoku: every generated puzzle has exactly one solution (" + unique + "/30)", unique == 30);

        // wrong number: exactly 1 health is lost and the cell stays empty
        boolean wrongOk = false;
        for (int i = 0; i < 9 && !wrongOk; i++)
            for (int j = 0; j < 9 && !wrongOk; j++)
                for (int d = 1; d <= 9 && !s.isFilled(i, j); d++) {
                    int before = s.getHealth();
                    if (!s.placeNumber(i, j, d) && !s.isFilled(i, j)) {
                        wrongOk = s.getHealth() == before - 1;
                        break;
                    }
                }
        check("#13 Sudoku: a wrong number costs 1 health and the cell stays empty", wrongOk);

        // on Hard (3 health) enough wrong numbers end the game
        SodokuLogic h = new SodokuLogic(3, 3);
        for (int i = 0; i < 9 && !h.isDead(); i++)
            for (int j = 0; j < 9 && !h.isDead(); j++)
                for (int d = 1; d <= 9 && !h.isFilled(i, j) && !h.isDead(); d++)
                    h.placeNumber(i, j, d);
        check("#14 Sudoku: dead when health runs out", h.isDead());

        check("#15 Sudoku: score (Normal, 4 health, 300 s = 2200)", new SodokuLogic(2, 3).calculateScore(300) == 2200);

        // ----- Database -----
        // A temporary player is used, deleted (with its scores) at the end,
        // so the real data is not touched.
        String testUser = "__test_" + System.currentTimeMillis();
        Database db = new Database();
        int playerId = db.getOrCreatePlayer(testUser);
        try {
            check("#16 Database: new player's total score is 0",
                    db.getTotalScore(playerId, "Nonogramm") == 0);

            db.saveScore(playerId, "Nonogramm", 680);
            db.saveScore(playerId, "Nonogramm", 1380);
            boolean inLeaderBoard = false;
            try (ResultSet rs = db.getLeaderBoard("Nonogramm")) {
                while (rs.next()) {
                    if (rs.getString("username").equals(testUser)
                            && rs.getInt("totalScore") == 2060) {
                        inLeaderBoard = true;
                    }
                }
            }
            check("#17 Database: saved scores are read back (total 2060, on leaderboard)",
                    db.getTotalScore(playerId, "Nonogramm") == 2060 && inLeaderBoard);
        } finally {
            db.close();
            deleteTestPlayer(playerId);
        }

        System.out.println("\nPassed: " + (total - fail) + "/" + total);
        System.out.println(fail == 0 ? "All tests passed." : "Failed tests: " + fail);
    }

    // Foreign keys are enforced, so the scores must be deleted before the player.
    static void deleteTestPlayer(int playerId) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:nonogram.db")) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM scores WHERE player_id = ?")) {
                ps.setInt(1, playerId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM players WHERE id = ?")) {
                ps.setInt(1, playerId);
                ps.executeUpdate();
            }
        }
    }

    // Independent backtracking solver: counts solutions, stops at 'limit'.
    static int countSolutions(int[][] g, int limit) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (g[r][c] != 0) continue;
                int count = 0;
                for (int d = 1; d <= 9 && count < limit; d++) {
                    if (fits(g, r, c, d)) {
                        g[r][c] = d;
                        count += countSolutions(g, limit - count);
                        g[r][c] = 0;
                    }
                }
                return count;
            }
        }
        return 1; // no empty cell left: one complete solution
    }

    static boolean fits(int[][] g, int r, int c, int d) {
        for (int i = 0; i < 9; i++)
            if (g[r][i] == d || g[i][c] == d) return false;
        int br = (r / 3) * 3, bc = (c / 3) * 3;
        for (int i = br; i < br + 3; i++)
            for (int j = bc; j < bc + 3; j++)
                if (g[i][j] == d) return false;
        return true;
    }
}