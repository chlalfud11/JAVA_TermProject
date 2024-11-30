package minigame_cmr;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.Random;

public class Gaming extends Application {

    private static final int GRID_SIZE = 8; // 그리드 크기
    private static final int TILE_SIZE = 50; // 블록 크기
    private Button[][] grid = new Button[GRID_SIZE][GRID_SIZE];
    private Image[] images; // 블록 이미지
    private Text scoreText = new Text("Score: 0");
    private int score = 0;

    private Button firstClicked = null; // 첫 번째 클릭 저장
    private Button secondClicked = null; // 두 번째 클릭 저장

    @Override
    public void start(Stage primaryStage) {
        showStoryScene(primaryStage); // 게임 스토리 화면으로 시작
    }

    private void showStoryScene(Stage primaryStage) {
        VBox storyLayout = new VBox(20);
        storyLayout.setAlignment(Pos.CENTER);

        Text storyText = new Text("퍼즐 조각들은 캐릭터의 정체성을 나타내며, \n" +
                "당신은 퍼즐 조각을 맞추어 캐릭터의 성장을 돕게 됩니다.");
        Button nextButton = new Button("Next");

        nextButton.setOnAction(e -> startGame(primaryStage));

        storyLayout.getChildren().addAll(storyText, nextButton);

        Scene storyScene = new Scene(storyLayout, 400, 300);
        primaryStage.setScene(storyScene);
        primaryStage.show();
    }

    private void startGame(Stage primaryStage) {
        Pane root = new Pane();
        GridPane gridPane = new GridPane();
        gridPane.setLayoutX(50);
        gridPane.setLayoutY(50);

        // 이미지 초기화
        images = new Image[]{
                new Image("file:javafx/images/cmr/red.png"),
                new Image("file:javafx/images/cmr/blue.png"),
                new Image("file:javafx/images/cmr/green.png"),
                new Image("file:javafx/images/cmr/yellow.png"),
                new Image("file:javafx/images/cmr/purple.png")
        };

        // 그리드 초기화
        initializeGrid(gridPane);

        // 점수 표시
        scoreText.setLayoutX(50);
        scoreText.setLayoutY(30);
        root.getChildren().addAll(gridPane, scoreText);

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("Anipang Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeGrid(GridPane gridPane) {
        Random random = new Random();

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int randomIndex = random.nextInt(images.length);
                Button button = new Button();
                button.setGraphic(new ImageView(images[randomIndex]));
                button.setMinSize(TILE_SIZE, TILE_SIZE);
                button.setMaxSize(TILE_SIZE, TILE_SIZE);

                int currentI = i; // i와 j를 람다식에서 사용하기 위해 복사
                int currentJ = j;

                button.setOnAction(e -> handleTileClick(currentI, currentJ));

                grid[i][j] = button;
                gridPane.add(button, j, i);
            }
        }
    }

    private void handleTileClick(int i, int j) {
        Button clickedButton = grid[i][j];

        if (firstClicked == null) {
            // 첫 번째 클릭
            firstClicked = clickedButton;
        } else {
            // 두 번째 클릭
            secondClicked = clickedButton;

            // 위치 교환 로직
            if (swapTiles(firstClicked, secondClicked)) {
                checkMatches(); // 매칭 확인 및 처리
            }

            // 클릭 초기화
            firstClicked = null;
            secondClicked = null;
        }
    }

    private boolean swapTiles(Button btn1, Button btn2) {
        // 두 블록이 인접한지 확인
        int row1 = GridPane.getRowIndex(btn1);
        int col1 = GridPane.getColumnIndex(btn1);
        int row2 = GridPane.getRowIndex(btn2);
        int col2 = GridPane.getColumnIndex(btn2);

        if (Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1) {
            // 두 블록의 그래픽 교환
            ImageView graphic1 = (ImageView) btn1.getGraphic();
            ImageView graphic2 = (ImageView) btn2.getGraphic();

            btn1.setGraphic(graphic2);
            btn2.setGraphic(graphic1);

            return true; // 교환 성공
        } else {
            return false; // 교환 실패
        }
    }

    private void checkMatches() {
        boolean[][] matched = new boolean[GRID_SIZE][GRID_SIZE]; // 매칭된 블록 기록
        boolean foundMatch = false;

        // 가로 매칭 확인
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE - 2; j++) {
                ImageView first = (ImageView) grid[i][j].getGraphic();
                ImageView second = (ImageView) grid[i][j + 1].getGraphic();
                ImageView third = (ImageView) grid[i][j + 2].getGraphic();

                if (first.getImage().equals(second.getImage()) && second.getImage().equals(third.getImage())) {
                    matched[i][j] = true;
                    matched[i][j + 1] = true;
                    matched[i][j + 2] = true;
                    foundMatch = true;
                }
            }
        }

        // 세로 매칭 확인
        for (int j = 0; j < GRID_SIZE; j++) {
            for (int i = 0; i < GRID_SIZE - 2; i++) {
                ImageView first = (ImageView) grid[i][j].getGraphic();
                ImageView second = (ImageView) grid[i + 1][j].getGraphic();
                ImageView third = (ImageView) grid[i + 2][j].getGraphic();

                if (first.getImage().equals(second.getImage()) && second.getImage().equals(third.getImage())) {
                    matched[i][j] = true;
                    matched[i + 1][j] = true;
                    matched[i + 2][j] = true;
                    foundMatch = true;
                }
            }
        }

        if (foundMatch) {
            removeAndGenerateTiles(matched); // 매칭된 블록 제거 및 새로운 블록 생성
        }

        if (score >= 3000) {
            showGameClearScreen();
        }
    }

    private void removeAndGenerateTiles(boolean[][] matched) {
        Random random = new Random();

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (matched[i][j]) {
                    // 매칭된 블록을 제거하고 새 블록 생성
                    int randomIndex = random.nextInt(images.length);
                    grid[i][j].setGraphic(new ImageView(images[randomIndex]));
                    updateScore(50); // 매칭된 블록당 점수 추가
                }
            }
        }
    }

    private void updateScore(int points) {
        score += points;
        scoreText.setText("Score: " + score);
    }

    private void showGameClearScreen() {
        Stage clearStage = new Stage();
        VBox clearLayout = new VBox(20);
        clearLayout.setAlignment(Pos.CENTER);

        Text clearMessage = new Text("게임을 클리어한 것을 축하해! 그럼 다음 질문을 할게!");
        Button nextButton = new Button("Next");

        nextButton.setOnAction(e -> showMBTIScene(clearStage));

        clearLayout.getChildren().addAll(clearMessage, nextButton);

        Scene clearScene = new Scene(clearLayout, 300, 200);
        clearStage.setScene(clearScene);
        clearStage.show();
    }

    private void showMBTIScene(Stage previousStage) {
        previousStage.close(); // 이전 창 닫기

        Stage mbtiStage = new Stage();
        VBox mbtiLayout = new VBox(20);
        mbtiLayout.setAlignment(Pos.CENTER);

        Text question = new Text("친구가 시험에 떨어졌다고 했을 때,\n 넌 뭐라고 말 할 거야?");
        Button aloneButton = new Button("다음엔 꼭 붙을 거야!");
        Button togetherButton = new Button("몇점인데?");

        aloneButton.setOnAction(e -> showThanksScreen(mbtiStage));
        togetherButton.setOnAction(e -> showThanksScreen(mbtiStage));

        mbtiLayout.getChildren().addAll(question, aloneButton, togetherButton);

        Scene mbtiScene = new Scene(mbtiLayout, 300, 200);
        mbtiStage.setScene(mbtiScene);
        mbtiStage.show();
    }

    private void showThanksScreen(Stage previousStage) {
        previousStage.close(); // 이전 창 닫기

        Stage thanksStage = new Stage();
        VBox thanksLayout = new VBox(20);
        thanksLayout.setAlignment(Pos.CENTER);

        // 이미지 추가
        ImageView imageView = new ImageView(new Image("file:javafx/images/2nd.png"));
        imageView.setFitWidth(300);
        imageView.setFitHeight(200);

        // 메시지 추가
        Text thanksMessage = new Text("고마워! 너 덕분에 난 조금씩 깨어나고 있어!");
        thanksMessage.setFont(loadCustomFont("/font.ttf", 18)); // 폰트 적용

        thanksLayout.getChildren().addAll(imageView, thanksMessage);

        Scene thanksScene = new Scene(thanksLayout, 400, 300);
        thanksStage.setScene(thanksScene);
        thanksStage.show();
    }

    private Font loadCustomFont(String fontPath, int size) {
        try {
            String fontUrl = getClass().getResource(fontPath).toExternalForm();
            return Font.loadFont(fontUrl, size);
        } catch (Exception e) {
            e.printStackTrace();
            return Font.font("Arial", size); // 기본 폰트 대체
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}