import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestionScreen extends JFrame {
    private JPanel mainPanel;
    private JTextPane questionText;
    private JButton option1Button;
    private JButton option2Button;
    private AtomicInteger questionIndex = new AtomicInteger(0);

    // 질문과 답변을 위한 배열
    private String[] questions = {
            "너는 성장하는 데에 있어 혼자만의 시간이 중요하다고 생각해?",
            "너는 갈등 상황에서 차분하게 이 상황을 분석하려고 해?"
    };
    private String[][] options = {
            {"혼자만의 시간이 중요해", "다른 사람들과 함께하는 게 중요해"},
            {"차분하게 분석하려고 해", "상대방의 감정을 먼저 생각해"}
    };

    public QuestionScreen() {
        // 기본 설정
        setTitle("MBTI Questionnaire");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel(new BorderLayout());

        // 질문 표시할 텍스트 패널 설정
        questionText = new JTextPane();
        questionText.setFont(new Font("Arial", Font.PLAIN, 18));
        questionText.setEditable(false);
        questionText.setOpaque(false);
        mainPanel.add(questionText, BorderLayout.NORTH);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // 첫 번째 버튼
        option1Button = new JButton();
        option1Button.setFont(new Font("Arial", Font.BOLD, 16));
        option1Button.addActionListener(new OptionButtonListener());

        // 두 번째 버튼
        option2Button = new JButton();
        option2Button.setFont(new Font("Arial", Font.BOLD, 16));
        option2Button.addActionListener(new OptionButtonListener());

        buttonPanel.add(option1Button);
        buttonPanel.add(option2Button);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // 첫 번째 질문과 버튼 텍스트 설정
        updateQuestion();

        // 메인 패널 추가
        add(mainPanel);
        setVisible(true);
    }

    private void updateQuestion() {
        int index = questionIndex.get();
        if (index < questions.length) {
            questionText.setText(questions[index]);
            option1Button.setText(options[index][0]);
            option2Button.setText(options[index][1]);
        } else {
            // 모든 질문이 끝났을 때 행동
            questionText.setText("모든 질문이 완료되었습니다.");
            option1Button.setEnabled(false);
            option2Button.setEnabled(false);
        }
    }

    private class OptionButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 현재 질문 인덱스 증가하고 다음 질문 업데이트
            questionIndex.incrementAndGet();
            updateQuestion();
        }
    }

    public static void main(String[] args) {
        new QuestionScreen();
    }
}

