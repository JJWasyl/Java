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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import javax.swing.JFrame;

/**
* Klasa Serwera gry
* Pilka jest obliczana tutaj
* Adres serwera jest ustalany tutaj
* To jest "prawdziwy" stan gry
*/

public class PongServer extends JFrame implements KeyListener, Runnable,
                                                  WindowListener{

  //Ramka
  private static final String TITLE = "Pong - Serwer";
  private static final int WIDTH = 800;
  private static final int HEIGHT = 460;

  //Zmienne do oczekiwania na gre
  boolean isRunning = false;
  boolean check = true;
  boolean initgame = false;

  Ball pilka;
  private PlayerServer playerS;
  private PlayerClient playerC;

  private int ballSpeed = 3;	//Predkosc pilki
  private int bar_Width = 8;	//Szerokosc paletki
  private int player_Height = 60;	//Slugosc Paletki
  private int max_Score = 11;	//Maksymalny wynik
  private int mPLAYER = 15;	//Predkosc paletki
  private boolean Restart = false;	//Check na restart gry
	private boolean restartON = false;

	//Socket Serwera
	private static Socket clientSoc = null;
	private static ServerSocket serverSoc = null;
	private int portAdd;

	//Wiadomosci gry
	private Graphics g;
	private Font sFont = new Font("Georgia",Font.BOLD,88);
	private Font mFont = new Font("Georgia",Font.BOLD,25);
	private Font nFont = new Font("Georgia",Font.BOLD,16);
	private Font rFont = new Font("Georgia",Font.BOLD,18);
  private String[] wiad;
  private Thread movB;

  private String getIP(){
		String ip = "";
		try{
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch(Exception e) {System.out.println(e);}
		return ip;
	}

  //Konstruktor
	public PongServer(String servername, String portAdd){

		//Klasy graczy
		playerS = new PlayerServer();
		playerC = new PlayerClient("");
		playerS.setName(servername);

  	//Ustawienie okna
		this.portAdd = Integer.parseInt(portAdd);
		this.isRunning = true;
		this.setTitle(TITLE+" IP: "+getIP());
		this.setSize(WIDTH,HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);

    //Listener
		addKeyListener(this);

		//Pilka
		pilka = new Ball(playerS.getBallx(), playerS.getBally(), ballSpeed,
		                 ballSpeed , 45, WIDTH, HEIGHT);

	}


 /**
	 * Zaczynamy watek tutaj
	 */

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
  	try{
   		serverSoc = new ServerSocket(portAdd);
      System.out.println("Adres IP serwera: "+
			                    InetAddress.getLocalHost().getHostAddress());
      System.out.println("Serwer uruchomiony na porcie: "+portAdd+
			                   "\nOczekiwanie na klienta...");
      System.out.println("Oczekiwanie...");
      playerS.setImessage("Oczekiwanie na drugiego gracza...");

      clientSoc = serverSoc.accept();
      System.out.println("Podlaczono gracza...");

      //Petla gry rusza przy podlaczeniu gracza
      if(clientSoc.isConnected()){
        boolean notchecked = true;
        movB = new Thread(pilka);

        while(true){

        		//Stan gry na koniec
        	if(playerS.getScoreP() >= max_Score ||
					   playerS.getScoreS()>= max_Score && Restart==false){

        		if(playerS.getScoreS()>playerS.getScoreP()){
        			playerS.setOmessage("Wygrana     Przegrana\nRestart?: Dowolny klawisz || Wyjscie: Esc|N");
        			playerS.setImessage("Wygrana     Przegrana\nRestart?: Dowolny klawisz || Wyjscie: Esc|N");
        			Restart = true;
        		} else {
        			playerS.setImessage("Przegrana   Wygrana\nRestart?: Dowolny klawisz || Wyjscie: Esc|N");
        			playerS.setOmessage("Przegrana   Wygrana\nRestart?: Dowolny klawisz || Wyjscie: Esc|N");
      				Restart = true;
        		}
            movB.suspend();
          }

         	//Sprawdzamy czy klient gotowy
				 	if(playerC.ok && notchecked){
	       		playerS.setImessage("");
          	movB.start();
          	notchecked = false;
         	}
				 	updateBall();

				 	//Strumien obiektow do zebrania z sieci
         	ObjectInputStream getObj = new ObjectInputStream(
					                                 clientSoc.getInputStream());

				 	playerC = (PlayerClient) getObj.readObject();
				 	getObj = null;

					//Strumien obiektow do wyslania w siec
					ObjectOutputStream sendObj = new ObjectOutputStream(
					                                 clientSoc.getOutputStream());

          sendObj.writeObject(playerS);
          sendObj = null;

          //Restart gry
          if(restartON){
						if(playerC.restart){
            	playerS.setScoreP(0);
            	playerS.setScoreS(0);
            	playerS.setOmessage("");
            	playerS.setImessage("");
            	Restart = false;
              playerS.setRestart(false);
              playerS.setBallx(380);
              playerS.setBally(230);
              pilka.setX(380);
              pilka.setY(230);
              movB.resume();
              restartON = false;
            }
          }
          repaint();
        }
      } else {
        System.out.println("Rozlaczono...");
      }
    } catch (Exception e) {System.out.println(e);}
	}


  //Grafika
	private Image createImage(){

		//Buffered Image do przetwarzania klatek
	  BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT,
		                                  BufferedImage.TYPE_INT_RGB);

	  g = bufferedImage.createGraphics();

	  //Tablica
	  g.setColor(new Color(15,9,9));
	  g.fillRect(0, 0, WIDTH, HEIGHT);

	  //Linie kortowe
	  g.setColor(Color.white);
	  g.fillRect(WIDTH/2-5, 0, 5, HEIGHT);
	  g.fillRect(WIDTH/2+5, 0, 5, HEIGHT);

	  //Wynik
	  g.setFont(sFont);
	  g.setColor(new Color(228,38,36));
	  g.drawString(""+playerS.getScoreS(), WIDTH/2-60, 120);
	  g.drawString(""+playerS.getScoreP(), WIDTH/2+15, 120);

	  //Ksywki graczy
	  g.setFont(nFont);
	  g.setColor(Color.white);
	  g.drawString(playerS.getName(),WIDTH/10,HEIGHT-20);
	  g.drawString(playerC.getName(),600,HEIGHT-20);

	  //Gracze, pozycje
	  g.setColor(new Color(57,181,74));
	  g.fillRect(playerS.getX(), playerS.getY(), bar_Width, player_Height);
	  g.setColor(new Color(57,181,74));
	  g.fillRect(playerC.getX(), playerC.getY(), bar_Width, player_Height);

	  //Pilka
	  g.setColor(new Color(255,255,255));
	  g.fillOval(playerS.getBallx(), playerS.getBally(), 25, 25);
	  g.setColor(new Color(228,38,36));
	  g.fillOval(playerS.getBallx()+2, playerS.getBally()+2, 25-5, 25-5);

	  //Wiadomosci serwer-klient
	  wiad = playerS.getImessage().split("-");
	  g.setFont(mFont);
	  g.setColor(Color.white);
	  if(wiad.length!=0){
	  	g.drawString(wiad[0],WIDTH/4-31,HEIGHT/2+38);
	  	if(wiad.length>1){
	  		if(wiad[1].length()>6){
	    		g.setFont(rFont);
	    		g.setColor(new Color(228,38,36));
	    		g.drawString(wiad[1],WIDTH/4-31,HEIGHT/2+100);
	    	}
	   	}
	 	}
		return bufferedImage;
	}

	@Override
	public void paint(Graphics g){
		g.drawImage(createImage(), 0, 0, this);
	}



  //Ruch pilki
	public void updateBall(){
		checkCol();
		playerS.setBallx(pilka.getX());
		playerS.setBally(pilka.getY());
	}


	//Ruch paletek
	public void playerUP(){
		if(playerS.getY() - mPLAYER > player_Height/2-5){
			playerS.setY(playerS.getY()-mPLAYER);
		}
	}

	public void playerDOWN(){
		if(playerS.getY() + mPLAYER < HEIGHT - player_Height - 15){
			playerS.setY(playerS.getY()+mPLAYER);
		}
	}

	//Sprawdzanie kolizji
	public void checkCol(){

		//Jesli wpadl punkt
		if(playerS.getBallx() < playerC.getX() &&
		   playerS.getBallx() > playerS.getX()){

			check = true;
		}

		//Punkt dla kogo
		if(playerS.getBallx()>playerC.getX() && check){
			playerS.setScoreS(playerS.getScoreS()+1);
			check = false;
		} else if(playerS.getBallx()<=playerS.getX() && check){
			playerS.setScoreP(playerS.getScoreP()+1);
			check = false;
		}

		//Trzeba sprawdzac pozycje pilki w serwerze i kliencie
    //Serwer
		if(pilka.getX()<=playerS.getX()+bar_Width &&
		   pilka.getY()+pilka.getRadius()>= playerS.getY() &&
			 pilka.getY()<=playerS.getY()+player_Height ){

		  pilka.setX(playerS.getX()+bar_Width);
			playerS.setBallx(playerS.getX()+bar_Width);
			pilka.setXv(pilka.getXv()*-1);
		}


		//Klient
		if(pilka.getX()+pilka.getRadius()>=playerC.getX() &&
		   pilka.getY() + pilka.getRadius() >= playerC.getY() &&
			 pilka.getY()<=playerC.getY()+player_Height ){

			pilka.setX(playerC.getX()-pilka.getRadius());
			playerS.setBallx(playerC.getX()-pilka.getRadius());
			pilka.setXv(pilka.getXv()*-1);
		}

	}

  //Zczytywanie klawiatury
	@Override
	public void keyPressed(KeyEvent arg0) {

		int keycode = arg0.getKeyCode();
		if(keycode == KeyEvent.VK_UP){
			playerUP();
			repaint();
		}

		if(keycode == KeyEvent.VK_DOWN){
			playerDOWN();
			repaint();
		}

		if(Restart == true){
			restartON = true;
			playerS.setRestart(true);
		}

		if(keycode == KeyEvent.VK_N || keycode == KeyEvent.VK_ESCAPE && Restart == true){

			try {
				this.setVisible(false);
				serverSoc.close();
				System.exit(EXIT_ON_CLOSE);
			} catch(IOException e){
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

  //Zamkniecie watku
	@SuppressWarnings("deprecation")
	@Override
	public void windowClosing(WindowEvent arg0) {
		Thread.currentThread().stop();
		this.setVisible(false);
		try {
			serverSoc.close();
		} catch(IOException e){
			e.printStackTrace();
		}
		System.exit(1);
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
