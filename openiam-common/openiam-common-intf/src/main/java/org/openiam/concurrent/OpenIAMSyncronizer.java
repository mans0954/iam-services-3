package org.openiam.concurrent;

/**
 * Created by alexander on 07/07/16.
 */
public class OpenIAMSyncronizer {
    public void doWait(long l){
        synchronized(this){
            try {
                this.wait(l);
            } catch(InterruptedException e) {
            }
        }
    }

    public void doNotify() {
        synchronized(this) {
            this.notify();
        }
    }

    public void doWait() {
        synchronized(this){
            try {
                this.wait();
            } catch(InterruptedException e) {
            }
        }
    }
}
