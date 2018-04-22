import java.io.Serializable;


public class PlayerClient implements Serializable {

  /**
	 * Dane gracza klient
	 */

	private String name ="";
	private int	x, y;
  boolean ok = false;
  boolean restart = false;

	public PlayerClient(String name){
		this.name = name;
		x = 740;
		y = 210;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return "Klient [name=" + name + ", x=" + x + ", y=" + y + "]";
	}
}
