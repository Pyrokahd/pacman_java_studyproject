import java.util.Random;
public class GhostRandom extends Ghost
{
	private Random randomGen = new Random();
	
	//NOTE hätte auch den Constructor der Mutterklasse in diesem Constructor aufrufen können
	// um X und Y zu setzen
	
	//Bewege den Ghost je nach Richtung
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
	//TODO Verbessern, das er random einen gang entlang geht, bis eine Abzweigung kommt und nicht jedes Feld Random ist.
	//Berechne welche Richtung der Ghost gehen muss
	//In dem fall eine zufällige Richtung (welche nicht mit einer Wand kollidiert)
	public Controller.direction calcMovementDir( Controller controller)
	{
		Controller.direction _direction; //Um ausgabe zwischenzuspeichern
		int _world[][] = controller.getWorld();
		
		while(true) //Dauerschleife ist möglich, da mit 'return' aus dem Loop bzw der Methode gesprungen wird
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
		
}
