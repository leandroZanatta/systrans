package br.com.lar.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;

public class TimerThread extends Thread {

	private JLabel lbdata;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public TimerThread(JLabel lbdata) {
		this.lbdata = lbdata;
	}

	@Override
	public void run() {

		while (true) {

			lbdata.setText(simpleDateFormat.format(new Date()));

			try {
				sleep(1000);

			} catch (InterruptedException e) {
				System.out.println("erro executando timer" + e.getLocalizedMessage());
			}
		}
	}

}
