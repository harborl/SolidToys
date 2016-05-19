package io.harborl.solid.toys.filter;

/**
 * A Condition Filter is a duty chain like {@linkplain Filter} that composes
 * a underlying filter with {@code and}, {@code or} condition operation.
 * <p>
 * Suppose you have four conditions that are A, B, C D,
 * and you might want to compose those conditions with different sequence and 
 * different condition operations.
 * <br>
 * The bellow code will show you how to use <tt>Condition</tt> 
 * to build above requirement.
 * <br>
 * <tt>
 * Condition.newInstance(A).and(B).or(C).and(D).test();
 * </tt><br>
 * <i>which equals </i><br>
 * {@code A && B || C && D }
 * 
 * @author Harbor Luo
 * @since 0.0.1
 *
 * @param <T> the test term type
 */
public class Condition<T> implements Filter<T> {

  /** The enums of all condition operations. */
  private enum ConOp { AND, OR, DUMMY }

  private final Filter<T> underlying;
  private final ConOp condition;
  
  private Condition(Filter<T> underlying, ConOp condition) {
    this.underlying = underlying;
    this.condition = condition;
  }

  /**
   * Creates a condition filter with the 'header' underlying condition.
   * 
   * @param underlying the evaluation filter
   * @return a instance of the condition filter
   * 
   * @param <KEY> the test term type
   */
  public static <KEY> Condition<KEY> newInstance(Filter<KEY> underlying) {
    if (underlying == null) throw new NullPointerException("header filter is null");
    return new Condition<KEY>(underlying, ConOp.DUMMY);
  }

  /**
   * Appends a {@code and} condition operation with a underlying {@linkplain Filter}.
   * 
   * @param underlying the evaluation filter
   * @return a instance of the condition filter
   */
  public Condition<T> and(final Filter<T> underlying) {
    if (underlying == null) throw new NullPointerException("successor filter is null");
    return new Condition<T>(this, ConOp.AND) {
      @Override protected boolean evaluate(T t) { 
        return underlying.test(t); 
      }
    };
  }

  /**
   * Appends a {@code or} condition operation with a underlying {@linkplain Filter}.
   * 
   * @param underlying the evaluation filter
   * @return a instance of the condition filter
   */
  public Condition<T> or(final Filter<T> underlying) {
    if (underlying == null) throw new NullPointerException("successor filter is null");
    return new Condition<T>(this, ConOp.OR) {
      @Override protected boolean evaluate(T t) { 
        return underlying.test(t); 
      }
    };
  }

  /**
   * The implementation details: <br>
   * Except for the header condition that must be a dummy type 
   * operation, other underlying conditions will be chained and tests through it's evaluate call.
   */
  @Override public boolean test(T t) {
    if (condition == ConOp.AND) {
      return underlying.test(t) && this.evaluate(t);
    } else if (condition == ConOp.OR) {
      return underlying.test(t) || this.evaluate(t);
    } else { // Dummy node as a header condition
      return underlying.test(t);
    }
  }

  protected boolean evaluate(T t) {
    // The dummy node should always be the header condition
    if (this.condition == ConOp.DUMMY)
      throw new AssertionError("invalid state! - The dummy node should always be the header condition");
    return true;
  }
}
