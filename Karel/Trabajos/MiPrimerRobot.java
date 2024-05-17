import kareltherobot.*;
import java.awt.Color;

public class MiPrimerRobot implements Directions {
    public static void main(String[] args) {
        // Usamos el archivo que creamos del mundo
        World.readWorld("Mundo.kwld");
        

         
        // Coloca el robot en la posición inicial del mundo (1,1),
        // mirando al Este, sin ninguna sirena.
        Robot Karel = new Robot(1, 1, East, 0);
        // Mover el robot 4 pasos
        Karel.move();
        Karel.move();
        Karel.move();
        Karel.move();
        // Recoger los 5 beepers
        Karel.pickBeeper();
        Karel.pickBeeper();
        Karel.pickBeeper();
        Karel.pickBeeper();
        Karel.pickBeeper();
        // Girar a la izquierda y salir de los muros
        Karel.turnLeft();
        Karel.move();
        Karel.move();
        // Poner los beepers fuera de los muros
        Karel.putBeeper();
        Karel.putBeeper();
        Karel.putBeeper();
        Karel.putBeeper();
        Karel.putBeeper();
        // Ponerse en otra posición y apagar el robot
        Karel.move();
        Karel.turnOff();
    }
}
