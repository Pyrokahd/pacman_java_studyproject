import java.util.Random;
import java.util.HashMap;
import java.util.Map;


public class Controller 
{
	//Key , Value
	//früher Dictionary... verbindet 2 Werte miteinander
	public Map<String, Fruit> fruitMap = new HashMap<String, Fruit>();
	
	//sozusagen eine Variable vom Typ direction. Welche die Werte north east south oder west haben kann
	//public static, da ich das enum in den Ghost-Klassen verwende
	public static enum direction 
	{
		north, east, south, west
	}
	
	//Die Objekte, welche hier genutzt werden
	//Werden aber erst mit der fillWorld() Methode initialisiert
	public Player player; //public, da wir eine Player Methode in UI aufrufen
	private GhostRandom ghostRandom;
	private GhostShortest ghostShortest;
	private GhostLongest ghostLongest;
	private GhostWallPassing ghostWallPassing;
	
	Random randomGen = new Random();
	private UI ui;
	
	private int[][] world; //Die gesamte Spielwelt
	private int lifes = 3;
	private int score;
	
	//Constructor
	public Controller(UI _ui)
	{
		ui = _ui;
	}
	
	
	/**
	 * Creates a "World" as Array[][] with these values<br>
	 * It Also crates a line of "Walls" around it (Value 1)
	 * @param _x (int) 	length
	 * @param _y (int) 	height
	 *
 	 */
	public void createWorld(int _x, int _y)
	{
		
		world = new int[_x][_y];
		int xMin = 0; //eigentlich unnötig aber zur besseren verständlichkeit
		int yMin = 0;
		int offset = 0;
		boolean lückenLassen = false;
		int randomLücke = 0;
		
		// Wird gebraucht, damit bei gerader Y Anzahl keine Wände in die Mitte gespawnt werden.
		// Tests zeigten, dass sonst unerreichbare gebiete entstehen können (das "<=" bei (im while header) zu einem "<" hätte den gleichen effekt, ist aber aufwendiger)
		int yGerade =0;
		if(_y%2==0) yGerade = 1; 
		
		//_y entspricht maximal Wert von Y im array, gleiches gilt für _x
		//Walls solange in einem Viereck spawnen bis halt nimme geht
		while(xMin+offset < _x/2 && yMin+offset <= _y/2-yGerade) //bedenke: wird abgerundet und Array mit der länge 6/2 = 3, was aber dem 4. Feld vom array entspricht (0,1,2,3) deshalb -1
		{
			for(int n = 0+offset; n < _x-offset;n++) // Obereste Reihe auf 1 setzen
			{
				//Obere Reihe
				//Y = 0 | X = n
				world[n][yMin+offset] = 1;
			}
				//Lücken im Wall spawnen
				if(lückenLassen)
				{ 
					int Anzahl = Math.round(((_x-offset)-offset)/5f)+1; //5f um bei z.B. 9/5 1.8 zubekommen, was dann auf 2 gerundet wird. (Sonst wäre es nur 1)
					for(int a=0;a<Anzahl;a++)
					{
						randomLücke = randomGen.nextInt(_x-2*offset)+offset;
						world[randomLücke][yMin+offset] = 0;
					}
				}
			for (int n = 0+offset; n < _x-offset;n++) // Unterste Reihe auf 1 setzen
			{
				//Untere Reihe
				//Y =_y | X = n
				world[n][_y-1-offset] = 1;
			}
				//Lücken im Wall spawnen
				if(lückenLassen)
				{ 
					int Anzahl = Math.round(((_x-offset)-offset)/5f)+1;
					for(int a=0;a<Anzahl;a++)
					{
						randomLücke = randomGen.nextInt(_x-2*offset)+offset;
						world[randomLücke][_y-1-offset] = 0;
					}
				}
			for(int n = 0+offset; n < _y-offset;n++) // Linke Reihe auf 1 setzen
			{
				//Linke Reihe
				//Y = 0 | X = n
				world[xMin+offset][n] = 1;
			}
				//Lücken im Wall spawnen
				if(lückenLassen)
				{ 
					int Anzahl = Math.round(((_y-offset)-offset)/3f);
					for(int a=0;a<Anzahl;a++)
					{
						randomLücke = randomGen.nextInt(_y-2*offset)+offset;
						world[xMin+offset][randomLücke] = 0;
					}
				}
			for (int n = 0+offset; n < _y-offset;n++) // Rechte Reihe auf 1 setzen
			{
				//Rechte Reihe
				//Y =_y | X = n
				world[_x-1-offset][n] = 1;
			}
				//Lücken im Wall spawnen
				if(lückenLassen)
				{ 
					int Anzahl = Math.round(((_y-offset)-offset)/3f);
					for(int a=0;a<Anzahl;a++)
					{
						randomLücke = randomGen.nextInt(_y-2*offset)+offset;
						world[_x-1-offset][randomLücke] = 0;
					}
				}
			offset= offset +2;
			lückenLassen = true; //beginnt also erst ab 2. durchlauf 
		}
		fillWorld(_x,_y,world); //Welt füllen, wenn Wände erstellt wurden
								//eine extra Methode dafür ist zwar unnötig aber übersichtilcher
		
	}
	  
	/**
	 *  fills the created World with fruits, best to call it within createWorld()
	 * @param max_x (int) length
	 * @param max_y (int) height
	 * @param _world (int[][]) Which world should be filled?
	 */
	public void fillWorld(int max_x, int max_y, int[][] _world)
	{
		// SPAWN FRUITS
		//TODO welt Superfruits füllen
		//Loop durchdas gesamte Array. Überall wo keine wand ist wird eine Frucht platziert
		//Diese wird in der fruitMap mit eine String (bestehend aus dem X und Y Wert und einem Trennstrich z.B. "1|1")
		//verknüpft um diese später abfragen zu können
		for(int y=0;y < max_y;y++) //x
		{
			for(int x=0;x<max_x;x++) //y
			{
				if (_world[x][y]==0)
				{
					Fruit fruit = new Fruit(x,y);
					String key = (x+"|"+y);
					fruitMap.put(key,fruit);
					
				}
				
			}
		}
		// SPAWN PLAYER
		// NOTE Maybe random spawn points ?
		player = new Player(1,1); //Obe links
		
		// SPAWN GHOSTS
		//X-Spawn mittig -1 (da array bei 0 beginnt)
		int ghostSpawnX = max_x/2-1;
		int ghostSpawnY;
		//Y-Spawn Unterhalb der mittleren Wand, bei ungeraden max_y. Sonst in der mitte bzw die "obere Mitte"
		if(max_y%2 == 0) //gerade
		{
			// falls die Mitte auß einer Doppelmauer besteht
			if(_world[max_x/2-1][max_y/2]==1)
			{
				ghostSpawnY = max_y/2-2;  //Über der Doppelmauer
			}
			else 
			{
				ghostSpawnY = max_y/2-1; //Sonst in der oberen der 2 Reihen
			}
		}
		else //ungerade
		{
			//Wenn im mittlersten Feld eine Wand ist (dann auch in der ganzen Reihe), dann den Spawn um 1 nach unten verschieben
			//FIXME die ganze Reihe muss beachtet werden nicht nur das mittlerste FELD ! 
			if(_world[max_x/2-1][max_y/2]==1) 
			{
				ghostSpawnY = max_y/2+1; //wird abgerundet (auf die Reihe der mitte, deshalb +1 falls dort Wand ist um auf unterhalb der Mitte zu kommen)
			}
			else
			{
				ghostSpawnY = max_y/2; //Falls die Genaue Mitte keine Wand hat (z.B. 13/2 = abgerundet 6 [int rundet ab], was genau der mitte im array entspricht)
			}
		}
		
		// Nur Zur sicherheit, falls der Geist in einem Wall gespawnt werden würde. (solange random Array-Wert, bis es passt)
		// quasi falls alle anderen Abfragen versagen
		while(_world[ghostSpawnX][ghostSpawnY]==1)
		{
			ghostSpawnX=randomGen.nextInt(max_x);
			ghostSpawnY=randomGen.nextInt(max_y);
		}
		//ansonsten Spawne den Geist und setze seine X / Y Werte auf den Spawnpunkt
		ghostRandom = new GhostRandom();
		ghostRandom.setX(ghostSpawnX);
		ghostRandom.setY(ghostSpawnY);
		ghostSpawnX++; //Um 1 nach rechts Versetzen, um den Geist neben den vorherigen zu Spawnen
		// Nur Zur sicherheit, falls der Geist in einem Wall gespawnt werden würde. (solange random Array-Wert, bis es passt)
		while(_world[ghostSpawnX][ghostSpawnY]==1)
		{
			ghostSpawnX=randomGen.nextInt(max_x);
			ghostSpawnY=randomGen.nextInt(max_y);
		}
		//ansonsten Spawne den Geist und setze seine X / Y Werte auf den Spawnpunkt
		ghostShortest = new GhostShortest();
		ghostShortest.setX(ghostSpawnX);
		ghostShortest.setY(ghostSpawnY); 
		ghostSpawnX++; //Um 1 nach rechts Versetzen, um den Geist neben den vorherigen zu Spawnen
		// Nur Zur sicherheit, falls der Geist in einem Wall gespawnt werden würde. (solange random Array-Wert, bis es passt)
		while(_world[ghostSpawnX][ghostSpawnY]==1)
		{
			ghostSpawnX=randomGen.nextInt(max_x);
			ghostSpawnY=randomGen.nextInt(max_y);
		}
		//ansonsten Spawne den Geist und setze seine X / Y Werte auf den Spawnpunkt
		ghostLongest = new GhostLongest();
		ghostLongest.setX(ghostSpawnX);
		ghostLongest.setY(ghostSpawnY);
		ghostSpawnX++; //Um 1 nach rechts Versetzen, um den Geist neben den vorherigen zu Spawnen
		// Nur Zur sicherheit, falls der Geist in einem Wall gespawnt werden würde. (solange random Array-Wert, bis es passt)
		while(_world[ghostSpawnX][ghostSpawnY]==1)
		{
			ghostSpawnX=randomGen.nextInt(max_x);
			ghostSpawnY=randomGen.nextInt(max_y);
		}
		//ansonsten Spawne den Geist und setze seine X / Y Werte auf den Spawnpunkt
		ghostWallPassing = new GhostWallPassing();
		ghostWallPassing.setX(ghostSpawnX);
		ghostWallPassing.setY(ghostSpawnY);
		//FIXME Ghosts können auf dem Spieler spawnen !
	}
	
	
	/**
	 * Checks for only static Collisions!<br>
	 * for example moving on a fruit or a Ghost
	 */
	public void checkStaticCollisions()
	{
					
		//Steht der player auf food?
		//Wenn ja Score erhöhen und fruit entfernen
		String key = (player.getX()+"|"+player.getY()).toString();
		if(fruitMap.containsKey(key))
		{
			setScore(getScore()+1);
			fruitMap.remove(key);
			
		} //TODO superfruit ?
		if(	(player.getX() == ghostRandom.getX() && player.getY() == ghostRandom.getY()) ||
			(player.getX() == ghostShortest.getX() && player.getY() == ghostShortest.getY()) ||
			(player.getX() == ghostLongest.getX() && player.getY() == ghostLongest.getY()) ||
			(player.getX() == ghostWallPassing.getX() && player.getY() == ghostWallPassing.getY()) )
		{
			player.setAlive(false);
			System.out.println("Du hast verloren =(");
		}
		
	} 
	
	// NOTE Could be changed so you can input a Object like Player/Ghost, then we wouldnt need a ghostWallCollision() methode
	
	/**
	 * Checks Players wall-collision (used before moving)<br>
	 * true if we collide<br>
	 * false if we dont collide
	 */
		// 	but (would require a Superclasse for Ghost and Player)
private boolean playerWallCollision(direction _direction)
	{
		boolean ausgabe = true;
		switch(_direction)
		{
		case north:
				if(world[player.getX()][player.getY()-1] == 1) ausgabe = true; //y-1
				else ausgabe = false;
			break;
		case east:
				if(world[player.getX()+1][player.getY()] == 1) ausgabe = true; //x+1
				else ausgabe = false;
			break;
		case south:
				if(world[player.getX()][player.getY()+1] == 1) ausgabe = true; //y+1
				else ausgabe = false;
			break;
		case west:
				if(world[player.getX()-1][player.getY()] == 1) ausgabe = true; //x-1
				else ausgabe = false;
			break;
		default:
			System.out.println("Keine Richtungsangabe! -> false ERROR in Controller class -> WallCollisions()");
			ausgabe = true;
		break;
		}
		
		return ausgabe;
	}
	
	/**
	 * same as playerWallCollision just for Ghosts.<br>
	 * Checks if he would collide with a wall before moving<br>
	 * uses the ghosts Position to check.
	 */
	private boolean ghostWallCollision(direction _direction, Ghost ghost)
	{ 
		boolean ausgabe = true;
		switch(_direction)
		{
		case north:
				if(world[ghost.getX()][ghost.getY()-1] == 1) ausgabe = true; //y-1
				else ausgabe = false;
			break;
		case east:
				if(world[ghost.getX()+1][ghost.getY()] == 1) ausgabe = true;//x+1
				else ausgabe = false;
			break;
		case south:
				if(world[ghost.getX()][ghost.getY()+1] == 1) ausgabe = true; //y+1
				else ausgabe = false;
			break;
		case west:
				if(world[ghost.getX()-1][ghost.getY()] == 1) ausgabe = true; //x-1
				else ausgabe = false;
			break;
		default:
			System.out.println("Keine Richtungsangabe! -> ERROR in Controller class -> ghostWallCollisions()");
			ausgabe = true;
		break;
		}
		
		return ausgabe;
	}
	
	// NOTE ich hätte die Methode auch einfach anderst nennen können, dient nur zur Übung von Überladungen
	// [anhand der Parameterübergabe wird entschieden welcher der Überladenen Methode genutzt wird]
	/**
	 * Eine "Überladete" Methode, nimm spezifische X und Y Werte für die Position in der Welt<br>
	 * anstatt die position vom Geist 
	 * @param _direction (direction enum parameter)
	 * @param xPos  (X fürs World[][] Array)
	 * @param yPos	(Y fürs World[][] Array)
	 * @return
	 */
	public boolean ghostWallCollision(direction _direction, int xPos, int yPos)
	{ 
		boolean ausgabe = true;
		switch(_direction)
		{
		case north:
				if(world[xPos][yPos-1] == 1) ausgabe = true; //y-1
				else ausgabe = false;
			break;
		case east:
				if(world[xPos+1][yPos] == 1) ausgabe = true;//x+1
				else ausgabe = false;
			break;
		case south:
				if(world[xPos][yPos+1] == 1) ausgabe = true; //y+1
				else ausgabe = false;
			break;
		case west:
				if(world[xPos-1][yPos] == 1) ausgabe = true; //x-1
				else ausgabe = false;
			break;
		default:
			System.out.println("Keine Richtungsangabe! -> ERROR in Controller class -> ghostWallCollisions()");
			ausgabe = true;
		break;
		}
		
		return ausgabe;
	}
	
	
	/**
	 * Moves Player according to the Input to N,E,S or W
	 * @param _input (input must be String w,a,s or d)
	 */
	public void movePlayer(String _input)
	{ 
	   String input = _input;
	   switch (input)
	   {
		   case "w":
			   		if(!playerWallCollision(direction.north)) //Wenn keine Collision stattfindet
			   		{
			   			player.setY(player.getY()-1);
			   		}
			   		else System.out.println("\nthere is a wall!");
			   break;
		   case "d":
			   		if(!playerWallCollision(direction.east))
			   		{
			   			player.setX(player.getX()+1);
			   		}
			   		else System.out.println("\nthere is a wall!");
			   break;
		   case "s":
		   			if(!playerWallCollision(direction.south))
			   		{
		   				player.setY(player.getY()+1);
			   		}
		   			else System.out.println("\nthere is a wall!");
			   break;
		   case "a":
			   		if(!playerWallCollision(direction.west))
			   		{
			   			player.setX(player.getX()-1);
			   		}
			   		else System.out.println("\nthere is a wall!");
			   break;
		   default:
   			   System.out.println("Default in Controller class -> checkInput! Maybe wrong input?");
			   break;
	   }
	   
	}
	
	
	/**
	 * Moves Ghost according to his calcMovementDir() Methode
	 * @param _ghost (Which ghost(type) it is)
	 */
	public void moveGhosts(Ghost _ghost)
	{
		// Wenn das _ghost Objekt NICHT "leer" ist
		if(_ghost != null)
		{
			//temporärer Richtungsspeicher
			direction _direction = _ghost.calcMovementDir(this); 
			//wenn da kein Hinderniss ist bewege den Geist
			//NOTE ist eine zusaätzliche Sicherheitsabfrage, theoretisch berechnet "calcMovementDir()" immer eine Richtung ohne Kollision
			//		Das widerum macht die erste ghostWallCollision() Methode eigentlich unnötig
			if(!ghostWallCollision(_direction, _ghost)) 
			{
				_ghost.moveGhost(_direction);
			}
			// _ghost.calcMovementDir() gibt einen direction Parameter aus, _ghost is das Object vom Typ Ghost
		}
		else
		{
			System.out.println("Error! _ghost [in Controller -> moveGhost()] variable is empty! Maybe the Ghost wasnt spawned? Or has no X/Y values");
		}
	}
	
	
	//#####################
	//Get bzw. Set Methoden
	//#####################
	
	/**
	 * Gibt das world[][] Array aus
	 * @return
	 */
	public int[][] getWorld()
	{
		return world;
	}
	public int getLifes()
	{
		return lifes;
	}
	public void setLifes(int _lifes)
	{
		lifes = _lifes;
	}
	public int getScore()
	{
		return score;
	}
	public void setScore(int _score)
	{
		score = _score;
	}
	public boolean gewonnen()
	{
		// Wenn es keine Früchte mehr gibt hat man gewonnen
		if(fruitMap.isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public GhostRandom getGhostRandom()
	{
	return ghostRandom;
	}
	public GhostShortest getGhostShortest()
	{
	return ghostShortest;
	}
	public GhostLongest getGhostLongest()
	{
	return ghostLongest;
	}
	public GhostWallPassing getGhostRunaway()
	{
	return ghostWallPassing;
	}

}
