package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatAppClient {
	private String userName;

	public void establishConnection() throws UnknownHostException, IOException {
		Socket connection = new Socket("localhost", 5000);
		BufferedReader clientInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		PrintWriter clientOutput = new PrintWriter(connection.getOutputStream(), true);

		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter your name: ");
		this.userName = scanner.nextLine();
		clientOutput.println(this.userName);
		System.out.print("\nYou are now connected.\n\n");

		String message = "";
		String response = "";

		do {
			System.out.print("Enter a message: ");
			message = scanner.nextLine();
			if (message.toLowerCase().equals("exit"))
				break;
			clientOutput.println(message);
			response = clientInput.readLine();
			System.out.println(response);
		} while (true);

		connection.close();
	}

	public static void main(String... strings) throws UnknownHostException, IOException {
		new ChatAppClient().establishConnection();
	}
}
