import java.util.Scanner;

public class WebServer {
	
	/**
	 * Main that starts the server and waits for any keyboard input to shutdown the server.
	 * @param args
	 */
	public static void main(String[] args){
		
		AcceptConnections ac = new AcceptConnections(1888);
		Thread t1 = new Thread(ac);
		t1.start();
		
		System.out.println("Type anything to shutdown the server");
		Scanner scan = new Scanner(System.in);
		scan.next();
		scan.close();
		
		ac.shutDown();
		
		try {
			t1.join();
		} catch (InterruptedException e) {
			System.out.println("Could not join on t1");
			e.printStackTrace();
		}
		
		System.out.println("The web server is shut down");
	}
}
