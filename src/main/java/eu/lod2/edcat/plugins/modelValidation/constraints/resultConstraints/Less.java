package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;

import eu.lod2.edcat.utils.QueryResult;

/**
 * Indicates the QueryResult should contain less than the specified number of matches.
 */
public class Less extends QueryResultConstraint {

  /** The QueryResult should contain less than this amount of elements */
  private int lessThanThis;

  /**
   * Sugar for creating a new Less Constraint.
   *
   * @param count Lowest number of results which would still be invalid. Any QueryResult which
   *              yields less results than this is accepted as valid.
   */
  public static Less than( int count ) {
    return new Less( count );
  }

  /**
   * Constructs a new Less constraint.
   * <p/>
   * This class also serves as a factory for itself.  Using the than method this yields:
   * Less.than(number).
   *
   * @param count Lowest number of results that is still invalid.  Any QueryResult which yields less
   *              results than this will be accepted as valid.
   */
  private Less( int count ) {
    this.lessThanThis = count;
  }

  @Override
  public boolean valid( QueryResult queryResult ) {
    return queryResult.size() < this.lessThanThis;
  }
}