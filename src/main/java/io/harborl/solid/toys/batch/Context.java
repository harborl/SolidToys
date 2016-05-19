package io.harborl.solid.toys.batch;

import java.util.HashMap;
import java.util.Map;

/**
 * A batch run thread safe context.
 * It's used to wire up and fetch parameters of current request.
 * The instance of this class is immutable.
 * 
 * @author Harbor Luo
 * @since v0.0.1
 */
public class Context {

  private final String path;
  private final Map<String, Object> parameters;
  
  public static Context EMPTY = new Context();
  
  // Only used to build empty instance.
  private Context() {
    this.parameters = new HashMap<String, Object>();
    this.path = "";
  }
  
  private Context(Builder builder) {
    this.parameters = builder.parameters;
    this.path = builder.actionPath;
  }
  
  /** Make {@code Context} to be friendly to hash based container */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 17;
    result = prime * result + ((path == null) ? 0 : path.hashCode());
    result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    
    if (!(obj instanceof Context)) {
      return false;
    }

    Context other = (Context) obj;
    return this.path.equals(other.path)
           && this.parameters.equals(other.parameters);
  }

  public Object getParameter(String name) {
    return parameters.get(name);
  }

  public String getPath() {
    return path;
  }

  public static class Builder {
    private String actionPath;
    private Map<String, Object> parameters;
    
    public Builder() {
      parameters = new HashMap<String, Object>();
    }
    
    public Builder path(String path) {
      if (path == null || path.isEmpty()) 
        throw new IllegalArgumentException("path is null or empty");
      
      this.actionPath = path;
      return this;
    }
    
    public Builder parameter(String name, String value) {
      parameters.put(name, value);
      return this;
    }
    
    public Builder parameters(Map<String, Object> args) {
      if (args == null) 
        throw new NullPointerException("args = null");
      
      parameters.putAll(args);
      return this;
    }
    
    public Context build() {
      return new Context(this);
    }
  }

}
