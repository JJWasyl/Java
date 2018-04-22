import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class PongMain extends JFrame implements KeyListener, Runnable{

	/**
	 * Glowna klasa gry
	 */

	private static Image  image;
	private Graphics g;
	private static final String TITLE  = "Pong - Sieciowy";
	private static final int    WIDTH  = 800;
	private static final int    HEIGHT = 460;
	private String servername = "servername" , clientname = "clientname";

  //Deklaracja konstruktora
	public PongMain(){}

	@Override
	public void run() {
		this.setVisible(true);
		this.setTitle(TITLE);
		this.setSize(WIDTH,HEIGHT);
		this.setResizable(false);
		this.addKeyListener(this);
	}

	public static void main(String[] args){
		Toolkit tk = Toolkit.getDefaultToolkit();
		image = tk.getImage("background_pong.png");
		PongMain newGame = new PongMain();
		newGame.run();
	}


  //Narysowanie gry
	private Image createImage(){
	    BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	    g = bufferedImage.createGraphics();
	    g.fillRect(0, 0, WIDTH, HEIGHT);
	    g.drawImage(image,0, 0, this);
	    return bufferedImage;
	}

	@Override
	public void paint(Graphics g){
		g.drawImage(createImage(), 0, 20, this);
	}


  //KLawisze i obsluga ustawiania serwera i klienta
	@Override
	public void keyPressed(KeyEvent arg0) {
		int    keyCode = arg0.getKeyCode();
		String portAdd = null;
		String ipAdd   = null;

		//Stworzenie serwera
		if(keyCode==KeyEvent.VK_S){

			//Dodanie portu
			portAdd = JOptionPane.showInputDialog(null, "np. 1024", "Podaj port:", 1);

			//Alert
			if(portAdd!=null){
				if(!isPort(portAdd)){
					JOptionPane.showMessageDialog(null,"Bledny format portu", "BLAD!", 1);
				} else {
					//Ksywka gracza
					servername = JOptionPane.showInputDialog(null, "Ksywka:","Podaj ksywke:", 1);
					servername+="";

					//Jesli ksywka zla
					if(servername.length()>10 || servername.length()<3 ||
					   servername.startsWith("null")){

						JOptionPane.showMessageDialog(null,"Zly format ksywki","BLAD!", 1);
						} else {
        		//Tworzymy serwer
						PongServer myServer = new PongServer(servername,portAdd);
						Thread myServerT = new Thread(myServer);
						myServerT.start();
						this.setVisible(false);
					}
				}
			}
		}



		//Stworzenie klienta
		if(keyCode==KeyEvent.VK_C){

			//Wprowadzanie adresu IP
			ipAdd = JOptionPane.showInputDialog(null, "np. 127.0.0.1","Podaj adres IP:", 1);

			if(ipAdd!=null){

				//Bledne IP
				if(!isIPAddress(ipAdd)){
					JOptionPane.showMessageDialog(null, "Bledny format IP!", "BLAD:", 1);
				} else {
					//Wprowadzanie portu
					portAdd = JOptionPane.showInputDialog(null, "np. 1024","Podaj port:", 1);
					// Bledny port
					if(portAdd!=null){
						if(!isPort(portAdd)){
							JOptionPane.showMessageDialog(null,"Bledny format!", "BLAD!:", 1);
						} else {
							//Ksywka
							clientname = JOptionPane.showInputDialog(null,"Ksywka:", "Podaj Ksywke:", 1);
							clientname += "";

							//Bledna ksywka
							if(clientname.length()>10 || clientname.length()<3 ||
							   clientname.startsWith("null")){
								JOptionPane.showMessageDialog(null, "Zly format ksywki", "BLAD!", 1);
							} else {
								//Odpalanie klienta
								PongClient myClient = new PongClient(clientname, portAdd, ipAdd);
								Thread myClientT = new Thread(myClient);
								myClientT.start();
								this.setVisible(false);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent k){
	//pass
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		//pass
	}

	//Sprawdzanie portu
	private boolean isPort(String str) {
		Pattern pPattern = Pattern.compile("\\d{1,4}");
		return pPattern.matcher(str).matches();
	}

	//Sprawdzanie IP
	private boolean isIPAddress(String str) {
		Pattern ipPattern = Pattern.compile(
			                            "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
	  return ipPattern.matcher(str).matches();
	}
}
