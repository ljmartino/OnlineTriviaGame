import java.io.FileNotFoundException;

public class ClientWindowTest {
    public static void main(String[] args) throws FileNotFoundException{
		//Lukas IP: 10.111.142.92
		String ipAddress = "localhost";
		int port = 5000;
		new ClientWindow(ipAddress, port);
	}
}
