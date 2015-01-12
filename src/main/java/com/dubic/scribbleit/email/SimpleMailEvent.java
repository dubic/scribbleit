/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**sent to the queue. class describing simple non template message mail to be sent
 *
 * @author DUBIC
 */
public class SimpleMailEvent implements Delayed{
    protected List<String> to;
    protected String from;
    protected String cc;
    protected String message;
    protected String subject;
    protected Date scheduledTime =  new Date();

    public SimpleMailEvent(String...recs) {
        to = new ArrayList<String>(recs.length);
        this.to.addAll(Arrays.asList(recs));
    }

    public SimpleMailEvent(List<String> to) {
        this.to = to;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public void addTo(String to){
        this.to.add(to);
    }
    
/**gets the delay for the queue based on difference between scheduled time and now.time unit param is ignored
 *
 * @since notifications 1.0.0
 */
    @Override
    public long getDelay(TimeUnit unit) {
//        Calendar cal = Calendar.getInstance();
////        cal.add(Calendar.MINUTE, 4);
//        scheduledTime = cal.getTime();
        long delay = scheduledTime.getTime() - new Date().getTime();
        return delay;
    }

 /**sorts mail event based on their scheduled time.used internally by the DelayQueue
 *
 * @since notifications 1.0.0
 */
    @Override
    public int compareTo(Delayed o) {
        if(this.getDelay(TimeUnit.NANOSECONDS) < o.getDelay(TimeUnit.NANOSECONDS)){
            return -1;
        }else if(this.getDelay(TimeUnit.NANOSECONDS) > o.getDelay(TimeUnit.NANOSECONDS)){
            return 1;
        }
        return 0;
    }
    
    
}
