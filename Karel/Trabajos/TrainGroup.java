import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class TrainGroup  implements Runnable{
    private ArrayList<Train> trains;
    private Train leader;
    private ExchangePoint extractorExchangePoint;

    public TrainGroup(ExchangePoint exchangePoint){
        trains = new ArrayList<>();
        extractorExchangePoint = exchangePoint;
    }

    public void addTrain(Train train){
        trains.add(train);
    }



    public void run(){
        initialize();
    }

    public void initialize(){
        Thread lastThread = null;
        for (Train train : trains) {
            
            Thread trainThread = new Thread(train);
            lastThread = trainThread;
            trainThread.start();
        }

        try {
            ReentrantLock entryLock = trains.get(0).getLock(22, 22);
            lastThread.join();
            entryLock.lock();
            extractorExchangePoint.finish();
            entryLock.unlock();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

  




    


    
}
