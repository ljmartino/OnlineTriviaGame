import java.util.concurrent.ConcurrentLinkedQueue;

public class GameManager implements Runnable{
    private ConcurrentLinkedQueue queue;
    public GameManager(ConcurrentLinkedQueue q){
        queue = q;
    }

    @Override
    public void run() {
        
    }
}
