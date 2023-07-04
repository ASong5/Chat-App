package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import server.MultiThreadSocket;

public class ChatAppServer {
	
	public ChatAppServer() throws IOException {
		initServer();
	}
	
	private void initServer() throws IOException {
		ServerSocket server = new ServerSocket(5000);
		Socket socket = null;
		System.out.println("Chat Server has been started on port " + server.getLocalPort() + " at "
				+ ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));

		while (true) {
			socket = server.accept();
			new Thread(new MultiThreadSocket(socket)).start();
		}

	}

	public static void main(String... strings) throws IOException {
		new ChatAppServer().initServer();
	}
}