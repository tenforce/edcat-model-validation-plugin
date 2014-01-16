package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;

import eu.lod2.edcat.utils.QueryResult;

/**
 * Checks that there are exactly N elements in the QueryResult
 */
public class Exact extends QueryResultConstraint {

  /** the number of elements which must be in the QueryResult */
  private int amount;

  /**
   * Constructs a new exact match.  Uses a short and easy to read syntax.
   * <p/>
   * Eg: {@code Exact.ly(4)} would yield a
   *
   * @param amount Amount of elements which we expect to have in a successful match.
   * @return QueryResultConstraint which only accepts a QueryResult which has exactly amount
   * matches.
   */
  public static Exact ly( int amount ) {
    return new Exact( amount );
  }

  /**
   * Simple constructor for the Exact class.
   *
   * @param amount Amount of elements which we expect to have in a successful match.
   */
  private Exact( int amount ) {
    this.amount = amount;
  }

  @Override
  public boolean valid( QueryResult queryResult ) {
    return queryResult.size() == amount;
  }
}
