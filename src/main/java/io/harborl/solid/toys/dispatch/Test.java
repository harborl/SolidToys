package io.harborl.solid.toys.dispatch;

import io.harborl.solid.toys.filter.Condition;
import io.harborl.solid.toys.filter.Filter;


public class Test {
  
  public static void main(String[] args) {
    /* ----------------------------------------------------------------------
     * Preparation : Registers subscriber and its filter with a unique key.
     * ----------------------------------------------------------------------
     */
    Dispatch<String, String> dispatch = 
      ConcurrentDispatch.valueOf(Runtime.getRuntime().availableProcessors() + 1);
    dispatch.register(new Subscriber<String>() {

      @Override
      public void notify(String t) {
        System.out.println(t);
      }
      
    }, Condition.newInstance(new Filter<String>() {

      @Override
      public boolean test(String t) {
        return t == null ? false : t.startsWith("[INFO]");
      }
      
    }), "std-out");
    
    dispatch.register(new Subscriber<String>() {

      @Override
      public void notify(String t) {
        System.err.println(t);
      }
      
    }, Condition.newInstance(new Filter<String>() {

      @Override
      public boolean test(String t) {
        return t == null ? false : t.startsWith("[ERROR]");
      }
      
    }), "std-err");
    
    /* ----------------------------------------------------------------------
     * Dispatch : Dispatch the working logs asynchronously.
     * ----------------------------------------------------------------------
     */
    dispatch.dispatch("[INFO] Hi, human.");
    dispatch.dispatch("[ERROR] Damn!");
    
    /* ----------------------------------------------------------------------
     * Dismiss : At the end of program, dismiss it.
     * ----------------------------------------------------------------------
     */
    dispatch.dismiss();
  }

}
