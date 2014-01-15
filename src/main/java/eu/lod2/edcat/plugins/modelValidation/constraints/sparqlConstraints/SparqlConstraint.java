package eu.lod2.edcat.plugins.modelValidation.constraints.sparqlConstraints;

import eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints.QueryResultConstraint;

/**
 * Specifies a constraint as given by a SPARQL query and the constraint it places on its result.
 */
public class SparqlConstraint {


  // --- VARIABLES

  /** Text representation of the SPARQL query which ought to be executed on the set of statements */
  private String query;

  /** Contains the constraint placed on the result of executing the query. */
  private QueryResultConstraint constraint;


  // --- GETTERS

  /**
   * Returns the SPARQL query which will yield the result for validation.
   *
   * @return String representation of the query.
   */
  public String getQuery() {
    return query;
  }

  /**
   * Returns the object which defines the constrained result on the specified object.
   *
   * @return Constraint to which the result of executing {@code self.getQuery()} should be
   *         validated.
   */
  public QueryResultConstraint getConstraint() {
    return constraint;
  }


  // --- CONSTRUCTORS

  /**
   * Constructs a new SparqlConstraint definition which should accept its result if
   * {@code constraint} holds on the results of executing {@code query} on the graph containing
   * the DataSet.
   *
   * @param constraint limitation of the data which the sparql query may return.
   * @param query SPARQL query which generates the results.
   */
  public SparqlConstraint( QueryResultConstraint constraint, String query ){
    this.constraint = constraint;
    this.query = query;
  }
}
