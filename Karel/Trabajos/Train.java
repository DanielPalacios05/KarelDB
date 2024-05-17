import java.util.concurrent.locks.ReentrantLock;
import java.awt.Color;
import java.rmi.RemoteException;

import kareltherobot.Directions;

public class Train extends BaseConciousRobot implements Runnable, Directions {

    private ExchangePoint minerExchangePoint;
    private ExchangePoint extractorExchangePoint;
    private ReentrantLock trainQueueLock;
    private int pos;

    public Train(int street, int avenue, int beepers, Direction direction, int capacity, ReentrantLock[][] lockMap,
            ExchangePoint mep, ExchangePoint eep, int pos) {
        super(new BaseRobot(street, avenue, direction, beepers, Color.BLUE, capacity, RobotState.INITIALIZING), lockMap,
                avenue, street);
        this.minerExchangePoint = mep;
        this.extractorExchangePoint = eep;
        trainQueueLock = this.getLock(26, 26);
        this.pos = pos;
    }

    public void run() {
        if (getState() == RobotState.INITIALIZING) {
            initialize();
        }
    }

    public void initialize() {

        ReentrantLock entryLock = getLock(22, 22);

        lockActualPosition();
        goForward();
        turnRight();
        move();
        turnLeft();
        goForward();
        turnLeft();
        goForward();
        turnLeft();
        goForward();
        turnRight();
        move(4);


        while (getState() != RobotState.FINISHED) {

            if (this.getPosY() == 10 && this.getPosX() == 11) {
                boolean beepersExist = minerExchangePoint.collectFromPoint(this);

                turnRight();
                releaseActualLock();
                goForward();
                turnRight();
                goForward();
                turnLeft();
                move(4);
                trainQueueLock.lock();
                move();
                turnRight();
                move();
                if (anyBeepersInBeeperBag()) {

                    extractorExchangePoint.dropToPoint(this);

                    if (beepersExist) {
                        turnLeft();
                        turnLeft();
                        releaseActualLock();
                        move(2);
                        trainQueueLock.unlock();
                        goForward();
                        turnLeft();

                        move(6);
                        goForward();
                    } else {
                        trainQueueLock.unlock();
                        exitMine();
                    }
                
            }else{
                trainQueueLock.unlock();
                exitMine();
            }

        } else if (this.getPosY() == 10 && this.getPosX() == 7 && this.facingNorth()) {
            turnRight();
            move();
        } else {
            move();
        }

        }

    }

    public void exitMine() {
        goForward();
        turnRight();
        goForward();
        turnRight();
        move();
        turnLeft();
        move();
        turnRight();
        move(4);
        turnLeft();
        move(4);
        turnRight();
        move(pos);
        turnLeft();
        move();
        setState(RobotState.FINISHED);
    }

}
