package io.harborl.solid.toys.batch;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A mapper used to map the input request parameters to the actions.
 * 
 * @author Harbor Luo
 * @since v0.0.1
 *
 */
public enum Scatter {
  INSTANCE;
  
  final Map<String, Action> actionByPath;
  
  /* Initialize and registry all of actions here. */
  {
    Map<String, Action> actions = ActionLoader.INSTANCE.load(Scatter.class.getClassLoader());
    actionByPath = Collections.unmodifiableMap(actions);
  }
  
  private Scatter() { }
  
  /**
   * Maps the requests to the actions according request path.
   * 
   * @param requests the request path and the parameters
   * @return the mapped action results.
   */
  public Map<Context, Action> 
  map(Map<String, Map<String, Object>> requests) {
    Map<Context, Action> result = new HashMap<Context, Action>();

    for (String path : requests.keySet()) {
      if (actionByPath.containsKey(path)) {
        Context.Builder bilder = 
            new Context.Builder()
              .path(path)
              .parameters(requests.get(path));

        result.put(bilder.build(), actionByPath.get(path));
      } else {
        result.put(
            new Context.Builder().path(path).build(), NotFound.ACTION);
      }
    }
    return result;
  }
 
}
