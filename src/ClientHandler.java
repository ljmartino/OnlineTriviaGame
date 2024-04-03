import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ClientHandler implements Runnable {
        private Socket clientSocket;
        // unique client identifier
        private int ID;
        private int scoreCount;
        private ObjectOutputStream out;
        private DataInputStream in;
        private boolean waiting = false;
        public int questionNumber;

        // correct answer is updated whenever we send a file
        private int correctAnswer = -1;
        private int finalScore = 0;

        public ClientHandler(Socket clientSocket, int ID)
        {
            this.clientSocket = clientSocket;
            this.ID = ID;
            this.scoreCount = 0;
        }

        public int getID(){
            return this.ID;
        }

        public int getScore(){
            return this.scoreCount;
        }

        @Override
        public void run()
        {
           
            try 
            {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new DataInputStream(clientSocket.getInputStream());

                // send the client it's ID so that it knows it
                out.writeInt(this.ID);
                out.flush();

                // call sendFile when you want to send a certain question and just put in the question number
                // everything else is handled
                sendFile(1);


                // this thread will monitor answers from the client and then send them score
                Thread thread = new Thread(() -> {
                    while (true){
                        try {
                            // if boolean is true, it's an answer. if boolean is false, its the final score
                            if (in.readBoolean()){
                                int currAnswer = in.readInt();
                                waiting = false;
                                System.out.println("They answered " + currAnswer + ", and the correct answer is " + correctAnswer);
                                // if answer is correct send them 10 points, if wrong send them -10 points
                                if (currAnswer == correctAnswer){
                                    out.writeObject("Score");
                                    out.writeInt(10);
                                }
                                else {
                                    out.writeObject("Score");
                                    out.writeInt(-10);
                                }
                                out.flush();
                            }
                            else {
                                finalScore = in.readInt();
                            }
                            
                        } catch(SocketException se){
                            System.out.println("Client "+this.ID+" left");
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

                // example of calling final score below
                // System.out.println("The final score is " + finalScore());

                // while game is running - this will send acks and nacks
                while (GameManager.gameIsRunning){
                    // if the ID of the client answering is this client and not waiting for an answer already, then send Ack
                    if (GameManager.arrayQ[questionNumber-1] == (Integer)this.ID && !waiting){
                        waiting = true;
                        out.writeObject("Ack");
                        out.flush();
                    }
                    // else if not first is equal to this ID, remove it and send a nack
                    else if (!GameManager.notFirst.isEmpty()){
                        if (GameManager.notFirst.peek().getID() == (Integer) this.ID){
                            GameManager.notFirst.remove();
                            out.writeObject("Nack");
                            out.flush();
                        }
                    }
                }

            } catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }

        // this method sends the file of the question number that is input
        public void sendFile(int questionNumber){
            this.questionNumber = questionNumber;
            File file = null;
            String filepath = "";
            if (questionNumber < 10){
                filepath = "question0" + questionNumber + ".txt";
            }
            else {
                filepath = "question" + questionNumber + ".txt";
            }
            file = new File(filepath);


            // read and send the file line by line
            try (Scanner scanner = new Scanner(file)){
                // first send the type of message that the client will receive
                String msgType = "File";
                out.writeObject(msgType);
                // then send the name of the file
                out.writeInt(questionNumber);
                // then send file line by line as strings
                int counter = 0;
                while (counter < 5) {
                    String line = scanner.nextLine();
                    out.writeObject(line); 
                    counter++;
                }
                // last line in file is just an int for the answer so store it
                correctAnswer = scanner.nextInt();
                out.flush();

                scanner.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        // this method asks for final score and returns it
        // the final score variable is updated within the thread within this class
        public int finalScore(){
            try {
                out.writeObject("Final");
                out.flush();
                // will try to get final score for like 2-3 seconds
                // if nothing comes back, we assume that client has been disconnected or something idk
                int counter = 0;
                while (finalScore == 0 && counter < 120){
                    Thread.sleep(30);
                    counter++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return finalScore;
        }

        // kill switch - close the socket and don't let client buzz anymore
        public void killSwitch(){
            try {
                out.writeObject("Kill");
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
