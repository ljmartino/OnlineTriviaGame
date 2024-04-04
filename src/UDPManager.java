import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UDPManager implements Runnable {
    private int port;
    private DatagramSocket datagramSocket;
    // need to add a type here
    private ConcurrentLinkedQueue<Item> queue;
    public static boolean[] notAnsweredArray;

    public UDPManager(int port, ConcurrentLinkedQueue<Item> q){
        this.port = port;
        // not sure what type the queue should be, probably need to specify at some point
        queue = q;
        notAnsweredArray = new boolean[21];
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
        byte[] buf = new byte[5];
        while (true){
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            try {
                datagramSocket.receive(datagramPacket);
                Integer clientID, questionNumber;
                if(buf[1]==44){
                    clientID = (int)buf[0]-48;
                    questionNumber = ((int)buf[2]-48)*10+(int)buf[3]-48;
                    queue.add(new Item(clientID, questionNumber));
                } 
                // if it is an exclamation point, that means nobody answered this question
                else if (buf[1] == 33){
                    questionNumber = ((int)buf[2]-48)*10+(int)buf[3]-48;
                    UDPManager.notAnsweredArray[questionNumber-1] = true;
                    System.out.println("Not answered array for question " + (questionNumber) + " is now true");
                }
                else {
                    clientID = ((int)buf[0]-48)*10+(int)buf[1]-48;
                    questionNumber = ((int)buf[3]-48)*10+(int)buf[4]-48;
                    queue.add(new Item(clientID, questionNumber));
                }
                // queue.add(new Item(clientID, questionNumber));

            } catch (IOException e) {
                e.printStackTrace();
            }
            // } catch (InterruptedException e){
            //     e.printStackTrace();
            // }
            

            /*
             * Need to mess around with UDP some more and figure out how exactly to send and receive
             */
        }
    }

    
}