
public class Fruit
{
	//Testweise
	private int x;
	private int y;
	
	Fruit(int _x, int _y)
	{
		x = _x;
		y = _y;
	}
	/**
	 * Just to test X and Y
	 **/
	void testeFruit() 
	{
		System.out.println("X:"+x+" Y:"+y);
	}
	int getX()
	{
		return x;
	}
	int getY()
	{
		return y;
	}
}
