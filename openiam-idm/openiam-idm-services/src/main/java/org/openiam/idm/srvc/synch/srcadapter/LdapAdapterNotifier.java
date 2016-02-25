package org.openiam.idm.srvc.synch.srcadapter;

/**
 * Created by alexander on 24/02/16.
 */
public class LdapAdapterNotifier {
    public void doWait(long l){
        synchronized (this){
            try{
                this.wait(l);
            }catch (InterruptedException e){

            }
        }
    }
    public void doWait(){
        synchronized (this){
            try{
                this.wait();
            }catch (InterruptedException e){

            }
        }
    }
    public void doNotify(){
        synchronized (this){
            this.notify();
        }
    }
}
