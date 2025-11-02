package src.connect;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

class DataQueue extends LinkedBlockingDeque<Message> {
    private final int maxSize;
    private final Object IS_NOT_FULL = new Object();
    private final Object IS_NOT_EMPTY = new Object();

    DataQueue(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    public void waitIsNotEmpty() throws InterruptedException {
        synchronized (IS_NOT_EMPTY) {
            IS_NOT_EMPTY.wait();
        }
    }

    public void notifyIsNotEmpty() {
        synchronized (IS_NOT_EMPTY) {
            IS_NOT_EMPTY.notify();
        }
    }

    public void waitIsNotFull() throws InterruptedException {
        synchronized (IS_NOT_FULL) {
            IS_NOT_FULL.wait();
        }
    }    

    public void notifyIsNotFull() {
        synchronized (IS_NOT_FULL) {
            IS_NOT_FULL.notify();
        }
    }

    public boolean isEmpty(){
        return super.isEmpty();
    }

    public boolean isFull(){
        return super.size() == maxSize;
    }

    @Override
    public boolean add(Message message) {
        if (isFull()) return false;
        boolean val = super.add(message);
        if (val) notifyIsNotEmpty();
        return val;
    }

    @Override
    public Message poll(long timeout, TimeUnit tu) {
        Message mess = Message.NULLMSG;
        try {
            mess = super.poll(timeout, tu);   
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyIsNotFull();
        return mess;
    }
}