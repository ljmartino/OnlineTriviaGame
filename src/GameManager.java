import java.util.concurrent.ConcurrentLinkedQueue;

public class GameManager implements Runnable{
    private ConcurrentLinkedQueue<Item> queue;
    public GameManager(ConcurrentLinkedQueue<Item> q){
        queue = q;
    }

    @Override
    public void run() {
        
    }
}
