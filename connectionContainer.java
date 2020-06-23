import java.io.*;
import java.net.Socket;

public class connectionContainer implements Runnable {
    private Socket s;
    private Boolean connected = false;
    private int elementNumber;
    private int player;

    public connectionContainer(Socket s){
        setSocket(s);
    }
    public connectionContainer(Socket s, Boolean connected, int elementNumber){
        setSocket(s);
        setConnected(connected);
        setElementNumber(elementNumber);
    }

    public void run(){
        boolean alive = true;
        server s = new server();
        while(alive) {
            try {
                System.out.println("Waiting for input");
                s.dataTransfer(boardToArray(input()), getElementNumber());
            } catch (Exception e) {
                System.out.println(this.getIP() + " Connection was killed");
                e.printStackTrace();
                alive = false;
            }
        }
    }
    private String input() throws IOException {
        InputStream input = getSocket().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String board = reader.readLine();
        return board;
    }
    public void output(String board) throws  IOException{
        OutputStream output = getSocket().getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        writer.println(board);
    }
    public void printBoard(int[][] board){
        for(int y = 5; y >= 0; y--){
            System.out.println();
            for(int x = 0; x < 7; x++){
                System.out.print(board[x][y] + " ");
            }
        }
    }


    public void setSocket(Socket s){
        this.s = s;
    }
    public Socket getSocket(){
        return s;
    }

    public Boolean getConnected() {
        return connected; }
    public void setConnected(Boolean connected) {
        this.connected = connected; }

    public void setElementNumber(int elementNumber) {
        this.elementNumber = elementNumber;
    }
    public int getElementNumber(){
        return elementNumber;
    }
    public void setPlayer(int player){
        this.player = player;
    }
    public int getPlayer(){
        return player;
    }

    public String getIP(){
        Socket s = getSocket();
        String IP = s.getInetAddress() + ":" + s.getPort();
        return IP;
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
        printBoard(gameBoard);
        return gameBoard;
    }
    public String boardToString(int[][] board){
        StringBuilder gameBoard = new StringBuilder();

        for(int y = 0; y < 6; y++){
            for(int x = 0; x < 7; x++) {
                gameBoard.append(board[x][y]);
            }
        }

        return gameBoard.toString();
    }

}
