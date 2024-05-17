import kareltherobot.*;
import java.awt.Color;
import java.util.ArrayList;



public class Robots implements Directions {

    public ArrayList<Robot> robots;

    public Robots(){
        robots = new ArrayList<Robot>();
    }

    public void move(){

        for (Robot robot : robots) {
            robot.move();
        }
    } 

    public void pickBeeper(){
        for (Robot robot : robots) {
            robot.pickBeeper();
        }

    }

    public void turnLeft(){
        for (Robot robot : robots) {
            robot.turnLeft();
        }
    }

    public void putBeeper(){
        for (Robot robot : robots) {
            robot.putBeeper();
        }
    }

    public void turnOff(){
        for (Robot robot : robots) {
            robot.turnOff();
        }
    }
    public static void main(String[] args) {
        // Usamos el archivo que creamos del mundo
        World.readWorld("Mundo.kwld");
        World.setVisible(true);
        World.setDelay(10);
        Robots Karel = new Robots();

        // Coloca el robot en la posición inicial del mundo (1,1),
        // mirando al Este, sin ninguna sirena.
        Robot robot1 = new Robot(1, 1, East, 0);
        Robot robot2 = new Robot(1, 2, East, 0,Color.BLUE);
        
        Karel.robots.add(robot1);
        Karel.robots.add(robot2);
        // Mover el robot 4 pasos
      
    }
}
