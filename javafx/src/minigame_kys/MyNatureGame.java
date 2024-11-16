package minigame_kys;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class MyNatureGame extends Application {
    private Rectangle player;
    private List<Circle> meteors = new ArrayList<>();
    private Random random = new Random();
    private int timeElapsed = 0;
    private Pane root;
    private AnimationTimer timer;
    private Label timerLabel;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, 400, 600);
        setupGame();

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT && player.getX() > 0) {
                player.setX(player.getX() - 10);
            } else if (event.getCode() == KeyCode.RIGHT && player.getX() < scene.getWidth() - 20) {
                player.setX(player.getX() + 10);
            }
        });

        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000) {
                    timeElapsed++;
                    updateTimerLabel();
                    lastUpdate = now;
                    if (timeElapsed >= 30) { // 30초 동안 피하면 승리
                        stop();
                        showWinMessage();
                    }
                }
                spawnMeteors();
                moveMeteors();
                checkCollision();
            }
        };

        timer.start();
        primaryStage.setScene(scene);
        primaryStage.setTitle("운석 피하기 - 위험을 피해 성장하라");
        primaryStage.show();
    }

    private void setupGame() {
        root.getChildren().clear();
        player = new Rectangle(20, 20, Color.BLUE);
        player.setX(190);
        player.setY(570);
        root.getChildren().add(player);

        meteors.clear();
        timeElapsed = 0;

        // 타이머 라벨 설정
        timerLabel = new Label("Time: 30");
        timerLabel.setLayoutX(10);
        timerLabel.setLayoutY(10);
        root.getChildren().add(timerLabel);
    }

    private void spawnMeteors() {
        if (random.nextInt(20) == 0) {
            Circle meteor = new Circle(10, Color.RED);
            meteor.setCenterX(random.nextDouble() * 380);
            meteor.setCenterY(0);
            meteors.add(meteor);
            root.getChildren().add(meteor);
        }
    }

    private void moveMeteors() {
        for (Iterator<Circle> iterator = meteors.iterator(); iterator.hasNext(); ) {
            Circle meteor = iterator.next();
            meteor.setCenterY(meteor.getCenterY() + 5);

            if (meteor.getCenterY() > 600) {
                iterator.remove();
            }
        }
    }

    private void checkCollision() {
        for (Circle meteor : meteors) {
            if (meteor.getBoundsInParent().intersects(player.getBoundsInParent())) {
                Platform.runLater(this::showGameOverScreen);
                timer.stop();
                return;
            }
        }
    }

    private void updateTimerLabel() {
        int timeLeft = 30 - timeElapsed;
        timerLabel.setText("Time: " + timeLeft);
    }

    private void showGameOverScreen() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("게임 오버");
        alert.setHeaderText("운석 피하기 실패!");
        alert.setContentText("운석에 맞았습니다. 다시 도전해보세요!");

        Button restartButton = new Button("다시 시작");
        restartButton.setOnAction(e -> {
            alert.close();
            resetGame();
        });

        Pane dialogPane = new Pane();
        dialogPane.getChildren().add(restartButton);
        restartButton.setLayoutX(80);
        restartButton.setLayoutY(100);

        alert.getDialogPane().setContent(dialogPane);
        alert.showAndWait();
    }

    private void showWinMessage() {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("게임 클리어");
            alert.setHeaderText("운석 피하기 성공!");
            alert.setContentText("30초 동안 생존했습니다. 질문에 답해 주세요:\n" +
                "하늘에서 운석이 떨어져서 친구가 다쳤다.");

            ButtonType choiceT = new ButtonType("그래도 죽을 정도는 아니라 다행이다.");
            ButtonType choiceF = new ButtonType("아 어떡해…. 친구야 괜찮아?ㅠㅠㅠㅠㅠ");
            alert.getButtonTypes().setAll(choiceT, choiceF);

            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(answer -> {
                if (answer == choiceT) {
                    System.out.println("선택한 답변: 그래도 죽을 정도는 아니라 다행이다.");
                } else if (answer == choiceF) {
                    System.out.println("선택한 답변: 아 어떡해…. 친구야 괜찮아?ㅠㅠㅠㅠㅠ");
                }
            });
        });
    }

    private void resetGame() {
        setupGame();
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
