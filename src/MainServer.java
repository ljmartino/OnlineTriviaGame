import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer{

    private ConcurrentLinkedQueue<Item> queue;
    private ServerSocket serverSocket;
    private boolean gameOver;
    // gets incremented everytime a client is added, assigned to client
    private int clientIDs;
    // executor service is what spins off threads
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public MainServer(int port){
        gameOver = false;
        queue = new ConcurrentLinkedQueue<>();
        this.clientIDs = 0;
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
        
        while (!gameOver){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                clientSocket.setReuseAddress(true);
                System.out.println("New client connected: " + clientSocket);
                // make a new client handler with the socket as well as give them an ID
                clientIDs++;
                System.out.println(clientIDs);
                executorService.submit(new ClientHandler(clientSocket, clientIDs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }




    public static void main(String[] args){
        
        // hardcoding the port, if someone cares then change it i have no issues
        MainServer server = new MainServer(5000);
        server.startServer();


    }
}