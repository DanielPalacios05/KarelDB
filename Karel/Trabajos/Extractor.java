import java.awt.Color;
import java.util.concurrent.locks.ReentrantLock;

import kareltherobot.Directions.Direction;

public class Extractor extends BaseConciousRobot implements Runnable {

    private int pos;
    private ExchangePoint extractorExchangePoint;


    private int[] warehouse;
    private ReentrantLock warehouseLock;
    private ReentrantLock entryLock;
    private ExtractorGroup group;


      public Extractor(int street, int avenue, int beepers,Direction direction, int capacity, ReentrantLock[][] lockMap,int pos, ExchangePoint ep,int[] wr, ExtractorGroup group){
        super(new BaseRobot(street, avenue, direction, beepers, Color.RED, capacity, RobotState.INITIALIZING),lockMap,avenue,street);     
        this.extractorExchangePoint = ep;
        this.pos = pos;
        this.warehouse = wr;
        warehouseLock = getLock(23, 23);
        this.group = group;
        entryLock = getLock(22, 22);


    }

    public void run(){
        if(getState() == RobotState.INITIALIZING){
            initialize();
        }
    }

    public void initialize(){
        

        lockActualPosition();
        
        goForward();
        turnRight();
        move();
        turnLeft();
        goForward();
        turnLeft();
        move();            
        turnLeft();
        
        move(5-pos);
        
        turnLeft();
        turnLeft();

        
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        boolean hasCollected = false;
        while (!hasCollected) {
        if (getPosY() == 1 && getPosX() == 1){

            if (pos+1 == group.getNumExtractors()) {
                group.signalAllEntered();
            }
            extractorExchangePoint.collectFromPoint(this);
            hasCollected = true;
            releaseActualLock();
            turnRight();
            move();
            turnRight();
            goForward();
            turnRight();
            move();
            turnLeft();
            warehouseLock.lock();
            move();
            turnRight();
            move();
            turnRight();
            dropInWarehouse();

            turnLeft();
            move();
            move();
            warehouseLock.unlock();
            turnLeft();

            move(getPosX());
            turnRight();
            move(4+pos);
            turnRight();
            move();
            turnRight();

            releaseActualLock();

        }else{
        
            if(frontIsClear()){
                move();
            }
            }
        }  
    }

    public void dropInWarehouse() {

        move();
        turnLeft();
        for (int i = 0; i < warehouse.length; i++) {
            
            if(warehouse[i] < 300){

                while (anyBeepersInBeeperBag() && warehouse[i] < 300) {
                    putBeeper();
                    warehouse[i]++;
                }
            }
            
            if(!anyBeepersInBeeperBag()){
                break;
            }
            else{
                move();
            }

        }
        
    }
}

