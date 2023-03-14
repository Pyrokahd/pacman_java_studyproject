
//Abstract bei der Klasse bedeutet, dass man kein Objekt dieser Klasse nutzen kann
//dient also nur dazu um Superklassen zu erstellen
public abstract class Ghost 
{
	private int x;
	private int y;
	
//	public Ghost(int _x, int _y)
//	{
//		setX(_x);
//		setY(_y);
//	} Constructer geht nicht siehe http://stackoverflow.com/questions/1197634/java-error-implicit-super-constructor-is-undefined-for-default-constructor?newreg=324c77ae651b4205a4d730632fa24080
	
	//abstarct bedeutet, dass diese Methode in der Kindklasse überschrieben werden muss
	//Führt Bewegung aus
	public abstract void moveGhost(Controller.direction _direction); 
	//Berechnet welche Bewegung ausgegeben werden soll, und gibt dann eine direction aus, braucht 2dim. Array als Übergabewert und den Spieler
	public abstract Controller.direction calcMovementDir(Controller controller); 
	
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	public void setX(int _x)
	{
		x = _x;
	}
	public void setY(int _y)
	{
		y = _y;
	}
}
