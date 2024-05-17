package karel;
import java.awt.Color;
import kareltherobot.*;


// USAGE java -cp .:KarelJRobot.jar ControllerMain street ave dir beepers worldfile red green blue
// eg java -cp .:KarelJRobot.jar ControllerMain 5 3 east infinity test.kwld 255 0 0
// You may give any initial prefix of these and defaults will be taken for the rest
// Errors cause defaults from the first error to the end. 
// street, ave, reg, green, blue, must be non negative integers. 
// red, green, blue, must be < 256. They define the badge color in rgb.
// beepers must be a non negative integer or the word infinity
// dir must be east, west, or south or the default (north) is used. 
// The direction names are not case sensitive here, nor is the word infinity
// the worldfile can be the empty string "". 

// Note that this class is just a sample of what you can do with a RemoteControl.
// It also shows somehthing about parsing input arguments when you need
// to run from the command line. 
public class ControllerMain implements Directions
{
	public static void main(String[] args)
	{
		UrRobot karel = null;
		int street = 1;
		int avenue = 1;
		Direction direction = North;
		int beepers = 0;
		String world = "";
		int red = 0;
		int green = 0;
		int blue = 0;
		Color color = null;
		try
		{
			street = Integer.parseInt(args[0]);
			avenue = Integer.parseInt(args[1]);
			String which = args[2];
			if(which.equalsIgnoreCase("South")) direction = South;
			else if(which.equalsIgnoreCase("East")) direction = East;
			else if(which.equalsIgnoreCase("West")) direction = West;
			if(args[3].equalsIgnoreCase("infinity"))beepers = infinity;
			else beepers = Integer.parseInt(args[3]);
			world = args[4];		
			if(world != null && world != "")World.readWorld(world);
			red = Integer.parseInt(args[5]);
			green = Integer.parseInt(args[6]);
			blue = Integer.parseInt(args[7]);
			color = new Color(red, green, blue);
		}
		catch (Throwable e)
		{
			System.out.println("Using some default arguments");
		}
		World.asObject().setDelay(0);
		World.asObject.setVisible(true);
		
		RemoteControl controller = new RemoteControl(street, avenue, direction, beepers, color);
		//RemoteControl controller2 = new RemoteControl(1,2,North,0,null);
	}

}
