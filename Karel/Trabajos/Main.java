import java.util.concurrent.locks.ReentrantLock;

import kareltherobot.Directions;
import kareltherobot.World;

public class Main implements Directions{

    public static void main(String[] args) {


    int m = 0;
    int t = 0;
    int e = 0;
    
    // Process the arguments

    if (args.length != 6) {
        System.err.println("Error: No hay suficientes argumentos");
        return;
    }
    for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-m")) {
          m = Integer.parseInt(args[i + 1]);
          if (m != 2) {
            System.err.println("Error: m debe ser 2.");
            return; // Exit program if m is less than 2
          }
        } else if (args[i].equals("-t")) {
          t = Integer.parseInt(args[i + 1]);
          if (t < 2 || t > 9) {  // Check for both minimum and maximum of t
            System.err.println("Error: t must be between 0 and 9.");
            return; // Exit program if t is invalid
          }
        } else if (args[i].equals("-e")) {
          e = Integer.parseInt(args[i + 1]);
          if (e < 2 || e > 5) {  // Check for both minimum and maximum of e
            System.err.println("Error: e must be between 0 and 5.");
            return; // Exit program if e is invalid
          }
        }
      }
  
      // Use the parsed values (m, t, e) in your program logic (assuming they are valid now)
      System.out.println("mineros: " + m + ", trenes: " + t + ", extractores: " + e);


      World.setTrace(false);

      World.readWorld("Mina.kwld");
      World.setVisible(true);
      World.showSpeedControl(true);



        ReentrantLock[][] lockMap = new ReentrantLock[30][30];

        
        
        
        for (int i = 0; i < lockMap.length; i++) {
            for (int j = 0; j < lockMap.length; j++) {
                lockMap[i][j] = new ReentrantLock();
            }
        }

        ExchangePoint ep = new ExchangePoint(lockMap[10][12], 0,120);
        ExchangePoint extractorExchangePoint = new ExchangePoint(lockMap[0][1], 0,50);
        int[] warehouse = new int[5];

        

        int numTrains = t;
        int numExtractors = e;
        int numMiners = m;

        
        
        Miner leader = new Miner(9, 2, 0,South,50,lockMap,ep,true);
        Miner follower = new Miner(10, 2, 0, South,50, lockMap,ep,false);
        
        MinerGroup minerGroup = new MinerGroup(leader, follower);
        TrainGroup trainGroup = new TrainGroup(extractorExchangePoint);
        ExtractorGroup extractorGroup = new ExtractorGroup(extractorExchangePoint,lockMap[22][22],lockMap[25][25]);

                
        for (int i = 0; i < numTrains; i++) {
            Train newTrain = new Train(11+i, 2, 0, South, 120, lockMap,ep,extractorExchangePoint,i);
            
            trainGroup.addTrain(newTrain);
            
        }
        for (int i = 0; i < numExtractors; i++) {
            Extractor newExtractor = new Extractor(12+numTrains+i, 2, 0, South, 50, lockMap,i,extractorExchangePoint,warehouse,extractorGroup);
            
            extractorGroup.addExtractor(newExtractor);
        }



        

        
    

        minerGroup.run();
        
        Thread tr1 = new Thread(trainGroup);
        tr1.start();

        extractorGroup.run();


    }
    
}
