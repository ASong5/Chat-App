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
import java.net.Socket;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ChatApplication extends Application {
	// declare our member variables

	private String userName;

	Socket connection = null;
	BufferedReader clientInput;
	PrintWriter clientOutput;

	TextArea chatWindow;
	TextArea textBox;
	TextArea usersWindow;

	// constructs and sets the main opening scene
	public void mainScreen(Stage stage) {
		VBox vBox = new VBox();
		Label titleLabel = new Label("ChatApp");
		titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
		Label label = new Label("Enter your name: ");
		TextField textBox = new TextField();
		textBox.setMaxHeight(10);
		textBox.setAlignment(Pos.CENTER);
		Button submitBtn = new Button("Connect");
		// stores the user's name
		// calls the .establishConnection method
		submitBtn.setOnAction(e -> {
			this.userName = textBox.getText();
			this.establishConnection(stage);
		});

		vBox.getChildren().addAll(titleLabel, label, textBox, submitBtn);
		vBox.setSpacing(10);
		vBox.setAlignment(Pos.CENTER);

		Scene scene = new Scene(vBox, 400, 200);
		stage.setScene(scene);
		stage.show();
	}

	// establishes the connection between the client socket and the main server
	// socket
	// also constructs the main chat scene and shows it
	public void establishConnection(Stage stage) {
		try {
			// initializes the connection and the stream buffers, and then
			// sends message containing user's name to the main server socket
			connection = new Socket("localhost", 5000);
			this.initStreamBuffers(connection);
			clientOutput.println(this.userName);

		} catch (IOException e) {
		}

		VBox vBox = new VBox();
		VBox vBox2 = new VBox();
		GridPane grid = new GridPane();
		Label usersInChatLabel = new Label("Users in chat:");
		usersInChatLabel.setAlignment(Pos.TOP_CENTER);
		Label titleLabel = new Label("ChatApp");

		usersWindow = new TextArea();
		usersWindow.setEditable(false);
		usersWindow.setMaxWidth(200);

		titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
		this.chatWindow = new TextArea();
		this.chatWindow.setEditable(false);
		this.textBox = new TextArea();
		this.textBox.setMaxHeight(10);
		this.usersWindow.setPrefHeight(350);
		Button sendBtn = new Button("Send");

		vBox.getChildren().addAll(titleLabel, this.chatWindow, this.textBox, sendBtn);
		vBox.setAlignment(Pos.CENTER);
		vBox.setSpacing(10);

		vBox2.getChildren().addAll(usersInChatLabel, usersWindow);

		grid.add(vBox, 0, 0);
		grid.add(vBox2, 1, 0);
		GridPane.setRowSpan(usersInChatLabel, 20);
		grid.setHgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));

		// when the send button is clicked, send the message to the main server socket
		sendBtn.setOnAction(e -> {
			this.clientOutput.println(textBox.getText());
			this.textBox.clear();
		});

		// when the "Enter" key is pressed, send the message to the main server socket
		textBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.ENTER) {
					clientOutput.println(textBox.getText().substring(0, textBox.getLength() - 1));
					textBox.clear();
				}
			}
		});
		stage.setScene(new Scene(grid, 700, 400));
		textBox.requestFocus();

		this.updateChatWindow();

	}

	// initializes our stream buffers for our socket connection to receive and send
	// message
	public void initStreamBuffers(Socket connection) {
		try {
			this.clientInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			this.clientOutput = new PrintWriter(connection.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// spawns a new thread that continuously waits for responses from the main
	// server socket
	// and updates the UI accordingly
	public void updateChatWindow() {
		new Thread(() -> {
			do {
				while (true) {
					try {
						String message = "";
						if ((message = this.clientInput.readLine()).contains("!code:setUserList"))
							// updates the "users in chat" window with the string response from the main
							// server socket
							// formats the users line by line
							this.usersWindow
									.setText(message.substring(18, message.length() - 1).replaceAll(", ", "\n") + "\n");
						else
							chatWindow.appendText(message + "\n");

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} while (true);

		}).start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.mainScreen(primaryStage);

		// when the connection is closed, send a message to the main server socket
		primaryStage.setOnCloseRequest(e -> {
			this.clientOutput.println("!exit");
		});
	}

	public static void main(String... strings) {
		launch(strings);
	}

}
