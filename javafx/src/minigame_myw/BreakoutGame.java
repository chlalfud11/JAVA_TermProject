package minigame_myw;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label; // Label 클래스 임포트 추가
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality; // Modality 클래스 임포트 추가
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import java.util.Random;

public class BreakoutGame extends Application {
    private Ball ball;
    private Paddle paddle;
    private Block[][] blocks;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean paused = false; // 일시정지 상태 변수 추가
    private static final int BLOCK_ROWS = 5;
    private static final int BLOCK_COLS = 7;
    private static final int BLOCK_WIDTH = 50;
    private static final int BLOCK_HEIGHT = 20;
    private static final int BLOCK_MARGIN = 5;

    private Stage primaryStage; // primaryStage를 인스턴스 변수로 선언
    private Canvas canvas;
    private GraphicsContext gc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // 인스턴스 변수에 할당
        primaryStage.setTitle("Brick breaking Game");

        // 버튼 생성
        Button startButton = new Button("게임 시작");
        Button exitButton = new Button("게임 종료");

        // 버튼 클릭 이벤트 설정
        startButton.setOnAction(e -> startGame());
        exitButton.setOnAction(e -> primaryStage.close());

        // 레이아웃 설정
        VBox layout = new VBox(10); // 10픽셀 간격
        layout.getChildren().addAll(startButton, exitButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene startScene = new Scene(layout, 400, 300);
        startScene.getStylesheets().add(getClass().getResource("/javaProject/style.css").toExternalForm());
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    private void startGame() {
        ball = new Ball(200, 200);
        paddle = new Paddle(200, 450);
        blocks = createBlocks();

        canvas = new Canvas(400, 500);
        gc = canvas.getGraphicsContext2D();

        Scene gameScene = new Scene(new StackPane(canvas), 400, 500);
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                moveLeft = true;
            } else if (event.getCode() == KeyCode.RIGHT) {
                moveRight = true;
            } else if (event.getCode() == KeyCode.P) { // P 키로 일시정지
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

        primaryStage.setScene(gameScene); // 인스턴스 변수 사용
        primaryStage.setTitle("Breakout Game");

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if (!paused) { // 일시정지 상태가 아닐 때만 업데이트
                    update();
                }
                render(gc);
            }
        }.start();
    }

    private void togglePause() {
        paused = !paused; // paused 상태 토글
        if (paused) {
            showPauseMenu(); // 일시정지 메뉴 표시
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
            paused = false; // 일시정지 해제
            pauseStage.close(); // 팝업 창 닫기
        });

        exitButton.setOnAction(e -> {
            pauseStage.close(); // 팝업 창 닫기
            primaryStage.close(); // 메인 게임 창 닫기
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(pauseLabel, resumeButton, exitButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene pauseScene = new Scene(layout, 200, 150);
        pauseScene.getStylesheets().add(getClass().getResource("/javaProject/style.css").toExternalForm());
        pauseStage.setScene(pauseScene);
        pauseStage.showAndWait(); // 모달 창으로 띄우기
    }

    private Block[][] createBlocks() {
        Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_COLS];
        Random rand = new Random();
        double startX = (400 - (BLOCK_COLS * BLOCK_WIDTH + (BLOCK_COLS - 1) * BLOCK_MARGIN)) / 2;

        for (int i = 0; i < BLOCK_ROWS; i++) {
            for (int j = 0; j < BLOCK_COLS; j++) {
                double x = startX + j * (BLOCK_WIDTH + BLOCK_MARGIN);
                double y = i * (BLOCK_HEIGHT + BLOCK_MARGIN) + BLOCK_MARGIN;
                Color color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
                blocks[i][j] = new Block(x, y, color);
            }
        }
        return blocks;
    }

    private void update() {
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

// Ball 클래스
class Ball {
private double x, y;
private double radius = 10;
private double dx = 3, dy = -3;


public Ball(double x, double y) {
    this.x = x;
    this.y = y;
}

public void move() {
    x += dx;
    y += dy;

    if (x <= 0 || x >= 400 - radius * 2) {
        dx = -dx;
    }
    if (y <= 0) {
        dy = -dy;
    }
    if (y >= 500) {
        System.out.println("Game Over!");
        System.exit(0);
    }
}

public void reverseY() {
    dy = -dy;
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

public void draw(GraphicsContext gc) {
    gc.setFill(Color.WHITE);
    gc.fillOval(x, y, radius * 2, radius * 2);
}
}

// Paddle 클래스
class Paddle {
private double x, y;
private double width = 60, height = 10;


public Paddle(double x, double y) {
    this.x = x;
    this.y = y;
}

public void move(double dx) {
    x += dx;
    if (x < 0) x = 0;
    if (x > 400 - width) x = 400 - width;
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

public void draw(GraphicsContext gc) {
    gc.setFill(Color.BLUE);
    gc.fillRect(x, y, width, height);
}
}

// Block 클래스
class Block {
private double x, y;
private double width = 50, height = 20;
private boolean visible = true;
private Color color;


public Block(double x, double y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
}

public void draw(GraphicsContext gc) {
    gc.setFill(color);
    gc.fillRect(x, y, width, height);
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