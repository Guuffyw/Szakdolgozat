import java.util.ArrayList;

public class NonogrammLogic {
    private int[][] board;
    public int N = 10;
    public NonogrammLogic(){
        board = new int[N][N];
        fillBoard();
    }
    //1 == BLACK ||  0 == WHITE
    public void fillBoard(){
        for(int i = 0; i < N;i++)
            for(int j = 0; j < N;j++)
                board[i][j] = (int)Math.round( Math.random() )  ;
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

}
