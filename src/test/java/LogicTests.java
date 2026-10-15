public class LogicTests {
    static int fail = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "OK    " : "FAIL  ") + name);
        if (!ok) fail++;
    }

    public static void main(String[] args) {
        // ----- Nonogram -----
        NonogrammLogic n = new NonogrammLogic(5, 3);
        check("Nonogram: setDiff", n.setDiff("Easy") == 5 && n.setDiff("Normal") == 10 && n.setDiff("Hard") == 15);
        check("Nonogram: health (5x5 = 3, 10x10 = 4, 15x15 = 5)",
                new NonogrammLogic(5, 3).getHealth() == 3
                        && new NonogrammLogic(10, 3).getHealth() == 4
                        && new NonogrammLogic(15, 3).getHealth() == 5);
        check("Nonogram: clue (3 1 / 0)",
                n.buildCount(new boolean[]{true, true, true, false, true}).equals("3 1")
                        && n.buildCount(new boolean[5]).equals("0"));

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                n.revealCell(i, j);
        check("Nonogram: won after revealing every cell", n.isWon());
        int health = n.getHealth();
        n.revealCell(0, 0);
        check("Nonogram: revealing a cell again costs no health", n.getHealth() == health);
        check("Nonogram: score (3 health, 60 s = 680)", new NonogrammLogic(5, 3).calculateScore(60) == 680);

        // ----- Sudoku -----
        SodokuLogic s = new SodokuLogic(1, 3);
        check("Sudoku: setDiff", s.setDiff("Easy") == 1 && s.setDiff("Normal") == 2 && s.setDiff("Hard") == 3);
        check("Sudoku: health (Easy = 5, Normal = 4, Hard = 3)",
                new SodokuLogic(1, 3).getHealth() == 5
                        && new SodokuLogic(2, 3).getHealth() == 4
                        && new SodokuLogic(3, 3).getHealth() == 3);

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
        check("Sudoku: a wrong number costs 1 health and the cell stays empty", wrongOk);

        // on Hard (3 health) enough wrong numbers end the game
        SodokuLogic h = new SodokuLogic(3, 3);
        for (int i = 0; i < 9 && !h.isDead(); i++)
            for (int j = 0; j < 9 && !h.isDead(); j++)
                for (int d = 1; d <= 9 && !h.isFilled(i, j) && !h.isDead(); d++)
                    h.placeNumber(i, j, d);
        check("Sudoku: dead when health runs out", h.isDead());

        check("Sudoku: score (Normal, 4 health, 300 s = 2200)", new SodokuLogic(2, 3).calculateScore(300) == 2200);

        System.out.println(fail == 0 ? "\nAll tests passed." : "\nFailed tests: " + fail);
    }
}