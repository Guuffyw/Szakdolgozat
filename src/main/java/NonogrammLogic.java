import java.util.ArrayList;

public class NonogrammLogic {
    private int[][] board;
    private boolean[][] reavealed;
    private int N;

    private int health;
    private int totalBlackCounter;
    private int foundBlackCounter;

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

        board = new int[N][N];
        reavealed = new boolean[N][N];
        fillBoard();
    }

    //1 == BLACK ||  0 == WHITE
    public void fillBoard(){
        foundBlackCounter = 0;
        totalBlackCounter = 0;

        for(int i = 0; i < N;i++){
            for(int j = 0; j < N;j++){
                board[i][j] = (int)Math.round(Math.random());
                if (board[i][j] ==1){
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

        if (board[i][j] == 1){
            foundBlackCounter++;
            return true;
        }
        health--;
        return false;
    }

    public boolean isReavealed(int i,int j){
        return reavealed[i][j];
    }

    public ArrayList<Integer> countColoumns(int i){
        ArrayList<Integer> list = new ArrayList<>();
        int counter =0;
        for (int j = 0;j < N; j++){
            if( board[j][i] == 1){
                counter++;
                if (j == N-1){
                    list.add(counter);
                }
            }
            else {
                if (counter != 0) {
                    list.add(counter);
                }

                counter = 0;
            }
        }
        return list;
    }
    public ArrayList<Integer> countRows(int i){
        ArrayList<Integer> list = new ArrayList<>();
        int counter =0;
        for (int j = 0;j < N; j++){
            if( board[i][j] == 1){
                counter++;
                if (j == N-1){
                    list.add(counter);
                }
            }
            else {
                if (counter != 0) {
                    list.add(counter);
                }

                counter = 0;
            }
        }
        return list;
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


}
