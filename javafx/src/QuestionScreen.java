import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.text.Font;

import java.util.concurrent.atomic.AtomicInteger;

public class QuestionScreen extends Application {

    private VBox mainPanel;
    private Label questionLabel;
    private Button option1Button;
    private Button option2Button;
    private AtomicInteger questionIndex = new AtomicInteger(0);

    // 질문과 답변 배열
    private String[] questions = {
            "너는 성장하는 데에 있어 혼자만의 시간이 중요하다고 생각해?",
            "너는 갈등 상황에서 차분하게 이 상황을 분석하려고 해?"
    };
    private String[][] options = {
            {"혼자만의 시간이 중요해", "다른 사람들과 함께하는 게 중요해"},
            {"차분하게 분석하려고 해", "상대방의 감정을 먼저 생각해"}
    };

    @Override
    public void start(Stage primaryStage) {
        // 메인 레이아웃
        mainPanel = new VBox(20);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setStyle("-fx-padding: 20;");

        // 질문 레이블 설정
        questionLabel = new Label();
        questionLabel.setFont(new Font("Arial", 18));
        questionLabel.setWrapText(true);
        questionLabel.setAlignment(Pos.CENTER);

        // 버튼 패널
        HBox buttonPanel = new HBox(20);
        buttonPanel.setAlignment(Pos.CENTER);

        // 첫 번째 버튼
        option1Button = new Button();
        option1Button.setFont(new Font("Arial", 16));
        option1Button.setOnAction(e -> handleOptionSelection());

        // 두 번째 버튼
        option2Button = new Button();
        option2Button.setFont(new Font("Arial", 16));
        option2Button.setOnAction(e -> handleOptionSelection());

        buttonPanel.getChildren().addAll(option1Button, option2Button);

        // 메인 패널 구성
        mainPanel.getChildren().addAll(questionLabel, buttonPanel);

        // 첫 번째 질문과 버튼 텍스트 설정
        updateQuestion();

        // 장면 설정
        Scene scene = new Scene(mainPanel, 800, 600);
        primaryStage.setTitle("MBTI Questionnaire");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateQuestion() {
        int index = questionIndex.get();
        if (index < questions.length) {
            questionLabel.setText(questions[index]);
            option1Button.setText(options[index][0]);
            option2Button.setText(options[index][1]);
        } else {
            // 모든 질문이 끝났을 때
            questionLabel.setText("모든 질문이 완료되었습니다.");
            option1Button.setDisable(true);
            option2Button.setDisable(true);
        }
    }

    private void handleOptionSelection() {
        // 현재 질문 인덱스 증가 후 질문 업데이트
        questionIndex.incrementAndGet();
        updateQuestion();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
