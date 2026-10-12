import java.util.Random;

public class SodokuLogic {
    private static final int SIZE = 9;

    private final int[][] solution = new int[SIZE][SIZE];
    private final int[][] current = new int[SIZE][SIZE];      // 0 == empty
    private final boolean[][] given = new boolean[SIZE][SIZE];
    private final Random random = new Random();

    private int level;              // 1 == Easy, 2 == Normal, 3 == Hard
    private int health;
    private int totalToFill;
    private int filledCounter;

    public int setDiff(String string){
        if (string.equals("Easy")){
            level = 1;
        } else if (string.equals("Normal")) {
            level = 2;
        } else if (string.equals("Hard")) {
            level = 3;
        }
        return level;
    }


    public SodokuLogic(int level, int startingHealth){
        this.level = level;
        this.health = startingHealth;

        fillBoard(level);
    }

    // builds a full valid grid, then removes numbers while the puzzle stays uniquely solvable
    public void fillBoard(int level){
        int removeTarget;
        switch (level){
            case 3: removeTarget = 54; health = 3; break;
            case 2: removeTarget = 48; health = 4; break;
            default: removeTarget = 40; health = 5; break;
        }
        filledCounter = 0;

        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                solution[i][j] = 0;
            }
        }
        fillGrid(0);

        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                current[i][j] = solution[i][j];
            }
        }

        int[] order = new int[SIZE * SIZE];
        for (int i = 0; i < order.length; i++){
            order[i] = i;
        }
        shuffle(order);

        int removed = 0;
        for (int idx : order){
            if (removed >= removeTarget){
                break;
            }
            int r = idx / SIZE;
            int c = idx % SIZE;
            int backup = current[r][c];
            current[r][c] = 0;
            if (countSolutions(copyOf(current), 2) == 1){
                removed++;
            } else {
                current[r][c] = backup;
            }
        }
        totalToFill = removed;

        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                given[i][j] = current[i][j] != 0;
            }
        }
    }

    // returns true when the number is correct (and locks it in), otherwise costs one health
    public boolean placeNumber(int i, int j, int value){
        if (current[i][j] != 0){
            return false;
        }

        if (solution[i][j] == value){
            current[i][j] = value;
            filledCounter++;
            return true;
        }
        health--;
        return false;
    }

    public int calculateScore(int seconds) {
        int healthBonus = getHealth() * 100;
        int timeBonus = Math.max(0, 1000 - seconds);

        return (healthBonus + timeBonus) * level;
    }

    public boolean isFilled(int i, int j){
        return current[i][j] != 0;
    }

    public boolean isGiven(int i, int j){
        return given[i][j];
    }

    public int getValue(int i, int j){
        return current[i][j];
    }

    // how many times a digit is already on the board (9 == complete)
    public int countOf(int value){
        int count = 0;
        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                if (current[i][j] == value){
                    count++;
                }
            }
        }
        return count;
    }






    public boolean isDead(){
        return health <= 0;
    }
    public boolean isWon(){
        return filledCounter == totalToFill;
    }
    public int getHealth(){
        return health;
    }
    public int getSize(){
        return SIZE;
    }
    public int getLevel(){
        return level;
    }



    //---------------- GENERATION HELPERS ----------------
    private boolean fillGrid(int pos){
        if (pos == SIZE * SIZE){
            return true;
        }
        int r = pos / SIZE;
        int c = pos % SIZE;

        int[] digits = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        shuffle(digits);

        for (int d : digits){
            if (canPlace(solution, r, c, d)){
                solution[r][c] = d;
                if (fillGrid(pos + 1)){
                    return true;
                }
                solution[r][c] = 0;
            }
        }
        return false;
    }

    // counts solutions (stops early at limit), always picking the cell with the fewest candidates
    private int countSolutions(int[][] g, int limit){
        int bestR = -1;
        int bestC = -1;
        int bestCount = 10;

        for (int r = 0; r < SIZE; r++){
            for (int c = 0; c < SIZE; c++){
                if (g[r][c] != 0){
                    continue;
                }
                int count = 0;
                for (int d = 1; d <= 9; d++){
                    if (canPlace(g, r, c, d)){
                        count++;
                    }
                }
                if (count == 0){
                    return 0;
                }
                if (count < bestCount){
                    bestCount = count;
                    bestR = r;
                    bestC = c;
                }
            }
        }

        if (bestR == -1){
            return 1;
        }

        int total = 0;
        for (int d = 1; d <= 9; d++){
            if (canPlace(g, bestR, bestC, d)){
                g[bestR][bestC] = d;
                total += countSolutions(g, limit - total);
                g[bestR][bestC] = 0;
                if (total >= limit){
                    break;
                }
            }
        }
        return total;
    }

    private boolean canPlace(int[][] g, int r, int c, int d){
        for (int k = 0; k < SIZE; k++){
            if (g[r][k] == d || g[k][c] == d){
                return false;
            }
        }
        int boxR = (r / 3) * 3;
        int boxC = (c / 3) * 3;
        for (int i = boxR; i < boxR + 3; i++){
            for (int j = boxC; j < boxC + 3; j++){
                if (g[i][j] == d){
                    return false;
                }
            }
        }
        return true;
    }

    private int[][] copyOf(int[][] g){
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++){
            copy[i] = g[i].clone();
        }
        return copy;
    }

    private void shuffle(int[] arr){
        for (int i = arr.length - 1; i > 0; i--){
            int j = random.nextInt(i + 1);
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }
}