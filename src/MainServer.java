import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer{

    private ServerSocket serverSocket;
    private boolean gameOver;
    // executor service is what spins off threads
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public MainServer(int port){
        gameOver = false;
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
        executorService.submit(new UDPManager(1111));
        // spin off game Manager
        executorService.submit(new GameManager());
        
        while (!gameOver){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
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