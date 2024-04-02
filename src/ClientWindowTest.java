import java.io.FileNotFoundException;

public class ClientWindowTest {
    public static void main(String[] args) throws FileNotFoundException{
		String ipAddress = "10.111.142.92";
		int port = 5000;
		ClientWindow window = new ClientWindow(ipAddress, port);
	}
}
