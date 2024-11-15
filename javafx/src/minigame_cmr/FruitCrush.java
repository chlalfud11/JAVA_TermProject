package minigame_cmr;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class FruitCrush extends JFrame { // 메인 메뉴
	public static HashMap<String, Integer> user_result = new HashMap<String, Integer>();

	public static JLabel user_name = new JLabel("");
	public static JTextArea ta;

	JPanel inputid;
	JPanel start;
	JPanel result;
	CardLayout card;
	Container c;

	boolean start_g = false;

	public FruitCrush() { // 판 셋팅

		setTitle("candy");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		c = getContentPane();
		inputid = new JPanel();
		start = new JPanel();
		result = new JPanel();
		result.setLayout(new BorderLayout());
		card = new CardLayout();

		JButton start_button = new JButton("Start");
		JButton exit_button = new JButton("Exit");
		JButton result_button = new JButton("기록");
		JButton before_button = new JButton("이전");

		JLabel NewLabel = new JLabel("ID를 입력 하시오");
		JTextField text = new JTextField(10);

		ta = new JTextArea(7, 20);

		text.addActionListener(new ActionListener() { // 이름 입력
			public void actionPerformed(ActionEvent e) {
				JTextField t = (JTextField) e.getSource();
				user_name.setText(t.getText());
				t.setText("");
				Gaming.total = 0;
				new Gaming();
				start_g = true;
				card.next(c);
			}
		});

		inputid.add(NewLabel);
		inputid.add(text);

		start_button.addActionListener(new Start());
		exit_button.addActionListener(new Start());
		result_button.addActionListener(new Start());
		before_button.addActionListener(new Start());

		start.add(start_button);
		start.add(result_button);
		start.add(exit_button);
		result.add(before_button, BorderLayout.NORTH);
		result.add(ta, BorderLayout.CENTER);

		c.setLayout(card);
		c.add(start); // 시작화면
		c.add(inputid); // id 입력화면
		c.add(result); // 기록화면
		setSize(500, 400);
		setVisible(true);

		BlockDown bd = new BlockDown();
		Skill sk = new Skill();
		while (true) {
			if (start_g == false) {
				System.out.println("  ");
			} else {
				sk.specialBlock_create();
				bd.blockDown();
				bd.xCheck();
			}
		}

	}

	class Start implements ActionListener { // 시작화면
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Start")) { // id입력으로 이동
				card.next(c);
			}
			if (e.getActionCommand().equals("Exit")) { // 종료
				System.exit(0);
			}
			if (e.getActionCommand().equals("기록")) { // 기록
				card.next(c);
				card.next(c);
			}
			if (e.getActionCommand().equals("이전")) { // 이전
				card.next(c);
			}

		}

	}

	public static void main(String[] args) {
		System.out.println("Game START");
		new FruitCrush();

	}

}
