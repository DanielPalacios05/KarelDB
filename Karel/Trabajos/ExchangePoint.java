import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ExchangePoint {
    private ReentrantLock exchangePointLock;
    private ReentrantLock queueLock;
    private Condition canCollect;
    private Condition canEnter;
    private int beepersInPlace;
    private boolean isAvailable;
    private int collectorsCapacity;

    public ExchangePoint(ReentrantLock lock, int beepersInPlace,int collectorsCapacity){
        exchangePointLock = lock;
        queueLock = new ReentrantLock();
        canEnter = queueLock.newCondition();
        canCollect = exchangePointLock.newCondition();
        this.beepersInPlace = beepersInPlace;
        isAvailable = true;
        this.collectorsCapacity = collectorsCapacity;
    }

    public boolean areAnyBeepers(){

        if (beepersInPlace != 0){
            return true;
        }
        queueLock.lock();

        try {
            canEnter.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            queueLock.unlock();
        }
        return beepersInPlace != 0;
      
    }


    public boolean collectFromPoint(BaseConciousRobot collectingRobot) {
        try {
            exchangePointLock.lock();
            if (beepersInPlace < collectorsCapacity && isAvailable){
                canCollect.await();
            }
            collectingRobot.move();

            while (beepersInPlace > 0 && !collectingRobot.isFull()) {
                collectingRobot.pickBeeper();
                beepersInPlace--;
            }
            if (collectingRobot.getActualBeepers() == 0){
                isAvailable = false;
                System.out.println("Exchange not available");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isAvailable;



    }

    //Miner mined until 120
    public void dropToPoint(BaseConciousRobot droppingRobot){


            int beepersToDrop = droppingRobot.getActualBeepers();
            
            droppingRobot.dropBeepers();
            beepersInPlace += beepersToDrop;

            if (beepersInPlace >= collectorsCapacity || beepersToDrop < droppingRobot.getCapacity() ){
                canCollect.signalAll();
                queueLock.lock();
                canEnter.signalAll();
                queueLock.unlock();
            }
    }

    public int getBeepersInPlace() {
       return this.beepersInPlace;
    }

    public void finish() {
    isAvailable = false;
    exchangePointLock.lock();
    canCollect.signalAll();
    exchangePointLock.unlock();

    queueLock.lock();
    canEnter.signalAll();
    queueLock.unlock();
    
    }



}
