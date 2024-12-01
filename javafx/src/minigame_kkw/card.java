package minigame_kkw;

import javafx.animation.PauseTransition;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import utils.character_result;

public class card extends Application {
    private int gridSize = 2; // 시작 크기 2x2
    private Button[][] buttons;
    private int[][] answer;
    private Button firstClick = null;
    private int firstRow = -1, firstCol = -1;
    private int matchCount = 0;

    private boolean isFirstGame = true;  // 게임이 처음 시작할 때만 설명 창을 띄우도록 추적하는 변수

    @Override
    public void start(Stage stage) {
        initializeGame(stage);
    }

    private void initializeGame(Stage stage) {
        // 게임 시작 전에 설명을 보여주는 알림 창 (첫 게임만)
        if (isFirstGame) {
            Alert gameStartAlert = new Alert(Alert.AlertType.INFORMATION);
            gameStartAlert.setTitle("게임 시작!");
            gameStartAlert.setHeaderText(null);
            gameStartAlert.setContentText("게임 설명:\n\n" +
                    "다양한 카드들은 캐릭터의 기억과 감각을 상징하며, 플레이어는 이들을 짝지어 올바르게 맞추어야 합니다. \n" +
                    "기억과 감각을 찾으면 캐릭터는 점점 더 성장할 준비를 합니다.\n\n" +
                    "마우스를 이용하여 카드를 클릭하여 카드 짝을 맞추면 되는 게임입니다.");
            gameStartAlert.showAndWait(); // 알림 창을 띄우고 사용자가 확인할 때까지 대기

            isFirstGame = false;  // 첫 게임 이후에는 설명 창을 띄우지 않도록 설정
        }

        // GridPane에 버튼 추가
        GridPane grid = new GridPane();
        grid.setHgap(10); // 카드 간의 가로 간격
        grid.setVgap(10); // 카드 간의 세로 간격
        buttons = new Button[gridSize][gridSize];
        answer = new int[gridSize][gridSize];
        matchCount = 0;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button button = new Button();
                button.setPrefSize(90, 90); // 카드 크기 설정
                button.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-radius: 10;");
                button.setGraphic(new ImageView(new Image("file:javafx/images/kkw/backimg.PNG"))); // 뒷면 이미지
                buttons[i][j] = button;

                int finalI = i;
                int finalJ = j;

                button.setOnAction(e -> handleCardClick(button, finalI, finalJ, stage));
                grid.add(button, j, i);
            }
        }

        initAnswer();
        resetCardBack(); // 초기화 시 모든 카드 뒷면 이미지 설정

        // StackPane으로 감싸서 화면 가운데 배치
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER); // 가운데 정렬
        root.setPrefSize(390, 390); // StackPane 크기 설정
        root.getChildren().add(grid);

        // 게임 크기에 따라 GridPane의 위치 변경
        if (gridSize == 2) {
            // 2x2 게임일 경우 (100, 100) 지점에서 시작
            grid.setTranslateX(100);
            grid.setTranslateY(100);
        } else if (gridSize == 4) {
            // 4x4 게임일 경우 (0, 0) 지점에서 시작
            grid.setTranslateX(0);
            grid.setTranslateY(0);
        }

        Scene scene = new Scene(root, 390, 390);
        stage.setScene(scene);
        stage.setTitle("카드 매칭 게임");
        stage.show();
    }

    private void initAnswer() {
        List<Integer> cardValues = new ArrayList<>();
        for (int i = 0; i < (gridSize * gridSize) / 2; i++) {
            cardValues.add(i);
            cardValues.add(i); // 같은 값 두 개 추가
        }
        Collections.shuffle(cardValues);

        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                answer[i][j] = cardValues.get(index++);
            }
        }
    }

    private void resetCardBack() {
        // 게임 시작 시 모든 카드의 뒷면 이미지를 설정
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                buttons[i][j].setGraphic(new ImageView(new Image("file:javafx/images/kkw/backimg.PNG"))); // 뒷면 이미지로 초기화
            }
        }
    }

    private void handleCardClick(Button button, int row, int col, Stage stage) {
        if (button.getStyle().contains("gray")) return; // 이미 매칭된 카드 클릭 방지

        button.setGraphic(new ImageView(new Image("file:javafx/images/kkw/cardimg" + answer[row][col] + ".PNG"))); // 앞면 이미지

        if (firstClick == null) {
            // 첫 번째 클릭
            firstClick = button;
            firstRow = row;
            firstCol = col;
        } else {
            // 두 번째 클릭
            if (button == firstClick) {
                return; // 동일한 카드 클릭 방지
            }

            if (answer[row][col] == answer[firstRow][firstCol]) {
                // 카드 매칭 성공
                button.setStyle("-fx-background-color: gray; -fx-border-color: black;");
                firstClick.setStyle("-fx-background-color: gray; -fx-border-color: black;");
                button.setDisable(true);
                firstClick.setDisable(true);
                matchCount++;

                if (matchCount == (gridSize * gridSize) / 2) {
                    nextLevel(stage); // 모든 카드가 맞춰졌다면 다음 단계로
                }
            } else {
                // 카드 매칭 실패 - 0.5초 후 뒤집기
                Button tempFirst = firstClick;
                Button tempSecond = button;

                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> {
                    tempFirst.setGraphic(new ImageView(new Image("file:javafx/images/kkw/backimg.PNG"))); // 뒷면 이미지로 전환
                    tempSecond.setGraphic(new ImageView(new Image("file:javafx/images/kkw/backimg.PNG"))); // 뒷면 이미지로 전환
                });
                pause.play();
            }

            firstClick = null; // 첫 번째 클릭 초기화
        }
    }

    private void nextLevel(Stage stage) {
        if (gridSize < 4) {
            gridSize += 2; // 크기 증가
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("레벨 업!");
            alert.setHeaderText(null);
            alert.setContentText("다음 레벨로 이동합니다! " + gridSize + "x" + gridSize);
            alert.showAndWait();
            initializeGame(stage); // 게임 초기화
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("게임 완료!");
            alert.setHeaderText(null);
            alert.setContentText("게임 클리어\n" + 
                                 "너 덕분에 내가 잃어버린 기억을 다시 되찾았어.\n" +
                                 "이제 마지막 질문을 할게\n" + 
                                 "기억에 남는 순간들을 되새길 때, 너는 그 순간을 다른 사람들과 함께 공유하는 편이야, \n" +
                                 "아니면 혼자 조용히 되새기는 걸 좋아해?");

            ButtonType choiceE = new ButtonType("다른 사람들과 공유하는 걸 좋아해.");
            ButtonType choiceI = new ButtonType("혼자 조용히 되새기는 게 좋아.");
            alert.getButtonTypes().setAll(choiceE, choiceI);

            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(answer -> {
                if (answer == choiceE) {
                    utils.character_result.incrementE(50); // e +50
                    System.out.println("선택한 답변: 다른 사람들과 공유하는 걸 좋아해.");
                } else if (answer == choiceI) {
                    utils.character_result.incrementI(50); // i +50
                    System.out.println("선택한 답변: 혼자 조용히 되새기는 게 좋아.");
                }
            });
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
