import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class ExtractorGroup  implements Runnable{
    private ArrayList<Extractor> extractors;
    private ExchangePoint extractorExchangePoint;
    private ArrayList<Thread> robotThreads;
    private ReentrantLock entryLock;

    private ReentrantLock allEnteredLock;
    private Condition allEntered;

    public ExtractorGroup(ExchangePoint exchangePoint,ReentrantLock entryLock, ReentrantLock allEnteredLock){
        extractors = new ArrayList<>();
        extractorExchangePoint = exchangePoint;
        robotThreads = new ArrayList<>();
        this.entryLock = entryLock;
        this.allEnteredLock = allEnteredLock;
        allEntered = allEnteredLock.newCondition();
    }

    public void addExtractor(Extractor extractor){
        extractors.add(extractor);
        robotThreads.add(null);
    }



    public void run(){       
            try {
                do {
                entryLock.lock();
                allEnteredLock.lock();
                initialize();
                allEntered.await();
                allEnteredLock.unlock();
                entryLock.unlock();
                System.out.println("Esperando ultimo extractor");
                robotThreads.get(0).join();
                System.out.println("Esperando confirmacion de beepers");
            } while (extractorExchangePoint.areAnyBeepers());
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public void initialize(){
        for (int i = 0; i < robotThreads.size(); i++) {
            robotThreads.set(i, new Thread(extractors.get(i)));
            robotThreads.get(i).start();
        }
    }

    public int getNumExtractors() {
        return extractors.size();
    }

    public void signalAllEntered() {
        allEnteredLock.lock();
        this.allEntered.signalAll();
        allEnteredLock.unlock();
    }





    


    
}