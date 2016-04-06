import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		// create the server
		Server server = new Server();
		Thread serverThread = new Thread(server);
		// run the server
		serverThread.start();
		
		
		server.close();
		//Scanner sc = new Scanner(System.in);
		//System.out.println(sc.nextLine());
		
	}
	
}
