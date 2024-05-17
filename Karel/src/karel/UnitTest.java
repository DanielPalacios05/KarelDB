package karel;
import kareltherobot.*;
//import junit.framework.*;
import org.junit.Before;
import org.junit.Test;

public class UnitTest extends KJRTest
{

	private UrRobot karel;

	@Before
	public void setUp()
	{   karel = new UrRobot(1, 1, North, 1);
	}

	@Test
	public void testActions()
	{   assertFrontIsClear(karel);
		karel.move();
		karel.turnLeft();
		assertFrontIsBlocked(karel);
		assertOnStreet(karel, 2);
		assertOnAvenue(karel, 1);
		assertHasNoNeighbor(karel);
		assertFacingWest(karel);
		assertNotAt(karel, 1, 1);
		assertBeepersInBeeperBag(karel);
		assertNotNextToABeeper(karel);
		karel.putBeeper();
		assertNoBeepersInBeeperBag(karel);
		assertNextToABeeper(karel);
	}

	@Test
	public void testTurnoff()
	{	assertRunning(karel);
		karel.turnOff();
		assertNotRunning(karel);
	}

	@Test
	public void testErrorTurnoff()
	{	assertRunning(karel);
		assertFrontIsClear(karel);
		karel.turnLeft();
		assertFrontIsBlocked(karel);
		karel.move();
		assertNotRunning(karel);
	}

}