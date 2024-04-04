import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameManager implements Runnable{
    public static Boolean gameIsRunning = true;
    public static Boolean nextQ;
    public static Boolean clientAnswered = false;
    private ConcurrentLinkedQueue<Item> queue;
    // public static ConcurrentLinkedQueue<Item> notFirst;
    public static int[] arrayQ;
    public static int startingQuestion;
    public static ArrayList<Boolean> nackList;
    public GameManager(ConcurrentLinkedQueue<Item> q){
        queue = q;
        nextQ = false;
        // notFirst = new ConcurrentLinkedQueue<Item>();
        arrayQ = new int[21];
        GameManager.startingQuestion = 1;
        GameManager.nackList = new ArrayList<Boolean>();
    }
    public static boolean someoneAnswering = false;
    public static boolean thisQFlipped = false;

    public static boolean finalScoresFound = false;

    @Override
    public void run() {
        // System.out.println("run gameManager");
         //Using a thread to constantly look at the queue and handle new buzzes as they come in
         Thread queueLooker = new Thread(() -> {
            while(true){
                if (!queue.isEmpty()){
                    try{
                        Item next = queue.remove();
                        //Looks at array and finds out if someone answered already
                        if(GameManager.arrayQ[next.getQuestionNumber()-1] != 0){
                            //question was already answered by another client
                            // flip value in NackList to true
                            GameManager.nackList.set(next.getID()-1, true);
                            // GameManager.notFirst.add(next);
                        }
                        else{//nothing was in the spot: first buzz for that specific question
                            GameManager.arrayQ[next.getQuestionNumber()-1] = next.getID(); //Adds client ID to the array
                            if (next.getQuestionNumber() == GameManager.startingQuestion){
                                GameManager.someoneAnswering = true;
                            }
                            
                        }
                    }
                    catch(NoSuchElementException e){
                        //deal with it -> whatchu gonna do?
                    }
                }
                else{
                    // if nobody answered this specific question, move on
                    if (GameManager.startingQuestion < 21 && UDPManager.notAnsweredArray[GameManager.startingQuestion -1] && !GameManager.someoneAnswering && !GameManager.thisQFlipped){
                        System.out.println("Client answered has been flipped to true by notAnswered");
                        System.out.println("Starting question value is " + GameManager.startingQuestion);
                        clientAnswered = true;
                        GameManager.someoneAnswering = false;
                        GameManager.thisQFlipped = true;
                        // make a quick thread that will make sure that the question doesn't get double skipped over
                        Thread thisQThread = new Thread(() -> {
                            try{
                                Thread.sleep(2000);
                                GameManager.thisQFlipped = false;
                            }
                            catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        });
                        thisQThread.start();
                    }
                    //System.out.println("Queue is empty");
                }

                // if the final scores have been found, declare a winner and end the loop
                if (GameManager.finalScoresFound){
                    MainServer.winner();
                    break;
                }
                
            }
        });
        queueLooker.start();

        while(GameManager.gameIsRunning){
            while(!clientAnswered){
                System.out.print("");
            }
            nextQ = true;
            GameManager.startingQuestion++;
            GameManager.someoneAnswering = false;
            clientAnswered = false;
        }
        System.out.println("exited last loop");
    }
}
