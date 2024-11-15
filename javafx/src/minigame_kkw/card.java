package minigame_kkw;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class card implements ActionListener {
    JFrame f;
    int getsu = 2; // 처음은 2x2로 시작
    JButton[][] btn;
    int[][] answer;

    JButton firstClick = null;
    int firstRow = 0, firstCol = 0;
    int matchCount = 0; // 짝 맞춘 개수

    public card() {
        f = new JFrame("게임");
        initGame(); // 게임 초기화
    }

    void initGame() {
        f.getContentPane().removeAll(); // 기존 컴포넌트 제거
        f.revalidate();
        f.repaint();

        btn = new RoundedButton[getsu][getsu]; // 둥근 모서리 버튼 사용
        answer = new int[getsu][getsu];
        matchCount = 0;

        f.setLayout(new GridLayout(getsu, getsu));

        for (int i = 0; i < getsu; i++) {
            for (int j = 0; j < getsu; j++) {
                // 뒷면 이미지 아이콘을 로드하고 크기에 맞춰 버튼 크기를 설정
                ImageIcon backIcon = new ImageIcon("images/kkw/back_img.png");
                btn[i][j] = new RoundedButton(backIcon); // 둥근 모서리 버튼
                btn[i][j].setPreferredSize(new Dimension(backIcon.getIconWidth(), backIcon.getIconHeight())); // 이미지 크기에 맞춰 버튼 크기 설정
                f.add(btn[i][j]);
                answer[i][j] = '0';
                btn[i][j].addActionListener(this);
            }
        }

        initChar();
        f.pack(); // 컴포넌트에 맞게 창 크기를 조정
        f.setSize(800, 800); // 창 크기를 800x800으로 설정
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void initChar() {
        int alpha = 0;
        DASI: for (int i = 0; i < getsu * getsu;) {
            if (i % 2 == 0) {
                alpha = (int) (Math.random() * (getsu * getsu / 2));

                for (int r = 0; r < getsu; r++) {
                    for (int c = 0; c < getsu; c++) {
                        if (answer[r][c] == alpha) continue DASI;
                    }
                }
            }

            boolean ok = false;
            do {
                int row = (int) (Math.random() * getsu);
                int col = (int) (Math.random() * getsu);
                if (answer[row][col] == '0') {
                    answer[row][col] = alpha;
                    i++;
                    ok = true;
                }
            } while (!ok);
        }
    }

    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();

        for (int i = 0; i < getsu; i++) {
            for (int j = 0; j < getsu; j++) {
                if (b == btn[i][j]) {
                    // 이미 맞춘 카드(회색 카드)는 클릭되지 않도록 처리
                    if (btn[i][j].getBackground() == Color.gray) {
                        return; // 이미 짝이 맞춰진 카드이면 아무 동작도 하지 않음
                    }

                    if (firstClick == null) { // 첫 번째 클릭
                        firstClick = b;
                        firstRow = i;
                        firstCol = j;
                        ImageIcon frontIcon = new ImageIcon("images/kkw/cardimg" + answer[firstRow][firstCol] + ".png");
                        firstClick.setIcon(frontIcon); // 앞면 이미지
                        firstClick.setPreferredSize(new Dimension(frontIcon.getIconWidth(), frontIcon.getIconHeight())); // 이미지 크기에 맞춰 버튼 크기 설정
                    } else { // 두 번째 클릭
                        if (b == firstClick) {
                            // 같은 버튼 클릭 시 무시
                            return;
                        }
                        ImageIcon frontIcon = new ImageIcon("images/kkw/cardimg" + answer[i][j] + ".png");
                        b.setIcon(frontIcon); // 앞면 이미지
                        b.setPreferredSize(new Dimension(frontIcon.getIconWidth(), frontIcon.getIconHeight())); // 이미지 크기에 맞춰 버튼 크기 설정

                        if (answer[i][j] == answer[firstRow][firstCol]) { // 일치할 때
                            firstClick.setBackground(Color.gray); // 카드 비활성화
                            b.setBackground(Color.gray); // 카드 비활성화
                            firstClick.setEnabled(false); // 더 이상 클릭되지 않도록 비활성화
                            b.setEnabled(false); // 더 이상 클릭되지 않도록 비활성화
                            matchCount++; // 짝 맞추기 성공

                            if (matchCount == getsu * getsu / 2) { // 모든 짝을 맞췄다면
                                nextLevel();
                            }
                            firstClick = null; // 첫 번째 클릭 초기화
                        } else { // 불일치할 때
                            // 두 번째 클릭을 로컬 변수로 저장
                            JButton tempFirst = firstClick;
                            JButton tempSecond = b;

                            Timer timer = new Timer(500, new ActionListener() { // 0.5초 후 카드 다시 뒤집기
                                public void actionPerformed(ActionEvent evt) {
                                    tempFirst.setIcon(new ImageIcon("images/kkw/back_img.png")); // 다시 뒷면 이미지로
                                    tempSecond.setIcon(new ImageIcon("images/kkw/back_img.png")); // 다시 뒷면 이미지로
                                }
                            });
                            timer.setRepeats(false);
                            timer.start();

                            firstClick = null; // 첫 번째 클릭 초기화
                        }
                    }
                }
            }
        }
    }

    void nextLevel() {
        if (getsu < 8) { // 최대 8x8까지
            getsu += 2; // 크기 증가
            JOptionPane.showMessageDialog(f, "다음 레벨로 이동합니다! " + getsu + "x" + getsu);
            initGame(); // 게임 재시작
        } else {
            JOptionPane.showMessageDialog(f, "모든 레벨을 완료했습니다! 게임을 성공적으로 마쳤습니다.");
            System.exit(0); // 게임 종료
        }
    }

    public static void main(String[] args) {
        new card();
    }
}

// 둥근 모서리를 가진 버튼 클래스
class RoundedButton extends JButton {
    public RoundedButton(Icon icon) {
        super(icon);
        setContentAreaFilled(false); // 배경 비활성화
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 안티앨리어싱 활성화
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // 둥근 모서리 사각형 (모서리 둥글기 조정 가능)
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30); // 둥근 모서리 경계선
        g2.dispose();
    }
}
