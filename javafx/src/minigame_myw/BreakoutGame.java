package minigame_myw;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Random;

public class BreakoutGame extends Application {
    private Ball ball;
    private Paddle paddle;
    private Block[][] blocks;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean paused = false;
    private boolean gameOver = false; // 게임 종료 상태를 저장하는 플래그
    private static final int BLOCK_ROWS = 5;
    private static final int BLOCK_COLS = 7;
    private static final int BLOCK_WIDTH = 50;
    private static final int BLOCK_HEIGHT = 20;
    private static final int BLOCK_MARGIN = 5;

    private Stage primaryStage;
    private Canvas canvas;
    private GraphicsContext gc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Brick Breaking Game");

        Button startButton = new Button("게임 시작");
        Button exitButton = new Button("게임 종료");

        startButton.setOnAction(e -> startGame());
        exitButton.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(startButton, exitButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene startScene = new Scene(layout, 400, 300);
        // CSS 적용
        startScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    private void startGame() {
        ball = new Ball(200, 200);
        paddle = new Paddle(200, 450);
        blocks = createBlocks();

        canvas = new Canvas(400, 500);
        gc = canvas.getGraphicsContext2D();

        StackPane gameRoot = new StackPane(canvas);
        Scene gameScene = new Scene(gameRoot, 400, 500);
        // CSS 적용
        gameScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                moveLeft = true;
            } else if (event.getCode() == KeyCode.RIGHT) {
                moveRight = true;
            } else if (event.getCode() == KeyCode.P) {
                togglePause();
            }
        });

        gameScene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                moveLeft = false;
            } else if (event.getCode() == KeyCode.RIGHT) {
                moveRight = false;
            }
        });

        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Breakout Game");

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if (!paused) {
                    update();
                }
                render(gc);
            }
        }.start();
    }

    private void togglePause() {
        paused = !paused;
        if (paused) {
            showPauseMenu();
        }
    }

    private void showPauseMenu() {
        Stage pauseStage = new Stage();
        pauseStage.initModality(Modality.APPLICATION_MODAL);
        pauseStage.setTitle("일시 정지");

        Label pauseLabel = new Label("게임 일시 정지");
        Button resumeButton = new Button("게임 재개");
        Button exitButton = new Button("게임 종료");

        resumeButton.setOnAction(e -> {
            paused = false;
            pauseStage.close();
        });

        exitButton.setOnAction(e -> {
            pauseStage.close();
            primaryStage.close();
        });

        VBox layout = new VBox(10, pauseLabel, resumeButton, exitButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Scene pauseScene = new Scene(layout, 200, 150);
        pauseScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        pauseStage.setScene(pauseScene);
        pauseStage.showAndWait();
    }

    private Block[][] createBlocks() {
        Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_COLS];
        Random rand = new Random();
        double startX = (400 - (BLOCK_COLS * BLOCK_WIDTH + (BLOCK_COLS - 1) * BLOCK_MARGIN)) / 2;

        for (int i = 0; i < BLOCK_ROWS; i++) {
            for (int j = 0; j < BLOCK_COLS; j++) {
                double x = startX + j * (BLOCK_WIDTH + BLOCK_MARGIN);
                double y = i * (BLOCK_HEIGHT + BLOCK_MARGIN) + BLOCK_MARGIN;
                Color color = Color.rgb(rand.nextInt(200) + 55, rand.nextInt(200) + 55, rand.nextInt(200) + 55);
                blocks[i][j] = new Block(x, y, color);
            }
        }
        return blocks;
    }

    private void update() {
        if (gameOver) {
            return; // 게임 종료 상태라면 업데이트 중단
        }

        ball.move();
        if (moveLeft) {
            paddle.move(-5);
        }
        if (moveRight) {
            paddle.move(5);
        }

        if (ball.getY() + ball.getRadius() >= paddle.getY() && ball.getX() >= paddle.getX() && ball.getX() <= paddle.getX() + paddle.getWidth()) {
            ball.reverseY();
        }

        for (Block[] row : blocks) {
            for (Block block : row) {
                if (block.isVisible() && ball.getY() <= block.getY() + block.getHeight() &&
                        ball.getX() + ball.getRadius() >= block.getX() &&
                        ball.getX() - ball.getRadius() <= block.getX() + block.getWidth()) {
                    ball.reverseY();
                    block.setVisible(false);
                }
            }
        }

        if (ball.getY() >= 500) {
            gameOver = true; // 게임 종료 플래그 설정
            showGameOver(); // 게임 종료 창 표시
        }
    }

    private void showGameOver() {
        Platform.runLater(() -> {
            Stage gameOverStage = new Stage();
            gameOverStage.initModality(Modality.APPLICATION_MODAL); // 모달 창 설정
            gameOverStage.setTitle("게임 종료");

            Label gameOverLabel = new Label("게임이 종료되었습니다!");
            Button exitButton = new Button("게임 종료");

            // 종료 버튼 동작
            exitButton.setOnAction(e -> {
                gameOverStage.close(); // 게임 종료 창 닫기
                primaryStage.close();  // 전체 게임 창 닫기
            });

            VBox layout = new VBox(10, gameOverLabel, exitButton);
            layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
            Scene scene = new Scene(layout, 250, 150);

            // 스타일 추가 (선택 사항)
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            gameOverStage.setScene(scene);
            gameOverStage.show(); // 창 띄우기
        });
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 400, 500);
        ball.draw(gc);
        paddle.draw(gc);
        for (Block[] row : blocks) {
            for (Block block : row) {
                if (block.isVisible()) {
                    block.draw(gc);
                }
            }
        }
    }
}

// Ball, Paddle, Block 클래스는 이전 코드와 동일
//Ball 클래스
class Ball {
 private double x, y, radius;
 private double dx = 2, dy = -2;

 public Ball(double x, double y) {
     this.x = x;
     this.y = y;
     this.radius = 10;
 }

 public void move() {
     x += dx;
     y += dy;

     // 경계 체크
     if (x <= radius || x >= 400 - radius) {
         dx = -dx; // X축 반사
     }
     if (y <= radius) {
         dy = -dy; // Y축 반사
     }
 }

 public void draw(GraphicsContext gc) {
     gc.setFill(Color.WHITE);
     gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
 }

 public double getX() {
     return x;
 }

 public double getY() {
     return y;
 }

 public double getRadius() {
     return radius;
 }

 public void reverseY() {
     dy = -dy;
 }
}

//Paddle 클래스
class Paddle {
 private double x, y;
 private double width = 60, height = 10;

 public Paddle(double x, double y) {
     this.x = x;
     this.y = y;
 }

 public void move(double dx) {
     x += dx;
     // 경계 체크
     if (x < 0) {
         x = 0;
     } else if (x > 400 - width) {
         x = 400 - width;
     }
 }

 public void draw(GraphicsContext gc) {
     gc.setFill(Color.BLUE);
     gc.fillRect(x, y, width, height);
 }

 public double getX() {
     return x;
 }

 public double getY() {
     return y;
 }

 public double getWidth() {
     return width;
 }
}

//Block 클래스
class Block {
 private double x, y;
 private Color color;
 private boolean visible = true;
 private double width = 50, height = 20;

 public Block(double x, double y, Color color) {
     this.x = x;
     this.y = y;
     this.color = color;
 }

 public void draw(GraphicsContext gc) {
     if (visible) {
         gc.setFill(color);
         gc.fillRect(x, y, width, height);
     }
 }

 public boolean isVisible() {
     return visible;
 }

 public void setVisible(boolean visible) {
     this.visible = visible;
 }

 public double getX() {
     return x;
 }

 public double getY() {
     return y;
 }

 public double getWidth() {
     return width;
 }

 public double getHeight() {
     return height;
 }
}