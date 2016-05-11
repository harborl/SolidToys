package io.harborl.solid.toys.filter;

public class Test {

  public static void main(String[] args) {
    Filter<String> filter = Condition.newInstance(
        new Filter<String>() {
          @Override public boolean test(String t) {
            return t.length() != 1;
          }
        })
        .or(new Filter<String>() {
          @Override public boolean test(String t) {
            return t.length() == 3;
          }
        })
        .and(new Filter<String>() {
          @Override public boolean test(String t) {
            return t.contains("m");
          }
        })
        ;

    String[] samples = { "good", "morning", "sir", "male", "a" };
    System.out.println(Stream.valueOf(samples).filter(filter).get());
  }

}
