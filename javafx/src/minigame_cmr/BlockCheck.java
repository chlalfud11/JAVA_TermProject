package minigame_cmr;
/* 블럭들을 확인하여 3개 이상 연속된 블럭이 있으면 파괴시키는 역할  */
import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;

public class BlockCheck  extends Gaming{ // 똑같은 블럭 3개이상을 체크 하여 -> 블럭 파괴

	public BlockCheck(){
		super(11);
	}
	Vector<Integer> x_deletebtnX = new Vector<Integer>();
	Vector<Integer> x_deletebtnY = new Vector<Integer>();
	Vector<Integer> y_deletebtnX = new Vector<Integer>();
	Vector<Integer> y_deletebtnY = new Vector<Integer>();

	int cnt = 0;
	int y_n = 0;
	
	public void blockDelete_X() { // x축 3개 이상인 블럭 x 처리
		HashMap<JButton, Integer> hs = new HashMap<JButton, Integer>();

		Iterator<Integer> it = x_deletebtnX.iterator();
		Iterator<Integer> it1 = x_deletebtnY.iterator();
		int count = 0;
		while (it.hasNext() && it1.hasNext()) {
			int i = 0;
			int a = it.next();
			int b = it1.next();

			btn[a][b].setIcon(x); // 5개 같을 때 스페셜 버튼 만들기

			total += 1;
			score.setText(Integer.toString(total));

		}
	}

	public void blockDelete_Y() { // y축 3개 이상인 블럭x 처리
		Iterator<Integer> it = y_deletebtnX.iterator();
		Iterator<Integer> it1 = y_deletebtnY.iterator();
		while (it.hasNext() && it1.hasNext()) {
			int a = it.next();
			int b = it1.next();

			btn[a][b].setIcon(x);

			total += 1;
			score.setText(Integer.toString(total));
		}
	}

	public void blockCheckX() {
		boolean delete = false;
		for (int i = 0; i < btn.length; i++) {
			for (int j = 1; j < btn[i].length - 1; j++) {
				if (btn[i][j].getIcon().equals(btn[i][j - 1].getIcon())
						&& btn[i][j].getIcon().equals(btn[i][j + 1].getIcon())) {
					delete = true;

					x_deletebtnX.add(i); // x좌표 y좌표 하나씩 해시맵에 투입
					x_deletebtnX.add(i);
					x_deletebtnX.add(i);
					x_deletebtnY.add(j - 1);
					x_deletebtnY.add(j);
					x_deletebtnY.add(j + 1);
				}

			}
			if (delete == true) {
				blockDelete_X();
				delete = false;
			}
		}
	}

	public void blockCheckY() {

		boolean delete1 = false;
		for (int i = 0; i < btn.length; i++) {
			for (int j = 1; j < btn[i].length - 1; j++) {
				if (btn[j][i].getIcon().equals(btn[j - 1][i].getIcon())
						&& btn[j][i].getIcon().equals(btn[j + 1][i].getIcon())) {
					delete1 = true;

					y_deletebtnX.add(j - 1);
					y_deletebtnX.add(j);
					y_deletebtnX.add(j + 1);
					y_deletebtnY.add(i);
					y_deletebtnY.add(i);
					y_deletebtnY.add(i);
				}

			}
			if (delete1 == true) {
				blockDelete_Y();
				delete1 = false;
			}
		}
	}

}
