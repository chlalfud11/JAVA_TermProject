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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import minigame_cmr.Gaming;
import minigame_kkw.card;
import minigame_kys.MyNatureGame;
import minigame_myw.BreakoutGame;

import utils.character_result;

public class Frame extends Application {

    private String[] storyLines = {
            "안녕? 내가 누군지 궁금해? 하지만 내가 누구인지는 아무도 몰라. 너의 선택과 행동을 통해 내가 누군인지 알 수 있어. 나를 도와줄래?",
            "내가 깨어나기 위해서는 너의 도움이 필요해! 지금부터 질문을 할게. 네가 이 질문에 답을 하면, 난 조금씩 깨어날 수 있어!"
    };
    private int currentLineIndex = 0;
    private TextArea storyText;

    private String[] MBTILines = {
            "너는 갈등 상황에서 차분하게 이 상황을 분석하려고 해? 아니면 상대방의 감정을 먼저 생각하려고 하니?",
            "너는 성장하는 데에 있어 혼자만의 시간이 중요하다고 생각해? 아니면 다른 사람들과 함께하는 것이 중요하다고 생각하니?"
    };
    private String[][] MBTIOptions = {
    		{"차분하게 분석하려고 해", "상대방의 감정을 먼저 생각해"},
            {"혼자만의 시간이 중요해", "사람들과 함께하는 게 중요해"}
    };
    private int currentMBTIIndex = 0;
    
    private MediaPlayer bgmPlayer; // 배경음악 MediaPlayer 추가

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My Nature");
        
        // 배경음악 초기화 및 재생
        initializeBackgroundMusic();

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
    
    private void initializeBackgroundMusic() {
        try {
            // 배경음악 파일 경로
            String bgmPath = getClass().getResource("/MyNature_BGM.mp3").toExternalForm();
            Media bgmMedia = new Media(bgmPath);
            bgmPlayer = new MediaPlayer(bgmMedia);

            // 무한 반복 재생 설정
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.setVolume(0.5); // 음량 조절 (0.0 ~ 1.0)
            bgmPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("배경음악 파일을 로드할 수 없습니다. 경로를 확인하세요.");
        }
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

        // 이미지 추가
        ImageView contentImage = new ImageView(new Image("file:javafx/images/1st.png"));
        contentImage.setFitWidth(200);
        contentImage.setFitHeight(200);

        VBox content = new VBox(20, storyText, contentImage, nextButton);
        content.setAlignment(Pos.CENTER);
        storyScreen.setCenter(content);

        StackPane root = new StackPane(backgroundImage, storyScreen);
        return new Scene(root, 520, 620);
    }

    private void showNextStoryLine(Stage primaryStage) {
        if (currentLineIndex < storyLines.length) {
            applyTypewriterEffect(storyText, storyLines[currentLineIndex++]);
        } else {
            primaryStage.setScene(createMBTIScene(primaryStage)); // 수정된 부분: MBTI 화면으로 전환
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

        // 첫 번째 질문
        Button option1Button = new Button(MBTIOptions[currentMBTIIndex][0]);
        option1Button.setFont(loadCustomFont("/font.ttf", 16));
        Button option2Button = new Button(MBTIOptions[currentMBTIIndex][1]);
        option2Button.setFont(loadCustomFont("/font.ttf", 16));

        option1Button.setOnAction(e -> {
            if (currentMBTIIndex == 0) {
                utils.character_result.incrementT(50); // 첫 번째 질문에서 첫 번째 버튼 선택 시 t +50
            } else if (currentMBTIIndex == 1) {
                utils.character_result.incrementI(50); // 두 번째 질문에서 첫 번째 버튼 선택 시 i +50
            }
            handleMBTIOption(primaryStage, MBTIText, option1Button, option2Button);
        });

        option2Button.setOnAction(e -> {
            if (currentMBTIIndex == 0) {
                utils.character_result.incrementF(50); // 첫 번째 질문에서 두 번째 버튼 선택 시 f +50
            } else if (currentMBTIIndex == 1) {
                utils.character_result.incrementE(50); // 두 번째 질문에서 두 번째 버튼 선택 시 e +50
            }
            handleMBTIOption(primaryStage, MBTIText, option1Button, option2Button);
        });

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
            primaryStage.setScene(createIntermediateScene(primaryStage));
        }
    }

    private Scene createIntermediateScene(Stage primaryStage) {
        BorderPane intermediateScreen = new BorderPane();
        ImageView backgroundImage = new ImageView(new Image("file:javafx/images/storybackground.jpeg"));
        backgroundImage.setFitWidth(520);
        backgroundImage.setFitHeight(620);

        TextArea intermediateText = new TextArea();
        intermediateText.setFont(loadCustomFont("/font.ttf", 18));
        intermediateText.setEditable(false);
        intermediateText.setWrapText(true);

        // 텍스트 상자 스타일
        intermediateText.setStyle("-fx-control-inner-background: rgba(240, 248, 255, 0.9); " +
                                  "-fx-border-color: #5f9ea0; " +
                                  "-fx-border-radius: 10; " +
                                  "-fx-padding: 10; " +
                                  "-fx-text-fill: #333333;");

        String intermediateMessage = "앞으로 우리의 여정은 더 험난해질 거야. 하지만 네가 포기하지 않고 끝까지 나를 도와줬으면 좋겠어. "
                + "앞으로 총 4번의 고비가 찾아올 거야. 하나의 고비를 넘길 때마다, 너는 나를 깨어나게 할 수 있는 기회를 얻을 수 있어! 준비 됐어?";
        applyTypewriterEffect(intermediateText, intermediateMessage);

        Button nextButton = new Button("NEXT");
        nextButton.setFont(loadCustomFont("/font.ttf", 16));
        nextButton.setOnAction(e -> primaryStage.setScene(createMiniGameScene(primaryStage)));

        // 이미지 추가
        ImageView contentImage = new ImageView(new Image("file:javafx/images/1st.png"));
        contentImage.setFitWidth(200);
        contentImage.setFitHeight(200);

        VBox content = new VBox(20, intermediateText, contentImage, nextButton);
        content.setAlignment(Pos.CENTER);
        intermediateScreen.setCenter(content);

        StackPane root = new StackPane(backgroundImage, intermediateScreen);
        return new Scene(root, 520, 620);
    }

    private Scene createMiniGameScene(Stage primaryStage) {
        BorderPane nextScreen = new BorderPane();
        ImageView backgroundImage = new ImageView(new Image("file:javafx/images/storybackground.jpeg"));
        backgroundImage.setFitWidth(520);
        backgroundImage.setFitHeight(620);

        Button gamingButton = new Button("자아의 조각을 찾아라");
        gamingButton.setFont(loadCustomFont("/font.ttf", 18));
        gamingButton.setOnAction(e -> {
            Stage gamingStage = new Stage();
            new Gaming().start(gamingStage);
        });

        Button cardButton = new Button("기억의 조화");
        cardButton.setFont(loadCustomFont("/font.ttf", 18));
        cardButton.setOnAction(e -> {
            Stage cardStage = new Stage();
            new card().start(cardStage);
        });

        Button kysButton = new Button("위험을 피해 성장하라");
        kysButton.setFont(loadCustomFont("/font.ttf", 18));
        kysButton.setOnAction(e -> {
            Stage myNatureStage = new Stage();
            new MyNatureGame().start(myNatureStage);
        });

        Button mywButton = new Button("알의 보호막을 깨라");
        mywButton.setFont(loadCustomFont("/font.ttf", 18));

        // VBox에 버튼 추가
        VBox buttonPanel = new VBox(20, gamingButton, cardButton, kysButton, mywButton);
        buttonPanel.setAlignment(Pos.CENTER);

        // NEXT 버튼을 나중에 추가하기 위해 생성만 해둠
        Button nextButton = new Button("NEXT");
        nextButton.setFont(loadCustomFont("/font.ttf", 16));
        nextButton.setOnAction(e -> primaryStage.setScene(createFinalScene(primaryStage)));
        nextButton.setVisible(false); // 처음에는 보이지 않도록 설정

        // 네 번째 버튼 클릭 시 NEXT 버튼 표시
        mywButton.setOnAction(e -> {
            Stage breakoutStage = new Stage();
            new BreakoutGame().start(breakoutStage);
            nextButton.setVisible(true); // NEXT 버튼 표시
        });

        VBox bottomPanel = new VBox(nextButton);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-padding: 10 0 20 0;");

        nextScreen.setCenter(buttonPanel);
        nextScreen.setBottom(bottomPanel);

        StackPane root = new StackPane(backgroundImage, nextScreen);
        return new Scene(root, 520, 620);
    }
    
    private Scene createFinalScene(Stage primaryStage) {
        BorderPane finalScreen = new BorderPane();
        ImageView backgroundImage = new ImageView(new Image("file:javafx/images/storybackground.jpeg"));
        backgroundImage.setFitWidth(520);
        backgroundImage.setFitHeight(620);

        TextArea finalText = new TextArea();
        finalText.setFont(loadCustomFont("/font.ttf", 18));
        finalText.setEditable(false);
        finalText.setWrapText(true);

        // 텍스트 상자 스타일
        finalText.setStyle("-fx-control-inner-background: rgba(240, 248, 255, 0.9); " +
                           "-fx-border-color: #5f9ea0; " +
                           "-fx-border-radius: 10; " +
                           "-fx-padding: 10; " +
                           "-fx-text-fill: #333333;");

        // 타자기 효과로 텍스트 출력
        applyTypewriterEffect(finalText, "정말 대단해! 지금까지 여러 고난이 있었지만, 너 덕분에 잘 헤쳐나갈 수 있었어.\n"
                + "난 곧 깨어날 거야! 내 모습이 어떤지 궁금하지 않아?");

        // 이미지 추가
        ImageView finalImage = new ImageView(new Image("file:javafx/images/3rd.png"));
        finalImage.setFitWidth(300);
        finalImage.setFitHeight(200);

        // NEXT 버튼 추가
        Button nextButton = new Button("NEXT");
        nextButton.setFont(loadCustomFont("/font.ttf", 16));
        nextButton.setOnAction(e -> primaryStage.setScene(createResultScene(primaryStage))); // 새로운 화면으로 전환

        VBox content = new VBox(20, finalText, finalImage, nextButton);
        content.setAlignment(Pos.CENTER);
        finalScreen.setCenter(content);

        StackPane root = new StackPane(backgroundImage, finalScreen);
        return new Scene(root, 520, 620);
    }

    private Scene createFinalFarewellScene(Stage primaryStage, ImageView resultImage) {
        BorderPane farewellScreen = new BorderPane();
        ImageView backgroundImage = new ImageView(new Image("file:javafx/images/space_background.png"));
        backgroundImage.setFitWidth(520);
        backgroundImage.setFitHeight(620);

        TextArea farewellText = new TextArea();
        farewellText.setFont(loadCustomFont("/font.ttf", 18));
        farewellText.setEditable(false);
        farewellText.setWrapText(true);

        // 텍스트 상자 스타일 (우주 테마 색상 적용)
        farewellText.setStyle("-fx-control-inner-background: rgba(25, 25, 112, 0.9); " +
                              "-fx-border-color: #4682b4; " +
                              "-fx-border-radius: 10; " +
                              "-fx-padding: 10; " +
                              "-fx-text-fill: #ffffff;");

        // 타자기 효과로 텍스트 출력
        String farewellMessage = "난 사실 지구가 아니라 다른 별에서 왔어. 나는 이제 내가 태어난 곳으로 돌아가려고 해! "
                               + "너를 만나서 난 정말 행복했어. 다음에 기회가 된다면 또 만나자!";
        applyTypewriterEffect(farewellText, farewellMessage);

        // NEXT 버튼 추가
        Button nextButton = new Button("FINISH");
        nextButton.setFont(loadCustomFont("/font.ttf", 16));
        nextButton.setOnAction(e -> primaryStage.close()); // 프로그램 종료

        VBox content = new VBox(20, farewellText, resultImage, nextButton);
        content.setAlignment(Pos.CENTER);
        farewellScreen.setCenter(content);

        StackPane root = new StackPane(backgroundImage, farewellScreen);
        return new Scene(root, 520, 620);
    }

    private Scene createResultScene(Stage primaryStage) {
        BorderPane resultScreen = new BorderPane();
        ImageView backgroundImage = new ImageView(new Image("file:javafx/images/storybackground.jpeg"));
        backgroundImage.setFitWidth(520);
        backgroundImage.setFitHeight(620);

        TextArea resultText = new TextArea();
        resultText.setFont(loadCustomFont("/font.ttf", 18));
        resultText.setEditable(false);
        resultText.setWrapText(true);

        // 텍스트 상자 스타일
        resultText.setStyle("-fx-control-inner-background: rgba(240, 248, 255, 0.9); " +
                            "-fx-border-color: #5f9ea0; " +
                            "-fx-border-radius: 10; " +
                            "-fx-padding: 10; " +
                            "-fx-text-fill: #333333;");

        // 타자기 효과로 텍스트 출력
        applyTypewriterEffect(resultText, "우와! 이게 내 모습이야!! 이건 다 너 덕분이야. 너 덕분에 깨어날 수 있었어!! 고마워!!");

        // 조건에 따라 이미지 결정
        String resultImagePath;
        if (utils.character_result.e > utils.character_result.i) {
            if (utils.character_result.f > utils.character_result.t) {
                resultImagePath = "file:javafx/images/e+f.png";
            } else {
                resultImagePath = "file:javafx/images/e+t.png";
            }
        } else {
            if (utils.character_result.f > utils.character_result.t) {
                resultImagePath = "file:javafx/images/i+f.png";
            } else {
                resultImagePath = "file:javafx/images/i+t.png";
            }
        }

        // 결과 이미지 설정
        ImageView resultImage = new ImageView(new Image(resultImagePath));
        resultImage.setFitWidth(200);
        resultImage.setFitHeight(200);

        Button nextButton = new Button("NEXT");
        nextButton.setFont(loadCustomFont("/font.ttf", 16));
        nextButton.setOnAction(e -> primaryStage.setScene(createFinalFarewellScene(primaryStage, resultImage))); // 새로운 화면으로 전환

        VBox content = new VBox(20, resultText, resultImage, nextButton);
        content.setAlignment(Pos.CENTER);
        resultScreen.setCenter(content);

        StackPane root = new StackPane(backgroundImage, resultScreen);
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
