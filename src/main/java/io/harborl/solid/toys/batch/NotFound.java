/**
 * 
 */
package io.harborl.solid.toys.batch;

/**
 * One implementation of {@code Action} to indicate 'path not found' meaning.
 * @author Harbor Luo
 * @since v0.0.1
 */
public enum NotFound implements Action {
  ACTION;

  private NotFound()  { }
  
  @Override
  public String apply(Context context) {
    return "{ code:404, message:\"method not found\" }";
  }

  @Override
  public String path() {
    return null; // should be same with action
  }

}
