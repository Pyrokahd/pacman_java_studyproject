import java.util.Scanner;


//import java.awt.Robot; //Hier um Entertasten-druck zu simulieren -> gescheiterter Versuch
//import java.awt.event.KeyEvent; //same
//import java.awt.AWTException;

//import javax.sound.sampled.AudioInputStream; //Audio zeug
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Clip;
//import java.io.File;


public class UI 
{
	//controller Object
	private Controller controller = new Controller(this);
	private Scanner scanner = new Scanner(System.in);
	private int max_x = 30;
	private int max_y = 13;
	
	public static void main(String[] args) 
	{
		//Spielregeln
		System.out.println("Use 'w,a,s,d' (then enter) to move and collect the points.");
		System.out.println("If you touch a Ghost $,&,%,€ you die.");
		System.out.println("---------------------------------------------");
		
		UI ui = new UI(); //Object von UI erstellen
		// Map Größe
		System.out.println("Standard size (30/13)? y/n");
		String answer = ui.scanner.nextLine();
		if(answer.equals("y"))
		{
			System.out.println("initializing standard map...");
		}
		else if (answer.equals("n"))
		{
			  
			try //try / catch Angaben können Weggelassen werden, sorgen aber dafür, dass bei eingabe von z.B. Text anstatt Zahlen ein Error ausgelöst wird
			{
			System.out.println("Eingabe von max X (min 5): ");
			ui.max_x = Integer.parseInt(ui.scanner.nextLine());
			System.out.println("Eingabe von max Y (min 5): ");
			ui.max_y = Integer.parseInt(ui.scanner.nextLine());
			}
			catch (NumberFormatException e)
			{
				System.out.println("Error: Invalid Input! Standard size is initialised");
			}
			//Mindestgrößte von 5 durchsetzen
			if(ui.max_x < 5) 
			{
				ui.max_x = 5; 
				System.out.println("X Value was to small: 5 is used!");
			}
			if(ui.max_y < 5) 
			{
				ui.max_y = 5; 
				System.out.println("Y Value was to small: 5 is used!");
			}
		}
		///////////////////////////////////////////////////////
		// Map erstellen
		ui.controller.createWorld(ui.max_x,ui.max_y);
		System.out.println("map created...");
		
		ui.controller.checkStaticCollisions();
		ui.refreshUI();
		
		///////////////////////////////////////////////////////
		//eigentliches Spiel beginnt und läuft bis man stirbt oder gewonnen hat
		while(ui.controller.player.getAlive() && !ui.controller.gewonnen())
		{
			
			
			// --><-- Input handling --><--
			boolean rightInput=false;
			String input = new String();
			while(!rightInput)
			{
				System.out.println("waiting for Input (wasd)...");
				input = ui.scanner.nextLine();
				
				if(input.equals("w")||input.equals("a")||input.equals("s")||input.equals("d"))
				{
					rightInput=true;
				}
				else System.out.println("Error: Invalid Input!");
			}
			// MOVE PLAYER
			
			if(!input.isEmpty()) ui.controller.movePlayer(input);
			// --><-- Input done --><--
			// ---geister movement---
			System.out.println("GeisterMovements");
			ui.controller.moveGhosts(ui.controller.getGhostRandom()); 
			System.out.println("Random Ghost fertig");
			ui.controller.moveGhosts(ui.controller.getGhostShortest()); 
			ui.controller.moveGhosts(ui.controller.getGhostLongest()); 
			ui.controller.moveGhosts(ui.controller.getGhostRunaway());
			System.out.println("GeisterMovements fertig");
			// ---geister movement---
			
			ui.refreshUI();
			ui.controller.checkStaticCollisions();
		}
		if(ui.controller.gewonnen()) System.out.println("Du hast gewonnen :D  Score: "+ui.controller.getScore());
		
		ui.scanner.close();
// Audio Datei Versuche: (gab keine Errors, jedoch war nichts zu hören)		
//		try
//		{
//			Clip clip;
//			File soundFile = new File( "WeCanDance.wav" );
//			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( soundFile );
//			clip = AudioSystem.getClip();
//			clip.open(audioInputStream);
//			clip.start();//This plays the audio
//		}
//		catch (Exception exc)
//	    {
//			exc.printStackTrace(System.out);
//			System.out.println("fehler");
//	    }
		
	}
	
	/**
	 * Draws the Whole map and every Object on it.
	 */
	private void refreshUI()
	{
		
		// Schleife
		int[][] _world = controller.getWorld();
		
		//Loop durch alle Array felder
		for(int y=0;y < max_y;y++) //X
		{
			for(int x=0;x<max_x;x++) //y
			{
				// compare key um nach Fruits zu suchen und diese darzustellen
				
				String compareKey = (x+"|"+y);
				if (_world[x][y]==1)
				{
					//Spawn Wall
					System.out.print("\u2588"); //Unicode zeichencode    //"\u25A0"); 
					//25A0
				}
				// GEISTER darstellen
				else if(x==controller.getGhostRandom().getX() && y == controller.getGhostRandom().getY())
				{
					System.out.print("$");
				}
				else if(x==controller.getGhostShortest().getX() && y == controller.getGhostShortest().getY())
				{
					System.out.print("&");
				}
				else if(x==controller.getGhostLongest().getX() && y == controller.getGhostLongest().getY())
				{
					System.out.print("%");
				}
				else if(x==controller.getGhostRunaway().getX() && y == controller.getGhostRunaway().getY())
				{
					System.out.print("€");
				}
				// Spieler darstellen
				else if(x == controller.player.getX() && y == controller.player.getY())
				{
					System.out.print("@"); //\u25B8
				}
				// Früchte darstellen
				else if(controller.fruitMap.containsKey(compareKey))
				{
					System.out.print(".");
				}		
				//wenn keine Frucht da ist (und keine Wand) nichts darstellen
				else if(_world[x][y] == 0)
				{
					System.out.print(" ");
				}
			}
			System.out.println(); // Zeilenumbruch nach jeder Reihe
		}
		//Print Score
		System.out.println("Score: "+controller.getScore());
		System.out.println();
	}
	
	
	
}

