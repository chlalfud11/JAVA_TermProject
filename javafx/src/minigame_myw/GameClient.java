package minigame_myw;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class GameClient extends Application {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private TextArea chatArea;
    private TextField nicknameField;
    private Button enterButton;
    private Button playMiniGameButton;
    private Button characterStatusButton;
    private Button personalityStatusButton;
    private Button takePersonalityTestButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My Nature - 메인 메뉴");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;"); // 여백 추가

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.getStyleClass().add("text-area"); // CSS 스타일 추가

        nicknameField = new TextField();
        nicknameField.setPromptText("닉네임 입력");
        nicknameField.getStyleClass().add("text-field"); // CSS 스타일 추가

        enterButton = new Button("입장");
        enterButton.getStyleClass().add("button"); // CSS 스타일 추가
        enterButton.setOnAction(e -> enterGame());

        playMiniGameButton = new Button("미니게임 진행");
        playMiniGameButton.getStyleClass().add("button"); // CSS 스타일 추가
        playMiniGameButton.setOnAction(e -> playMiniGame());
        playMiniGameButton.setDisable(true); // 초기에는 비활성화

        characterStatusButton = new Button("캐릭터 성장 정보");
        characterStatusButton.getStyleClass().add("button"); // CSS 스타일 추가
        characterStatusButton.setOnAction(e -> requestCharacterStatus());
        characterStatusButton.setDisable(true); // 초기에는 비활성화

        personalityStatusButton = new Button("성향 검사 정보");
        personalityStatusButton.getStyleClass().add("button"); // CSS 스타일 추가
        personalityStatusButton.setOnAction(e -> requestPersonalityStatus());
        personalityStatusButton.setDisable(true); // 초기에는 비활성화

        takePersonalityTestButton = new Button("성향 검사 진행");
        takePersonalityTestButton.getStyleClass().add("button"); // CSS 스타일 추가
        takePersonalityTestButton.setOnAction(e -> takePersonalityTest());
        takePersonalityTestButton.setDisable(true); // 초기에는 비활성화

        layout.getChildren().addAll(nicknameField, enterButton, playMiniGameButton, characterStatusButton, personalityStatusButton, takePersonalityTestButton, chatArea);
        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/javaProject/style1.css").toExternalForm()); // CSS 파일 추가
        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 서버와의 통신 스레드 시작
            new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        chatArea.appendText(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enterGame() {
        String nickname = nicknameField.getText();
        if (!nickname.isEmpty()) {
            out.println(nickname);
            playMiniGameButton.setDisable(false);
            characterStatusButton.setDisable(false);
            personalityStatusButton.setDisable(false);
            takePersonalityTestButton.setDisable(false);
            nicknameField.setDisable(true);
            enterButton.setDisable(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "닉네임을 입력하세요!");
            alert.showAndWait();
        }
    }

    private void playMiniGame() {
        out.println("PLAY_MINIGAME");
    }

    private void requestCharacterStatus() {
        out.println("GET_CHARACTER_STATUS");
    }

    private void requestPersonalityStatus() {
        out.println("GET_PERSONALITY_STATUS");
    }

    private void takePersonalityTest() {
        out.println("TAKE_PERSONALITY_TEST");
    }

    @Override
    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}