import java.util.concurrent.ConcurrentLinkedQueue;

public class GameManager implements Runnable{
    public static Boolean nextQ;
    public static int clientAnswering;
    private ConcurrentLinkedQueue<Item> queue;
    public GameManager(ConcurrentLinkedQueue<Item> q){
        queue = q;
        nextQ = false;
        clientAnswering = 0;//"Null" value for clinets -> No client 0
    }

    @Override
    public void run() {
        Boolean answerRecieved = false;
        while(!answerRecieved){
            if(nextQ == false){
                if(queue.isEmpty()){
                    answerRecieved = false;
                }
                if(!queue.isEmpty()){
                    answerRecieved = true;
                }
            }
        } 
        //If reached this point, a client has answered and the queue is not empty
        //Pull first item from the queue
        clientAnswering = queue.peek().getID();//Gets ID of first client inside the queue
        //Clients will look at this then the correct one will answer
        
    }
}
