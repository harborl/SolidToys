package io.harborl.solid.toys.profiling;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A profiling class used to measure time cost of the real action.
 */
public class TimeWatcher implements InvocationHandler {
  
  private final Object action;
  private TimeWatcher(Object action) {
    this.action = action;
  }
  
  /** 
   * On behalf of an action to run and measure the time meanwhile.
   * 
   * @param action the action need to measure the run time
   * @param <T> the action type
   * 
   * @return returns the decorated instance.
   */
  @SuppressWarnings("unchecked")
  public static <T> T with(T action) {
    return (T)new TimeWatcher(action).bind();
  }

  private Object bind() {
    return Proxy.newProxyInstance(
        this.action.getClass().getClassLoader(), 
        this.action.getClass().getInterfaces(),
        this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // profiling stuff put here
    return method.invoke(this.action, args);
  }
  
}
