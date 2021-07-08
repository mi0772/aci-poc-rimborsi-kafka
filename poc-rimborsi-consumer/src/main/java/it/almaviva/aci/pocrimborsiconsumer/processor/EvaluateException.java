package it.almaviva.aci.pocrimborsiconsumer.processor;

import java.util.concurrent.CountDownLatch;

public interface EvaluateException {

    public String getMessageBody();

    public Throwable getException();

    public int numberOfExceptions();

    public void setCountDownOne(CountDownLatch latch);

    public void setCountDownTwo(CountDownLatch latch);

    public void setCountDownThree(CountDownLatch latch);

}
