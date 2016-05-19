/**
 * 
 */
package io.harborl.solid.toys.batch;

/**
 * An action interface to react a specified request path.
 * 
 * The request action should implement bellow both methods:
 * <ol>
 *   <li>{@code apply(Context context)}: process current request and 
 *   response the result, usually with JSON formated string.</li>
 *   <li>{@code path()}: returns the request path, it should be unique with other actions. </li>
 * </ol>
 * 
 * <b>Thread Safe: </b>
 * You should always assume that every single action instance will be 
 * applied to different threads.
 * 
 * @author Harbor Luo
 * @since v0.0.1
 */
public interface Action {
  /**
   * Process current request and 
   *   response the result, usually with JSON formated string.
   * @param context the context for current request.
   * @return the response
   */
  String apply(Context context);
  
  /**
   * returns the action path for a request
   * @return action path 
   */
  String path();
}
