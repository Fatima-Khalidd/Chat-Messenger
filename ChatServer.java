package chat.ui.sourcecode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer extends Application {

    private static List<ClientHandler> clients = new ArrayList<>();
    private static VBox chatBox = new VBox();
    private static ArrayList<String> chatHistory = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Chat Server");

        AnchorPane textLayout = new AnchorPane();
        TextArea chat = new TextArea();
        chat.setPrefWidth(400);
        chat.setPrefHeight(30);
        chat.setEditable(true);
        chat.setStyle("-fx-font: Times New Roman,14");
        textLayout.setBottomAnchor(chat,5.0);
        textLayout.setLeftAnchor(chat,5.0);
        textLayout.getChildren().add(chat);

        // send button
        Button sendButton = new Button("Send");
        sendButton.setPrefSize(60, 30);
        textLayout.setBottomAnchor(sendButton, 10.0);
        textLayout.setRightAnchor(sendButton, 20.0);
        textLayout.getChildren().add(sendButton);

        chatBox = new VBox();
        ScrollPane scrollPane = new ScrollPane(chatBox);
        textLayout.setTopAnchor(scrollPane, 10.0);
        textLayout.setLeftAnchor(scrollPane, 10.0);
        textLayout.setRightAnchor(scrollPane, 10.0);
        textLayout.setBottomAnchor(scrollPane, 50.0);
        textLayout.getChildren().add(scrollPane);

        Scene chatScene = new Scene(textLayout, 400, 500);
        chatScene.setFill(Color.BEIGE);
        textLayout.setStyle("-fx-background-color: TEAL");

        stage.setScene(chatScene);
        stage.show();

        // Start the server in a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        }).start();

        // event handler for the "Send" button
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

               String message = chat.getText();
                if (!message.isEmpty()) {
                    broadcastMessage(message);
                    chatHistory.add("You: " + message);
                    updateChatBox();
                    chat.clear();
                }
            }
        });
    }

    private void startServer() {
        int port = 3232;


        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                ClientHandler clientHandler = new ClientHandler(socket, inputStream, outputStream);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        chatHistory.add(message);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateChatBox();
            }
        });

    }

    private  void updateChatBox() {
        chatBox.getChildren().clear();
        for (String messageInHistory : chatHistory) {
            Label sentLabel = new Label(messageInHistory);
            sentLabel.setWrapText(true);
            chatBox.setAlignment(Pos.TOP_RIGHT);
            chatBox.getChildren().add(sentLabel);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = inputStream.readUTF();
                if (message != null) {
                    System.out.println("Received: " + message);
                    ChatServer server =new ChatServer();
                    server.broadcastMessage("Client: " + message);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        outputStream.writeUTF(message);
    }
}
