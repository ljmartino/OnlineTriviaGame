import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
        private Socket clientSocket;
        // unique client identifier
        private int ID;

        public ClientHandler(Socket clientSocket, int ID)
        {
            this.clientSocket = clientSocket;
            this.ID = ID;
        }
        @Override
        public void run()
        {
           
            try (DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                 DataInputStream in = new DataInputStream(clientSocket.getInputStream());)
            {

                // send the client it's ID so that it knows it
                out.writeInt(this.ID);
                out.flush();
                

                // how to send things
                // out.writeObject();
                // out.flush();

                // how to read from client
                // int words = in.readInt();
                // System.out.println("Words: " + words);

                //Closing socket
                // clientSocket.close();
            } catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }
}
