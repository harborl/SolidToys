package io.harborl.solid.toys.dispatch;

import io.harborl.solid.toys.filter.Filter;


/**
 * A {@code Dispatch} defines a user case of dispatching message to different subscribers.
 * Every subscriber should be registered with a specified {@linkplain Filter} to test if 
 * current message need to be notified.
 * 
 * @author Harbor Luo
 * @since 0.1.0
 *
 * @param <T> the type of dispatching message.
 * @param <K> the type of identity of a subscriber.
 */
public interface Dispatch<T, K> {
  /**
   * Invokes the dispatch action for a message
   * 
   * @param t the dispatching message
   */
  void dispatch(T t);
  
  /**
   * Registers a subscriber with its specified filter and identity.
   * 
   * @param o the subscriber
   * @param f the filter
   * @param key the identity
   * @return true if succeed, false if the specified identity had been existed.
   */
  boolean register(Subscriber<T> o, Filter<T> f, K key);
  
  /**
   * Removes the subscriber with this identity.
   * 
   * @param key this identity
   * @return true if succeed, false if this identity is not existed.
   */
  boolean remove(K key);
  
  /**
   * Removes all the subscribers and stop overall dispatching actions.
   */
  void dismiss();
}
