import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JFrame;


public class PongClient extends JFrame implements KeyListener, Runnable,
                                                  WindowListener{

	/**
	 * Klasa klienta, mniej rozbudowany niz serwer
	 * Szczegolowe komentarze w klasie Serwera
	 * Tutaj mamy sporo kopiowania z serwera
	 */

	//Ramka
	private static final String TITLE  = "Pong - Klient";
	private static final int WIDTH  = 800;
	private static final int HEIGHT = 460;
	boolean isRunning = false;

	//Gracze
	private PlayerServer playerS;
	private PlayerClient playerC;
	private int bar_Width = 8;
	private int player_Height = 60;
	private int mPLAYER = 15;

	//Serwer do podpiecia
	private static Socket clientSoc;
	private int portAdd;
	private String ipAdd;
	private boolean reset = false;
	private int countS = 0;

	//Stale graficzne
	private Graphics g;
	private Font sFont = new Font("Georgia",Font.BOLD,88);
	private Font mFont = new Font("Georgia",Font.BOLD,25);
	private Font nFont = new Font("Georgia",Font.BOLD,16);
	private Font rFont = new Font("Georgia",Font.BOLD,18);
  private String[] message;


  //Konstruktor
	public PongClient(String clientname, String portAdd, String ipAdd){
		playerS = new PlayerServer();
		playerC = new PlayerClient(clientname);
		playerS.setName(clientname);

		this.ipAdd = ipAdd;
		this.portAdd = Integer.parseInt(portAdd);
		this.isRunning = true;

		this.setTitle(TITLE);
		this.setSize(WIDTH,HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		addKeyListener(this);
	}

  //Watek klienta
	@Override
	public void run() {
		//Polaczenie z serwerem
   	try {
    	System.out.println("Poszukiwanie serwera...\nLacze z: "+ipAdd+":"+portAdd);
      clientSoc = new Socket(ipAdd, portAdd);
    	System.out.println("Polaczono z serwerem...");

      if(clientSoc.isConnected()){
      	System.out.println("Pong");

        //Petla gry, strona klienta
        while(true){

        	ObjectOutputStream sendObj = new ObjectOutputStream(
					                                         clientSoc.getOutputStream());
        	sendObj.writeObject(playerC);
        	sendObj = null;

        	ObjectInputStream getObj = new ObjectInputStream(
					                                          clientSoc.getInputStream());
        	playerS = (PlayerServer) getObj.readObject();
        	getObj = null;

        	if(reset){
        		if(countS>5){
        			playerC.restart = false;
        			reset = false;
        			countS = 0;
        		}
        	}
        	countS++;
        	repaint();
        }

      } else {
      	System.out.println("Rozlaczono...");
      }
   } catch (Exception e) {System.out.println(e);}
	}


	//Grafika
	private Image createImage(){
		BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT,
			                                            BufferedImage.TYPE_INT_RGB);
	  g = bufferedImage.createGraphics();

    //Plansza
	  g.setColor(new Color(15,9,9));
	  g.fillRect(0, 0, WIDTH, HEIGHT);

		//Linie
	  g.setColor(Color.white);
	  g.fillRect(WIDTH/2-5, 0, 5, HEIGHT);
	  g.fillRect(WIDTH/2+5, 0, 5, HEIGHT);

    //Wyniki
	  g.setColor(new Color(228,38,36));
	  g.setFont(sFont);
	  g.drawString(""+playerS.getScoreS(), WIDTH/2-60, 120);
	  g.drawString(""+playerS.getScoreP(), WIDTH/2+15, 120);

    //Gracze
	  g.setFont(nFont);
	  g.setColor(Color.white);
	  g.drawString(playerS.getName(),WIDTH/10,HEIGHT-20);
	  g.drawString(playerC.getName(),600,HEIGHT-20);

    //Paletki
	  g.setColor(new Color(57,181,74));
	  g.fillRect(playerS.getX(), playerS.getY(), bar_Width, player_Height);
	  g.setColor(new Color(57,181,74));
	  g.fillRect(playerC.getX(), playerC.getY(), bar_Width, player_Height);

	  //Pilka
	  g.setColor(new Color(255,255,255));
	  g.fillOval(playerS.getBallx(), playerS.getBally(), 25, 25);
	  g.setColor(new Color(228,38,36));
	  g.fillOval(playerS.getBallx()+2, playerS.getBally()+2, 25-5, 25-5);

	  //Wiadomosci klient-serwer
	  message = playerS.getImessage().split("-");
	  g.setFont(mFont);
	  g.setColor(Color.white);
	  if(message.length!=0){
	  	g.drawString(message[0],WIDTH/4-31,HEIGHT/2+38);
	    if(message.length>1){
	    	if(message[1].length()>6){
	    		g.setFont(rFont);
	    		g.setColor(new Color(228,38,36));
	    		g.drawString(message[1],WIDTH/4-31,HEIGHT/2+100);
	    	}
	    }
	  }
	  return bufferedImage;
	}


	public void paint(Graphics g){
		g.drawImage(createImage(), 0, 0, this);
		playerC.ok = true;
	}

	//Obliczanie ruchu graczy
	public void playerUP(){
		if(playerC.getY() - mPLAYER > player_Height/2-5){
			playerC.setY(playerC.getY()-mPLAYER);
		}
	}

	public void playerDOWN(){
		if(playerC.getY() + mPLAYER < HEIGHT - player_Height - 15){
			playerC.setY(playerC.getY()+mPLAYER);
		}
	}


  //Klawisze
	@Override
	public void keyPressed(KeyEvent arg0){
		int keycode = arg0.getKeyCode();
		if(keycode == KeyEvent.VK_UP){
			playerUP();
			repaint();
		}

		if(keycode == KeyEvent.VK_DOWN){
			playerDOWN();
			repaint();
		}

		if(playerS.isRestart()){
			playerC.restart = true;
			reset = true;
		}

		if(keycode == KeyEvent.VK_ESCAPE || keycode == KeyEvent.VK_N && playerS.isRestart()){
			try {
				this.setVisible(false);
				clientSoc.close();
				System.exit(EXIT_ON_CLOSE);
			} catch (IOException e){
				e.printStackTrace();
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

	@Override
	public void windowActivated(WindowEvent arg0) {
		//pass
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		//pass
  }

  //Zamykanie watku
	@SuppressWarnings("deprecation")
	@Override
	public void windowClosing(WindowEvent arg0){
		Thread.currentThread().stop();
		this.setVisible(false);
		try {
			clientSoc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			//pass
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			//pass
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			//pass
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			//pass
	}
}
