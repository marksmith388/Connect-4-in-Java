import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class mainClass {

    private int[][] gameBoard = new int[7][6];
    public static void main (String[] args){
        mainClass main = new mainClass(50907);

    }
    public mainClass(int port) {
        try{
            Socket socket = new Socket("127.0.0.1", 9500, InetAddress.getByName("127.0.0.1"), port);
            System.out.println(socket.getInetAddress() + ":" + socket.getLocalPort());

            String response = " ";
            response = input(socket);
            System.out.println("received existing game");
            if(response.equals("Y")) {
                System.out.println("Do you want to continue your saved game? Y/N");
                Scanner scanner = new Scanner(System.in);
                response = scanner.nextLine();
                output(socket, response);
            }
            int counter;
            counter = Integer.parseInt(input(socket));
            System.out.println(counter + "Counter value");

            if(counter == 2){
                int[][] board;
                board = boardToArray(input(socket));
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
                output(socket, boardToString(getGameBoard()));
                setGameBoard(boardToArray(input(socket)));
                printBoard(getGameBoard());
            }
        }catch(Exception e){
            System.out.println("Unable to make connection");
            e.printStackTrace();
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
    private String input(Socket ss) throws IOException {
        InputStream input = ss.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String data = reader.readLine();
        return data;
    }
    private int[][] boardToArray(String board){
        int[][] gameBoard = new int[7][6];
        int counter = 0;
        for(int y = 0; y < 6; y++){
            for(int x = 0; x < 7; x++) {
                gameBoard[x][y] = Integer.parseInt(String.valueOf(board.charAt(counter)));
                counter++;
            }
        }
        return gameBoard;
    }
    private String boardToString(int[][] board){
        StringBuilder gameBoard = new StringBuilder();

        for(int y = 0; y < 6; y++){
            for(int x = 0; x < 7; x++) {
                gameBoard.append(board[x][y]);
            }
        }
        return gameBoard.toString();
    }
    private void output(Socket s, String board) throws IOException{
        OutputStream output = s.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        writer.println(board);
    }
}
