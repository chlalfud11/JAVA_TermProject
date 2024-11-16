package minigame_cmr;

import javax.swing.ImageIcon;

/* 파괴된 블럭들을 확인하고 내리는 역할 */
public class BlockDown extends Thread {// x 로 된 블럭을 찾아 블럭을 내리는 역할

	public void blockDown() {

		for (int i = 0; i < Gaming.btn.length; i++) {
			for (int j = 0; j < Gaming.btn[i].length; j++) {
				if (Gaming.btn[i][j].getIcon().equals(Gaming.x) && i > 0) {
					int n_y = Gaming.btn[i][j].getY();
					int y = Gaming.btn[i - 1][j].getY();
					int y1 = Gaming.btn[i - 1][j].getY();
					int x1 = Gaming.btn[i - 1][j].getX();
					while (true) {
						try {
							sleep(2);
							Gaming.btn[i - 1][j].setLocation(Gaming.btn[i - 1][j].getX(), y);
							y++;
							if (y == n_y + 1) {
								Gaming.btn[i][j].setIcon(Gaming.btn[i - 1][j].getIcon());
								Gaming.btn[i - 1][j].setLocation(x1, y1);
								Gaming.btn[i - 1][j].setIcon(Gaming.x);

								break;
							}

						} catch (InterruptedException e) {
							System.out.println("false");
							e.printStackTrace();
						}

					}
				}
			}
		}
	}

	public void xCheck() { // x가 파괴되고 내려가게 되면 맨위에서 생성이 필요
		for (int i = 0; i < Gaming.btn.length; i++) {
			if (Gaming.btn[0][i].getIcon().equals(Gaming.x)) {
				int a = (int) (Math.random() * 4);
				switch (a) {
				case 0:
					Gaming.btn[0][i].setIcon(Gaming.red);
					break;
				case 1:
					Gaming.btn[0][i].setIcon(Gaming.green);
					break;
				case 2:
					Gaming.btn[0][i].setIcon(Gaming.blue);
					break;
				case 3:
					Gaming.btn[0][i].setIcon(Gaming.yellow);
					break;
				}

			}
		}
	}

}
