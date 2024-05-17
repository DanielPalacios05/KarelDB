import kareltherobot.World;

public class MinerGroup implements Runnable {

    private Miner leader;
    private Miner follower;

    public MinerGroup(Miner leader, Miner follower){   
        this.leader = leader;
        this.follower = follower;

    }

    public void goForward(){
        
        while (leader.frontIsClear()){
            leader.move();
            follower.move();
        }
    }

    public void initialize(){
        
        goForward();

        leader.turnRight();
        leader.move();
        leader.turnLeft();
        leader.move();
        follower.move();
        follower.turnRight();
        follower.move();
        follower.turnLeft();

        goForward();

        leader.turnLeft();
        leader.move();
        follower.move();
        follower.turnLeft();

        goForward();

        leader.turnLeft();
        leader.move();

        follower.move();
        follower.turnLeft();

        move(4);
        leader.turnRight();
        leader.move();

        follower.move();
        follower.turnRight();

        goForward();

        leader.turnLeft();
        leader.move();

        follower.move();
        follower.turnLeft();

        move(4);


        leader.turnRight();

        leader.move();

        

        follower.turnRight();
        follower.move();
        follower.turnLeft();

        follower.turnRight();
        follower.move();
        follower.turnLeft();
    }





    public void move(int times){
        for (int i = 0; i < times; i++) {
            leader.move();
            follower.move();
        }
    }


    public void run(){
        initialize();
        leader.releaseActualLock();
        follower.releaseActualLock();

        follower.setState(RobotState.RESTING);
        Thread leaderTr = new Thread(leader);
        leaderTr.start();
        Thread followerTr = new Thread(follower);
        followerTr.start();
    }



    
}
