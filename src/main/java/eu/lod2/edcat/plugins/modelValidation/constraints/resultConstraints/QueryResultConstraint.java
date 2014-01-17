package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;

import eu.lod2.edcat.utils.QueryResult;

/**
 * Represents a constraint on a QueryResult.
 * <p/>
 * This is used to check whether or not a DataSet to insert is valid.  Such validation is done
 * through the use of SPARQL queries.  The result of such a query must abide certain constraints in
 * order for the model to be valid.  It is exactly this, which is checked through an instance of
 * this class.
 */
public abstract class QueryResultConstraint {

  /**
   * Returns true iff queryResult abides to the supplied constraint.
   *
   * @return true iff the constraint holds up on queryResult.
   */
  public abstract boolean valid( QueryResult queryResult );

}
