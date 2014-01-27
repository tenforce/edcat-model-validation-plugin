package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;

import eu.lod2.edcat.plugins.modelValidation.Constants;
import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.query.Sparql;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for constructing QueryResultConstraint objects.
 * <p/>
 * This factory helps you in constructing a ResultConstraint from other formats.
 */
public class QueryResultConstraintBuilder {

  /**
   * Constructs a new QueryResultConstraint from its URI and the engine in which it's stored.
   *
   * @param constraintURI URI which describes the constraint
   * @param engine        Engine as an access point to the graph where the settings are stored.
   * @return QueryResultConstraint as defined by the constraintURI in the database.
   * @throws UnknownQueryResultConstraintException when the constraintURI could not be converted to
   *                                               a QueryResultConstraint due to incorrect or
   *                                               lacking information.
   */
  public static QueryResultConstraint fromSparql( URI constraintURI, SparqlEngine engine ) throws UnknownQueryResultConstraintException {
    Map<URI, String> settings = constraintConfigParameters( constraintURI, engine );
    try {
      if ( settings.containsKey( Sparql.namespaced( "cterms", "matchExactly" ) ) )
        // Exact.ly
        return Exact.ly( intSetting( Sparql.namespaced( "cterms", "matchExactly" ), settings ) );
      else if ( settings.containsKey( Sparql.namespaced( "cterms", "matchLessThan" ) ) )
        // Less.than
        return Less.than( intSetting( Sparql.namespaced( "cterms", "matchLessThan" ), settings ) );
      else if ( settings.containsKey( Sparql.namespaced( "cterms", "matchMoreThan" ) ) )
        // More.than
        return More.than( intSetting( Sparql.namespaced( "cterms", "matchMoreThan" ), settings ) );
    } catch ( NumberFormatException e ) {
      throw new UnknownQueryResultConstraintException( constraintURI );
    }
    throw new UnknownQueryResultConstraintException( constraintURI );
  }

  /**
   * Returns the value of a setting in a result map, assuming it has an integer value.
   *
   * @param setting  Name of the integer setting to pull.
   * @param settings Map containing the key/value pairs of the settings.
   * @return Parsed value of the setting.
   */
  private static int intSetting( URI setting, Map<URI, String> settings ) {
    return Integer.parseInt( settings.get( setting ) );
  }

  /**
   * Retrieves all links (and what has been connected to the links) which are connected to the
   * {@code constraintURI} in the configuration graph.
   *
   * @param constraint The URI id of the Constraint.  This is the object in the searched
   *                   triples.
   * @param engine     Connection to the database containing the information about the
   *                   constraint.
   * @return Map which contains the predicates and the values of the linked information.  In the
   * map, the key is the predicate of the linked information and the value is the object.
   */
  private static Map<URI, String> constraintConfigParameters( URI constraint, SparqlEngine engine ) {
    // fetch results
    String query = Sparql.query( "" +
      " @PREFIX " +
      " SELECT ?predicate, ?object" +
      " FROM $rulesGraph" +
      " WHERE {" +
      "    $constraint ?predicate ?object." +
      " }",
      "constraint", constraint,
      "rulesGraph", Constants.RULES_GRAPH);

    QueryResult results = engine.sparqlSelect( query );

    // convert results to query-agnostic mapping
    Map<URI, String> variables = new HashMap<URI, String>();
    for ( Map<String, String> result : results )
      variables.put( new URIImpl( result.get( "predicate" ) ), result.get( "object" ) );

    return variables;
  }
}
