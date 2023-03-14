
public class Player 
{
	private boolean alive = true;
	private int x;
	private int y;
	
	Player(int _x, int _y)
	{
		x = _x;
		y = _y;
	}
	void setAlive(boolean _alive)
	{
		alive = _alive;
	}
	boolean getAlive()
	{
		return alive;
	}
	int getX()
	{
		return x;
	}
	int getY()
	{
		return y;
	}
	void setX(int _x)
	{
		x = _x;
	}
	void setY(int _y)
	{
		y = _y;
	}
}
