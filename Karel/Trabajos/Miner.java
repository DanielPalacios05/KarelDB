import kareltherobot.*;

import java.awt.Color;
import java.util.concurrent.locks.ReentrantLock;

public class Miner extends BaseConciousRobot implements Directions,Runnable {

    private ExchangePoint minerExchangePoint;
    private ReentrantLock trainQueueLock; 
    private boolean isLeader;

    public Miner(int street, int avenue, int beepers,Direction direction, int capacity, ReentrantLock[][] lockMap,ExchangePoint ep,boolean isLeader){
        super(new BaseRobot(street, avenue, direction, beepers, Color.BLACK, capacity, RobotState.INITIALIZING),lockMap,avenue,street);     
        minerExchangePoint = ep;
        trainQueueLock = getLock(26, 26);
        this.isLeader = isLeader;
    }


    public void run(){
        lockActualPosition();
        while (getState() != RobotState.FINISHED) {
        boolean hasMined = mine();

        if(hasMined){
        setState(RobotState.DROPING);
        minerExchangePoint.dropToPoint(this);
        goRest();
        }else{
            exitMine();
            setState(RobotState.FINISHED);
            if(!isLeader){
                minerExchangePoint.finish();
            }
            break;
        }
        }
    }


    public void exitMine() {
        ReentrantLock entryLock = getLock(22, 22);
        turnLeft();
        goForward();
        turnRight();
        goForward();
        turnLeft();
        move(4);
        trainQueueLock.lock();
        entryLock.lock();
        move();
        turnRight();
        goForward();
        trainQueueLock.unlock();
        turnRight();
        goForward();
        turnRight();
        move();
        turnLeft();
        move(2);
        turnRight();
        move(3);
        entryLock.unlock();
        turnLeft();
        
        if(isLeader){
            move(4);
        }else{
            move(3);
        }
       

        
    }


    private boolean mine() {

        int minerDepth = 1;
        int beepersMined = 0;
        boolean hasMined = false;

        ReentrantLock mineLock = getLock(19,   19);

        mineLock.lock();

        

        if (this.getState() == RobotState.RESTING){
            move();
            turnRight();
        } 
        this.setState(RobotState.MINING);

        while (!hasMined && !facingWest()){
            if(nextToABeeper()){
                while (!isFull() && nextToABeeper()){
                    pickBeeper();
                    beepersMined++;
                }
                turnLeft();
                turnLeft();
            hasMined = true;
            }else if (frontIsClear()){
                move();
                minerDepth++;
            }   
            else{
                turnLeft();
                turnLeft();
            }
        }

        // Exit Mine
        move(minerDepth);

        

        mineLock.unlock();

        if (beepersMined == 0){
            return false;
        }else{
            return true;
        }
    
        
    }




    private void goRest() {
        turnLeft();
        move();
        turnLeft();
        move();
        turnLeft();
        setState(RobotState.RESTING);
    }
    
}
