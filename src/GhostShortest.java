import java.util.Random;

import javax.swing.JTable.PrintMode;

public class GhostShortest extends Ghost
{
	private int vorherigesFeldX = 0;
	private int vorherigesFeldY = 0;
	private int vorherigesFeldXSim =0;
	private int vorherigesFeldYSim =0;
	
	private Random randomGen = new Random();
	//NOTE Die Klasse ist totales Chaos. Viele gescheiterte Versuche den kürzesten Weg zum Spieler zu Calculieren	
	//Damit es notgedrungen wenigsten irgendwie Funktioniert wird nur die Methode calcMovementDir() benutzt [und moveGhost() , calcDirections()]
	//Auch viele der Variablen sind ungenutzt...
	public void moveGhost(Controller.direction _direction)
	{
		
		switch (_direction)
		{
			case north:
					this.setY(this.getY()-1); //1 nach oben
				break;
			case east:
					this.setX(this.getX()+1); //1 nach rechts
				break;
			case south:
					this.setY(this.getY()+1); //1 nach unten 
				break;
			case west:
					this.setX(this.getX()-1); //1 nach links 
				break;
		}
	}
	
	public Controller.direction calcMovementDir( Controller controller)
	{
		Controller.direction _direction; //Um ausgabe zwischenzuspeichern
		int _world[][] = controller.getWorld();
		
		while(true) //Dauerschleife ist möglich, da mit 'return' aus dem Loop gesprungen wird
		{
			int zufallsZahl = randomGen.nextInt(100)+1; //Random zahl zwischen 0 und 99 +1 also (1 bis 100)
			if(zufallsZahl <= 25 ) // = 25%
			{
				_direction = Controller.direction.north; 
			}
			else if (zufallsZahl > 25 && zufallsZahl <= 50) // 25%
			{
				_direction = Controller.direction.east;
			}
			else if (zufallsZahl > 50 && zufallsZahl <= 75) // 25%
			{
				_direction = Controller.direction.south;
			}
			else if (zufallsZahl > 75 && zufallsZahl <= 100) // 25%
			{
				_direction = Controller.direction.west;
			}
			else
			{
				System.out.println("ERROR: GhostRandom -> calcMovemetnDir(): kein zufallsWert hat gepasst, default north wurde genutzt");
				_direction = Controller.direction.north;
			}
			
			if( (_direction == Controller.direction.north && _world[this.getX()][this.getY()-1] != 1) ||  //Norden und nördlich ist keine Wand, oder
				(_direction == Controller.direction.east && _world[this.getX()+1][this.getY()]  != 1) || //Osten und östlich ist keine Wand, oder
				(_direction == Controller.direction.south && _world[this.getX()][this.getY()+1] != 1) || //Süden und südlich ist keine Wand, oder
				(_direction == Controller.direction.west && _world[this.getX()-1][this.getY()]  != 1) )	//Westen und westlich ist keine Wand
			{
				return _direction; //verlässt Methode mit entsprechendem Ausgabewert vom typ Controller.direction
			}
		}
	}
	
	private Controller.direction _directionXfirst = Controller.direction.east; //default Wert für Enum sollte überschrieben werden
	private Controller.direction _directionXsecond = Controller.direction.west; //default Wert für Enum sollte überschrieben werden
	private Controller.direction _directionYfirst = Controller.direction.north; //default Wert für Enum sollte überschrieben werden
	private Controller.direction _directionYsecond = Controller.direction.south; //default Wert für Enum sollte überschrieben werden
	
	//private Controller.direction wallPassDirection = Controller.direction.north; //Wird "überall" gesetzt aber nur von goAroundWall() genutzt. könnte auch in der Methode only sein.
	
	private Controller.direction _ausgabeDirection = Controller.direction.north; //Wird nur in goAroundWall() genutzt

	// speichert die 4 mögliche ausgaberichtungen
	private Controller.direction ausgabeDirection1 = Controller.direction.north; //default Wert sollte überschrieben werden
	private Controller.direction ausgabeDirection2 = Controller.direction.north; //default Wert sollte überschrieben werden
	private Controller.direction ausgabeDirection3 = Controller.direction.north; //default Wert sollte überschrieben werden
	private Controller.direction ausgabeDirection4 = Controller.direction.north; //default Wert sollte überschrieben werden

	
	//Um die Bewegung zum Sieler zu simulieren
	private int xSim; 
	private int ySim;
	
	//Benötigt um bei goAroundWall() durch die Lücke zu laufen;
	private boolean throughGapNorth;
	private boolean throughGapEast;
	private boolean throughGapSouth;
	private boolean throughGapWest;
	
	//2 Variablen als Speicher wo man nicht hin darf, weil man da schon war
	//sollte eigentlich keine 2 Variablen sein
	int alreadyX;
	int alreadyY;
	int alreadyX2;
	int alreadyY2;
	//FIXME also man setzt die Variable gleich dem dem Feld auf das man gelaufen ist. Und da darf man nächstes ma nicht hin
	//stattessen wird dann halt ne andere richtung genommen in den 4 hauptdingern ? wie siehts im goAroundWall() aus
	//kurz vor ausgabe ? 
	
	//Boolean um zu schauen ob wir schon ein Feld bei throughGap... Setzung gesetzt haben
	boolean fieldAlrdySet = false;
	
	// Berechnet die Richtung, welche den kürzesten Weg zum Spieler ergiebt
	public Controller.direction calcMovementDir2(Controller controller)
	{
		fieldAlrdySet = false; //Jeden durchgang darf darf das einmal gesetzt werde
		
		// #Es geht darum, in welcher Reihenfolge man die Richtungen am besten abfrägt um den schnellsen Weg zu finden#
		// Px < Gx -> west  //Px => X Wert vom Player  // Gx => X Wert vom Geist  //gleiches mit Y
		// Px > Gx -> east
		// Py < Gy -> north
		// Py > Gy -> south 
		// => 2 Richtungen in die man gehen muss
		// 4 Himmelsrichtungen ergeben 4! = 24 Möglichkeiten/2 ,da wir nur die 2 errechneten Richtungen beachten 12 Möglichkeiten
		// Bsp mit |Px < Gx -> west| und |Py < Gy -> north|, also north and west
		// Wenn man nun davon ausgeht, dass North und West immer die 2 ersten Abfragewerte sind (da wir in diese Richtung wollen)
		// ergeben sich 2 Fälle je 2!=2 Möglichkeiten also 2^2=4 Möglichkeiten insgesamt.
		// Abfrag Reihenfolge
		// Fall 1: NWES | NWSE
		// Fall 2: WNES | WNSE
				
		//FIXME Clean the SYSO Methoden
				
		//Schritte um später zu vergleichen welcher Weg kürzer ist
		int steps1=0;
		int steps2=0;
		int steps3=0;
		int steps4=0;
		
		
		xSim = this.getX(); //Ghost X Position übergeben
		ySim = this.getY(); //Ghost Y Position übergeben
		
		//Primäre Richtungen bestimmen
		calcDirections(controller);
		
		vorherigesFeldXSim = vorherigesFeldX;
		vorherigesFeldYSim = vorherigesFeldY;
		
		//Solange Der Geist und der Spieler nicht auf dem selben Feld sind
		//wird weiter berechnet
		//Possibility 1 NWES (anhand bsp. xfirst = west and yfirst = north)
		// DO WHILE schleife, weil ich sonst nich in die schleife komme, falls PX = GX oder PY = GY
		while((controller.player.getX() != xSim) || (controller.player.getY() != ySim))
		{

	
//			if (throughGapNorth && xSim == alreadyX && ySim == alreadyY)
//			{
//				throughGapNorth = false;
//				System.out.println("return north");
//				return Controller.direction.north;
//			}
//			else if(throughGapEast && xSim == alreadyX && ySim == alreadyY)
//			{
//				throughGapEast = false;
//				System.out.println("return east");
//				return Controller.direction.east;
//			}
//			else if(throughGapSouth && xSim == alreadyX && ySim == alreadyY)
//			{
//				throughGapSouth = false;
//				System.out.println("return south");
//				return Controller.direction.south;
//			}
//			else if(throughGapWest && xSim == alreadyX && ySim == alreadyY)
//			{
//				throughGapWest = false;
//				System.out.println("return west");
//				return Controller.direction.west;
//			}
			
			
			System.out.println("while1");

				if (!controller.ghostWallCollision(_directionXfirst, xSim, ySim) && xSim != controller.player.getX() && nichtAufVorherigesFeld(_directionXfirst, xSim, ySim))
				{System.out.println("A");
				System.out.println(vorherigesFeldX);
				System.out.println(vorherigesFeldY);
					switch (_directionXfirst)
					{ 
					case east:
						vorherigesFeldXSim = xSim;
						vorherigesFeldYSim = ySim;
						xSim++; //Nach rechts
						break;
					case west:
						vorherigesFeldXSim = xSim;
						vorherigesFeldYSim = ySim;
						xSim--; //Nach links
						break;
						default:
							System.out.println("ERROR: GhostShortest -> calcMovementDir() : Default should not be called! 01");
						break;
					}
					if(steps1==0) 
					{
						ausgabeDirection1 = _directionXfirst; //nimmt die erste Richtung als ausgabe (wird später gebraucht)
						vorherigesFeldX = xSim;
						vorherigesFeldY = ySim;
					}
					steps1++;
				}
				else if(!controller.ghostWallCollision(_directionYfirst, xSim, ySim) && ySim != controller.player.getY() && nichtAufVorherigesFeld(_directionYfirst, xSim, ySim))
				{System.out.println("B");
				System.out.println(vorherigesFeldX);
				System.out.println(vorherigesFeldY);
					switch (_directionYfirst)
					{ 
					case south:
						vorherigesFeldXSim = xSim;
						vorherigesFeldYSim = ySim;
						ySim++; //Nach unten
						break;
					case north:
						vorherigesFeldXSim = xSim;
						vorherigesFeldYSim = ySim;
						ySim--; //Nach oben
						break;
						default:
							System.out.println("ERROR: GhostShortest -> calcMovementDir() : Default should not be called! 02");
						break;
					}
					if(steps1==0)
					{
						ausgabeDirection1 = _directionYfirst;
						vorherigesFeldX = xSim;
						vorherigesFeldY = ySim;
					}
					steps1++;
				}
				//hier war vorher ohne "!" und Xfirst
				else if(!controller.ghostWallCollision(_directionXsecond, xSim, ySim) && nichtAufVorherigesFeld(_directionXsecond, xSim, ySim))
				{System.out.println("C");
				System.out.println(vorherigesFeldX);
				System.out.println(vorherigesFeldY);
					if(_directionXfirst == Controller.direction.east)
					{
						//gehe west
						vorherigesFeldXSim = xSim;
						vorherigesFeldYSim = ySim;
						xSim--;
						if(steps1==0) 
						{
							ausgabeDirection1 = Controller.direction.west;
							vorherigesFeldX = xSim;
							vorherigesFeldY = ySim;
						}
						steps1++;
					}
					else if(_directionXfirst == Controller.direction.west)
					{
						//gehe east
						vorherigesFeldXSim = xSim;
						vorherigesFeldYSim = ySim;
						xSim++;
						if(steps1==0) 
						{
							ausgabeDirection1 = Controller.direction.east;
							vorherigesFeldX = xSim;
							vorherigesFeldY = ySim;
						}
						steps1++;
					}
					//System.out.println("X wall go around");
					//steps1 += goAroundWall(controller,steps1,_directionXfirst);
					//ausgabeDirection1 = _ausgabeDirection;
				}
				//Hier war vorher ohne "!" und Yfirst
				
				else if (!controller.ghostWallCollision(_directionYsecond, xSim, ySim)&& nichtAufVorherigesFeld(_directionYsecond, xSim, ySim))
				{System.out.println("D");
				System.out.println(vorherigesFeldX);
				System.out.println(vorherigesFeldY);
					if(_directionYfirst == Controller.direction.north)
					{
						//gehe south
						vorherigesFeldXSim = xSim;
						vorherigesFeldYSim = ySim;
						ySim++;
						if(steps1==0) 
						{
							ausgabeDirection1 = Controller.direction.south;
							vorherigesFeldXSim = xSim;
							vorherigesFeldYSim = ySim;
						}
						steps1++;
					}
					else if(_directionYfirst == Controller.direction.south)
					{
						//gehe north
						vorherigesFeldXSim = xSim;
						vorherigesFeldYSim = ySim;
						ySim--;
						if(steps1==0) 
						{
							ausgabeDirection1 = Controller.direction.north;
							vorherigesFeldXSim = xSim;
							vorherigesFeldYSim = ySim;
						}
						steps1++;
					}
					//System.out.println("Y wall go around");
					//steps1 += goAroundWall(controller, steps1, _directionYfirst); 
					//ausgabeDirection1 = _ausgabeDirection;

				}
				else 
				{
					System.out.println("NIX GEHT ALSO LECKM ICH AM SACH UND MACH WAS WILLST");
					// DA ISN VERSCHISSENER DENKFEHLER IN MEINER NICHT AUF VORHERIGES FELD KACKE
					return _directionXfirst;
				}
					
				System.out.println("Steps: --  " + steps1);
				System.out.println("xSim "+xSim);
				System.out.println("ySim "+ySim);
		}
		return ausgabeDirection1;
		/*
		//Varialben reseten
		throughGapNorth = false; //FIXME nach denken sind das alle DInge ? was mit alreadyX oder fieldAlrdyUsed ?
		throughGapEast = false;
		throughGapSouth = false;
		throughGapWest = false;
		xSim = this.getX();
		ySim = this.getY();
		while((controller.player.getX() != xSim) || (controller.player.getY() != ySim))
		{
			//um die secondaryGoAroundWall() sache auszugleichen (verhindert endlosschleife)
			if (throughGapNorth)
			{
				throughGapNorth = false;
				return Controller.direction.north;
			}
			else if(throughGapEast)
			{
				throughGapEast = false;
				return Controller.direction.east;
			}
			else if(throughGapSouth)
			{
				throughGapSouth = false;
				return Controller.direction.south;
			}
			else if(throughGapWest)
			{
				throughGapWest = false;
				return Controller.direction.west;
			}
			
			
			System.out.println("while2");
			System.out.println("xSim "+xSim);
			System.out.println("ySim "+ySim);

				if(!controller.ghostWallCollision(_directionYfirst, xSim, ySim) && ySim != controller.player.getY())
				{
					switch (_directionYfirst)
					{ 
					case south:
						ySim++; //Nach unten
						break;
					case north:
						ySim--; //Nach oben
						break;
						default:
							System.out.println("ERROR: GhostShortest -> calcMovementDir() : Default should not be called! 001");
						break;
					}
					if(steps2==0) ausgabeDirection2 = _directionYfirst;
					steps2++;
				}
				else if (!controller.ghostWallCollision(_directionXfirst, xSim, ySim) && xSim != controller.player.getX())
				{
					switch (_directionXfirst)
					{ 
					case east:
						xSim++; //Nach rechts
						break;
					case west:
						xSim--; //Nach links
						break;
						default:
							System.out.println("ERROR: GhostShortest -> calcMovementDir() : Default should not be called! 002");
						break;
					}
					if(steps2==0) ausgabeDirection2 = _directionXfirst; //nimmt die erste Richtung als ausgabe (wird später gebraucht)
					steps2++;
				}
				
				else if (controller.ghostWallCollision(_directionYfirst, xSim, ySim))
				{
					System.out.println("Y wall go around 2");
					steps2 += goAroundWall(controller, steps2, _directionYfirst); 
					ausgabeDirection2 = _ausgabeDirection;
				}
				else if(controller.ghostWallCollision(_directionXfirst, xSim, ySim))
				{
					System.out.println("X wall go around 2");
					steps1 += goAroundWall(controller,steps2,_directionXfirst);
					ausgabeDirection2 = _ausgabeDirection;
				}
				System.out.println("Steps: --  " + steps2);
				System.out.println("xSim "+xSim);
				System.out.println("ySim "+ySim);
		}*/
		
		//XXX wieder wegmache später
//		
//		steps2 =999;
//		if (steps1 <= steps2)
//		{
//			System.out.println();
//			System.out.println("A1: " +ausgabeDirection1);
//			return ausgabeDirection1;
//		}
//		else
//		{
//			System.out.println();
//			System.out.println("A2: " +ausgabeDirection2);
//			return ausgabeDirection2;
//		}

	}
	
/**
 * True wenn das Feld wo man hin will KEINES ist wo man scho war
 * @param _direction
 * @param controller
 * @param _x
 * @param _y
 * @return
 */
	private boolean nichtAufVorherigesFeld(Controller.direction _direction, int _x, int _y)
	{
		boolean ausgabe = true;
		switch(_direction)
		{
		case north:
			if(_x != vorherigesFeldXSim || _y-1 != vorherigesFeldYSim ) ausgabe = true;
			else ausgabe =false;
			break;
		case east:
			if(_x+1 != vorherigesFeldXSim || _y != vorherigesFeldYSim ) ausgabe = true;
			else ausgabe = false;
			break;
		case south:
			if(_x != vorherigesFeldXSim || _y+1 != vorherigesFeldYSim ) ausgabe = true;
			else ausgabe = false;
			break;
		case west:
			if(_x-1 != vorherigesFeldXSim || _y != vorherigesFeldYSim ) ausgabe = true;
			else ausgabe = false;
			break;
		}
		return ausgabe;
	}
	
	/**
	 * Berechnet welche Richtungen X und Y,<br>
	 * in Richtung spieler zeigen. (N,E,S or W)<br>
	 * Braucht Controller für die Player Pos
	 * @param controller
	 */
	private void calcDirections(Controller controller)
	{
		if (controller.player.getX() < xSim)
		{
			_directionXfirst = Controller.direction.west;
			_directionXsecond = Controller.direction.east;
		}
		else if (controller.player.getX() > xSim)
		{
			_directionXfirst = Controller.direction.east;
			_directionXsecond = Controller.direction.west;
		}
		else if (controller.player.getX() == xSim) //Spielt keine Rolle was davon first und was second ist
		{
			_directionXfirst = _directionXfirst;
			_directionXsecond = _directionXsecond;
		}
					
		if (controller.player.getY() < ySim)
		{
			_directionYfirst = Controller.direction.north;
			_directionYsecond = Controller.direction.south;
		}
		else if (controller.player.getY() > ySim)
		{
			_directionYfirst = Controller.direction.south;
			_directionYsecond = Controller.direction.north;
		}
		else if (controller.player.getY() == ySim) //Spielt keine Rolle was davon first und was second ist
		{
			_directionYfirst = _directionYfirst;
			_directionYsecond = _directionYsecond;
		}
		if(controller.player.getX() == xSim && controller.player.getY() == ySim) 
		{ 
			System.out.println("ERROR: GhostShortest -> calcMovementdir() :Der Geist war bereits auf dem Spieler -> Verloren bzw. Lebensabzug"); 
		}
	}
		
	/**
	 * Simuliert den Weg um einen Wall herum<br>
	 * gibt dann die Anzahl der nötigen Schritte aus <br>
	 * und setzt die moveDirection, wo der Geist hinlaufen muss
	 * @param controller
	 * @param _steps (what steps have to be counted?)
	 * @param wallPassDirection (in which direction is the wall)
	 */
	private int goAroundWall(Controller controller, int _steps, Controller.direction wallPassDirection)
	{
		
		boolean endFound = false;
		boolean startFound = false;
		int nRU = 0; //nach rechts/unten
		int nLO = 0; //nach links/oben
		int wStart = 0; //Wall Start
		int wEnd = 0;	//Wall End
		int[][] _world = controller.getWorld();
		int additionalLengthStart = 0;
		int additionalLengthEnd = 0;
		
		//Berechnet wStart und wEnd , um zu wissen wo der Wall startet und endet.
		//NOTE Vieleicht besser Switch ?
		switch (wallPassDirection)
		{
		case north:
			while(!endFound || !startFound)
			{
				if(_world[xSim - nLO][ySim -1] != 1 && !startFound)
				{

					wStart = xSim - nLO; //Das Feld mit der Lücke links
					startFound = true;
					if(_world[wStart + 1][ySim] == 1 || _world[wStart][ySim] == 1) //FIXME XXX Values der Übergabe stimmen von goAroundSecondaryValue nicht!!!
					{
						additionalLengthStart = goAroundWallSecondaryValue(wStart +2,ySim,controller, Controller.direction.west, Controller.direction.north);
					}
				}
				else
				{
					nLO++;
				}
				if(_world[xSim + nRU][ySim-1] != 1 && !endFound)
				{
					wEnd = xSim + nRU; //Das Feld mit der Lücke rechts
					endFound = true;
					//Wall wall?
					if(_world[wEnd - 1][ySim] == 1 || _world[wEnd][ySim] == 1) //Diese abfrage ist auf diese Map abgestimmt und würde bei anderen Maps einne "Ecke" nicht erkennen
					{
						additionalLengthEnd = goAroundWallSecondaryValue(wEnd -2,ySim,controller, Controller.direction.west, Controller.direction.north);
					}
				}
				else
				{
					nRU++;
				}
			}
		break;
		case east:
			while(!endFound || !startFound)
			{
				if(_world[xSim + 1][ySim - nLO] != 1 && !startFound)
				{
					wStart = ySim - nLO; //Das Feld mit der Lücke Oben
					startFound = true;
					//Wall wall?
					if(_world[xSim][wStart +1] == 1 || _world[xSim][wStart] == 1)
					{
						additionalLengthStart = goAroundWallSecondaryValue(xSim,wStart+2,controller, Controller.direction.west, Controller.direction.north);
					}
				}
				else
				{
					nLO++;
				}
				if(_world[xSim + 1][ySim + nRU] != 1 && !endFound)
				{
					wEnd = ySim + nRU; //Das Feld mit der Lücke Unten
					endFound = true;
					//Wall wall?
					if(_world[xSim][wEnd -1] == 1 || _world[xSim][wEnd] == 1)
					{
						additionalLengthEnd = goAroundWallSecondaryValue(xSim,wStart-2,controller, Controller.direction.west, Controller.direction.north);
					}
				}
				else
				{
					nRU++;
				}
			}
		break;
		case south:
			while(!endFound || !startFound)
			{
				if(_world[xSim - nLO][ySim + 1] != 1 && !startFound)
				{
					wStart = xSim - nLO; //Das Feld mit der Lücke links
					startFound = true;
					//Wall wall?
					if(_world[wStart + 1][ySim] == 1 || _world[wStart][ySim] == 1)
					{
						additionalLengthStart = goAroundWallSecondaryValue(wStart +2,ySim,controller, Controller.direction.west, Controller.direction.north);
					}
				}
				else
				{
					nLO++;
				}
				if(_world[xSim + nRU][ySim+1] != 1 && !endFound)
				{
					wEnd = xSim + nRU; //Das Feld mit der Lücke rechts
					endFound = true;
					//Wall wall?
					if(_world[wEnd - 1][ySim] == 1 || _world[wEnd][ySim] == 1)
					{
						additionalLengthEnd = goAroundWallSecondaryValue(wStart -2,ySim,controller, Controller.direction.west, Controller.direction.north);
					}
				}
				else
				{
					nRU++;
				}
			}
		break;
		case west:
			while(!endFound || !startFound)
			{
				System.out.println("Anfang west go Around While");
				if(_world[xSim - 1][ySim - nLO] != 1 && !startFound)
				{
					System.out.println("nLO "+ nLO);
					wStart = ySim - nLO; //Das Feld mit der Lücke Oben
					System.out.println("wStart "+wStart);
					startFound = true;
					//Wall wall?
					if(_world[xSim][wStart +1] == 1 || _world[xSim][wStart] == 1) 
					{
						additionalLengthStart = goAroundWallSecondaryValue(xSim,wStart+2,controller, Controller.direction.west, Controller.direction.north);
					}
				}
				else
				{
					nLO++;
				}
				if(_world[xSim - 1][ySim + nRU] != 1 && !endFound)
				{
					wEnd = ySim + nRU; //Das Feld mit der Lücke Unten
					endFound = true;
					//Wall wall?
					if(_world[xSim][wEnd -1] == 1 || _world[xSim][wEnd] == 1)
					{
						additionalLengthEnd = goAroundWallSecondaryValue(xSim,wStart-2,controller, Controller.direction.west, Controller.direction.north);
					}
				}
				else
				{
					nRU++;
				}
			}
		break;
		default:
			System.out.println("ERROR: GhostShortest -> goAroundWall 1. Switch");
			break;
		}
		
		//XXX jetz hab ich wStart und wEnd. 
		// Nun geht man in die Richtung, wo die nähste Lücke ist
		int deltaLeftVertical = Math.abs(wStart - xSim);
		int deltaRightVertical = Math.abs(wEnd - xSim);
		int deltaUpHorizontal = Math.abs(wStart - ySim);
		int deltaDownHorizontal = Math.abs(wEnd - ySim);
		
		boolean atTheGap = false;
		boolean isMirEgal = false;
		//Wenn man for na 1 Langen Wand steht und quasi mitten durch müsste, um zu bestimmen ob links oder rechts vorbei
		boolean downOrRight = false;
		
		switch (wallPassDirection)
		{
		case north:
			if(Math.abs(wStart - xSim)+2*additionalLengthStart == Math.abs(wEnd - xSim)+2*additionalLengthEnd)
			{
				//Wenn die Wand nur 1 Lang ist
				if(Math.abs(wStart - xSim) == 1 && Math.abs(wEnd - xSim) == 1)
				{
					System.out.println("wand isch 1 lang");
					//dann gehe NICHT DORT wo zuerst die wand ist
					//dann entweder nach Links 1 oder nach Rechts 1 und 2 nach obe
					for(int n = ySim; n >= controller.player.getY() ;n--)
					{
						if(_world[xSim+1][n] == 1)
						{
							downOrRight = false;
							break; //NOTE Break is unschön
						}
						else if(_world[xSim-1][n]== 1)
						{
							downOrRight = true;
							break;
						}
					}
					if(downOrRight) 
					{ 
						if(_steps == 0){ _ausgabeDirection = Controller.direction.east; } xSim ++; _steps++; 
						if(!fieldAlrdySet)
						{
							fieldAlrdySet = true;
							throughGapNorth = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
					} 
					else if(!downOrRight) 
					{ 
						if(_steps == 0){ _ausgabeDirection = Controller.direction.west; } xSim --; _steps++;
						if(!fieldAlrdySet)
						{
							fieldAlrdySet = true;
							throughGapNorth = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
					}
				} 
				else
				{
					isMirEgal = true;
				}
			}
			else if(Math.abs(wStart - xSim)+2*additionalLengthStart > Math.abs(wEnd - xSim)+2*additionalLengthEnd || isMirEgal)//Osten
			{
				if(_steps == 0)
				{
					_ausgabeDirection = Controller.direction.east; //FIXME das ist ein Problem wenn man in einer Ecke ist
					// Wenn man goAroundWallSecondary() hat und dann nach z.B. North im Osten ne Wand hat
					//geht man nach unten um da drumherum zu gehen. aber man geht in wirklichkeit nur ein Feld und dann
					//kann man im nächsten durchgange wieder nach Oben wodurch man in einer Dauerschleife ist
				}
				
				for(int n = 0; n <= deltaLeftVertical; n++)//Osten
				{
					//Laufe + n
					atTheGap = false;
					if(!controller.ghostWallCollision(Controller.direction.east, xSim, ySim))
					{
						 xSim++;
						 _steps++;
						 atTheGap = true;
					}
					
				}
				if(atTheGap)
				{
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapNorth = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					ySim -= 2; //2 nach oben
					_steps += 2;
					
				}
				if (controller.ghostWallCollision(Controller.direction.east, xSim, ySim)) //Westen weil delta start kürzer ist
				{
					if(_steps==0){_ausgabeDirection = Controller.direction.south;}
					_steps += demiGoAroundWall(_steps, controller, Controller.direction.east, Controller.direction.north);
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapEast = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					xSim += 2;
					_steps += 2;
				}
				//Überall nochma 2 Schritte nach vorn um durch die Lücke zu gehen
				
			}
			else if(Math.abs(wStart - xSim)+2*additionalLengthStart < Math.abs(wEnd - xSim)+2*additionalLengthEnd)//Westen
			{
				if(_steps == 0)
				{
					_ausgabeDirection = Controller.direction.west;
				}
				for(int n = 0; n <= deltaRightVertical; n++)
				{
					//laufe - n
					atTheGap = false;
					if(!controller.ghostWallCollision(Controller.direction.west, xSim, ySim))
					{
						 xSim--;
						 _steps++;
						 atTheGap = true;
					}
				}
					if(atTheGap)
					{
						if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
						{
							fieldAlrdySet = true;
							throughGapNorth = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
						ySim -= 2;
						_steps += 2;
					}
					if (controller.ghostWallCollision(Controller.direction.west, xSim, ySim))
					{
						if(_steps==0){_ausgabeDirection = Controller.direction.south;}
						_steps += demiGoAroundWall(_steps, controller, Controller.direction.west, Controller.direction.north);
						if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
						{
							fieldAlrdySet = true;
							throughGapWest = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
						xSim -= 2;
						_steps += 2;
					}
				
			}
			
		break;
		
		case east:
			if(Math.abs(wStart - ySim)+2*additionalLengthStart == Math.abs(wEnd - ySim)+2*additionalLengthEnd)
			{
				if(Math.abs(wStart - ySim) == 1 && Math.abs(wEnd - ySim) == 1)
				{
					//dann gehe NICHT DORT wo zuerst die wand ist
					//dann entweder nach Links 1 oder nach Rechts 1 und 2 nach obe
					for(int n = xSim; n <= controller.player.getX() ;n++)
					{
						if(_world[n][ySim+1] == 1)
						{
							downOrRight = false;
							break; //NOTE Break is unschön
						}
						else if(_world[n][ySim-1]== 1)
						{
							downOrRight = true;
							break;
						}
					}
					if(downOrRight) 
					{ 
						if(_steps==0){_ausgabeDirection=Controller.direction.south;} ySim ++; _steps++; 
						if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
						{
							fieldAlrdySet = true;
							throughGapEast = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
					}
					else if(!downOrRight) 
					{ 
						if(_steps==0){_ausgabeDirection=Controller.direction.north;} ySim --; _steps++; 
						if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
						{
							fieldAlrdySet = true;
							throughGapEast = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
					}
				}
				else
				{
					isMirEgal = true;
				}
			}
			else if(Math.abs(wStart - ySim)+2*additionalLengthStart > Math.abs(wEnd - ySim)+2*additionalLengthEnd || isMirEgal)//South
			{
				if(_steps == 0)
				{
					_ausgabeDirection = Controller.direction.south;
				}
				for(int n = 0; n <= deltaUpHorizontal; n++)//South
				{
					//Laufe + n
					atTheGap = false;
					if(!controller.ghostWallCollision(Controller.direction.south, xSim, ySim))
					{
						 ySim++;
						 _steps++;
						 atTheGap = true;
					}
				}
				if(atTheGap)
				{
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapEast = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					xSim += 2;
					_steps += 2;
				}
				if (controller.ghostWallCollision(Controller.direction.south, xSim, ySim))
				{
					if(_steps==0){_ausgabeDirection = Controller.direction.west;}
					_steps += demiGoAroundWall(_steps, controller, Controller.direction.south, Controller.direction.east);
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapSouth = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					ySim += 2;
					_steps += 2;
				}
				
				
			}
			else if(Math.abs(wStart - ySim)+2*additionalLengthStart < Math.abs(wEnd - ySim)+2*additionalLengthEnd)//North
			{
				if(_steps == 0)
				{
					_ausgabeDirection = Controller.direction.north;
				}
				
				for(int n = 0; n <= deltaDownHorizontal; n++)
				{
					//laufe - n
					atTheGap = false;
					if(!controller.ghostWallCollision(Controller.direction.north, xSim, ySim))
					{
						 ySim--;
						 _steps++;
						 atTheGap = true;
					}
				}
				if(atTheGap)
				{
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapEast = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					xSim += 2;
					_steps += 2;
				}
				if (controller.ghostWallCollision(Controller.direction.north, xSim, ySim))
				{
					if(_steps==0){_ausgabeDirection = Controller.direction.west;}
					_steps += demiGoAroundWall(_steps, controller, Controller.direction.north, Controller.direction.east);
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapNorth = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					ySim -= 2;
					_steps += 2;
				}
			}
			
			break;
			
		case south:
			if(Math.abs(wStart - xSim)+2*additionalLengthStart == Math.abs(wEnd - xSim)+2*additionalLengthEnd)
			{
				if(Math.abs(wStart - xSim) == 1 && Math.abs(wEnd - xSim) == 1)
				{
					//dann gehe NICHT DORT wo zuerst die wand ist
					//dann entweder nach Links 1 oder nach Rechts 1 und 2 nach obe
					for(int n = ySim; n <= controller.player.getY() ;n++)
					{
						if(_world[xSim+1][n] == 1)
						{
							downOrRight = false;
							break; //NOTE Break is unschön
						}
						else if(_world[xSim-1][n]== 1)
						{
							downOrRight = true;
							break;
						}
					}
					if(downOrRight) 
					{ 
						if(_steps == 0){_ausgabeDirection=Controller.direction.east;} xSim ++; _steps++; 
						if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
						{
							fieldAlrdySet = true;
							throughGapSouth = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
					}
					else if(!downOrRight) 
					{ 
						if(_steps == 0){_ausgabeDirection=Controller.direction.west;} xSim --; _steps++;
						if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
						{
							fieldAlrdySet = true;
							throughGapSouth = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
					}
				}
				else
				{
					isMirEgal = true;
				}
			}
			else if(Math.abs(wStart - xSim)+2*additionalLengthStart > Math.abs(wEnd - xSim)+2*additionalLengthEnd)
			{
				if(_steps == 0)
				{
					_ausgabeDirection = Controller.direction.east;
				}
				for(int n = 0; n <= deltaLeftVertical; n++)//Osten
				{
					//Laufe + n
					atTheGap = false;
					if(!controller.ghostWallCollision(Controller.direction.east, xSim, ySim))
					{
						 xSim++;
						 _steps++;
						 atTheGap=true;
					}
				}
				if(atTheGap)
				{
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapSouth = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					ySim += 2;
					_steps += 2;
				}
				if (controller.ghostWallCollision(Controller.direction.east, xSim, ySim))
				{
					if(_steps==0){_ausgabeDirection = Controller.direction.north;}
					_steps += demiGoAroundWall(_steps, controller, Controller.direction.east, Controller.direction.south);
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapEast = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					xSim += 2;
					_steps += 2;
				}
			}
			else if(Math.abs(wStart - xSim)+2*additionalLengthStart < Math.abs(wEnd - xSim)+2*additionalLengthEnd)//Westen
			{
				if(_steps == 0)
				{
					_ausgabeDirection = Controller.direction.west;
				}
				for(int n = 0; n <= deltaRightVertical; n++)
				{
					//laufe - n
					atTheGap = false;
					if(!controller.ghostWallCollision(Controller.direction.west, xSim, ySim))
					{
						 xSim--;
						 _steps++;
						 atTheGap = true;
					}
				}
				if(atTheGap)
				{
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapSouth = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					ySim += 2;
					_steps += 2;
				}
				if (controller.ghostWallCollision(Controller.direction.west, xSim, ySim))
				{
					if(_steps==0){_ausgabeDirection = Controller.direction.north;}
					_steps += demiGoAroundWall(_steps, controller, Controller.direction.west, Controller.direction.south);
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapWest = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					xSim -= 2;
					_steps += 2;
				}
			}
			
			break;
			
		case west:
			if(Math.abs(wStart - ySim)+2*additionalLengthStart == Math.abs(wEnd - ySim)+2*additionalLengthEnd)
			{
				if(Math.abs(wStart - ySim) == 1 && Math.abs(wEnd - ySim) == 1)
				{
					//dann gehe NICHT DORT wo zuerst die wand ist
					//dann entweder nach Links 1 oder nach Rechts 1 und 2 nach obe
					for(int n = xSim; n >= controller.player.getX() ;n--)
					{
						if(_world[n][ySim+1] == 1)
						{
							downOrRight = false;
							break; //NOTE Break is unschön
						}
						else if(_world[n][ySim-1]== 1)
						{
							downOrRight = true;
							break;
						}
					}
					if(downOrRight) 
					{ 
						if(_steps == 0){_ausgabeDirection=Controller.direction.south;} ySim ++; _steps++;
						if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
						{
							fieldAlrdySet = true;
							throughGapWest = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
					}
					else if(!downOrRight) 
					{ 
						if(_steps == 0){_ausgabeDirection=Controller.direction.north;} ySim --; _steps++; 
						if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
						{
							fieldAlrdySet = true;
							throughGapWest = true;
							alreadyX = xSim;
							alreadyY = ySim;
						}
					}
				}
				else
				{
					isMirEgal = true;
				}
			}
			else if(Math.abs(wStart - ySim)+2*additionalLengthStart > Math.abs(wEnd - ySim)+2*additionalLengthEnd)
			{
				if(_steps == 0)
				{
					_ausgabeDirection = Controller.direction.south;
				}
				for(int n = 0; n <= deltaUpHorizontal; n++)//South
				{
					//Laufe + n
					atTheGap = false;
					if(!controller.ghostWallCollision(Controller.direction.south, xSim, ySim))
					{
						 ySim++;
						 _steps++;
						 atTheGap = true;
					}
				}
				if(atTheGap)
				{
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapWest = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					xSim -= 2;
					_steps += 2;
				}
				if (controller.ghostWallCollision(Controller.direction.south, xSim, ySim))
				{
					if(_steps==0){_ausgabeDirection = Controller.direction.east;}
					_steps += demiGoAroundWall(_steps, controller, Controller.direction.south, Controller.direction.west);
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapSouth = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					ySim += 2;
					_steps += 2;
				}
				
			}
			else if(Math.abs(wStart - ySim)+2*additionalLengthStart < Math.abs(wEnd - ySim)+2*additionalLengthEnd)//North
			{
				if(_steps == 0)
				{
					_ausgabeDirection = Controller.direction.north;
				}
				for(int n = 0; n <= deltaDownHorizontal; n++)
				{
					//laufe - n
					atTheGap = false;
					if(!controller.ghostWallCollision(Controller.direction.north, xSim, ySim))
					{
						 ySim--;
						 _steps++;
						 atTheGap = true;
					}
				}
				if(atTheGap)
				{
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapWest = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					xSim -= 2;
					_steps += 2;
				}
				if (controller.ghostWallCollision(Controller.direction.north, xSim, ySim))
				{
					if(_steps==0){_ausgabeDirection = Controller.direction.east;}
					_steps += demiGoAroundWall(_steps, controller, Controller.direction.north, Controller.direction.west);
					if(!fieldAlrdySet) //ich will das Feld vor der Lücke 
					{
						fieldAlrdySet = true;
						throughGapNorth = true;
						alreadyX = xSim;
						alreadyY = ySim;
					}
					ySim -= 2;
					_steps += 2;
				}
			}
			
			break;
		default:
			System.out.println("ERROR: GhostShortest -> goAroundWall 2. Switch");
			break;
		}
		//^ Switch Ende Klammer
		return _steps;
		
	}
	
	/**
	 * This is a sub-Method for the goAroundWall() Method<br>
	 * its supposed to clean up the code<br>
	 * and should only be used within it.
	 * @param x Wert, wo der Geist stehen würde wenn er auf die Wand trifft 
	 * @param y Wert, wo der Geist stehen würde wenn er auf die Wand trifft	
	 * @param controller
	 * @param wallPassDirection In which dirction is the new Wall
	 * @param notToGoDirection Where was the old Wall
	 * @return returns the Length of the Path between G and the gap
	 */
	private int goAroundWallSecondaryValue(int xSim, int ySim, Controller controller, Controller.direction wallPassDirection, Controller.direction notToGoDirection)
	{
		boolean endFound = false;
		boolean startFound = false;
		int nRU = 0; //nach rechts/unten
		int nLO = 0; //nach links/oben
		int wStart = 0; //Wall Start
		int wEnd = 0;	//Wall End
		int[][] _world = controller.getWorld();
		
		
		//Berechnet wStart und wEnd , um zu wissen wo der Wall startet und endet.
		//NOTE Vieleicht besser Switch ?
		switch (wallPassDirection)
		{
		case north:
			while(!endFound || !startFound)
			{
				 //Wenn die Wand im norden ist, heißt es das notToGoDirection
				//entweder east oder west sein muss, da die vorherige Wand ach east oder west gewesen sein muss
				switch (notToGoDirection)
				{
				case east: //nach Osten geht NICHT
					// wir zählen nur nach links, rechts ist die erste Wand
					wEnd = xSim;
					endFound = true; 
					if(_world[xSim - nLO][ySim -1] != 1 && !startFound)
					{
						wStart = xSim - nLO;
						startFound = true;
					}
					else
					{
						nLO++;
					}
				break;
				case west: //nach Westen geht NICHT
					//wir zählen nur nach rechts, links ist die erste Wand
					wEnd = xSim;
					endFound = true;
					if(_world[xSim + nRU][ySim -1] != 1 && !startFound)
					{
						wStart = xSim + nRU;
						startFound = true;
					}
					else
					{
						nRU++;
					}
				break;
				default:
					System.out.println("ERROR");
					break;
				}
			}
				break;
		case east:
			while(!endFound || !startFound)
			{
				switch (notToGoDirection)
				{
				case north:
					// wir zählen nur nach unten, oben ist die erste Wand
					wEnd = ySim;
					endFound = true;
					if(_world[xSim +1][ySim +nRU] != 1 && !startFound)
					{
						wStart = ySim + nRU;
						startFound = true;
					}
					else
					{
						nRU++;
					}
				break;
				case south:
					//wir zählen nur nach rechts, links ist die erste Wand
					wEnd = ySim;
					endFound = true;
					if(_world[xSim +1][ySim - nLO] != 1 && !startFound)
					{
						wStart = ySim - nLO;
						startFound = true;
					}
					else
					{
						nLO++;
					}
				break;
				default:
					System.out.println("ERROR");
					break;
				}
			}			
		break;
		case south:
			while(!endFound || !startFound)
			{
				switch (notToGoDirection)
				{
				case east:
					// wir zählen nur nach links, rechts ist die erste Wand
					wEnd = xSim;
					endFound = true;
					if(_world[xSim - nLO][ySim +1] != 1 && !startFound)
					{
						wStart = xSim - nLO;
						startFound = true;
					}
					else
					{
						nLO++;
					}
				break;
				case west:
					//wir zählen nur nach rechts, links ist die erste Wand
					wEnd = xSim;
					endFound = true;
					if(_world[xSim + nRU][ySim +1] != 1 && !startFound)
					{
						wStart = xSim + nRU;
						startFound = true;
					}
					else
					{
						nRU++;
					}
				break;
				default:
					System.out.println("ERROR");
					break;
				}
			}
		break;
		case west:
			while(!endFound || !startFound)
			{
				switch (notToGoDirection)
				{
				case north:
					// wir zählen nur nach unten, oben ist die erste Wand
					wEnd = ySim;
					endFound = true;
					if(_world[xSim -1][ySim +nRU] != 1 && !startFound)
					{
						wStart = ySim + nRU;
						startFound = true;
					}
					else
					{
						nRU++;
					}
				break;
				case south:
					//wir zählen nur nach rechts, links ist die erste Wand
					wEnd = ySim;
					endFound = true;
					if(_world[xSim -1][ySim - nLO] != 1 && !startFound)
					{
						wStart = ySim - nLO;
						startFound = true;
					}
					else
					{
						nLO++;
					}
				break;
				default:
					System.out.println("ERROR");
					break;
				}
			}
		break;
		default:
			System.out.println("ERROR: GhostShortest -> goAroundWall 1. Switch");
			break;
		}
		if (wallPassDirection == Controller.direction.north || wallPassDirection == Controller.direction.south)
		{
			return Math.abs(wStart - xSim);
		}
		else if (wallPassDirection == Controller.direction.west || wallPassDirection == Controller.direction.east)
		{
			return Math.abs(wStart - ySim);
		}
		System.out.println("ERROR default return in goAroundWallSecondaryValue()");
		return 1;
		
	}
	
	// Hier wird werden wieder steps ausgegeben und _ausgabeDirection wird gesetz
	
	/**
	 * Is called to go around a secondary Wall while in another goAroundWall() Method<br>
	 * has almost the same Parameters as goAroundWallSecondaryBalue()<br>
	 * its just doing the Part where we actually move the Ghost along the Wall to the gap
	 * @param _steps
	 * @param controller
	 * @param wallPassDirection
	 * @param notToGoDirection
	 * @return Returns the new _steps Value
	 */
	private int demiGoAroundWall(int _steps, Controller controller, Controller.direction wallPassDirection, Controller.direction notToGoDirection)
	{
		
		//TODO eigentlich das gleiche wie goAroundWallSecondaryValue, aber wir machen weiter mit delta Berechnung
		// ich muss nur in die richtige richtung laufen
		//das delta kommt von der Funktion goAroundWallSecondaryValue
		
		//X und Y Position zum Zeitpunkt als man vor der Wand stand
		int xPos = xSim;
		int yPos = ySim;
		
		switch (wallPassDirection)
		{
		case north:
			switch (notToGoDirection)
			{
			case east: //notToGo east also Westen
				if(_steps == 0){_ausgabeDirection = Controller.direction.west; }
				for(int n = 0; n <= goAroundWallSecondaryValue(xPos, yPos, controller, wallPassDirection, notToGoDirection); n++)
				{ 
					
					if(!controller.ghostWallCollision(Controller.direction.west, xSim, ySim))
					{
						 xSim--;
						 _steps++;
					}
					else if (controller.ghostWallCollision(Controller.direction.west, xSim, ySim))
					{
						System.out.println("If this happened i fucked up: GhostShortest -> demiGoAroundWall() 01");
					}
				}
				break;
			case west:
				if(_steps == 0){_ausgabeDirection = Controller.direction.east; }
				for(int n = 0; n <= goAroundWallSecondaryValue(xPos, yPos, controller, wallPassDirection, notToGoDirection); n++)
				{ 
					
					if(!controller.ghostWallCollision(Controller.direction.east, xSim, ySim))
					{
						 xSim++;
						 _steps++;
					}
					else if (controller.ghostWallCollision(Controller.direction.east, xSim, ySim))
					{
						System.out.println("If this happened i fucked up: GhostShortest -> demiGoAroundWall() 02");
					}
				}
				break;
				default:
					System.out.println("Error");
				break;
			}
			break;
			
		case east:
			switch (notToGoDirection)
			{
			case north:
				if(_steps == 0){_ausgabeDirection = Controller.direction.south; }
				for(int n = 0; n <= goAroundWallSecondaryValue(xPos, yPos, controller, wallPassDirection, notToGoDirection); n++)
				{
					if(!controller.ghostWallCollision(Controller.direction.south, xSim, ySim))
					{
						 ySim++;
						 _steps++;
					}
					else if (controller.ghostWallCollision(Controller.direction.south, xSim, ySim))
					{
						System.out.println("If this happened i fucked up: GhostShortest -> demiGoAroundWall() 03");
					}
				}
				break;
			case south:
				if(_steps == 0){_ausgabeDirection = Controller.direction.north; }
				for(int n = 0; n <= goAroundWallSecondaryValue(xPos, yPos, controller, wallPassDirection, notToGoDirection); n++)
				{ 
					
					if(!controller.ghostWallCollision(Controller.direction.north, xSim, ySim))
					{
						 ySim--;
						 _steps++;
					}
					else if (controller.ghostWallCollision(Controller.direction.north, xSim, ySim))
					{
						System.out.println("If this happened i fucked up: GhostShortest -> demiGoAroundWall() 04");
					}
				}
				break;
				default:
					System.out.println("Error");
				break;
			}
			break;
			
		case south:
			switch (notToGoDirection)
			{
			case east:
				if(_steps == 0){_ausgabeDirection = Controller.direction.west; }
				for(int n = 0; n <= goAroundWallSecondaryValue(xPos, yPos, controller, wallPassDirection, notToGoDirection); n++)
				{ 
					
					if(!controller.ghostWallCollision(Controller.direction.west, xSim, ySim))
					{
						 xSim--;
						 _steps++;
					}
					else if (controller.ghostWallCollision(Controller.direction.west, xSim, ySim))
					{
						System.out.println("If this happened i fucked up: GhostShortest -> demiGoAroundWall() 05");
					}
				}
				break;
			case west:
				if(_steps == 0){_ausgabeDirection = Controller.direction.east; }
				for(int n = 0; n <= goAroundWallSecondaryValue(xPos, yPos, controller, wallPassDirection, notToGoDirection); n++)
				{ 
					
					if(!controller.ghostWallCollision(Controller.direction.east, xSim, ySim))
					{
						 xSim++;
						 _steps++;
					}
					else if (controller.ghostWallCollision(Controller.direction.east, xSim, ySim))
					{
						System.out.println("If this happened i fucked up: GhostShortest -> demiGoAroundWall() 06");
					}
				}
				break;
				default:
					System.out.println("Error");
				break;
			}
			break;
			
		case west:
			switch (notToGoDirection)
			{
			case north:
				if(_steps == 0){_ausgabeDirection = Controller.direction.south; }
				for(int n = 0; n <= goAroundWallSecondaryValue(xPos, yPos, controller, wallPassDirection, notToGoDirection); n++)
				{ System.out.println(goAroundWallSecondaryValue(xPos, yPos, controller, wallPassDirection, notToGoDirection));
					System.out.println("hängt das? case west cas north in goAroundSecondary"); // XXX das wieder löschen
					if(!controller.ghostWallCollision(Controller.direction.south, xSim, ySim))
					{
						 ySim++;
						 _steps++;
					}
					else if (controller.ghostWallCollision(Controller.direction.south, xSim, ySim))
					{
						System.out.println("If this happened i fucked up: GhostShortest -> demiGoAroundWall() 07");
					}
				}
				break;
			case south:
				if(_steps == 0){_ausgabeDirection = Controller.direction.north; }
				for(int n = 0; n <= goAroundWallSecondaryValue(xPos, yPos, controller, wallPassDirection, notToGoDirection); n++)
				{ 
					if(!controller.ghostWallCollision(Controller.direction.north, xSim, ySim))
					{
						 ySim--;
						 _steps++;
					}
					else if (controller.ghostWallCollision(Controller.direction.north, xSim, ySim))
					{
						System.out.println("If this happened i fucked up: GhostShortest -> demiGoAroundWall() 08");
					}
				}
				break;
				default:
					System.out.println("Error");
				break;
			}
			break;
			default:
				System.out.println("Error");
			break;
		}
		return _steps;
	}
	
	
	//Klassen Klammer
}