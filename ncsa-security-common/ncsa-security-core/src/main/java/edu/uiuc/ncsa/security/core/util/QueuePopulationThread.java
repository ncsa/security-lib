package edu.uiuc.ncsa.security.core.util;

/**
 * A thread for populating a {@link QueueWithSpare}.    This thread will maintain a queue with
 * a maximum number of elements and will check to see if there need to be new elements
 * at a given interval. Generally this means that every request will receive a new element,
 * but should demand outstrip supply, the last element in the queue will be returned until
 * new elements can be created. This allows for generating expensive items (such
 * as key pairs) without creating a bottleneck in the system.
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/12 at  11:03 AM
 */
public abstract class QueuePopulationThread<E> extends Thread {
    protected QueuePopulationThread(MyLoggingFacade logger) {
        this.logger = logger;
    }

    protected QueuePopulationThread(QueueWithSpare<E> q) {
        this.q = q;
    }
    protected QueuePopulationThread(int maxQueueSize, long sleepInterval, QueueWithSpare<E> q) {
        this.maxQueueSize = maxQueueSize;
        this.sleepInterval = sleepInterval;
        this.q = q;
    }

    int maxQueueSize = 10;
    QueueWithSpare<E> q = new QueueWithSpare<E>();

    public QueueWithSpare<E> getQ() {
        return q;
    }

    protected MyLoggingFacade getLogger() {
        if (logger == null) {
            logger = new MyLoggingFacade(getClass().getName(), false);
        }
        return logger;
    }

    MyLoggingFacade logger;

    long sleepInterval = 10000L;


    public boolean isStopThread() {
        return stopThread;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    boolean stopThread;

    abstract protected E createNew();

    protected void log(String x) {
        getLogger().info(x);
    }

    @Override
    public void run() {
        log("starting queue populator");
        while (!isStopThread()) {
            try {
                sleep(sleepInterval);
                if (q.isEmpty() || q.size() < maxQueueSize) {
                    q.push(createNew());
                }
            } catch (InterruptedException e) {
                setStopThread(true); // just in case.
                getLogger().warn("Cleanup interrupted, stopping thread...");
            }
        }
    }

}
