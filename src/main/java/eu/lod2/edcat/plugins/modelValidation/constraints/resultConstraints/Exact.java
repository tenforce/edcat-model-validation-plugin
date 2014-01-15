package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;

import eu.lod2.edcat.utils.QueryResult;

/**
 * Checks that there are exactly N elements in the QueryResult
 */
public class Exact extends QueryResultConstraint {

  /** the number of elements which must be in the QueryResult */
  private int amount;

  /**
   * Constructs a new exact match.
   *
   * Eg: {@code Exact.ly(4)} would yield a
   *
   * @param amount
   * @return
   */
  public static Exact ly(int amount){
    return new Exact( amount );
  }

  private Exact( int amount ){
    this.amount = amount;
  }


  @Override
  public boolean valid(QueryResult queryResult) {
    return queryResult.size() == amount;
  }
}
