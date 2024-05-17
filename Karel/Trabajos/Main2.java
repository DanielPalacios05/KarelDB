import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import kareltherobot.*;

public class Main2 implements Directions{

    public static void main(String[] args) {

        World.setTrace(false);

        World.readWorld("Mina2.kwld");
        World.setVisible(true);

        Robot foo = new Robot(9, 9, East, 0,Color.BLACK);
        Robot var = new Robot(8, 9, East, 0,Color.BLUE);
        Robot se = new Robot(7, 9, North, 0,Color.RED);

        se.move();

        Enumeration a = var.neighbors();

        while (a.hasMoreElements()) {
             ((Robot)a.nextElement()).turnOff();
        }
    }
    
}
