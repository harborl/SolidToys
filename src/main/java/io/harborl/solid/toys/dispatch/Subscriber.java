package io.harborl.solid.toys.dispatch;

/**
 * A {@code Subscriber} is a actor who only receives the specified messages.
 * Generally, It uses a {@linkplain Condition} {@linkplain Filter} to test the message.
 * 
 * @author Harbor Luo
 * @since 0.1.0
 *
 * @param <T> the message type
 */
public interface Subscriber<T> {
  /**
   * Notifies the subscriber to receive specified message.
   * 
   * @param t the specified message
   */
  void notify(T t);
}
