import java.io.FileNotFoundException;

public class ClientWindowTest {
    public static void main(String[] args) throws FileNotFoundException{
		Integer ClientID = 1;
		String ipAddress = "localhost";
		int port = 5000;
		ClientWindow window = new ClientWindow(ClientID, ipAddress, port);
	}
}
