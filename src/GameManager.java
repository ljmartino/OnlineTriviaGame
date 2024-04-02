import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameManager implements Runnable{
    public static Boolean gameIsRunning = true;
    public static Boolean nextQ;
    public static Boolean clientAnswered;
    private ConcurrentLinkedQueue<Item> queue;
    public static ConcurrentLinkedQueue<Item> notFirst;
    public static int[] arrayQ;
    public GameManager(ConcurrentLinkedQueue<Item> q){
        queue = q;
        nextQ = false;
        notFirst = new ConcurrentLinkedQueue<Item>();
        arrayQ = new int[20];
    }

    @Override
    public void run() {

         //Using a thread to constantly look at the queue and handle new buzzes as they come in
         Thread queueLooker = new Thread(() -> {
            while(true){
                if (!queue.isEmpty()){
                    try{
                        Item next = queue.remove();
                        //Looks at array and finds out if someone answered already
                        if(GameManager.arrayQ[next.getQuestionNumber()-1] != 0){
                            //question was already answered by another client
                            GameManager.notFirst.add(next);
                            notFirst.wait(10);
                        }
                        else{//nothing was in the spot: first buzz for that specific question
                            GameManager.arrayQ[next.getQuestionNumber()-1] = next.getID(); //Adds client ID to the array
                        }
                    }
                    catch(NoSuchElementException e){
                        //deal with it -> whatchu gonna do?
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    //System.out.println("Queue is empty");
                }
                
            }
        });
        queueLooker.start();

        // while(gameIsRunning){
           
        //     while(!clientAnswered){
        //         //stay
        //     }
        //     nextQ = true;
        // }
    }
}
