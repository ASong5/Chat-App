/**********************************************
Workshop #9
Course:JAC444 - Semester 4
Last Name:Song
First Name:Andrew
ID:117301184
Section:NAA
This assignment represents my own work in accordance with Seneca Academic Policy.
Signature Andrew Song
Date:08/04/20
**********************************************/


package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Server extends Application {
	// declare member variables
	TextArea textArea = new TextArea();
	ArrayList<Socket> clientSockets;
	ArrayList<String> clients;

	// Inner thread class for multi-thread socket connections
	public class MultiThreadSocket implements Runnable {
		private Socket socket;
		private String userName;

		public MultiThreadSocket(Socket socket) {
			this.socket = socket;
		}

		@Override
		// responsible for outbound and inbound responses from client socket to server
		// socket
		public void run() {
			String message;
			try {
				textArea.setEditable(false);
				// displays the client's initialization logs
				textArea.appendText("\n\nConnection from Socket[addr=" + socket.getLocalAddress() + ", port="
						+ socket.getPort() + ", localport=" + socket.getLocalPort() + "]");
				// initialize our input stream buffer
				BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// waits for user's name from client socket, and appends it to our master
				// clients list
				this.userName = serverInput.readLine();
				clients.add(this.userName);
				// sends a "user connected" message to all connected client sockets
				for (Socket s : clientSockets) {
					new PrintWriter(s.getOutputStream(), true)
							.println(this.userName + " has connected to the chatroom.");
					// sends a string representation of all connected users with
					// a marked prefix to separate it from regular messages
					new PrintWriter(s.getOutputStream(), true).println("!code:setUserList" + clients.toString());
				}

				// continuously waits for responses from the client socket and sends
				// the message to all connected clients
				while (true) {
					message = serverInput.readLine();
					// if the user disconnects from the chat, or types in "!exit"
					// execute the following block
					if (message.equals("!exit")) {
						System.out.print(message);
						// removes the user from the master clients list
						clients.remove(this.userName);
						textArea.appendText("\n\nDisconnection from Socket[addr=" + socket.getLocalAddress() + ", port="
								+ socket.getPort() + ", localport=" + socket.getLocalPort() + "]");
						// removes the client socket from the connection
						clientSockets.remove(this.socket);
						// closes the socket
						this.socket.close();
						// sends a "user disconnected" message to all connected client sockets
						// and sends the updated client list
						for (Socket s : clientSockets) {
							new PrintWriter(s.getOutputStream(), true)
									.println(this.userName + " has disconnected from the chat.");
							new PrintWriter(s.getOutputStream(), true)
									.println("!code:setUserList" + clients.toString());
						}
						break;
					}

					// sends the received message to all connected client sockets prefixed with the
					// socket owner's name
					for (Socket s : clientSockets) {
						new PrintWriter(s.getOutputStream(), true).println(this.userName + ": " + message);
					}

					// displays the message on the server log
					textArea.appendText("\n\n" + this.userName + ": " + message);

				}
			} catch (Exception e) {
			}
		}

	}

	// initializes the server and constructs/sets the server log window
	public void initServer(Stage stage) throws IOException {
		VBox vBox = new VBox();

		// instantiates the server sockets
		ServerSocket server = new ServerSocket(5000);

		// displays the server's initialization logs
		this.textArea.setText("Chat Server has been started on port " + server.getLocalPort() + " at "
				+ ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));

		vBox.getChildren().add(textArea);
		vBox.setAlignment(Pos.TOP_CENTER);
		Scene scene = new Scene(vBox, 500, 200);
		stage.setScene(scene);
		stage.show();

		// instantiates the master client list and the clientSockets list
		this.clientSockets = new ArrayList<Socket>();
		clients = new ArrayList<String>();

		// spawns a new thread for ever new connected client socket
		new Thread(() -> {
			Socket socket = null;
			while (true) {
				try {
					socket = server.accept();
					// adds the client to the clientSocket list
					this.clientSockets.add(socket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// spawns a new thread for the client connection
				new Thread(new MultiThreadSocket(socket)).start();
			}
		}).start();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("ChatApp Server Logs");

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.exit(0);
			}
		});

		this.initServer(primaryStage);
	}

	public static void main(String... strings) {
		launch(strings);
	}

}
