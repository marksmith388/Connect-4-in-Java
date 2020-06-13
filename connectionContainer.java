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
        while(alive) {
            try {
                System.out.println("Waiting for input");
                server s = new server();
                s.dataTransfer(input(), getElementNumber());
            } catch (Exception e) {
                System.out.println("Connection was killed");
                alive = false;
            }
        }
    }
    private int[][] input() throws IOException {
        InputStream input = getSocket().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        int[][] board = new int[7][6];
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 7; x++) {
                board[x][y] = Integer.parseInt(reader.readLine());
            }
        }
        printBoard(board);
        return board;
    }
    private void output() throws IOException{
        int[][] board = new int[7][6];
        OutputStream output = getSocket().getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 7; x++) {
                writer.println(board[x][y]);
            }
        }
    }
    public void output(int[][] board) throws  IOException{
        OutputStream output = getSocket().getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 7; x++) {
                writer.println(board[x][y]);
            }
        }
    }
    public void outputPlayer() throws IOException{
        OutputStream output = getSocket().getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        writer.println(getPlayer());
    }
    private void printBoard(int[][] board){
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
}