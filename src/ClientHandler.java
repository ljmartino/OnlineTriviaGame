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
        private ObjectOutputStream out;
        private DataInputStream in;

        // correct answer is updated whenever we send a file
        private int correctAnswer = -1;

        public ClientHandler(Socket clientSocket, int ID)
        {
            this.clientSocket = clientSocket;
            this.ID = ID;
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

                // how to read from client
                // int words = in.readInt();
                // System.out.println("Words: " + words);

                // this thread will monitor answers from the client and then send them score
                Thread thread = new Thread(() -> {
                    while (true){
                        try {
                            int currAnswer = in.readInt();
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
                        } catch(SocketException se){
                            System.out.println("Client "+this.ID+" left");
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

                //Closing socket
                // clientSocket.close();
            } catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }

        public void sendFile(int questionNumber){
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
}
