import java.awt.Color;
import java.util.concurrent.locks.ReentrantLock;

import kareltherobot.*;

public class BaseRobot extends Robot implements Directions{
    
    private int capacity;
    private int actualBeepers;
    private RobotState actualState;



    public BaseRobot(int street, int avenue, Direction direction, int beepers, Color badge,int capacity, RobotState actualState){
        super(street, avenue, direction, beepers,badge);
        this.actualBeepers = beepers;
        this.capacity = capacity;
        this.actualState = actualState;
    }

    public void turnRight(){
        turnLeft();
        turnLeft();
        turnLeft();
    }

    public RobotState getState(){
        return this.actualState;
    }

    public void pickBeeper(int times){
        for (int i = 0; i < times; i++) {
            super.pickBeeper();
        }
    }

    public void setState(RobotState newState){
        this.actualState = newState;
    }

    public boolean isFull(){
        return this.actualBeepers >= this.capacity;
    }

    public void pickBeeper(){

        if (this.nextToABeeper() && !this.isFull()){
            super.pickBeeper();
            this.actualBeepers++;
        }


    }

    public void turnOff(){
        super.turnOff();
        setState(RobotState.OFF);
    }

    public void dropBeepers() {

        while (this.anyBeepersInBeeperBag()) {
            this.putBeeper();
            
        }
    }

    public void putBeeper(){

        if(anyBeepersInBeeperBag()){
            super.putBeeper();
            this.actualBeepers--;
        }

    }

    public int getActualBeepers(){
        return this.actualBeepers;
    }

    public int getCapacity(){
        return this.capacity;
    }



    


}
