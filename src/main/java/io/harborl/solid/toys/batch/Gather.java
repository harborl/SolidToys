package io.harborl.solid.toys.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * A Gather is used to apply the mapped action and collect the result.
 * 
 * @author Harbor Luo
 * @since v0.0.1
 */
public enum Gather {
  INSTANCE;

  private final ExecutorService executorService;
  /** max seconds to wait for a reduce process */
  private final static int MAX_TIMEOUT = 30; 

  private Gather() {
    executorService = Executors.newFixedThreadPool(
      Runtime.getRuntime().availableProcessors() + 3, 
      new ThreadFactory() {
        @Override public Thread newThread(Runnable runnable) {
          Thread result = new Thread(runnable, "Homethy Web Batch Reducer");
          result.setDaemon(false);
          return result;
        }
    });
  }
  
  /** Shut down the relevant threads, it's useful for command line program*/
  public void evictAll() {
    executorService.shutdown();
  }
  
  /** An simple immutable key and value pair entry, derived from {@code Map.Entry<K, V>}*/
  final static class ImmutableEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private final V value;

    /** simple factory method, used to deduce the generic type */
    public static <K, V> ImmutableEntry<K, V> of(K key, V value) {
      return new ImmutableEntry<K, V>(key, value);
    }

    private ImmutableEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override public K getKey() {
      return key;
    }

    @Override public V getValue() {
      return value;
    }

    @Override public V setValue(V value) {
      throw new AssertionError("change disabled!");
    }
  }

  /**
   * Apply all the actions and collect the results.
   * 
   * @param actions the mapped actions
   * @param needConcurrent apply actions concurrently or not
   * @return The paths and the action results
   */
  public Map<String, String> ruduce(Map<Context, ? extends Action> actions, boolean needConcurrent) {
    Map<String, String> result = new HashMap<String, String>();
    
    if (!needConcurrent) {
      throw new AssertionError("has not implemented");
    }
    
    List<Callable<Entry<String, String>>> tasks = 
        new ArrayList<Callable<Entry<String, String>>>(actions.size());
    
    for (final Context context : actions.keySet()) {
      final Action action = actions.get(context);
      final String path = context.getPath();
      tasks.add(new NamedCallable<Entry<String, String>>(path) {
        @Override protected Entry<String, String> exec() {
          String resp = null;
          try {
            resp = action.apply(context);
          } catch (Exception ignored) {
//            String fullStackTrace = ExceptionUtils.getFullStackTrace(ignored);
//            resp = "{ code:500, message:\"call failed\""
//                + ", fullStackTrace: \"" + fullStackTrace + "\""
//                + " }";
            // just ignore all the exceptions
            // and indicate error with a error response.
          }
          return ImmutableEntry.of(path, resp);
        }
      });
    }
    
    try {
      List<Future<Entry<String, String>>> futureList = 
          executorService.invokeAll(tasks, MAX_TIMEOUT, TimeUnit.SECONDS);
      
      // Waiting for completing
      for (Future<Entry<String, String>> future : futureList) {
        try {
          final Entry<String, String> ret = future.get();
          result.put(ret.getKey(), ret.getValue());
        } catch (ExecutionException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } 
      }
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }
}
