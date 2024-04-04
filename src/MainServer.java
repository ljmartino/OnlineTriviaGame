import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer{

    private ConcurrentLinkedQueue<Item> queue;
    private ServerSocket serverSocket;
    private boolean gameOver;
    // gets incremented everytime a client is added, assigned to client
    private int clientIDs;
    //List of active clients maintained by server
    private static ArrayList<ClientHandler> activeClients;
    // executor service is what spins off threads
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static int winningScore;
    private static String winnerMessage;

    public MainServer(int port){
        gameOver = false;
        queue = new ConcurrentLinkedQueue<>();
        this.clientIDs = 0;
        activeClients = new ArrayList<ClientHandler>();
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running");
        } 
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void startServer(){
        // spin off UDP thread with executorService.submit()
        executorService.submit(new UDPManager(1111,queue));
        // spin off game Manager
        executorService.submit(new GameManager(queue));
        
        System.out.println("Joining period has started, 30 seconds to join");  //Does this work?????
        try {
            Thread.sleep(1000); //Waits 30 seconds to start accepting
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!gameOver){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                clientSocket.setReuseAddress(true);
                System.out.println("New client connected: " + clientSocket);
                // make a new client handler with the socket as well as give them an ID
                clientIDs++;
                ClientHandler ch = new ClientHandler(clientSocket, clientIDs);
                activeClients.add(ch);
                executorService.submit(ch);
                //Used this to test if printing out final scores works
                //if(clientIDs==4) gameOver=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void winner(){
        if(!activeClients.isEmpty()) winningScore = activeClients.get(0).finalScore();
        else winnerMessage = "There are no active clients, so nobody won";

        for(int i=0;i<activeClients.size();i++){
            int clientID = activeClients.get(i).getID();
            int finalScore = activeClients.get(i).finalScore();

            System.out.println("Client "+clientID+"'s score is "+finalScore);

            if(finalScore>winningScore){
                winnerMessage = "Client "+clientID+" won with a score of "+finalScore;
                winningScore = finalScore;
            }
            else if(finalScore==winningScore){
                if(winnerMessage!=null) winnerMessage+="\nClient "+clientID+" also won with a score of "+finalScore;
                else winnerMessage = "Client "+clientID+" won with a score of "+finalScore;
            }
        }

        System.out.println(winnerMessage);
    }



    public static void main(String[] args){
        
        // hardcoding the port, if someone cares then change it i have no issues
        MainServer server = new MainServer(5000);
        server.startServer();


    }
}