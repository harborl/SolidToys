package io.harborl.solid.toys.filter;

/**
 * A filter used to test if the term qualifies the request.
 * 
 * @author Harbor Luo
 * @since v0.0.1
 *
 * @param <T> the test term type
 */
public interface Filter<T> {
  
  /** 
   * Returns the evaluation result.
   * 
   * @param t the evaluated entity type
   * @return the evaluation result.
   */
  boolean test(T t);
}
