package chat7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class MultiServer {

	static ServerSocket serverSocket = null;
	static Socket socket = null;
	HashMap<String, PrintWriter> clientMap;


	public MultiServer() {
		clientMap = new HashMap<String, PrintWriter>();
		Collections.synchronizedMap(clientMap);
	}

	public void init() {

		try {
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");

			while(true) {
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress()+":"+socket.getPort());
				Thread mst = new MultiServerT(socket);
				mst.start();
			}
		}
		catch (Exception e) {
			System.out.println("예외 : "+e);
		}
		finally {
			try {
				serverSocket.close();
			}
			catch (Exception e) {
				System.out.println("예외 : "+e);
			}
		}
	}


	public static void main(String[] args) {
		MultiServer ms = new MultiServer();
		ms.init();
	}

	public void sendAllMsg(String name, String msg) {
		Iterator<String> it = clientMap.keySet().iterator();
		while(it.hasNext()) {
			try {
				PrintWriter it_out = 
						(PrintWriter) clientMap.get(it.next());
				if(name.equals("")) {
					it_out.println(URLEncoder.encode(msg, "UTF-8"));
				}
				else {
					it_out.println("["+ name +"] "+ msg);
				}
			}
			catch(Exception e) {
				System.out.println("예외 : "+e);
			}
		}
	}


	public void NowUser(String name) {
		PrintWriter it_out = (PrintWriter) clientMap.get(name);

		Set<String> key = clientMap.keySet();
		it_out.println("현재 접속자");
		for (Iterator<String> iterator = key.iterator(); iterator.hasNext();) {
			String keyName = (String) iterator.next();
			it_out.println(keyName);
		}
	}

	//명령어 판단
	public void order(String name, String msg) {

		if (msg.indexOf("/list") != -1) {
			System.out.println("/list 명령어");
			NowUser(name);
		}
		else {
			return;
		}
	}


	class MultiServerT extends Thread {

		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;

		public MultiServerT(Socket socket) {
			this.socket = socket;
			try {
				out = new PrintWriter(this.socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
			}
			catch (Exception e) {
				System.out.println("예외 : "+ e);
			}
		}

		@Override
		public void run() {
			String name = "";
			String s = "";

			try {
				name = URLDecoder.decode(in.readLine(), "UTF-8");

				sendAllMsg("", name + "님이 입장하셨습니다.");

				clientMap.put(name, out);

				System.out.println(name + "접속");
				System.out.println("현재 접속자 수는 "+ clientMap.size()+"명 입니다.");

				// 입력한 메세지는 모든 클라이언트에게 Echo된다. 
				while (in!=null) {
					s = URLDecoder.decode(in.readLine(), "UTF-8");
					if ( s == null )
						break;

					System.out.println(name + " >> " + s);

					if (s.indexOf("/") !=0) {
					}
					else if (s.indexOf("/")==0) {
						order(name,s);
					}
				}
			}
			catch (Exception e) {
				System.out.println("예외 :"+ e);
			}
			finally {
				clientMap.remove(name);
				sendAllMsg("", name + "님이 퇴장하셨습니다."); 
				System.out.println(name + " [" + Thread.currentThread().getName() +  "] 퇴장");
				System.out.println("현재 접속자 수는 "+clientMap.size()+"명 입니다.");

				try {
					in.close();
					out.close();
					socket.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
