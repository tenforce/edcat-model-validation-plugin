package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;

import eu.lod2.edcat.plugins.modelValidation.Constants;
import eu.lod2.edcat.plugins.modelValidation.Constants.CtermsMatchPredicate;
import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.query.Sparql;
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
  public static QueryResultConstraint fromSparql( String constraintURI, SparqlEngine engine ) throws UnknownQueryResultConstraintException {
    Map<String, String> settings = constraintConfigParameters( constraintURI, engine );
    try {
      if ( settings.containsKey( CtermsMatchPredicate.EXACTLY.uri ) )
        // Exact.ly
        return Exact.ly( intSetting( CtermsMatchPredicate.EXACTLY, settings ) );
      else if ( settings.containsKey( CtermsMatchPredicate.LESS_THAN.uri ) )
        // Less.than
        return Less.than( intSetting( CtermsMatchPredicate.LESS_THAN, settings ) );
      else if ( settings.containsKey( CtermsMatchPredicate.MORE_THAN.uri ) )
        // More.than
        return More.than( intSetting( CtermsMatchPredicate.MORE_THAN, settings ) );
    } catch ( NumberFormatException e ) {
      throw new UnknownQueryResultConstraintException( constraintURI );
    }
    throw new UnknownQueryResultConstraintException( constraintURI );
  }

  /**
   * Returns the value of a setting in a result map, assuming it has an integer value.
   *
   * @param settingName Name of the integer setting to pull.
   * @param settings    Map containing the key/value pairs of the settings.
   * @return Parsed value of the setting.
   */
  private static int intSetting( CtermsMatchPredicate settingName, Map<String, String> settings ) {
    return Integer.parseInt( settings.get( settingName.uri ) );
  }

  /**
   * Retrieves all links (and what has been connected to the links) which are connected to the
   * {@code constraintURI} in the configuration graph.
   * <p/>
   * The URI of the configuration graph is retrieved from {@link Constants#CONFIG_BASE_URI}.
   *
   * @param constraintURI The URI id of the Constraint.  This is the object in the searched
   *                      triples.
   * @param engine        Connection to the database containing the information about the
   *                      constraint.
   * @return Map which contains the predicates and the values of the linked information.  In the
   * map, the key is the predicate of the linked information and the value is the object.
   */
  private static Map<String, String> constraintConfigParameters( String constraintURI, SparqlEngine engine ) {
    // fetch results
    String query = Sparql.query( "" +
      " @PREFIX " +
      " SELECT ?predicate, ?object" +
      " FROM @CONFIG_GRAPH" +
      " WHERE {" +
      "    $constraint ?predicate ?object." +
      " }",
      "constraint", new URIImpl( constraintURI ) );

    QueryResult results = engine.sparqlSelect( query );

    // convert results to query-agnostic mapping
    Map<String, String> variables = new HashMap<String, String>();
    for ( Map<String, String> result : results )
      variables.put( result.get( "predicate" ), result.get( "object" ) );

    return variables;
  }
}
