import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class mainClass {

    private int[][] gameBoard = new int[7][6];
    public static void main (String[] args){
        mainClass main = new mainClass();

    }
    public mainClass() {
        try{
            Socket socket = new Socket("127.0.0.1", 9500);
            System.out.println(socket.getInetAddress());
            int counter = 0;
            while(counter == 0) {
               counter = inputPlayer(socket);
            }
            if(counter == 2){
                int[][] board = new int[7][6];
                board[0][0] = 10;
                while(board[0][0] == 10){
                    board = input(socket);
                }
                setGameBoard(board);
                printBoard(board);
            }
            boolean gameContinues = true;
            while (gameContinues) {
                int[][] board = getGameBoard();
                int move = getMove(counter);
                if(move == 10){
                    System.out.println("Not a valid column number try again");
                }else {
                    int row = getRow(move, board);
                    if (row == 10) {
                        System.out.println("Column is full");
                    } else {
                        board[move][row] = counter;
                        setGameBoard(board);
                        printBoard(board);
                        gameContinues = checkWinConditionHV(move, row, counter, board);
                        if(gameContinues){
                            gameContinues = checkWinConditionD(move, row, counter, board);
                        }
                    }
                }
                output(socket, getGameBoard());
                setGameBoard(input(socket));
                printBoard(getGameBoard());
            }
        }catch(Exception e){
            System.out.println("Unable to make connection");
        }
    }
    private int[][] getGameBoard() {
        return gameBoard;
    }
    private void setGameBoard(int[][] gameBoard) {
        this.gameBoard = gameBoard;
    }
    private int getMove(int counter){
        int move;
        Scanner scanner = new Scanner(System.in);
        System.out.println("It's player " + counter + "'s turn. " + "Please enter the the column number you wish to play in, columns go from 0 to 6 ");
        try {
            move = Integer.parseInt(scanner.next());
            if (move > 6 || move < 0) {
                return 10;
            } else {
                return move;
            }
        }catch(Exception e){
            return 10;
        }
    }
    private int getRow(int column, int[][] board){
        int row;
        for(int y = 0; y < 6; y++){
            if(board[column][y] == 0){
                row = y;
                return row;
            }
        }
        row = 10;
        return row;
    }
    private void printBoard(int[][] board){
        for(int y = 5; y >= 0; y--){
            System.out.println();
            for(int x = 0; x < 7; x++){
                System.out.print(board[x][y] + " ");
            }
        }
        System.out.println();
    }
    private boolean checkWinConditionHV(int moveX, int moveY, int counter, int[][] board){
        int piecesInLine = 0;
        for(int y = 0; y < 6; y++){
            if(board[moveX][y] == counter){
                piecesInLine = piecesInLine + 1;
                if(piecesInLine == 4){
                    System.out.println("Player " + counter + " wins");
                    return false;
                }
            }else{
                piecesInLine = 0;
            }
        }
        for(int x = 0; x < 7; x++){
            if(board[x][moveY] == counter){
                piecesInLine = piecesInLine + 1;
                if(piecesInLine == 4){
                    System.out.println("Player " + counter + " wins");
                    return false;
                }
            }else {
                piecesInLine = 0;
            }
        }
        return true;
    }
    private boolean checkWinConditionD(int moveX, int moveY, int counter, int[][] board){
        int x = moveX;
        int y = moveY;
        int piecesInLine = 0;
        boolean edgeNotFound = true;
        while(edgeNotFound){
            if(y == 0 || x == 0){
                edgeNotFound = false;
            }else {
                x = x - 1;
                y = y - 1;
            }
        }
        while((x < 7 && x > -1) && (y < 6 && y > -1)) {
            if (board[x][y] == counter) {
                piecesInLine = piecesInLine + 1;
                if (piecesInLine == 4) {
                    System.out.println("Player " + counter + " wins");
                    return false;
                }
            } else {
                piecesInLine = 0;
            }
            x = x + 1;
            y = y + 1;
        }
        x = moveX;
        y = moveY;
        edgeNotFound = true;
        piecesInLine = 0;
        while(edgeNotFound){
            if(y == 5 || x == 0){
                edgeNotFound = false;
            }else {
                x = x - 1;
                y = y + 1;
            }
        }
        while((x < 7 && x > -1) && (y < 6 && y > -1)) {
            if (board[x][y] == counter) {
                piecesInLine = piecesInLine + 1;
                if (piecesInLine == 4) {
                    System.out.println("Player " + counter + " wins");
                    return false;
                }
            } else {
                piecesInLine = 0;
            }
            x = x + 1;
            y = y - 1;
        }
        return true;
    }
    private int[][] input(Socket ss) throws IOException {
        int[][] gameboard = new int[7][6];
        InputStream input = ss.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        for(int y = 0; y < 6; y++){
            for(int x = 0; x < 7; x++){
                String counterSpace = reader.readLine();
                gameboard[x][y] = Integer.parseInt(counterSpace);
            }
        }
        return gameboard;
    }
    private int inputPlayer(Socket ss) throws IOException{
        InputStream input = ss.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        int counter = Integer.parseInt(reader.readLine());
        return counter;
    }
    private void output(Socket s, int[][] board) throws IOException{
        OutputStream output = s.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        for(int y = 0; y < 6; y++) {
            for (int x = 0; x < 7; x++) {
                writer.println(board[x][y]);
            }
        }
    }
}
