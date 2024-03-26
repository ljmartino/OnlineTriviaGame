import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UDPManager implements Runnable {
    private int port;
    private DatagramSocket datagramSocket;
    // need to add a type here
    private ConcurrentLinkedQueue queue;

    public UDPManager(int port){
        this.port = port;
        // not sure what type the queue should be, probably need to specify at some point
        queue = new ConcurrentLinkedQueue<>();
        // set up the UDP socket
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        // runs until thread is destroyed, this class shouldn't have to care about that
        while (true){
            // datagramSocket.receive();

            /*
             * Need to mess around with UDP some more and figure out how exactly to send and receive
             */
        }
    }

    
}