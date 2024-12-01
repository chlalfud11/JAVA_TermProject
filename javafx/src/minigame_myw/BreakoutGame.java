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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import java.util.Random;

import utils.character_result;

public class BreakoutGame extends Application {
    private Ball ball;
    private Paddle paddle;
    private Block[][] blocks;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean paused = false;
    private boolean gameOver = false; // 게임 종료 상태를 저장하는 플래그
    private static final int BLOCK_ROWS = 3; // 벽돌 행 개수 감소
    private static final int BLOCK_COLS = 5; // 벽돌 열 개수 감소
    private static final int BLOCK_MARGIN = 10; // 간격 증가 (선택 사항)
    private static final int BLOCK_WIDTH = 50;
    private static final int BLOCK_HEIGHT = 20;

    private Stage primaryStage;
    private Canvas canvas;
    private GraphicsContext gc;
    private Stage clearStage; // 클래스 멤버 변수로 선언
    
    private boolean gameCleared = false; // 게임 클리어 상태 플래그


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Brick Breaking Game");

        // 게임 설명 창 먼저 표시
        showStoryDialog(() -> showStartMenu());

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
    
    private void showStoryDialog(Runnable onCloseCallback) {
        Stage storyStage = new Stage();
        storyStage.initModality(Modality.APPLICATION_MODAL);
        storyStage.setTitle("게임 설명");

        // X 버튼 클릭 시 애플리케이션 종료 설정
        storyStage.setOnCloseRequest(event -> {
            Platform.exit();  // JavaFX 애플리케이션 종료
            System.exit(0);   // JVM 종료
        });

        // 텍스트 스타일
        Label storyLabel = new Label(
            "플레이어는 알 속에 갇힌 캐릭터를 구출하기 위해\n" +
            "알 주위에 쌓인 벽돌을 깨부숴야 합니다.\n\n" +
            "보호막 같은 이 벽돌들은 캐릭터의 성장을 막고 있는 장애물입니다.\n" +
            "P를 눌러 일시 정지, 방향키로 이동하세요!"
        );
        storyLabel.setStyle(
            "-fx-font-size: 16px; " +           // 텍스트 크기
            "-fx-text-fill: white; " +          // 텍스트 색상을 흰색으로 설정
            "-fx-text-alignment: center; " +    // 텍스트 중앙 정렬
            "-fx-wrap-text: true;"              // 줄바꿈 활성화
        );

        // 버튼 스타일
        Button continueButton = new Button("계속하기");
        continueButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: #28a745; " +
            "-fx-padding: 10 20; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );

        continueButton.setOnAction(e -> {
            storyStage.close();
            onCloseCallback.run(); // 설명 창 종료 후 콜백 실행
        });

        // 레이아웃 스타일
        VBox layout = new VBox(20, storyLabel, continueButton);
        layout.setStyle(
            "-fx-padding: 20; " +
            "-fx-alignment: center; " +
            "-fx-background-color: #333333;" // 어두운 회색 배경
        );

        // 장면 설정
        Scene storyScene = new Scene(layout, 400, 250);
        storyStage.setScene(storyScene);
        storyStage.showAndWait();
    }
    
    private void showStartMenu() {
        Button startButton = new Button("게임 시작");
        Button exitButton = new Button("게임 종료");

        startButton.setOnAction(e -> startGame());
        exitButton.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(10, startButton, exitButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene startScene = new Scene(layout, 400, 300);
        startScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(startScene);
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
        if (gameOver || gameCleared) {
            return; // 게임 종료 상태나 클리어 상태라면 업데이트 중단
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

        boolean allBlocksCleared = true; // 모든 벽돌이 깨졌는지 확인
        for (Block[] row : blocks) {
            for (Block block : row) {
                if (block.isVisible()) {
                    allBlocksCleared = false;
                    if (ball.getY() <= block.getY() + block.getHeight() &&
                        ball.getX() + ball.getRadius() >= block.getX() &&
                        ball.getX() - ball.getRadius() <= block.getX() + block.getWidth()) {
                        ball.reverseY();
                        block.setVisible(false);
                    }
                }
            }
        }

        if (allBlocksCleared) {
            gameCleared = true; // 게임 클리어 상태로 설정
            showGameClear(); // 게임 클리어 창 표시
        }

        if (ball.getY() >= 500) {
            gameOver = true; // 공이 바닥에 닿으면 게임 종료
            showGameOver(); // 게임 종료 창 표시
        }
    }
    
    private void showGameClear() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("게임 클리어!");
            alert.setHeaderText(null);
            alert.setContentText("게임 클리어 했으므로 질문권을 드립니다. 질문에 답해 주세요:\n" +
                    "사람들과 함께하는 모임에서, 너는\n"
                    + "주로 이야기의 중심이 되는 것을 \n" +
                    "좋아해, 아니면 조용히 듣는 편이야?");

            // 선택지 추가
            ButtonType choiceE = new ButtonType("이야기의 중심이 되는 것을 좋아해.");
            ButtonType choiceI = new ButtonType("조용히 듣는 편이야.");
            alert.getButtonTypes().setAll(choiceE, choiceI);

            // 선택지 결과 처리
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(answer -> {
                if (answer == choiceE) {
                    utils.character_result.incrementE(50); // e +50
                    System.out.println("선택한 답변: 이야기의 중심이 되는 것을 좋아해.");
                    System.out.println("현재 e 값: " + utils.character_result.e);
                } else if (answer == choiceI) {
                    utils.character_result.incrementI(50); // i +50
                    System.out.println("선택한 답변: 조용히 듣는 편이야.");
                    System.out.println("현재 i 값: " + utils.character_result.i);
                }
                alert.close(); // 창 닫기
            });
        });
    }
    
    private void exitGame() {
        System.out.println("게임 종료");
        Platform.exit(); // JavaFX 애플리케이션 종료
        System.exit(0); // JVM 종료
    }

    private void closeStage(Stage stage) {
        Platform.runLater(() -> {
            if (stage != null && stage.isShowing()) {
                stage.close(); // 강제로 창 닫기
                System.out.println("창이 정상적으로 닫혔습니다.");
            } else {
                System.out.println("창이 이미 닫혀 있습니다.");
            }
        });
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
