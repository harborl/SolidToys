package io.harborl.solid.toys.filter;

/**
 * A filter used to test if the term qualifies the request. <br/>
 * 
 * @author Harbor Luo
 * @since v0.0.1
 *
 * @param <T> the test term type
 */
public interface Filter<T> {
  
  /** Returns the evaluation result. */
  boolean test(T t);
}
