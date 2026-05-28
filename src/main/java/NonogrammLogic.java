public class NonogrammLogic {
    private int[][] board;
    public int N = 10;
    public NonogrammLogic(){
        board = new int[N][N];
        fillBoard();
    }
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

    public int countColoumns(int i){
        int counter = 0;
        for (int j = 0; j<N; j++){
            if (board[j][i] == 1){
                counter++;
            }
        }
        return counter;
    }
    public int countRows(int i){
        int counter = 0;
        for (int j = 0; j<N; j++){
            if (board[i][j] == 1){
                counter++;
            }
        }
        return counter;
    }

}
