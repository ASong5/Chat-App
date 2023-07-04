package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MultiThreadSocket implements Runnable {
	private Socket socket;
	private String userName;

	public MultiThreadSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		String message = "";
		try {
			System.out.println("Connection from Socket[addr=" + socket.getLocalAddress() + ", port=" + socket.getPort()
					+ ", localport=" + socket.getLocalPort() + "]");
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);

			this.userName = serverInput.readLine();

			while (true) {
				message = serverInput.readLine();
				if (message.equals("exit")) {
					this.socket.close();
					break;

				}
				System.out.println(this.userName + ": " + message);

			}
		} catch (Exception e) {
		}
	}

}
