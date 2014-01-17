package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;


import eu.lod2.edcat.utils.QueryResult;

/**
 * Indicates the QueryResult should contain more than a specified number of matches.
 */
public class More extends QueryResultConstraint {

  /** The QueryResult should contain more than this amount of elements */
  private int moreThanThis;

  /**
   * Sugar for creating a new More Constraint.
   *
   * @param count Highest number of results which would still be invalid. Any QueryResult which
   *              yields more results than this is accepted as valid.
   */
  public static More than( int count ) {
    return new More( count );
  }

  /**
   * Constructs a new More constraint.
   * <p/>
   * This class also serves as a factory for itself.  Using the than method this yields:
   * More.than(number).
   *
   * @param count Highest number of results that is still invalid.  Any QueryResult which yields
   *              more results than this will be accepted as valid.
   */
  private More( int count ) {
    this.moreThanThis = count;
  }

  @Override
  public boolean valid( QueryResult queryResult ) {
    return queryResult.size() > this.moreThanThis;
  }
}
