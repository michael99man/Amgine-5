package chat_objects;

import io.Chatroom;

public class Poller implements Runnable {

    private boolean poll;
    private Chatroom parent;
    private int tick;
    
    public Poller(Chatroom cr){
        parent = cr;
        tick = 0;
    }
    
    public void startPolling(){
        poll = true;
    }
    
    public void stopPolling(){
        poll = false;
    }
    
    public boolean isPolling(){
        return poll;
    }
    
    public void run() {
        while(poll)
        try {
            //only update users once every 3 seconds
            parent.update(tick);
            Thread.sleep(1000);
            tick++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
