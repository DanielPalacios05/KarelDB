import kareltherobot.*;

import java.awt.Color;

public class StairClimber extends UrRobot
{
    public StairClimber(int street, int avenue, Direction d, int beepers, Color color)
    {   super(street, avenue, d, beepers, color);
    }
    
    public StairClimber(int street, int avenue, Direction d, int beepers)
    {   this(street, avenue, d, beepers, null);
    }

	public void turnRight()
	{   //move();
		turnLeft();
		//move();
		turnLeft();
		//move();
		turnLeft();
		//move();
	}

	public void climbOneStair()
	{   turnLeft();
		move();
		turnRight();
		move();
	}

	public void climbStairs()
	{   climbOneStair();
		climbOneStair();
		climbOneStair();
	}

	public void getBeeper()
	{
		climbStairs();
		pickBeeper();
		turnOff();
	}

	public static void task()
	{   World.reset();
		World.readWorld("stairworld.kwld");
		World.setVisible(true);
		StairClimber karel = new StairClimber(1, 1, East, 0, Color.red);
		karel.getBeeper();
	}

	public static void main(String [] args)
	{
		task();
	}
}