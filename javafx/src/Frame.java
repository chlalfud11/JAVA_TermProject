import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.File;

import minigame_cmr.Gaming;
import minigame_kkw.card;
import minigame_kys.MyNatureGame;
import minigame_myw.BreakoutGame;

public class Frame extends Application {

    private String[] storyLines = {
            "안녕? 내가 누군지 궁금해? 하지만 내가 누구인지는 아무도 몰라. 너의 선택과 행동을 통해 내가 누군인지 알 수 있어. 나를 도와줄래?",
            "내가 깨어나기 위해서는 너의 도움이 필요해! 지금부터 질문을 할게. 네가 이 질문에 답을 하면, 난 조금씩 깨어날 수 있어!"
    };
    private int currentLineIndex = 0;
    private TextArea storyText;

    private String[] MBTILines = {
            "너는 성장하는 데에 있어 혼자만의 시간이 중요하다고 생각해? 아니면 다른 사람들과 함께하는 것이 중요하다고 생각하니?",
            "너는 갈등 상황에서 차분하게 이 상황을 분석하려고 해? 아니면 상대방의 감정을 먼저 생각하려고 하니?"
    };
    private String[][] MBTIOptions = {
            {"혼자만의 시간이 중요해", "사람들과 함께하는 게 중요해"},
            {"차분하게 분석하려고 해", "상대방의 감정을 먼저 생각해"}
    };
    private int currentMBTIIndex = 0;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My Nature");

        // 첫 번째 화면: Start 버튼 화면 설정
        BorderPane startScreen = new BorderPane();
        ImageView backgroundImage = new ImageView(new Image("file:javafx/images/MyNatureLogo.jpeg"));
        backgroundImage.setFitWidth(520);
        backgroundImage.setFitHeight(620);
        startScreen.getChildren().add(backgroundImage);

        Button startButton = new Button();
        startButton.setGraphic(new ImageView(new Image("file:javafx/images/start.jpg")));
        startButton.setStyle("-fx-background-color: transparent;");
        startButton.setOnAction(e -> primaryStage.setScene(createStoryScene(primaryStage)));

        startScreen.setBottom(startButton);
        BorderPane.setAlignment(startButton, Pos.CENTER);

        Scene scene = new Scene(startScreen, 520, 620);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Scene createStoryScene(Stage primaryStage) {
        BorderPane storyScreen = new BorderPane();
        ImageView backgroundImage = new ImageView(new Image("file:javafx/images/storybackground.jpeg"));
        backgroundImage.setFitWidth(520);
        backgroundImage.setFitHeight(620);

        storyText = new TextArea();
        storyText.setFont(loadCustomFont("/font.ttf", 18));
        storyText.setEditable(false);
        storyText.setWrapText(true);

        // 텍스트 상자 스타일 개선
        storyText.setStyle("-fx-control-inner-background: rgba(240, 248, 255, 0.9); " +
                           "-fx-border-color: #5f9ea0; " +
                           "-fx-border-radius: 10; " +
                           "-fx-padding: 10; " +
                           "-fx-text-fill: #333333;");

        applyTypewriterEffect(storyText, storyLines[currentLineIndex++]);

        Button nextButton = new Button("NEXT");
        nextButton.setFont(loadCustomFont("/font.ttf", 16));
        nextButton.setOnAction(e -> showNextStoryLine(primaryStage));

        VBox content = new VBox(20, storyText, nextButton);
        content.setAlignment(Pos.CENTER);
        storyScreen.setCenter(content);

        StackPane root = new StackPane(backgroundImage, storyScreen);
        return new Scene(root, 520, 620);
    }

    private void showNextStoryLine(Stage primaryStage) {
        if (currentLineIndex < storyLines.length) {
            applyTypewriterEffect(storyText, storyLines[currentLineIndex++]);
        } else {
            primaryStage.setScene(createMBTIScene(primaryStage));
        }
    }

    private Scene createMBTIScene(Stage primaryStage) {
        BorderPane MBTIScreen = new BorderPane();
        ImageView backgroundImage = new ImageView(new Image("file:javafx/images/storybackground.jpeg"));
        backgroundImage.setFitWidth(520);
        backgroundImage.setFitHeight(620);

        TextArea MBTIText = new TextArea();
        MBTIText.setFont(loadCustomFont("/font.ttf", 18));
        MBTIText.setEditable(false);
        MBTIText.setWrapText(true);

        // 텍스트 상자 스타일 개선
        MBTIText.setStyle("-fx-control-inner-background: rgba(240, 248, 255, 0.9); " +
                          "-fx-border-color: #5f9ea0; " +
                          "-fx-border-radius: 10; " +
                          "-fx-padding: 10; " +
                          "-fx-text-fill: #333333;");

        applyTypewriterEffect(MBTIText, MBTILines[currentMBTIIndex]);

        Button option1Button = new Button(MBTIOptions[currentMBTIIndex][0]);
        option1Button.setFont(loadCustomFont("/font.ttf", 16));
        Button option2Button = new Button(MBTIOptions[currentMBTIIndex][1]);
        option2Button.setFont(loadCustomFont("/font.ttf", 16));

        option1Button.setOnAction(e -> handleMBTIOption(primaryStage, MBTIText, option1Button, option2Button));
        option2Button.setOnAction(e -> handleMBTIOption(primaryStage, MBTIText, option1Button, option2Button));

        HBox options = new HBox(20, option1Button, option2Button);
        options.setAlignment(Pos.CENTER);

        VBox content = new VBox(20, MBTIText, options);
        content.setAlignment(Pos.CENTER);

        MBTIScreen.setCenter(content);
        StackPane root = new StackPane(backgroundImage, MBTIScreen);
        return new Scene(root, 520, 620);
    }

    private void handleMBTIOption(Stage primaryStage, TextArea MBTIText, Button option1Button, Button option2Button) {
        currentMBTIIndex++;
        if (currentMBTIIndex < MBTILines.length) {
            applyTypewriterEffect(MBTIText, MBTILines[currentMBTIIndex]);
            option1Button.setText(MBTIOptions[currentMBTIIndex][0]);
            option2Button.setText(MBTIOptions[currentMBTIIndex][1]);
        } else {
            primaryStage.setScene(createMiniGameScene(primaryStage));
        }
    }

    private Scene createMiniGameScene(Stage primaryStage) {
        BorderPane nextScreen = new BorderPane();
        ImageView backgroundImage = new ImageView(new Image("file:javafx/images/storybackground.jpeg"));
        backgroundImage.setFitWidth(520);
        backgroundImage.setFitHeight(620);

        Button gamingButton = new Button("첫 번째 게임");
        gamingButton.setFont(loadCustomFont("/font.ttf", 18));
        gamingButton.setOnAction(e -> {
            Stage gamingStage = new Stage();
            new Gaming().start(gamingStage);
        });

        Button cardButton = new Button("두 번째 게임");
        cardButton.setFont(loadCustomFont("/font.ttf", 18));
        cardButton.setOnAction(e -> {
            Stage cardStage = new Stage();
            new card().start(cardStage);
        });

        Button kysButton = new Button("세 번째 게임");
        kysButton.setFont(loadCustomFont("/font.ttf", 18));
        kysButton.setOnAction(e -> {
            Stage myNatureStage = new Stage();
            new MyNatureGame().start(myNatureStage);
        });

        Button mywButton = new Button("네 번째 게임");
        mywButton.setFont(loadCustomFont("/font.ttf", 18));
        mywButton.setOnAction(e -> {
            Stage breakoutStage = new Stage();
            new BreakoutGame().start(breakoutStage);
        });

        GridPane buttonPanel = new GridPane();
        buttonPanel.setHgap(10);
        buttonPanel.setVgap(10);
        buttonPanel.add(gamingButton, 0, 0);
        buttonPanel.add(cardButton, 1, 0);
        buttonPanel.add(kysButton, 0, 1);
        buttonPanel.add(mywButton, 1, 1);
        buttonPanel.setAlignment(Pos.CENTER);

        nextScreen.setCenter(buttonPanel);

        StackPane root = new StackPane(backgroundImage, nextScreen);
        return new Scene(root, 520, 620);
    }

    private Font loadCustomFont(String fontPath, float size) {
        try {
            String fontUrl = getClass().getResource(fontPath).toExternalForm();
            Font customFont = Font.loadFont(fontUrl, size);
            return customFont != null ? customFont : Font.font("Serif", size);
        } catch (Exception e) {
            e.printStackTrace();
            return Font.font("Serif", size);
        }
    }

    private void applyTypewriterEffect(TextArea textArea, String text) {
        textArea.clear();
        Timeline timeline = new Timeline();
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            final int index = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(50 * (i + 1)), e -> {
                builder.append(text.charAt(index));
                textArea.setText(builder.toString());
            }));
        }

        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}