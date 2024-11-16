package minigame_cmr;
/*게임이 실행이 되고 9*9 블럭 화면이 보이고 블럭이 서로 변경이 되는 기능 */
import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.Timer;

public class Gaming extends JFrame {
	
	Container c;
	JPanel cardPanel;
	
	static public JButton[][] btn = new JButton[9][9];
	
	int count = 0;
	int skill_count = 0;
	
	JButton[] btn_click = new JButton[2];
	ImageIcon first_img;
	ImageIcon second_img;

	static public ImageIcon spe;
	static public ImageIcon red;
	static public ImageIcon blue;
	static public ImageIcon green;
	static public ImageIcon yellow;
	static public ImageIcon x;
	
	static JLabel countDown = new JLabel("60");
	JLabel ti = new JLabel("남은 시간 : ");
	static JLabel score = new JLabel("0");
	int score1 = 0;
	static int total=0;
	JLabel sc = new JLabel("점수 : ");
	JLabel name = new JLabel("ID  : ");
	int cnt = 60;
	
	String usr_name=FruitCrush.user_name.getText();
	public Gaming(int x) {
		
	}
	public Gaming() {
		setTitle("gaming");
		
		spe = new ImageIcon("images/cmr/special.png");
		x = new ImageIcon("images/cmr/x.png");
		red = new ImageIcon("images/cmr/red.png");
		blue = new ImageIcon("images/cmr/blue.png");
		green = new ImageIcon("images/cmr/green.png");
		yellow = new ImageIcon("images/cmr/yellow.png");

		c = getContentPane();

		cardPanel = new JPanel();
		cardPanel.setLayout(null);

		for (int i = 0; i < btn.length; i++) {
			for (int j = 0; j < btn[i].length; j++) { // 판 셋팅
				int a = (int) (Math.random() * 4);
				if (a == 0) {
					btn[i][j] = new JButton(red);

				} else if (a == 1) {
					btn[i][j] = new JButton(blue);

				} else if (a == 2) {
					btn[i][j] = new JButton(green);

				} else if (a == 3) {
					btn[i][j] = new JButton(yellow);

				}

				btn[i][j].setSize(30, 28);
				btn[i][j].setLocation(j * 35, i * 32);

				cardPanel.add(btn[i][j]);

				btn[i][j].addActionListener(new MyActionListener());
			}
		}
		
		playSetting(); // 재귀함수 이용해서 블럭 색상 3개 이상을 계속 수정
		countDown(); // 60초 카운트다운 
		
		sc.setSize(100,40);
		sc.setLocation(330,60);
		score.setSize(40,40);
		score.setLocation(400,60);
		
		ti.setSize(100,40);
		ti.setLocation(330,20);
		countDown.setSize(40,40);
		countDown.setLocation(400,20);
		
		name.setSize(100,40);
		name.setLocation(330,100);
		FruitCrush.user_name.setSize(40,40);
		FruitCrush.user_name.setLocation(400,100);
		
		cardPanel.add(sc);
		cardPanel.add(score);
		cardPanel.add(ti);
		cardPanel.add(countDown);
		cardPanel.add(name);
		cardPanel.add(FruitCrush.user_name);

		
		c.add(cardPanel); // 게임화면

		setSize(500, 400);
		setVisible(true);

	}

	public void countDown() {

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (cnt >= 0) {
					String a = Integer.toString(cnt);
					countDown.setText(a);
					if (cnt == 0) {
						FruitCrush.user_result.put(usr_name, Integer.parseInt(Gaming.score.getText())); // 유저 이름과 기록 저장 
						Set<String> keys = FruitCrush.user_result.keySet();
						Iterator<String> it = keys.iterator();
						FruitCrush.ta.setText("");
						List<String> listKeySet = new ArrayList<>(FruitCrush.user_result.keySet());

						Collections.sort(listKeySet, (value1, value2) -> (FruitCrush.user_result.get(value2).compareTo(FruitCrush.user_result.get(value1))));
						
						int rank=1;
						for(String key : listKeySet) {
							FruitCrush.ta.append(rank+++"등 >>"+key +"님의 점수 : " + FruitCrush.user_result.get(key) +"\n");
						}
						score1 = 0;
						score.setText("0");
					}
					cnt--;
				} else {
					timer.cancel();
					dispose(); // 현재창만 제거 
				}

			}

		};
		timer.schedule(task, 1000, 1000);

	}

	public int playSetting() {
		int cnt = 0; // 3개인게 있는지 확인 변수
		for (int i = 0; i < btn.length; i++) { // x축 검사
			for (int j = 0; j < btn[i].length; j++) {

				if (j > 1 && btn[i][j].getIcon().equals(btn[i][j - 1].getIcon())) {
					if (btn[i][j - 1].getIcon().equals(btn[i][j - 2].getIcon())) {
						if (btn[i][j].getIcon().equals(red)) {
							cnt++;
							int random = (int) (Math.random() * 2);
							if (random == 1)
								btn[i][j].setIcon(blue);
							else
								btn[i][j].setIcon(yellow);

						} else if (btn[i][j].getIcon().equals(green)) {
							cnt++;
							int random = (int) (Math.random() * 2);
							if (random == 1)
								btn[i][j].setIcon(blue);
							else
								btn[i][j].setIcon(yellow);

						} else if (btn[i][j].getIcon().equals(blue)) {
							cnt++;
							int random = (int) (Math.random() * 2);
							if (random == 1)
								btn[i][j].setIcon(red);
							else
								btn[i][j].setIcon(green);

						} else if (btn[i][j].getIcon().equals(yellow)) {
							cnt++;
							int random = (int) (Math.random() * 2);
							if (random == 1)
								btn[i][j].setIcon(red);
							else
								btn[i][j].setIcon(green);

						}
					}
				}
			}
		}
		for (int i = 0; i < btn.length; i++) { // y축 검사
			for (int j = 0; j < btn[i].length; j++) {
				if (j > 1 && btn[j][i].getIcon().equals(btn[j - 1][i].getIcon())) {
					if (btn[j - 1][i].getIcon().equals(btn[j - 2][i].getIcon())) {
						if (btn[j][i].getIcon().equals(red)) {
							cnt++;
							int random = (int) (Math.random() * 2);
							if (random == 1)
								btn[j][i].setIcon(blue);
							else
								btn[j][i].setIcon(yellow);
							;

						} else if (btn[j][i].getIcon().equals(green)) {
							cnt++;
							int random = (int) (Math.random() * 2);
							if (random == 1)
								btn[j][i].setIcon(blue);
							else
								btn[j][i].setIcon(yellow);

						} else if (btn[j][i].getIcon().equals(blue)) {
							cnt++;
							int random = (int) (Math.random() * 2);
							if (random == 1)
								btn[j][i].setIcon(red);
							else
								btn[j][i].setIcon(green);

						} else if (btn[j][i].getIcon().equals(yellow)) {
							cnt++;
							int random = (int) (Math.random() * 2);
							if (random == 1)
								btn[j][i].setIcon(red);
							else
								btn[j][i].setIcon(green);

						}
					}
				}
			}
		}
		System.out.println("cnt : " + cnt);
		if (cnt != 0) {
			playSetting(); // 3개 이상인게 없을때 까지 재귀 함수 호출로 판을 셋팅
			System.out.println("재귀호출 ");
		}
		return cnt;

	}

	class MyActionListener implements ActionListener { // 블럭 변경
		@Override
		public void actionPerformed(ActionEvent e) {
			btn_click[count] = (JButton) e.getSource();
			int nee =0;
			Skill sz = new Skill();
			if (count == 0) { // 블럭 변경시 첫번째 클릭 블럭
				first_img = (ImageIcon) btn_click[0].getIcon();
				System.out.println("아이콘은 >> " + btn_click[0].getIcon());
				for (int i = 0; i < btn.length; i++) {
					for (int j = 0; j < btn[i].length; j++) {
						if (btn[i][j] == btn_click[0]) {
							nee = i;
							System.out.println("btn[" + i + "][" + j + "]" + " 입니다.");
						}
					
					}
				}
				if(btn_click[0].getIcon().equals(spe) && skill_count==0) {
					sz.specialBlock_skill(nee);
					count=0;
					skill_count++;
				}
				count++;
			} else { // 블럭 변경시 두번째 클릭 블럭
				second_img = (ImageIcon) btn_click[1].getIcon();
				int resultX = btn_click[1].getX() - btn_click[0].getX();
				int resultY = btn_click[1].getY() - btn_click[0].getY();

				if (Math.abs(resultX) >= 35 && Math.abs(resultY) >= 32) { // 두칸이상 클릭시 무효
					count = 0;
				} else if (Math.abs(resultX) == 35) {
					second_img = (ImageIcon) btn_click[1].getIcon();
					btn_click[1].setIcon(first_img); // 두번째 클릭된거의 색상을 첫번째 클릭 색상으로 변경 (교환)
					btn_click[0].setIcon(second_img);
					count = 0;
				} else if (Math.abs(resultY) == 32) {
					second_img = (ImageIcon) btn_click[1].getIcon();
					btn_click[1].setIcon(first_img);
					btn_click[0].setIcon(second_img);
					count = 0;
				} else { // 두칸이상 클릭시 무효
					count = 0;
				}
				BlockCheck a = new BlockCheck();
				a.blockCheckX(); // 블럭 체크
				a.blockCheckY();

			}
		}

	}


}
