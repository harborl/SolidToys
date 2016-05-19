package io.harborl.solid.toys.batch;

import java.util.HashMap;
import java.util.Map;

/**
 * An action loader used to load the action instance.
 * 
 * @author Harbor Luo
 * @since 0.0.1
 *
 */
public enum ActionLoader {
  INSTANCE;
  
  private ActionLoader()  { }
  
  public Map<String, Action> load(ClassLoader classLoader) {
    // TODO: load actions with specified ClassLoader
    
    Map<String, Action> actions = new HashMap<String, Action>();
    
//    Action feed = Profiling.on(new FeedsAction());
//    actions.put(feed.path(), feed);
//    
//    Action message = Profiling.on(new MessageAction());
//    actions.put(message.path(), message);
    
    return actions;
  }

}
