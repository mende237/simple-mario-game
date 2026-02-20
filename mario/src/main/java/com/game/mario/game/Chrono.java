package com.game.mario.game;

public class Chrono implements Runnable {
	private static String timeElapse;
	private long begin;
	private long end;

	static {
		setTimeElapse("%d min:%d s".formatted(00, 00));
	}

	public Chrono() {
		this.begin = System.currentTimeMillis();
	}

	/****************************** getter ****************************/
	public static String getTimeElapse() {
		return timeElapse;
	}

	/****************************** setter ****************************/
	public static void setTimeElapse(String timeElapse) {
		Chrono.timeElapse = timeElapse;
	}

	/***************************** methods ***************************/
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.end = System.currentTimeMillis();
			long length = this.end - this.begin;

			length /= 1000;
			int minute = (int) (length / 60);
			int seconde = (int) (length % 60);

			setTimeElapse("%d min:%d s".formatted(minute, seconde));
		}

	}

}
