import static org.junit.Assert.fail;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import kareltherobot.Robot;

public class BaseConciousRobot {
    private BaseRobot robot;
    private ReentrantLock[][] lockMap;
    private int posX;
    private int posY;
    private ReentrantLock actualPosLock;


    public BaseConciousRobot(BaseRobot robot, ReentrantLock[][] lockMap,int initalPosX, int initalPosY){
            this.robot = robot;
            this.lockMap = lockMap;
            this.posX = initalPosX-1;
            this.posY = initalPosY-1;
            this.actualPosLock = lockMap[posY][posX];
    }

    public void releaseActualLock(){
        actualPosLock.unlock();
    }

    public int getActualBeepers(){
        return robot.getActualBeepers();
    }

    public int getCapacity(){
        return robot.getCapacity();
    }

    public boolean frontIsClear(){
        return robot.frontIsClear();
    }

    public void turnLeft(){
        robot.turnLeft();
    }
    public void turnRight(){
        robot.turnRight();
    }
    
    public void goForward(){
        while (frontIsClear()) {
            move();
        }
    }

    public void lockActualPosition(){
        actualPosLock.lock();
    }

    public void  move(){    
        if (robot.getState() != RobotState.OFF){
        int newPosX = posX;
        int newPosY = posY;
        if(robot.facingNorth()){
            newPosY++;
        }else if (robot.facingSouth()){
            newPosY--;
        }else if (robot.facingWest()){
            newPosX--;
        }
        else if (robot.facingEast()){
            newPosX++;
        }

        ReentrantLock nextPosLock = lockMap[newPosY][newPosX];
       
        if(robot.frontIsClear()){
            nextPosLock.lock();
            robot.move();
            if (actualPosLock.isHeldByCurrentThread()){
                actualPosLock.unlock();
            }
            
            if(robot.nextToARobot()){
                Enumeration neighborRobots = robot.neighbors();
                while (neighborRobots.hasMoreElements()) {
                    ((Robot)neighborRobots.nextElement()).turnOff();
                }
                robot.turnOff();
            }

            posX = newPosX;
            posY = newPosY;
            actualPosLock = nextPosLock;
        }

        



        
    }
}


    public ReentrantLock getLock(int posY, int posX){
        return lockMap[posY][posX];
    } 

    public void pickBeeper(){
        robot.pickBeeper();
    }

    public int getPosY(){
        return this.posY;
    }

    public int getPosX(){
        return this.posX;
    }

    public void pickBeeper(int times){
        robot.pickBeeper(times);
    }

    public boolean isFull(){
        return robot.isFull();
    }

    public RobotState getState(){
        return robot.getState();
    }

    public boolean facingNorth(){
        return robot.facingNorth();
    }

    public boolean facingEast(){
        return robot.facingEast();
    }

    public boolean facingWest(){
        return robot.facingWest();
    }

    public boolean facingSouth(){
        return robot.facingSouth();
    }

    public void setState(RobotState newState){
        robot.setState(newState);
    }

    public boolean nextToABeeper(){
        return robot.nextToABeeper();
    }

    public void move(int times){
        for (int i = 0; i < times; i++) {
            move();
        }
    }

    public void turnOff(){
        robot.turnOff();
    }

    public boolean anyBeepersInBeeperBag(){
        return robot.anyBeepersInBeeperBag();
    }

    public void putBeeper(){
        robot.putBeeper();
    }


    public void dropBeepers(){
        robot.dropBeepers();
    }
}
