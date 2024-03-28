import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UDPManager implements Runnable {
    private int port;
    private DatagramSocket datagramSocket;
    // need to add a type here
    private ConcurrentLinkedQueue queue;

    public UDPManager(int port, ConcurrentLinkedQueue q){
        this.port = port;
        // not sure what type the queue should be, probably need to specify at some point
        queue = q;
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
        byte[] buf = new byte[2];
        while (true){
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            try {
                datagramSocket.receive(datagramPacket);
                for(int i=0;i<buf.length;i++){
                    System.out.println("Received: "+buf[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            

            /*
             * Need to mess around with UDP some more and figure out how exactly to send and receive
             */
        }
    }

    
}