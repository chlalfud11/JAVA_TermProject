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
    private Stage gameStage; // 게임 Stage를 저장
    private ImageView player; // 플레이어 이미지
    private List<ImageView> meteors = new ArrayList<>();
    private List<ImageView> bigMeteors = new ArrayList<>();
    private List<ImageView> clocks = new ArrayList<>();
    private List<ImageView> heals = new ArrayList<>();
    private Random random = new Random();
    private int remainingTime = 30; // 남은 시간 관리
    private int playerHP = 100;
    private boolean gameOver = false; // 게임 종료 플래그
    private Pane root;
    private AnimationTimer timer;
    private Label timerLabel;
    private Rectangle hpBar;

    @Override
    public void start(Stage primaryStage) {
        this.gameStage = primaryStage; // 게임 Stage 저장
        root = new Pane();

        // 배경 이미지 추가
        Image bgImage = new Image(getClass().getResource("/javafx/images/kys/burning_forest_meteor_background.png").toExternalForm());
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(400);
        background.setFitHeight(600);
        root.getChildren().add(background);

        Scene scene = new Scene(root, 400, 600);
        setupGame();

        scene.setOnKeyPressed(event -> {
            if (!gameOver) {
                if (event.getCode() == KeyCode.LEFT && player.getX() > 0) {
                    player.setX(player.getX() - 10);
                } else if (event.getCode() == KeyCode.RIGHT && player.getX() < scene.getWidth() - player.getFitWidth()) {
                    player.setX(player.getX() + 10);
                }
            }
        });

        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (!gameOver) {
                    if (now - lastUpdate >= 1_000_000_000) {
                        remainingTime--; // 남은 시간을 줄임
                        updateTimerLabel();
                        lastUpdate = now;

                        if (remainingTime <= 0) { // 시간이 0초가 되면 성공으로 처리
                            timer.stop();
                            gameOver = true;
                            showWinMessage(); // 성공 메시지 호출
                            return;
                        }
                    }
                    spawnObjects();
                    moveObjects();
                    checkCollisions();
                }
            }
        };

        timer.start();
        primaryStage.setScene(scene);
        primaryStage.setTitle("운석 피하기 - 위험을 피해 성장하라");
        primaryStage.show();
    }

    private void setupGame() {
        root.getChildren().removeIf(node -> !(node instanceof ImageView)); // 배경 유지하고 초기화

        // 플레이어 이미지 설정
        Image playerImg = new Image(getClass().getResource("/javafx/images/burning_forest_meteor_background.png").toExternalForm());
        player = new ImageView(playerImg);
        player.setFitWidth(50);
        player.setFitHeight(50);
        player.setX(190);
        player.setY(570);
        root.getChildren().add(player);

        // HP 바 생성
        hpBar = new Rectangle(100, 10, Color.LIME);
        hpBar.setX(10);
        hpBar.setY(10);
        root.getChildren().add(hpBar);

        // 타이머 라벨 설정
        timerLabel = new Label("Time: 30");
        timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        timerLabel.setLayoutX(10);
        timerLabel.setLayoutY(30);
        root.getChildren().add(timerLabel);

        meteors.clear();
        bigMeteors.clear();
        clocks.clear();
        heals.clear();
        remainingTime = 30;
        playerHP = 100;
        gameOver = false; // 게임 종료 초기화
    }

    private void spawnObjects() {
        if (random.nextInt(40) == 0) {
            ImageView meteor = new ImageView(new Image(getClass().getResource("/javafx/images/kys/simple_item.png").toExternalForm()));
            meteor.setFitWidth(30);
            meteor.setFitHeight(30);
            meteor.setX(random.nextDouble() * 370);
            meteor.setY(0);
            meteors.add(meteor);
            root.getChildren().add(meteor);
        }

        if (random.nextInt(100) == 0) {
            ImageView bigMeteor = new ImageView(new Image(getClass().getResource("file:javafx/images/kys/death_item.png").toExternalForm()));
            bigMeteor.setFitWidth(50);
            bigMeteor.setFitHeight(50);
            bigMeteor.setX(random.nextDouble() * 350);
            bigMeteor.setY(0);
            bigMeteors.add(bigMeteor);
            root.getChildren().add(bigMeteor);
        }

        if (random.nextInt(150) == 0) {
            ImageView clock = new ImageView(new Image(getClass().getResource("/javafx/images/kys/clock_item.png").toExternalForm()));
            clock.setFitWidth(30);
            clock.setFitHeight(30);
            clock.setX(random.nextDouble() * 370);
            clock.setY(0);
            clocks.add(clock);
            root.getChildren().add(clock);
        }

        if (random.nextInt(150) == 0) {
            ImageView heal = new ImageView(new Image(getClass().getResource("/javafx/images/kys/health_recovery_icon.png").toExternalForm()));
            heal.setFitWidth(30);
            heal.setFitHeight(30);
            heal.setX(random.nextDouble() * 370);
            heal.setY(0);
            heals.add(heal);
            root.getChildren().add(heal);
        }
    }

    private void moveObjects() {
        moveImageViews(meteors, 3);
        moveImageViews(bigMeteors, 2);
        moveImageViews(clocks, 3);
        moveImageViews(heals, 3);
    }

    private void moveImageViews(List<ImageView> items, int speed) {
        for (Iterator<ImageView> iterator = items.iterator(); iterator.hasNext(); ) {
            ImageView item = iterator.next();
            item.setY(item.getY() + speed);

            if (item.getY() > 600) {
                iterator.remove();
                root.getChildren().remove(item);
            }
        }
    }

    private void checkCollisions() {
        if (gameOver) return;

        // 작은 운석 충돌
        for (Iterator<ImageView> iterator = meteors.iterator(); iterator.hasNext(); ) {
            ImageView meteor = iterator.next();
            if (meteor.getBoundsInParent().intersects(player.getBoundsInParent())) {
                playerHP -= 20;
                updateHPBar();
                iterator.remove();
                root.getChildren().remove(meteor);

                if (playerHP <= 0) {
                    timer.stop();
                    gameOver = true;
                    showGameOverScreen();
                    return;
                }
            }
        }

        // 큰 운석 충돌
        for (Iterator<ImageView> iterator = bigMeteors.iterator(); iterator.hasNext(); ) {
            ImageView bigMeteor = iterator.next();
            if (bigMeteor.getBoundsInParent().intersects(player.getBoundsInParent())) {
                playerHP = 0;
                updateHPBar();
                iterator.remove();
                root.getChildren().remove(bigMeteor);

                timer.stop();
                gameOver = true;
                showGameOverScreen();
                return;
            }
        }

        // 시계 아이템 충돌
        for (Iterator<ImageView> iterator = clocks.iterator(); iterator.hasNext(); ) {
            ImageView clock = iterator.next();
            if (clock.getBoundsInParent().intersects(player.getBoundsInParent())) {
                remainingTime = Math.max(0, remainingTime - 5);
                updateTimerLabel();
                iterator.remove();
                root.getChildren().remove(clock);
            }
        }

        // 회복 아이템 충돌
        for (Iterator<ImageView> iterator = heals.iterator(); iterator.hasNext(); ) {
            ImageView heal = iterator.next();
            if (heal.getBoundsInParent().intersects(player.getBoundsInParent())) {
                playerHP = Math.min(100, playerHP + 10);
                updateHPBar();
                iterator.remove();
                root.getChildren().remove(heal);
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
            gameStage.close(); // 미니게임 종료
        });
    }

    private void showWinMessage() {
        Platform.runLater(() -> {
            root.getChildren().clear(); // 화면 초기화

            // 성공 메시지
            Label winMessage = new Label("대단해! 너 덕분에 내가\n성장할 것 같은 기분이 들어.\n이제 너에게 질문을 할게.");
            winMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
            winMessage.setLayoutX(50);
            winMessage.setLayoutY(200);
            root.getChildren().add(winMessage);

            // NEXT 버튼
            Button nextButton = new Button("NEXT");
            nextButton.setLayoutX(180);
            nextButton.setLayoutY(300);
            root.getChildren().add(nextButton);

            nextButton.setOnAction(e -> showFinalQuestion());
        });
    }

    private void showFinalQuestion() {
        root.getChildren().clear(); // 화면 초기화

        Label questionLabel = new Label("쾅💥☄ 하늘에서 운석이 떨어져서\n친구가 심하게 다쳤다");
        questionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        questionLabel.setLayoutX(20);
        questionLabel.setLayoutY(150);
        root.getChildren().add(questionLabel);

        Button choice1 = new Button("그래도 죽을 정도는 아니라 다행이다");
        choice1.setLayoutX(50);
        choice1.setLayoutY(250);
        root.getChildren().add(choice1);

        Button choice2 = new Button("아 어떡해…. 친구야 괜찮아?ㅠㅠㅠㅠㅠ");
        choice2.setLayoutX(50);
        choice2.setLayoutY(300);
        root.getChildren().add(choice2);

        choice1.setOnAction(e -> gameStage.close());
        choice2.setOnAction(e -> gameStage.close());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
