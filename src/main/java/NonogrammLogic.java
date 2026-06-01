import java.util.ArrayList;

public class NonogrammLogic {
    private int[][] board;
    public int N = 5;
    public int startingHealth = 3;
    private int originalBlacks;
    public int blackCounter = 0;



    public NonogrammLogic(){
        board = new int[N][N];
        originalBlacks = fillBoard();
    }
    //1 == BLACK ||  0 == WHITE
    public int fillBoard(){
        blackCounter = 0;
        for(int i = 0; i < N;i++){
            for(int j = 0; j < N;j++){
                int rndnmbr = (int)Math.round(Math.random());
                board[i][j] = rndnmbr;
                if (rndnmbr ==1){
                    blackCounter++;
                }
            }
        }

        return blackCounter;
    }

    public int isBlack(int i,int j){
        if (board[i-1][j-1] == 0)
            return 0;
        else
            return 1;
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
    public void removeOneHearth(){
            startingHealth--;
    }

    public int isDead(int health){
        if(health == 0){
            return  1;
        }
        else return 0;
    }

    public void increaseBlack(){
        blackCounter++;
    }

    public int getOriginalBlacks(){
        return originalBlacks;
    }

    public int getBlacks(){
        return blackCounter;
    }

}
