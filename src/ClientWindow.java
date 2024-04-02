import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.TimerTask;
import java.util.Scanner;
import java.util.Timer;
import javax.swing.*;

public class ClientWindow implements ActionListener
{
	private JButton buzz;
	private JButton submit;
	private JRadioButton options[];
	private String optionsText[]; //To store text for each option
	private ButtonGroup optionGroup;
	private int currentSelection;
	private JLabel waitingMessage;
	private JLabel question;
	private JLabel timer;
	private JLabel timerText;
	private JLabel score;
    private int scoreCount;
	private TimerTask clock;

	// clientID is given from the server
	private Integer ClientID;
	private String questionNumber;
	private Socket socket;
	private ObjectInputStream inputStream;
	private DataOutputStream outputStream;
	private String serverIP;
	private boolean noNACKS;

	private JFrame window;
	
	private static SecureRandom random = new SecureRandom();
	
	// write setters and getters as you need
	
	public ClientWindow(String ipAddress, int port) throws FileNotFoundException
	{
		//JOptionPane.showMessageDialog(window, "This is a trivia game");
		this.serverIP = ipAddress;
		
		window = new JFrame("Trivia");
		
		waitingMessage = new JLabel("Waiting for game to start...");
		waitingMessage.setBounds(250, 150, 400, 50);
		waitingMessage.setFont(new Font("Calibri", Font.BOLD, 30));
		
		timer = new JLabel("TIMER");  // represents the countdown shown on the window
		timer.setBounds(250, 250, 100, 20);

		// clock = new TimerCode(35);  // represents clocked task that should run after X seconds
		// Timer t = new Timer();  // event generator
		// t.schedule(clock, 0, 1000); // clock is called every second
		
		score = new JLabel("SCORE: "+scoreCount); // represents the score
		score.setBounds(50, 250, 100, 20);

		timerText = new JLabel("TIMER"); // represents the time
		timerText.setBounds(235, 230, 100, 20);

		buzz = new JButton("Buzz");  // button that use clicks/ like a buzzer
		buzz.setBounds(10, 300, 100, 20);
		buzz.addActionListener(this);  // calls actionPerformed of this class
		
		submit = new JButton("Submit");  // button to submit their answer
		submit.setBounds(200, 300, 100, 20);
		submit.addActionListener(this);  // calls actionPerformed of this class
		
		
		window.setSize(600,400);
		window.setBounds(400, 200, 900, 400);
		window.setLayout(null);
		window.setVisible(true);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);

		window.add(waitingMessage);

		// call method to connect to server
		connect(ipAddress, port);

		
	}

	// connect to server with argument of IP address
	public void connect(String ipAddress, int port){
		try	{
			// create a socket connection to the server
			// the thread immediately sends the client its ID and the client saves it
			socket = new Socket(ipAddress, port);
			inputStream = new ObjectInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			this.ClientID = Integer.valueOf(inputStream.readInt());
			System.out.println("Hello i am client " + this.ClientID);
		} 
		catch(IOException ioException){
			ioException.printStackTrace();
		}

		// now we start up a "swingworker", which is essentially a thread
		// it will not block the main GUI from processing and can handle our network stuff
		SwingWorker<Void, Void> worker = new SwingWorker<Void,Void>() {
			@Override
			protected Void doInBackground() {
				try{
					while (true){
						// read message type from server and take different action depending on it
							String messageType = (String) inputStream.readObject();
							if (messageType.equals("File".trim())){
								// first thing that is sent is the question number
								int questionNum = inputStream.readInt();
								if (questionNum < 10){
									questionNumber = "0" + questionNum;
								}
								else {
									questionNumber = "" + questionNum;
								}
								// call process to start up a new question and display it
								String[] questionInfo = new String[5];
								questionInfo[0] = (String) inputStream.readObject();
								questionInfo[1] = (String) inputStream.readObject();
								questionInfo[2] = (String) inputStream.readObject();
								questionInfo[3] = (String) inputStream.readObject();
								questionInfo[4] = inputStream.readObject().toString();
								displayQuestion(questionInfo);
							}
							// if the message is a score message, then increment and display score
							else if (messageType.equals("Score".trim())){
								scoreCount += inputStream.readInt();
								score.setText("SCORE: "+scoreCount);
							}
							// if the message is an ack, then allow user to answer
							else if (messageType.equals("Ack".trim())){
								System.out.println("Ack received");
								submit.setEnabled(true);
            					options[0].setEnabled(true);
            					options[1].setEnabled(true);
            					options[2].setEnabled(true);
            					options[3].setEnabled(true);
								noNACKS = true;
							}
							else if (messageType.equals("Nack".trim()) && !noNACKS){
								// to do
								System.out.println("NACK");
							}
					}
				}
				catch (IOException e){
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return null;
				
			}
		};
		worker.execute();
		
	}

	public void displayQuestion(String[] questionFile){

		// Remove existing components from the window
		window.getContentPane().removeAll();
		window.setTitle("Trivia - Player #"+this.ClientID);
	
		clock = new TimerCode(30);
		Timer t = new Timer();  // event generator
		t.schedule(clock, 0, 1000); // clock is called every second

		// Add the new question label
		question = new JLabel(questionFile[0]);
		question.setFont(new Font("Calibri", Font.BOLD, 20));
		window.add(question);
		question.setBounds(10,5, 800, 100);
	
		// Add new radio buttons for options
		options = new JRadioButton[4];
		optionsText = new String[4];
		optionGroup = new ButtonGroup();
		for(int index=0; index<options.length; index++)
		{
			optionsText[index] = questionFile[index+1]; // Gets text from file for options
			options[index] = new JRadioButton(optionsText[index]);
			options[index].addActionListener(this);
			options[index].setBounds(10, 110+(index*20), 450, 20);
			window.add(options[index]);
			optionGroup.add(options[index]);
			options[index].setEnabled(false);
		}

		 // Add the buttons
		 window.add(buzz);
		 window.add(submit);
	 
		 // Add the timer labels
		 window.add(timer);
		 window.add(timerText);

		 // add the score
		 window.add(score);
	
		// Repaint the window to reflect changes
		window.revalidate();
		window.repaint();
	}
	

	// this method is called when you check/uncheck any radio button
	// this method is called when you press either of the buttons- submit/poll
	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("You clicked " + e.getActionCommand());

		// input refers to the radio button you selected or button you clicked
		String input = e.getActionCommand();  
		
		if(input.equals("Buzz")){
			// buzz.setEnabled(false);
			
			byte[] buf = null;
			String message = ClientID+","+questionNumber;
			buf = message.getBytes();
			InetAddress ip = null;
			try {
				ip = InetAddress.getByName(serverIP);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			int port = 1111;
			DatagramPacket pkt = new DatagramPacket(buf, buf.length, ip, port);
			DatagramSocket skt = null;
			try {
				skt = new DatagramSocket();
				skt.send(pkt);
				for(int i=0;i<buf.length;i++){
					System.out.println("You sent: "+buf[i]);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if(input.equals("Submit")){
			noNACKS = false;
			// send whatever option was selected
			try {
				outputStream.writeInt(currentSelection);
				outputStream.flush();
			}
			catch (IOException e2){
				e2.printStackTrace();
			}

            // buzz.setEnabled(true);
			submit.setEnabled(false);
            options[0].setEnabled(false);
            options[1].setEnabled(false);
            options[2].setEnabled(false);
            options[3].setEnabled(false);
		} else if(input.equals(optionsText[0])){
			currentSelection = 0;
		} else if(input.equals(optionsText[1])){
			currentSelection = 1;
		} else if(input.equals(optionsText[2])){
			currentSelection = 2;
		} else if(input.equals(optionsText[3])){
			currentSelection = 3;
		} else{
			System.out.println("Incorrect Option");
		}

		//question.setText("Q2. This is another test problem " + random.nextInt());
		
	}
	
	// this class is responsible for running the timer on the window
	public class TimerCode extends TimerTask
	{
		private int duration;  // write setters and getters as you need
		public TimerCode(int duration)
		{
			this.duration = duration;
		}
		@Override
		public void run()
		{
			if(duration < 0)
			{
				timer.setText("Timer expired");
				window.repaint();
				buzz.setEnabled(false);
				submit.setEnabled(false);
            	options[0].setEnabled(false);
            	options[1].setEnabled(false);
            	options[2].setEnabled(false);
            	options[3].setEnabled(false);
				JOptionPane.showMessageDialog(window, "Your final score is "+scoreCount);
				this.cancel();  // cancel the timed task
				return;
				// you can enable/disable your buttons for poll/submit here as needed
			}
			
			if(duration < 6)
				timer.setForeground(Color.red);
			else
				timer.setForeground(Color.black);
			
			timer.setText(duration+"");
			duration--;
			window.repaint();
		}
	}
	
}
