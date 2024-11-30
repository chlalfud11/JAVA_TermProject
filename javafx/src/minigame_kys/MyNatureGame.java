package minigame_kys;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MyNatureGame extends Application {
    private Stage gameStage;
    private ImageView player;
    private List<ImageView> meteors = new ArrayList<>();
    private List<ImageView> bigMeteors = new ArrayList<>();
    private List<ImageView> clocks = new ArrayList<>();
    private List<ImageView> heals = new ArrayList<>();
    private Random random = new Random();
    private int remainingTime = 30;
    private int playerHP = 100;
    private boolean gameOver = false;
    private Pane root;
    private AnimationTimer gameTimer;
    private Label timerLabel;
    private Rectangle hpBar;

    @Override
    public void start(Stage primaryStage) {
        this.gameStage = primaryStage;
        root = new Pane();
        root.setStyle("-fx-background-image: url('/images/kys/burning_forest_meteor_background.png'); -fx-background-size: cover;");

        Scene scene = new Scene(root, 400, 600);
        setupGame();

        scene.setOnKeyPressed(event -> {
            if (!gameOver) {
                if (event.getCode() == KeyCode.LEFT && player.getX() > 0) {
                    player.setX(player.getX() - 10);
                } else if (event.getCode() == KeyCode.RIGHT && player.getX() < scene.getWidth() - 40) {
                    player.setX(player.getX() + 10);
                }
            }
        });

        gameTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (!gameOver) {
                    if (now - lastUpdate >= 1_000_000_000) {
                        remainingTime--;
                        updateTimerLabel();
                        lastUpdate = now;

                        if (remainingTime <= 0) {
                            stop();
                            gameOver = true;
                            showWinMessage();
                            return;
                        }
                    }
                    spawnObjects();
                    moveObjects();
                    checkCollisions();
                }
            }
        };

        primaryStage.setScene(scene);
        primaryStage.setTitle("운석 피하기 - 위험을 피해 성장하라");
        showGameInstructions(); // 게임 시작 전 설명 화면 표시
        primaryStage.show();
    }

    private void setupGame() {
        root.getChildren().clear();

        // 플레이어 생성
        player = new ImageView(new Image(getClass().getResource("/images/1st.png").toExternalForm()));
        player.setFitWidth(40);
        player.setFitHeight(40);
        player.setX(190);
        player.setY(570);
        root.getChildren().add(player);

        // HP 바 생성
        hpBar = new Rectangle(100, 10, Color.LIME);
        hpBar.setLayoutX(10);
        hpBar.setLayoutY(10);
        root.getChildren().add(hpBar);

        // 타이머 라벨 설정
        timerLabel = new Label("Time: 30");
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setStyle("-fx-font-size: 18px;");
        timerLabel.setLayoutX(10);
        timerLabel.setLayoutY(30);
        root.getChildren().add(timerLabel);

        meteors.clear();
        bigMeteors.clear();
        clocks.clear();
        heals.clear();
        remainingTime = 30;
        playerHP = 100;
        gameOver = false;
    }

    private void showGameInstructions() {
        Platform.runLater(() -> {
            root.getChildren().clear();

            // 배경 박스 추가
            Rectangle backgroundBox = new Rectangle(360, 350, Color.BLACK);
            backgroundBox.setOpacity(0.7);
            backgroundBox.setX(20);
            backgroundBox.setY(120);
            root.getChildren().add(backgroundBox);

            // 게임 시작 멘트
            Label introLabel = new Label(
                    "우주별에서 운석이 떨어집니다.\n" +
                    "이는 마치 지구에 있어서는 안될 생물이\n" +
                    "지구에 있어, 자연의 섭리를 맞추기 위한\n" +
                    "과정같다고도 생각이 듭니다."
            );
            introLabel.setTextFill(Color.WHITE);
            introLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            introLabel.setLayoutX(40);
            introLabel.setLayoutY(130);
            root.getChildren().add(introLabel);

            // 게임 방법 이미지와 설명 추가
            ImageView clockImage = new ImageView(new Image(getClass().getResource("/images/kys/clock_item.png").toExternalForm()));
            clockImage.setFitWidth(30);
            clockImage.setFitHeight(30);
            clockImage.setX(60);
            clockImage.setY(240);
            root.getChildren().add(clockImage);

            Label clockLabel = new Label("Time -5");
            clockLabel.setTextFill(Color.WHITE);
            clockLabel.setStyle("-fx-font-size: 16px;");
            clockLabel.setLayoutX(100);
            clockLabel.setLayoutY(245);
            root.getChildren().add(clockLabel);

            ImageView smallMeteorImage = new ImageView(new Image(getClass().getResource("/images/kys/simple_item.png").toExternalForm()));
            smallMeteorImage.setFitWidth(30);
            smallMeteorImage.setFitHeight(30);
            smallMeteorImage.setX(60);
            smallMeteorImage.setY(280);
            root.getChildren().add(smallMeteorImage);

            Label smallMeteorLabel = new Label("HP -20");
            smallMeteorLabel.setTextFill(Color.WHITE);
            smallMeteorLabel.setStyle("-fx-font-size: 16px;");
            smallMeteorLabel.setLayoutX(100);
            smallMeteorLabel.setLayoutY(285);
            root.getChildren().add(smallMeteorLabel);

            ImageView bigMeteorImage = new ImageView(new Image(getClass().getResource("/images/kys/death_item.png").toExternalForm()));
            bigMeteorImage.setFitWidth(30);
            bigMeteorImage.setFitHeight(30);
            bigMeteorImage.setX(60);
            bigMeteorImage.setY(310);
            root.getChildren().add(bigMeteorImage);

            Label bigMeteorLabel = new Label("Game Over");
            bigMeteorLabel.setTextFill(Color.WHITE);
            bigMeteorLabel.setStyle("-fx-font-size: 16px;");
            bigMeteorLabel.setLayoutX(100);
            bigMeteorLabel.setLayoutY(315);
            root.getChildren().add(bigMeteorLabel);

            ImageView healImage = new ImageView(new Image(getClass().getResource("/images/kys/health_recovery_icon.png").toExternalForm()));
            healImage.setFitWidth(30);
            healImage.setFitHeight(30);
            healImage.setX(60);
            healImage.setY(350);
            root.getChildren().add(healImage);

            Label healLabel = new Label("HP +10");
            healLabel.setTextFill(Color.WHITE);
            healLabel.setStyle("-fx-font-size: 16px;");
            healLabel.setLayoutX(100);
            healLabel.setLayoutY(355);
            root.getChildren().add(healLabel);

            // 시작 버튼
            Button startButton = new Button("START");
            startButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #FF9800; -fx-text-fill: white;");
            startButton.setLayoutX(160);
            startButton.setLayoutY(420);
            root.getChildren().add(startButton);

            startButton.setOnAction(e -> startGame());
        });
    }

    private void startGame() {
        setupGame();
        gameTimer.start();
    }

    private void spawnObjects() {
        if (random.nextInt(40) == 0) {
            ImageView meteor = new ImageView(new Image(getClass().getResource("/images/kys/simple_item.png").toExternalForm()));
            meteor.setFitWidth(20);
            meteor.setFitHeight(20);
            meteor.setX(random.nextDouble() * 380);
            meteor.setY(0);
            meteors.add(meteor);
            root.getChildren().add(meteor);
        }

        if (random.nextInt(100) == 0) {
            ImageView bigMeteor = new ImageView(new Image(getClass().getResource("/images/kys/death_item.png").toExternalForm()));
            bigMeteor.setFitWidth(40);
            bigMeteor.setFitHeight(40);
            bigMeteor.setX(random.nextDouble() * 380);
            bigMeteor.setY(0);
            bigMeteors.add(bigMeteor);
            root.getChildren().add(bigMeteor);
        }

        if (random.nextInt(150) == 0) {
            ImageView clock = new ImageView(new Image(getClass().getResource("/images/kys/clock_item.png").toExternalForm()));
            clock.setFitWidth(20);
            clock.setFitHeight(20);
            clock.setX(random.nextDouble() * 380);
            clock.setY(0);
            clocks.add(clock);
            root.getChildren().add(clock);
        }

        if (random.nextInt(150) == 0) {
            ImageView heal = new ImageView(new Image(getClass().getResource("/images/kys/health_recovery_icon.png").toExternalForm()));
            heal.setFitWidth(20);
            heal.setFitHeight(20);
            heal.setX(random.nextDouble() * 380);
            heal.setY(0);
            heals.add(heal);
            root.getChildren().add(heal);
        }
    }

    private void moveObjects() {
        moveCircles(meteors, 3);
        moveCircles(bigMeteors, 2);
        moveCircles(clocks, 3);
        moveCircles(heals, 3);
    }

    private void moveCircles(List<ImageView> images, int speed) {
        for (Iterator<ImageView> iterator = images.iterator(); iterator.hasNext(); ) {
            ImageView image = iterator.next();
            image.setY(image.getY() + speed);

            if (image.getY() > 600) {
                iterator.remove();
                root.getChildren().remove(image);
            }
        }
    }

    private void checkCollisions() {
        if (gameOver) return;

        checkCollision(meteors, 20, true);
        checkCollision(bigMeteors, playerHP, true);
        checkCollision(clocks, -5, false);
        checkCollision(heals, 10, false);
    }

    private void checkCollision(List<ImageView> objects, int impact, boolean isDamage) {
        for (Iterator<ImageView> iterator = objects.iterator(); iterator.hasNext(); ) {
            ImageView object = iterator.next();
            if (object.getBoundsInParent().intersects(player.getBoundsInParent())) {
                if (isDamage) {
                    playerHP = Math.max(0, playerHP - impact);
                    updateHPBar();
                    if (playerHP <= 0) {
                        gameTimer.stop();
                        gameOver = true;
                        showGameOverScreen();
                        return;
                    }
                } else {
                    if (impact < 0) remainingTime = Math.max(0, remainingTime + impact);
                    else playerHP = Math.min(100, playerHP + impact);
                    updateHPBar();
                }
                iterator.remove();
                root.getChildren().remove(object);
            }
        }
    }

    private void updateHPBar() {
        hpBar.setWidth(playerHP);
        if (playerHP > 60) {
            hpBar.setFill(Color.LIME);
        } else if (playerHP > 30) {
            hpBar.setFill(Color.ORANGE);
        } else {
            hpBar.setFill(Color.RED);
        }
    }

    private void updateTimerLabel() {
        timerLabel.setText("Time: " + remainingTime);
    }

    private void showGameOverScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("게임 오버");
            alert.setHeaderText("운석 피하기 실패!");
            alert.setContentText("운석에 맞았습니다. 다시 도전해보세요!");
            alert.showAndWait();
            resetGame();
        });
    }

    private void showWinMessage() {
        Platform.runLater(() -> {
            root.getChildren().clear();

            // 배경 박스 추가
            Rectangle backgroundBox = new Rectangle(360, 150, Color.BLACK);
            backgroundBox.setOpacity(0.7);
            backgroundBox.setX(20);
            backgroundBox.setY(180);
            root.getChildren().add(backgroundBox);

            // 성공 메시지
            Label winMessage = new Label("대단해! 너 덕분에 내가\n성장할 것 같은 기분이 들어.\n이제 너에게 질문을 할게.");
            winMessage.setTextFill(Color.WHITE);
            winMessage.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            winMessage.setLayoutX(40);
            winMessage.setLayoutY(200);
            root.getChildren().add(winMessage);

            Button nextButton = new Button("NEXT");
            nextButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #FF9800; -fx-text-fill: white;");
            nextButton.setLayoutX(160);
            nextButton.setLayoutY(350);
            root.getChildren().add(nextButton);

            nextButton.setOnAction(e -> showFinalQuestion());
        });
    }

    private void showFinalQuestion() {
        Platform.runLater(() -> {
            root.getChildren().clear();

            // 배경 박스 추가
            Rectangle backgroundBox = new Rectangle(360, 200, Color.BLACK);
            backgroundBox.setOpacity(0.7);
            backgroundBox.setX(20);
            backgroundBox.setY(100);
            root.getChildren().add(backgroundBox);

            Label questionLabel = new Label("쾅💥☄ 하늘에서 운석이 떨어져서\n친구가 심하게 다쳤다\n");
            questionLabel.setTextFill(Color.WHITE);
            questionLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            questionLabel.setLayoutX(40);
            questionLabel.setLayoutY(120);
            root.getChildren().add(questionLabel);

            Button choice1 = new Button("그래도 죽을 정도는 아니라 다행이다");
            choice1.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
            choice1.setLayoutX(50);
            choice1.setLayoutY(250);
            root.getChildren().add(choice1);

            Button choice2 = new Button("아 어떡해…. 친구야 괜찮아?ㅠㅠㅠㅠㅠ");
            choice2.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: #2196F3; -fx-text-fill: white;");
            choice2.setLayoutX(50);
            choice2.setLayoutY(310);
            root.getChildren().add(choice2);

            choice1.setOnAction(e -> gameStage.close());
            choice2.setOnAction(e -> gameStage.close());
        });
    }

    private void resetGame() {
        setupGame();
        gameTimer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
