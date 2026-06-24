import java.util.ArrayList;
import java.util.Random;

public class NonogrammLogic {
    private boolean[][] board;
    private boolean[][] reavealed;
    private int N;
    private final Random random = new Random();

    private int health;
    private int totalBlackCounter;
    private int foundBlackCounter;
    private double density;
    private String[] rowClues;
    private String[] colClues;

    public int setDiff(String string){
        if (string.equals("Easy")){
            N = 5;
        } else if (string.equals("Normal")) {
            N = 10;
        } else if (string.equals("Hard")) {
            N = 15;
        }
        return N;
    }

    
    public NonogrammLogic(int size,int startingHealth){
        this.N = size;
        this.health = startingHealth;



        board = new boolean[N][N];
        reavealed = new boolean[N][N];
        fillBoard(N);
        rowClues = buildcountRows(board);
        colClues = buildcountColoumns(board);
    }

    //1 == BLACK ||  0 == WHITE
    public void fillBoard(int size){
        switch (size){
            case 15: density = 0.8; health = 5; break;
            case 10: density = 0.65; health = 4; break;
            default: density = 0.55; health = 3; break;
        }
        foundBlackCounter = 0;
        totalBlackCounter = 0;


        for(int i = 0; i < N;i++){
            for(int j = 0; j < N;j++){
                board[i][j] = random.nextDouble() < density;
                if (board[i][j]){
                    totalBlackCounter++;
                }
            }
        }
    }

    public boolean revealCell(int i, int j){
        if (reavealed[i][j]){
            return false;
        }

        reavealed[i][j] = true;

        if (board[i][j]){
            foundBlackCounter++;
            return true;
        }
        health--;
        return false;
    }

    public int calculateScore(int seconds){
        int healthbonus = getHealth() * 100;
        int time = 1000 - seconds;
        return (healthbonus + time);
    }

    public boolean isReavealed(int i,int j){
        return reavealed[i][j];
    }






    public boolean isDead(){
        return health <= 0;
    }
    public boolean isWon(){
        return foundBlackCounter == totalBlackCounter;
    }
    public int getHealth(){
        return health;
    }
    public int getSize(){
        return N;
    }
    public String countRows(int i){
        return rowClues[i];
    }
    public String countColoumns(int i){
        return colClues[i];
    }



    public String[] buildcountColoumns(boolean[][] grid) {
        int size = grid.length;
        String[] result = new String[size];
        for (int j = 0; j < size; j++) {
            boolean[] col = new boolean[size];
            for (int i = 0; i < size; i++) {
                col[i] = grid[i][j];
            }
            result[j] = buildCount(col);
        }
        return result;
    }

    public String[] buildcountRows(boolean[][] grid){
        String[] result = new String[grid.length];
        for (int i = 0; i < grid.length; i++){
            result[i] = buildCount(grid[i]);
        }
        return result;
    }

    public String buildCount(boolean[] line){
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (boolean cell : line){
            if (cell){
                count++;
            } else if (count > 0) {
                builder.append(count).append(" ");
                count = 0;
            }
        }
        if (count > 0){
            builder.append(count);
        }
        String result = builder.toString().trim();
        if (result.isEmpty()){
            return "0";
        }
        else {
            return result;
        }
    }
}
