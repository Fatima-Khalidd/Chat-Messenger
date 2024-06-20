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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class HelloApplication extends Application {

    private ArrayList<String> chatHistory = new ArrayList<>();
    private VBox chatBox = new VBox();
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private ArrayList<String> account=new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        GridPane layout = new GridPane();
        stage.setTitle("Chatify");
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setHgap(10);
        layout.setVgap(10);

        Text welcomeText = new Text("Chat with your friends");
        welcomeText.setFont(new Font("Times New Roman", 30));
        layout.add(welcomeText, 1, 0);

        Label userName = new Label("User Name");
        TextField text = new TextField();
        layout.add(userName, 0, 1);
        layout.add(text, 1, 1);

        Label word = new Label("Password");
        layout.add(word, 0, 2);
        PasswordField password = new PasswordField();
        layout.add(password, 1, 2);

        Button signUpButton = new Button("Sign up");
        layout.add(signUpButton, 1, 3);

        Button loginButton = new Button("Log in");
        layout.add(loginButton, 2, 3);

        Button cancelButton = new Button("Cancel");
        layout.add(cancelButton, 3, 3);
        chatBox.setStyle("-fx-background-color: TEAL");
        layout.setStyle("-fx-background-color: TEAL");



        Label alertLabel = new Label();

        Scene scene = new Scene(layout, 500, 400);
        scene.setFill(Color.TEAL);

        stage.setScene(scene);

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Alert alert=new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Cancel");
                alert.setHeaderText("Cancel Log In");
                alert.setContentText("Your Log in is cancelled");
                System.out.println("Log in Cancelled!");
                stage.hide();
            }
        });
        signUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage signUpStage=new Stage();
                Account(signUpStage);
                System.out.println("Sign Up button pressed");
                System.out.println("Account Method called");
            }
        });

           loginButton.setOnAction(new EventHandler<ActionEvent>() {
               @Override
               public void handle(ActionEvent actionEvent) {
                   System.out.println("Log in button clicked!");

                   alertLabel.setText(userName.getText());

                   AnchorPane textLayout = new AnchorPane();
                   TextArea chat = new TextArea();
                   chat.setPrefWidth(310);
                   chat.setPrefHeight(30);
                   chat.setEditable(true);
                   textLayout.setBottomAnchor(chat, 5.0);
                   textLayout.setLeftAnchor(chat, 5.0);
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

                   // connecting to server
                   String serverIP = "localhost";
                   int serverPort = 3232;
                   try {
                       Socket socket = new Socket(serverIP, serverPort);
                       System.out.println("Connected to server");
                       outputStream = new DataOutputStream(socket.getOutputStream());
                       inputStream = new DataInputStream(socket.getInputStream());

                       // thread for receiving messages from the server
                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               try {
                                   while (true) {
                                       String serverMessage = inputStream.readUTF();
                                       if (serverMessage != null) {
                                           chatHistory.add("Server: " + serverMessage);
                                           Platform.runLater(new Runnable() {
                                               @Override
                                               public void run() {
                                                   updateChatBox();
                                               }
                                           });
                                       }
                                   }
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           }
                       }).start();
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }

                   stage.setScene(chatScene);
                   stage.show();

                   // event handler for the "Send" button
                   sendButton.setOnAction(new EventHandler<ActionEvent>() {
                       @Override
                       public void handle(ActionEvent actionEvent) {
                           String message = chat.getText();
                           if (!message.isEmpty()) {
                               try {
                                   outputStream.writeUTF(message);
                                   //chatHistory.add("You: " + message);
                                   updateChatBox();
                                   chat.clear();
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           }
                       }
                   });
               }
           });

           stage.show();

    }

    private void updateChatBox() {
        chatBox.getChildren().clear();
        for (String messageInHistory : chatHistory) {
            Label sentLabel = new Label(messageInHistory);
            sentLabel.setWrapText(true);
            sentLabel.setAlignment(Pos.TOP_LEFT);
            chatBox.getChildren().add(sentLabel);


        }
    }

//  account method
public void Account(Stage signUpStage) {
    VBox layout2 = new VBox();
    Label username = new Label("User Name");
    TextField text2 = new TextField();
    layout2.getChildren().add(username);
    layout2.getChildren().add(text2);

    // password
    Label Password1 = new Label("Password");
    PasswordField password2 = new PasswordField();
    layout2.getChildren().add(Password1);
    layout2.getChildren().add(password2);

    Label password3 = new Label("Re-enter Password");
    PasswordField passwordText = new PasswordField();
    layout2.getChildren().add(password3);
    layout2.getChildren().add(passwordText);

    // email input
    Label email = new Label("Email");
    TextField mail = new TextField();
    layout2.getChildren().add(email);
    layout2.getChildren().add(mail);

    // account button
    Button createAccount = new Button("Create Account");
    layout2.getChildren().add(createAccount);

    Scene signUpScene = new Scene(layout2, 400, 500);
    signUpStage.setTitle("Create Account");
    signUpStage.setScene(signUpScene);
    layout2.setAlignment(Pos.CENTER);
    layout2.setStyle("-fx-background-color: Teal");

    createAccount.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (!password2.getText().equals(passwordText.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Passwords do not match");
                alert.setContentText("Kindly enter same Passwords");
                alert.showAndWait();
            } else {
                String username = text2.getText();
                String password = password2.getText();
                String email = mail.getText();

                // adding contents to arrayList

                String[] accountData = new String[]{username, password, email};

                //saving content to file

                File file = new File("data.txt");
                try (FileWriter fr = new FileWriter(file, true)) {
                    fr.write(username + "," + password + "," + mail + "\n");
                    fr.flush();
                } catch (IOException e) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("ERROR");
                    alert1.setHeaderText("Could not Create an Account");
                    alert1.setContentText("An Error occured creating an Account");
                    throw new RuntimeException(e);
                }
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Success");
                alert2.setHeaderText("Account Created");
                alert2.setContentText("Account has Successfully been Created! yayyys!");

                // clear inputs
                text2.clear();
                password2.clear();
                passwordText.clear();
                mail.clear();

                // signUpStage.close();
            }
        }
    });

    signUpStage.show();
}

//    private boolean validateLogin(String username, String password) throws IOException {
//        FileReader reader = new FileReader("data.txt"); // Replace with your file path
//        BufferedReader bufferedReader = new BufferedReader(reader); // Use BufferedReader for line-by-line reading
//
//        String line;
//
//        // Read lines until EOF
//        while ((line = bufferedReader.readLine()) != null) {
//
//            String[] credentials = line.split(",");
//            if (credentials.length == 2 &&
//                    username.equals(credentials[0]) && password.equals(credentials[1])) {
//                bufferedReader.close();
//                return true;
//            }
//        }
//
//        bufferedReader.close();
//        return false;
//    }


    public static void main(String[] args) {
        launch(args);
    }
}
