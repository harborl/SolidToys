package io.harborl.solid.toys.dispatch;

import io.harborl.solid.toys.filter.Filter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * A max concurrent level specified simple thread-safe {@code Dispatch} 
 * that is used to dispatch the message to all subscribers with 
 * its specified {@linkplain Filter} asynchronously.
 * 
 * @author Harbor Luo
 * @since 0.1.0
 *
 * @param <T> the message type of dispatching
 */
public final class ConcurrentDispatch<T> implements Dispatch<T, String> {
  
  /**
   * The all subscribers hold in this dispatch.
   */
  private final ConcurrentHashMap<String, Pair<Subscriber<T>, Filter<T>>> subs;
  
  /**
   * The underlying thread pool based executor.
   */
  private final ExecutorService executor;
  
  private ConcurrentDispatch(int level) {
    /*
     * Use a ConcurrentHashMap to simplify the thread-safe issues.
     */
    this.subs = new ConcurrentHashMap<String, Pair<Subscriber<T>, Filter<T>>>();
    /*
     * Thread pool operation strategy:
     * Max concurrent level specified pool size.
     * With a synchronous hand-off queue.
     * Acts a Caller-Runs policy when pool is saturated.
     */
    this.executor = 
        new ThreadPoolExecutor(0, level,
          60L, TimeUnit.SECONDS,
          new SynchronousQueue<Runnable>(),
          new ThreadPoolExecutor.CallerRunsPolicy());
  }

  /**
   * Creates a max concurrent level specified dispatch instance.
   * 
   * @param level the max concurrent level
   * @return the dispatch instance
   * 
   * @param <T> the message type of dispatching
   */
  public static <T> Dispatch<T, String> valueOf(int level) {
    if (level <= 0) throw new IllegalArgumentException("level <= 0");
    
    return new ConcurrentDispatch<T>(level);
  }

  /**
   * Dispatch a specified message asynchronously.
   * 
   * @throws IllegalStateException if dispatch is in dismissed state.
   */
  @Override
  public void dispatch(final T t) {
    try {
      executor.execute(new Runnable() {
  
        @Override
        public void run() {
          for (Pair<Subscriber<T>, Filter<T>> pair : subs.values()) {
            if (pair.value.test(t)) {
              pair.key.notify(t);
            }
          }
        }
        
      });
    } catch (RejectedExecutionException shutdownAlready) {
      reject();
    }
  }

  /**
   * @throws IllegalStateException if dispatch is in dismissed state.
   */
  @Override
  public boolean register(Subscriber<T> o, Filter<T> f, String key) {
    if (o == null || f == null || key == null)
      throw new IllegalArgumentException();
    
    if (executor.isShutdown()) {
      reject();
    }

    return subs.putIfAbsent(key, Pair.of(o, f)) == null;
  }

  private void reject() {
    throw new IllegalStateException("dispatch is dismissed");
  }

  @Override
  public boolean remove(String key) {
    return subs.remove(key) != null;
  }

  /**
   * Rejects the further dispatch message and subscriber registering, 
   * and waits till the completion of preview messages to be dispatched.
   * Finally, clear all the subscribers hold in this dispatch and break.
   */
  @Override
  public void dismiss() {
    executor.shutdown();
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      subs.clear();
    }
  }

  /**
   * A simple immutable definition of key value pair.
   *
   * @param <K> the key type.
   * @param <V> the value type.
   */
  static class Pair<K, V> {
    final K key;
    final V value;
    Pair(K key, V value) {
      this.key = key;
      this.value = value;
    }
    static <K, V> Pair<K, V> of(K key, V value) {
      return new Pair<K, V>(key, value);
    }
  }

}
