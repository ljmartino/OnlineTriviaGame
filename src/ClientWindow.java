import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private int answerIndex; //Index of answer, last line of text file
    private boolean correctAnswer; //Determines if button user pressed was correct
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

	private JFrame window;
	
	private static SecureRandom random = new SecureRandom();
	
	// write setters and getters as you need
	
	public ClientWindow(String ipAddress, int port) throws FileNotFoundException
	{
		JOptionPane.showMessageDialog(window, "This is a trivia game");
        File file = new File("question11.txt");
		questionNumber = file.getName().substring(8,10);
        
        Scanner scan = new Scanner(file);
		
		window = new JFrame("Trivia");
		question = new JLabel(scan.nextLine()); // represents the question
		question.setFont(new Font("Calibri", Font.BOLD, 20));
		window.add(question);
		question.setBounds(10,5, 800, 100);;
		
		options = new JRadioButton[4];
		optionsText = new String[4];
		optionGroup = new ButtonGroup();
		for(int index=0; index<options.length; index++)
		{
			optionsText[index] = scan.nextLine(); //Gets text from file and stores each option as a string
			options[index] = new JRadioButton(optionsText[index]);  //Gets text in array and makes it an option
			// if a radio button is clicked, the event would be thrown to this class to handle
			options[index].addActionListener(this);
			options[index].setBounds(10, 110+(index*20), 450, 20);
			window.add(options[index]);
			optionGroup.add(options[index]);
            options[index].setEnabled(false);
		}

        answerIndex = scan.nextInt(); //Gets index of answer (0, 1, 2, or 3)
		scan.close();

		timer = new JLabel("TIMER");  // represents the countdown shown on the window
		timer.setBounds(250, 250, 100, 20);
		clock = new TimerCode(30);  // represents clocked task that should run after X seconds
		Timer t = new Timer();  // event generator
		t.schedule(clock, 0, 1000); // clock is called every second
		window.add(timer);
		
		
		score = new JLabel("SCORE: "+scoreCount); // represents the score
		score.setBounds(50, 250, 100, 20);
		window.add(score);

		timerText = new JLabel("TIMER"); // represents the time
		timerText.setBounds(235, 230, 100, 20);
		window.add(timerText);

		buzz = new JButton("Buzz");  // button that use clicks/ like a buzzer
		buzz.setBounds(10, 300, 100, 20);
		buzz.addActionListener(this);  // calls actionPerformed of this class
		window.add(buzz);
		
		submit = new JButton("Submit");  // button to submit their answer
		submit.setBounds(200, 300, 100, 20);
		submit.addActionListener(this);  // calls actionPerformed of this class
		window.add(submit);
		
		
		window.setSize(600,400);
		window.setBounds(400, 200, 900, 400);
		window.setLayout(null);
		window.setVisible(true);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);

		// call method to connect to server
		connect(ipAddress, port);
	}

	// connect to server with argument of IP address
	public void connect(String ipAddress, int port){
		try	{
			// create a socket connection to the server
			// the thread immediately sends the client its ID and the client saves it to a variable
			socket = new Socket(ipAddress, port);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
			this.ClientID = Integer.valueOf(in.readInt());
			System.out.println("Hello i am client " + this.ClientID);
		} 
		catch(IOException ioException){
			System.out.println(ioException);
		}
		
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
			buzz.setEnabled(false);
			submit.setEnabled(true);
            options[0].setEnabled(true);
            options[1].setEnabled(true);
            options[2].setEnabled(true);
            options[3].setEnabled(true);
			byte[] buf = null;
			String message = ClientID+","+questionNumber;
			buf = message.getBytes();
			InetAddress ip = null;
			try {
				ip = InetAddress.getLocalHost();
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
        	if(correctAnswer) scoreCount+=10;
            else scoreCount-=10;
            score.setText("SCORE: "+scoreCount);
            buzz.setEnabled(true);
			submit.setEnabled(false);
            options[0].setEnabled(false);
            options[1].setEnabled(false);
            options[2].setEnabled(false);
            options[3].setEnabled(false);
		} else if(input.equals(optionsText[0])){
			if(answerIndex==0) correctAnswer = true;
            else correctAnswer = false;
		} else if(input.equals(optionsText[1])){
			if(answerIndex==1) correctAnswer = true;
            else correctAnswer = false;
		} else if(input.equals(optionsText[2])){
			if(answerIndex==2) correctAnswer = true;
            else correctAnswer = false;
		} else if(input.equals(optionsText[3])){
			if(answerIndex==3) correctAnswer = true;
            else correctAnswer = false;
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
