package edu.uiuc.ncsa.security.util.cli;

/**
 * A message that is written by an external sourse. Receivers wait on this
 * until notified.
 * <p>Created by Jeff Gaynor<br>
 * on 8/17/22 at  3:11 PM
 */
public class Message {
    private String packet = null;

    // True if receiver should wait
    // False if sender should wait
    private boolean receiverWaits = true;

    public synchronized String receive() {
        while (receiverWaits) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        receiverWaits = true;

        String returnPacket = packet;
        notifyAll();
        return returnPacket;
    }

    public synchronized void send(String packet) {
        while (!receiverWaits) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        receiverWaits = false;

        this.packet = packet;
        notifyAll();
    }
}