package io.harborl.solid.toys.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A Stream is a collection wrapper that can be filtered with a {@linkplain Filter}.
 * It simplifies the usage of Filter operation of a collection data.
 * <br/>
 * The relevant operations are thread-safe if elements of collection are immutable.
 * <p/>
 * <strong>Usage:</strong>
 * <pre>{@code
 * List<String> results = Stream.valueOf(samples).filter(filter).get();
 * }</pre>
 * 
 * @author Harbor Luo
 * @since v0.0.1
 *
 * @param <T> the collection entity type
 */
public final class Stream<T> {

  private final Collection<T> collection;
  // Guarded by this
  private Filter<T> filter;
  
  private Stream(Collection<T> collection) {
    this.collection = 
        Collections.unmodifiableCollection(new ArrayList<T>(collection));
  }

  private Stream(T[] collection) {
    this.collection = 
        Collections.unmodifiableCollection(new ArrayList<T>(Arrays.asList(collection)));
  }
  
  /** Creates a Stream with a collection. */
  public static <T> Stream<T> valueOf(Collection<T> collection) {
    if (collection == null) throw new NullPointerException("collection == null");
    return new Stream<T>(collection);
  }
  
  /** Creates a Stream with a collection. */
  public static <T> Stream<T> valueOf(T[] collection) {
    if (collection == null) throw new NullPointerException("collection == null");
    return new Stream<T>(collection);
  }
  
  /** Injects the filter operation. */
  public synchronized Stream<T> filter(Filter<T> filter) {
    this.filter = filter;
    return this;
  }

  /** Filters and returns the collection result with a filter operation if it exists. */
  public List<T> get() {
    Filter<T> theFilter = null;
    synchronized (this) {
      theFilter = this.filter;
    }

    List<T> target = new ArrayList<T>();
    for (T t : collection) {
      if (theFilter == null || theFilter.test(t)) {
        target.add(t);
      }
    }
    return target;
  }
}
