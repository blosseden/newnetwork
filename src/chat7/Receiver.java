package chat7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;

public class Receiver extends Thread {

	Socket socket;
	BufferedReader in = null;

	public Receiver(Socket socket) {
		this.socket = socket;

		try {
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
		}
		catch (Exception e) {
			System.out.println("����>Receiver>������ : "+ e);
		}
	}

	@Override
	public void run() {

		while(in != null) {
			try {
				System.out.println(URLDecoder.decode (in.readLine(), "UTF-8"));
			}
			catch(SocketException ne) {
				System.out.println("SocketException");
				break;
			}
			catch (Exception e) {
				System.out.println("����>Receiver>run1:"+ e);
			}
		}

		try {
			in.close();
		}
		catch (Exception e) {
			System.out.println("����>Receiver>run2:"+ e);
		}
	}
}