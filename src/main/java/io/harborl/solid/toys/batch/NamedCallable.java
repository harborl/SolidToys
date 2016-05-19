
package io.harborl.solid.toys.batch;

import java.util.concurrent.Callable;

/**
 * A {@code Callable} implementation.
 * It contains a name and always changes current thread with it.
 * 
 * @author Harbor Luo
 * @since 0.0.1
 */
public abstract class NamedCallable<V> implements Callable<V> {
  
  private final String name;
  public NamedCallable(String format, Object... args) {
    this.name = String.format(format, args);
  }

  @Override public V call() throws Exception {
    String oldName = Thread.currentThread().getName();
    Thread.currentThread().setName(name);
    try {
      return exec();
    } finally {
      Thread.currentThread().setName(oldName);
    }
  }

  protected abstract V exec();
}
