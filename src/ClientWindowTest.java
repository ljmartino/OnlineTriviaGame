import java.io.FileNotFoundException;

public class ClientWindowTest {
    public static void main(String[] args) throws FileNotFoundException{
		String ipAddress = args[0];
		int port = 5000;
		new ClientWindow(ipAddress, port);
	}
}
