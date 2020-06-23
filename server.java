import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class server {

    private static List<connectionContainer> connections = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            try {
                ServerSocket ss = new ServerSocket(9500);
                Socket s = ss.accept();
                saveConnection(s, connections, writeConnection(s));
                ss.close();

            } catch (Exception e) {
                System.out.println("Connect failed");
                e.printStackTrace();
            }

        }
    }

    private void getBoard(int[][] board, String ip) {
        try {
            File file = new File("connections.txt");
            Scanner sc = new Scanner(file);
            boolean connectionNotFound = true;
            while (connectionNotFound) {
                String connection = sc.nextLine();
                if (connection.contains(ip)) {
                    System.out.println(connection);
                } else {
                }
            }

        } catch (Exception e) {
        }
    }

    public void dataTransfer(int[][] board, int elementNumber){
        try {
            connectionContainer cContainer = connections.get(elementNumber);
            cContainer.output(cContainer.boardToString(board));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void saveConnection(Socket s, List<connectionContainer> sConnections, String opponent) {
        boolean notConnected = true;
        String answer = " ";
        for(int x = 0; x < sConnections.size(); x++){
            if(sConnections.get(x).getIP().equals(opponent)){
                outputToClient(s,"Y");
                answer = inputFromClient(s);
                if(answer.equalsIgnoreCase("Y")){
                    boolean cOpponent = opponentResponse(sConnections.get(x).getSocket(), "Y");
                    System.out.print("Hello");
                    if(cOpponent) {
                        outputToClient(s, "Nooo");
                        notConnected = updateConnection(s, x, sConnections);
                    }
                }else if(answer.equals("Connection Failed")){
                    x--;
                }
            }
        }
        for (int x = 0; x < sConnections.size(); x++) {
            if (!sConnections.get(x).getConnected() && notConnected) {
                notConnected = updateConnection(s, x, sConnections);
            }else if(!notConnected){
                x = sConnections.size();
            }
        }
        if(notConnected) {
            connectionContainer sc = new connectionContainer(s);
            sConnections.add(sc);
        }

    }
    private static int setPlayer(){
        int counter;
        Random rand = new Random();
        if(rand.nextInt(2) == 0){
            counter = 1;
        }else{
            counter = 2;
        }
        return counter;
    }

    private static String writeConnection(Socket s) {
        List<String> connections = new ArrayList<>();
        String clientIP = s.getInetAddress().toString() + ":" + s.getPort();
        try {
            FileReader reader = new FileReader("connections.txt");
            Scanner sc = new Scanner(reader);
            while (sc.hasNextLine()) {
                connections.add(sc.nextLine());
            }
            sc.close();
            reader.close();

            for(int x = 0; x < connections.size(); x++){
                if(connections.get(x).contains(" ")){
                    String[] splitConnections = connections.get(x).split(" ");
                    if(clientIP.equals(splitConnections[0])){
                        return splitConnections[1];
                    }
                    else if(clientIP.equals(splitConnections[1])){
                        return splitConnections[0];
                    }
                }
            }
            boolean written = false;
            for (int x = 0; x < connections.size(); x++) {
                System.out.println(connections.get(x));
                if (connections.get(x).contains("/") && !connections.get(x).contains(" ")) {
                    FileWriter writer = new FileWriter("connections.txt", true);
                    PrintWriter write = new PrintWriter(writer);
                    write.println(" " + s.getInetAddress().toString() + ":" + s.getPort());
                    write.close();
                    writer.close();
                    x = connections.size();
                    written = true;
                }
            }
            if(!written){
                FileWriter writer = new FileWriter("connections.txt", true);
                PrintWriter write = new PrintWriter(writer);
                write.print(s.getInetAddress().toString() + ":" + s.getPort());
                write.close();
                writer.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Not Found";
    }

    private static boolean updateConnection(Socket s, int x, List<connectionContainer> sConnections){
        Thread t1 = new Thread(sConnections.get(x));
        t1.start();
        int counter = setPlayer();
        connectionContainer sc = new connectionContainer(s, true, x);
        sc.setPlayer(counter);
        sConnections.add(sc);

        int index = sConnections.indexOf(sc);
        sConnections.get(x).setConnected(true);
        sConnections.get(x).setElementNumber(index);

        if(counter == 1){
            sConnections.get(x).setPlayer(2);
        }else{
            sConnections.get(x).setPlayer(1);
        }

        try {
            sc.output(Integer.toString(sc.getPlayer()));
            sConnections.get(x).output(Integer.toString(sConnections.get(x).getPlayer()));
        }catch(Exception e){
            e.printStackTrace();
        }
        Thread t2 = new Thread(sc);
        t2.start();

        return false;
    }

    private static void outputToClient(Socket s, String data){
        try {
            OutputStream output = s.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String inputFromClient(Socket s){
        String response = "Connection Failed";
        try {
            InputStream input = s.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            response = reader.readLine();
        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

    private static boolean opponentResponse(Socket s, String data){
        String answer = " ";
        outputToClient(s, data);
        answer = inputFromClient(s);
        return answer.equalsIgnoreCase("Y");

    }
}
