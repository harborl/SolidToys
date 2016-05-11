package io.harborl.solid.toys.switcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A remote <tt>Boolean</tt> value based switcher. It is used to 
 * regularly fetch the remote configuration of a specified URL,
 * and return the value through a <tt>get</tt> method.
 * 
 * <h5>Note</h5>
 * The instance of this class is thread-safe and does not block
 * the JVM existing, so there is no any explicit shutdown method.
 * 
 * @author Harbor Luo
 * @since 0.9.0
 *
 */
public class RemoteSwitcher {
  
  /**
   * The underlying well-formated URL for 
   * fetching the boolean text content.
   */
  private final URL theURL;
  
  /**
   * The initial delay of scheduler.
   */
  private final long initialDelay;
  
  /**
   * The regular delay of scheduler.
   */
  private final long delay;
  
  /**
   * The measurement unit of delay.
   */
  private final TimeUnit unit;
  
  /** 
   * Guarded by the <tt>AtomicReference</tt> 
   */
  private final AtomicReference<Boolean> switcher;
  
  /**
   * A single thread 'thread-pool' based regular timer scheduler.
   */
  private final ScheduledExecutorService keeper;
  
  private RemoteSwitcher(String url, boolean defValue, long initialDelay, long delay, TimeUnit unit) {
    if (url == null || unit == null) throw new NullPointerException();
    if (delay <= 0 || initialDelay < 0) throw new IllegalArgumentException(); 
    
    try {
      this.theURL = new URL(url);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    this.delay = delay;
    this.unit = unit;
    this.initialDelay = initialDelay;
    this.switcher = new AtomicReference<Boolean>(defValue);

    /*
     * A single thread 'thread-pool' based timer scheduler.
     * It uses the daemon thread that desn't block the JVM stopping,
     * that is to say, it will shutdown automatically when the JVM exits.
     */
    this.keeper = 
        Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

      @Override
      public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, "[BooleanSwitcher] - keeper - " + theURL);
        thread.setDaemon(true);
        return thread;
      }

    });
    
    /*
     * Initializes the timer scheduler keeper.
     * It uses scheduleAtFixedRate to guarantees that:
     * "If any execution of this task
     * takes longer than its period, then subsequent executions
     * may start late, but will not concurrently execute."
     */
    {
      keeper.scheduleAtFixedRate(new Runnable() {

        @Override
        public void run() {
          try {
            /*
             * Read and covert a inputStream to a String.
             * Referred this: 
             * http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
             */
            Scanner s = new Scanner(theURL.openStream(), "utf-8").useDelimiter("\\A");
            try {
              String booleanText = s.hasNext() ? s.next() : "true";
              switcher.set(Boolean.valueOf(booleanText));
              System.out.println("[BooleanSwitcher] [INFO] - " + switcher.get());
            } finally {
              s.close();
            }
            /* We suppress all of exceptions just to
             * makes sure the scheduler loop be going-on, no matter what 
             * kind of error happens.
             */
          } catch (Throwable t) {
            /*
             * Reports above suppressed error to console, which is used
             * to find and track the root cause.
             */
            t.printStackTrace();
            System.out.println("[BooleanSwitcher] [RROR] - " + switcher.get());
          }
        }

      }, this.initialDelay, this.delay, this.unit);
    }
  }

  private static final ConcurrentMap<String, FutureTask<RemoteSwitcher>> fTable = 
      new ConcurrentHashMap<String, FutureTask<RemoteSwitcher>>();
  
  /**
   * Returns the singleton instance of specified URL, it always build a new one if 
   * there is no instance mapping to the specified URL or null if current thread 
   * interrupts during the creation phrase.
   * Moreover, this method guarantees that you will always fetch the same instance
   * for the same URL.
   * 
   * @param url the underlying well-formated URL for fetching the boolean text content
   * @param initialDelay the initial delay of scheduler
   * @param delay the regular delay of scheduler
   * @param unit the measurement unit of delay
   * @param defValue the initial default value of switcher
   * @return the singleton instance of specified URL or null if current thread 
   * interrupts during the creation phrase.
   */
  public static RemoteSwitcher 
  instanceOf(final String url, final boolean defValue, final long initialDelay, final long delay, final TimeUnit unit) {
    FutureTask<RemoteSwitcher> future = fTable.get(url);
    if (future == null) {
      FutureTask<RemoteSwitcher> ft = new FutureTask<RemoteSwitcher>(new Callable<RemoteSwitcher>() {

        @Override
        public RemoteSwitcher call() throws Exception {
          return new RemoteSwitcher(url, defValue, initialDelay, delay, unit);
        }

      });

      future = fTable.putIfAbsent(url, ft);
      if (future == null) {
        future = ft;
        future.run();
      }
    }

    try {
      return future.get();
    } catch (InterruptedException e) {
      fTable.remove(url);
      Thread.currentThread().interrupt();
      return null;
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  /**
   * Returns the <tt>Boolean</tt> switcher value.
   * 
   * @return the <tt>Boolean</tt> switcher value.
   */
  public boolean get() {
    return switcher.get();
  }

  public static void main(String[] args) throws InterruptedException {
    RemoteSwitcher.instanceOf(
        "https://s3-us-west-2.amazonaws.com/chimeroi-emails/config/switcher.boolean", true,
        0, 3, TimeUnit.SECONDS);
    RemoteSwitcher.instanceOf(
        "https://s3-us-west-2.amazonaws.com/chimeroi-emails/config/switcher.boolean", true,
        0, 3, TimeUnit.SECONDS);
    Thread.sleep(1000 * 10 * 3);
  }

}
