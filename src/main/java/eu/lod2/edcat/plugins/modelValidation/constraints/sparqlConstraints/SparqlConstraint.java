package eu.lod2.edcat.plugins.modelValidation.constraints.sparqlConstraints;

import eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints.QueryResultConstraint;
import eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints.QueryResultConstraintBuilder;
import eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints.UnknownQueryResultConstraintException;
import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.query.Sparql;
import org.openrdf.model.URI;

/**
 * Specifies a constraint as given by a SPARQL query and the constraint it places on its result.
 */
public class SparqlConstraint {


  // --- VARIABLES

  /** Text representation of the SPARQL query which ought to be executed on the set of statements */
  private String query;

  /** Contains the constraint placed on the result of executing the query. */
  private QueryResultConstraint constraint;

  /** Human description of the sparql constraint. */
  private String description;

  /** A URI which identifies the constraint. */
  private URI identifier;


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
   * validated.
   */
  public QueryResultConstraint getConstraint() {
    return constraint;
  }

  /**
   * Returns a human description of this constraint.
   *
   * @return String containing a human description of the used rule.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns an identifier for this SparqlConstraint.
   *
   * @return URI which identifies this SparqlConstraint.
   */
  public URI getIdentifier() {
    return identifier;
  }

  // --- CONSTRUCTORS

  /**
   * Constructs a new SparqlConstraint definition which should accept its result if {@code
   * constraint} holds on the results of executing {@code query} on the graph containing the
   * DataSet.
   *
   * @param constraint  limitation of the data which the sparql query may return.
   * @param query       SPARQL query which generates the results.
   * @param description a human description of what this rule checks.
   * @param identifier  URI identifier for the constraint.
   */
  @SuppressWarnings("UnusedDeclaration")
  public SparqlConstraint( QueryResultConstraint constraint, String query, String description, URI identifier ) {
    this.identifier = identifier;
    this.constraint = constraint;
    this.query = query;
  }

  /**
   * Constructs a new SparqlConstraint based on the sparql engine and the URI of the constraint.
   *
   * @param engine provides a connection to the database which contains the graph of constraints.
   * @param rule   URI id of the constraint.
   */
  public SparqlConstraint( SparqlEngine engine, URI rule ) throws UnknownQueryResultConstraintException {
    this.identifier = rule;
    this.constraint = QueryResultConstraintBuilder.fromSparql( rule.toString(), engine );
    this.fetchDescriptionAndQuery( engine, rule );
  }

  // --- HELPERS

  /**
   * Fetches the description and the sparql query from a supplied constraint.
   *
   * @param engine provides a connection to the database which contains the graph of constraints.
   * @param rule   URI id of the rule.
   */
  private void fetchDescriptionAndQuery( SparqlEngine engine, URI rule ) {
    String query = Sparql.query( "" +
      " @PREFIX " +
      " SELECT ?description, ?sparqlQuery " +
      " FROM @CONFIG_GRAPH " +
      " WHERE {" +
      "   $rule cterms:sparqlQuery ?sparqlQuery;" +
      "         cterms:description ?description." +
      " }",
      "rule", rule);

    QueryResult results = engine.sparqlSelect( query );

    if ( results.size() == 0 )
      throw new IllegalArgumentException( "Could not find description of " + rule + " in store." );

    this.query = results.get( 0 ).get( "sparqlQuery" );
    this.description = results.get( 0 ).get( "description" );
  }

}
