package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;

/**
 * Factory/singleton collection which makes the construction of Exact matches cleaner.
 */
public class Exactly {

  /** contain no results */
  public static QueryResultConstraint NOTHING = Exact.ly(0);
  /** contain exactly one result */
  public static QueryResultConstraint ONE = Exact.ly(1);
  /** contain exactly two results */
  public static QueryResultConstraint TWO = Exact.ly(2);
  /** contain exactly two results */
  public static QueryResultConstraint THREE = Exact.ly(3);
  /** contain exactly two results */
  public static QueryResultConstraint FOUR = Exact.ly(4);
  /** contain exactly two results */
  public static QueryResultConstraint FIVE = Exact.ly(5);

}
