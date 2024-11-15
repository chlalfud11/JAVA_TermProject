package minigame_cmr;

import javax.swing.ImageIcon;

public class Skill extends Gaming{
	public Skill() {
		super(10);
	}
	public void specialBlock_create() {
		int cnt = 0;
		for (int i = 0; i < btn.length; i++) {
			for (int j = 0; j < btn[i].length; j++) {
				if (btn[i][j].getIcon().equals(Gaming.x)) {
					cnt++;
					if (cnt == 5 && skill_count==0) {
						System.out.println("sk"+skill_count);
						btn[i][j - 2].setIcon(spe);
						skill_count++;
					}
				} else {
					cnt = 0;
				}
			}
		}
	}
	public void specialBlock_skill(int line) {
		for (int i = 0; i < btn.length; i++) {
			btn[line][i].setIcon(x);
			total += 10;
			score.setText(Integer.toString(total));
		}
	}

}
