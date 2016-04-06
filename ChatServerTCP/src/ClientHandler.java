import java.net.Socket;

public class ClientHandler implements Runnable {

	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";
	
	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";
	
	private Socket socket;
	
	public ClientHandler(Socket s) {
		this.socket = s;
	}
// send, receive, close, check	
	public void run() {
		
	}

}
