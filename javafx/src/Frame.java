import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import minigame_cmr.Gaming;
import minigame_kkw.card;
 
public class Frame extends JFrame {
    private JButton startButton, nextButton;
    private JLabel backgroundLabel, storyLabel;
    private JTextPane storyText;
    private JTextPane MBTIText;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // 스토리 라인과 인덱스 관리
    private String[] storyLines = {
            "안녕? 내가 누군지 궁금해? 하지만 내가 누구인지는 아무도 몰라. 너의 선택과 행동을 통해 내가 누군인지 알 수 있어. 나를 도와줄래?",
            "내가 깨어나기 위해서는 너의 도움이 필요해! 지금부터 질문을 할게. 네가 이 질문에 답을 하면, 난 조금씩 깨어날 수 있어!"
    };
    private int currentLineIndex = 0;
    private Timer typingTimer;
    private String currentText = "";
    private int charIndex = 0;

    // MBTI 질문과 선택지 설정
    private String[] MBTILines = {
            "너는 성장하는 데에 있어 혼자만의 시간이 중요하다고 생각해? 아니면 다른 사람들과 함께하는 것이 중요하다고 생각하니?",
            "너는 갈등 상황에서 차분하게 이 상황을 분석하려고 해? 아니면 상대방의 감정을 먼저 생각하려고 하니?"
    };
    private String[][] MBTIOptions = {
            {"혼자만의 시간이 중요해", "사람들과 함께하는 게 중요해"},
            {"차분하게 분석하려고 해", "상대방의 감정을 먼저 생각해"}
    };
    private AtomicInteger currentMBTIIndex = new AtomicInteger(0); // 현재 질문 인덱스 관리

    public Frame() {
        setTitle("My Nature");
        setSize(520, 620);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout 설정
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        getContentPane().add(cardPanel);

        // 첫 번째 화면: Start 버튼 화면 설정
        JPanel startScreen = new JPanel(new BorderLayout());
        ImageIcon backgroundImg = new ImageIcon("images/MyNatureLogo.jpeg");
        backgroundLabel = new JLabel(backgroundImg);
        backgroundLabel.setLayout(new BorderLayout());

        startButton = new JButton(new ImageIcon("images/start.jpg"));
        startButton.setPreferredSize(new Dimension(50, 50));
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "storyScreen");
            showNextStoryLine();
        });
        backgroundLabel.add(startButton, BorderLayout.SOUTH);
        startScreen.add(backgroundLabel, BorderLayout.CENTER);
        cardPanel.add(startScreen, "startScreen");

        // 두 번째 화면: 스토리 화면 설정
        JPanel storyScreen = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Image background = new ImageIcon("images/storybackground.jpeg").getImage();
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        storyScreen.setOpaque(false);

        storyText = new JTextPane();
        storyText.setFont(loadCustomFont("src/font.ttf", 18));
        storyText.setEditable(false);
        storyText.setOpaque(false);

        StyledDocument doc = storyText.getStyledDocument();
        Style style = storyText.addStyle("padding", null);
        StyleConstants.setLeftIndent(style, 30);
        StyleConstants.setRightIndent(style, 30);
        StyleConstants.setSpaceAbove(style, 100);
        StyleConstants.setSpaceBelow(style, 10);
        storyText.setStyledDocument(doc);

        storyScreen.add(storyText, BorderLayout.CENTER);

        nextButton = new JButton("NEXT");
        nextButton.addActionListener(e -> showNextStoryLine());
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(nextButton);
        storyScreen.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(storyScreen, "storyScreen");

        // 세 번째 화면 - MBTI 선택 화면 설정
        JPanel MBTIScreen = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Image background = new ImageIcon("images/storybackground.jpeg").getImage();
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        MBTIScreen.setOpaque(false);

        // MBTI 질문 표시 패널
        MBTIText = new JTextPane();
        MBTIText.setFont(loadCustomFont("src/font.ttf", 20));
        MBTIText.setEditable(false);
        MBTIText.setOpaque(false);
        MBTIScreen.add(MBTIText, BorderLayout.NORTH);

        // 옵션 패널 설정
        JPanel optionsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        optionsPanel.setOpaque(false);

        // 버튼 설정 및 이벤트 핸들러 추가
        JButton option1Button = new JButton();
        JButton option2Button = new JButton();
        option1Button.setFont(loadCustomFont("src/font.ttf", 18));
        option2Button.setFont(loadCustomFont("src/font.ttf", 18));

        // 버튼의 반투명 효과 적용
        option1Button.setOpaque(true);  // 배경 불투명도 설정
        option1Button.setContentAreaFilled(true);  // 버튼 내부 채우기 제거
        option1Button.setBorderPainted(false);  // 버튼 테두리 제거

        option2Button.setOpaque(true);
        option2Button.setContentAreaFilled(true);
        option2Button.setBorderPainted(false);

        ActionListener optionListener = e -> {
            int index = currentMBTIIndex.getAndIncrement();
            if (index < MBTILines.length - 1) {
                MBTIText.setText(MBTILines[index + 1]);
                option1Button.setText(MBTIOptions[index + 1][0]);
                option2Button.setText(MBTIOptions[index + 1][1]);
            } else {
            	// 미니게임 버튼 화면 추가
            	addMinigameButton();
                // 마지막 질문 이후의 동작 설정
                cardLayout.show(cardPanel, "NextScreen"); // 다음 화면 또는 결과 화면으로 이동
            }
        };

        option1Button.addActionListener(optionListener);
        option2Button.addActionListener(optionListener);

        optionsPanel.add(option1Button);
        optionsPanel.add(option2Button);
        MBTIScreen.add(optionsPanel, BorderLayout.CENTER);

        // 초기 질문 및 선택지 텍스트 설정
        MBTIText.setText(MBTILines[0]);
        option1Button.setText(MBTIOptions[0][0]);
        option2Button.setText(MBTIOptions[0][1]);

        // 카드 패널에 MBTI 화면 추가
        cardPanel.add(MBTIScreen, "MBTIScreen");
    }

    // 폰트 로드 메서드
    private Font loadCustomFont(String fontPath, float size) {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Serif", Font.PLAIN, 15);
        }
    }
    
    // 미니게임 버튼 추가 메서드
    private void addMinigameButton() {
        JPanel nextScreen = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Image background = new ImageIcon("images/storybackground.jpeg").getImage();
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        nextScreen.setOpaque(false);
        
        // 첫 번째 미니게임 버튼 설정 - minigame_cmr.gaming
        JButton gamingButton = new JButton("Go to Gaming");
        gamingButton.setFont(loadCustomFont("src/font.ttf", 20));
        gamingButton.setOpaque(true);
        gamingButton.setContentAreaFilled(true);
        gamingButton.setBorderPainted(false);
        gamingButton.addActionListener(e -> new Gaming()); // 실제로 Gaming 클래스를 불러옵니다.


        // 두 번째 미니게임 버튼 설정 - minigame_kkw.card
        JButton cardButton = new JButton("Go to Card Game");
        cardButton.setFont(loadCustomFont("src/font.ttf", 20));
        cardButton.setOpaque(true);
        cardButton.setContentAreaFilled(true);
        cardButton.setBorderPainted(false);
        cardButton.addActionListener(e -> new card());
       
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(gamingButton);
        buttonPanel.add(cardButton);
        nextScreen.add(buttonPanel, BorderLayout.CENTER);

        cardPanel.add(nextScreen, "NextScreen");
    }

    // 스토리 타이핑 효과 출력 메서드
    private void showNextStoryLine() {
        if (currentLineIndex < storyLines.length) {
            currentText = storyLines[currentLineIndex];
            charIndex = 0;
            storyText.setText("");

            typingTimer = new Timer(50, e -> {
                if (charIndex < currentText.length()) {
                    storyText.setText(storyText.getText() + currentText.charAt(charIndex++));
                } else {
                    typingTimer.stop();
                }
            });
            typingTimer.start();
            currentLineIndex++;
        } else {
            cardLayout.show(cardPanel, "MBTIScreen");
        }
    }

    public static void main(String[] args) {
        new Frame();
    }
}
