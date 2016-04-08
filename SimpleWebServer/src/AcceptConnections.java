import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AcceptConnections implements Runnable{

	private ServerSocket ss;
	private final int port;
	private ExecutorService executor;
	protected boolean running = true;

	@Override
	public void run() {
		acceptConnections();
	}
	
	/**
	 * Constructor, sets the port number and print a start message.
	 * @param port port number used to accept connections on
	 */
	public AcceptConnections(int port){
		this.port = port;
		System.out.println("Starting server on port: " + port);
	}
	
	/**
	 * Shuts down the Server socket that is waiting for connections.
	 */
	public void shutDown(){
		try {
			ss.close();
		} catch (IOException e) {
			System.out.println("Shut Down Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Listens on the set port for new connections. Utilizes a small thread pool to 
	 * process incoming connections. 
	 */
	public void acceptConnections(){
		
		try{
			this.ss = new ServerSocket(this.port);
			System.out.println("Server in now listening on port: " + ss.getLocalPort());
			this.executor = Executors.newFixedThreadPool(3);
			while(running){
				Socket workerSocket = ss.accept();
	            Runnable worker = new ProcessConnection(workerSocket);  
	            executor.execute(worker);//calling execute method of ExecutorService
			}
			executor.shutdownNow();
			ss.close();
			
		}catch(Exception e){
			if(!e.getMessage().equals("Socket closed")){				
				System.out.println("Exception: " + e.getMessage());
			}
			
			if(this.ss != null && !ss.isClosed()){
				try {
					ss.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			if(!executor.isTerminated()){
				executor.shutdownNow();					
			}
		}
	}
}
