import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ProcessConnection implements Runnable{

	Socket workerSocket;
	BufferedReader br;
	BufferedWriter bw;
	
	@Override
	public void run() {
		process();
	}
	
	/**
	 * Constructor, assigns the workerSocket.
	 * @param workerSocket the agrees socket to communicate on.
	 */
	public ProcessConnection(Socket workerSocket){
		this.workerSocket = workerSocket;
		try {
			this.workerSocket.setSoTimeout(500);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads in the input from the http request and sends out a simple response. 
	 */
	public void process(){
		try{
			br = new BufferedReader(new InputStreamReader(this.workerSocket.getInputStream()));
			String line = br.readLine();
			String[] words = line.split(" ");
			
			while(!line.isEmpty()){
                line = br.readLine();
			}
			
			switch (words[0]){
				case "GET":
				case "POST":
				case "PUT":
				case "DELETE":
					sendResponse();
					break;
				default:
					break;
			}
			
			br.close();
			workerSocket.close();
			
		}catch(IOException ee){
			System.out.println("Connection time out");
		}catch(Exception e){
			System.out.println("Process Exception: " + e.getMessage());
			try {
				workerSocket.close();
				br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends a simple http response
	 */
	public void sendResponse(){
		try {
			bw = new BufferedWriter(new OutputStreamWriter(this.workerSocket.getOutputStream()));
			String html = "<html><body><h1>Welcome!</h1></body></html>";
			bw.write("HTTP/1.1 200 OK\r\n");
			bw.write(getServerTime() + "\r\n");
			bw.write("Server: Java server\r\n");
			bw.write("Last-Modified: Tue, 22 Mar 2016 19:15:56 GMT\r\n");
			bw.write("Content-Length: " + html.getBytes().length + "\r\n");
			bw.write("Content-Type: text/html\r\n");
			bw.write("Connection: Closed\r\n");
			bw.write("\r\n");
			bw.write(html);
			bw.flush();
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			try {
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}
	
	/**
	 * Generates the formatted date for a http response.
	 * @return String formatted date
	 */
	public String getServerTime() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat dateFormat = new SimpleDateFormat(
	        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
	    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	    return dateFormat.format(cal.getTime());
	}
}
