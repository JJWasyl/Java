public class Ball extends Thread{

	/**
	 * Dane pilki
	 */

	private int x;
	private int y;
	private double dx;
	private double dy;
	private int radius;
	private int HEIGHT;
	private int WIDTH;

	@Override
	public void run() {
		while(true){
			move();
			try {
				sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Ball(int x, int y, double dx, double dy, int radius,
	            int WIDTH, int HEIGHT) {
		super();
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.radius = radius;
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
	}


	public void move(){
		if(x + dx > (WIDTH-radius) - 7){
			x= (WIDTH-radius)-7;
			dx = dx * -1;
		}

		if(x + dx < 9){
			x = 9;
			dx = dx *-1;
		}

		if(y + dy < radius/2+7){
			y = 29;
			dy = dy * -1;
		}

		if(y + dy > (HEIGHT - radius) - 6){
			y = (HEIGHT-radius)-6;
			dy = dy * -1;
		}
		x += dx;
		y += dy;
	}


	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public double getXv() {
		return dx;
	}
	public void setXv(double dx) {
		this.dx = dx;
	}
	public double getYv() {
		return dy;
	}
	public void setYv(double dy) {
		this.dy = dy;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}

}
