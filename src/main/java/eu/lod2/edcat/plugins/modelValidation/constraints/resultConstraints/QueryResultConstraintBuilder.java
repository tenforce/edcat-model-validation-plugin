package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;

import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for constructing QueryResultConstraint objects.
 * <p/>
 * This factory helps you in constructing a ResultConstraint from other formats.
 */
public class QueryResultConstraintBuilder {

  /**
   * Base URI for the config graph.  todo: pull this from Constants.
   */
  private static String CONFIG_URI = "http://lod2.tenforce.com/edcat/example/config"; // Constants.getConfigGraphUri(); //.getLocalName();

  /**
   * Contains the URI of each MatchPredicate.
   * <p/>
   * This makes the lookup code cleaner.
   */
  private enum MatchPredicate {
    EXACTLY(CONFIG_URI + "/matchExactly"),
    LESS_THAN(CONFIG_URI + "/matchLessThan"),
    MORE_THAN(CONFIG_URI + "/matchMoreThan");

    private String value;
    private MatchPredicate(String value) { this.value = value; }
  }

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
  public static QueryResultConstraint fromSparql(String constraintURI, SparqlEngine engine) throws UnknownQueryResultConstraintException {
    Map<String, String> settings = constraintConfigParameters(constraintURI, engine);
    try {
      if (settings.containsKey(MatchPredicate.EXACTLY.value))
        // Exact.ly
        return Exact.ly(intSetting(MatchPredicate.EXACTLY, settings));
      else if (settings.containsKey(MatchPredicate.LESS_THAN.value))
        // Less.than
        return Less.than(intSetting(MatchPredicate.LESS_THAN, settings));
      else if (settings.containsKey(MatchPredicate.MORE_THAN.value))
        // More.than
        return More.than(intSetting(MatchPredicate.MORE_THAN, settings));

    } catch (NumberFormatException e) {
      throw new UnknownQueryResultConstraintException(constraintURI);
    }
    throw new UnknownQueryResultConstraintException(constraintURI);
  }

  /**
   * Returns the value of a setting in a result map, assuming it has an integer value.
   *
   * @param settingName Name of the integer setting to pull.
   * @param settings    Map containing the key/value pairs of the settings.
   * @return Parsed value of the setting.
   */
  private static int intSetting(MatchPredicate settingName, Map<String, String> settings) {
    return Integer.parseInt(settings.get(settingName.value));
  }

  private static Map<String, String> constraintConfigParameters(String constraintURI, SparqlEngine engine) {
    // fetch results
    String query = "" +
        " SELECT ?predicate, ?object " +
        " FROM " + "<" + CONFIG_URI + "> " +
        " WHERE { " +
        "   <" + constraintURI + ">" + " ?predicate ?object. " +
        " }";
    QueryResult results = engine.sparqlSelect(query);

    // convert results to query-agnostic mapping
    Map<String, String> variables = new HashMap<String, String>();
    for (Map<String, String> result : results)
      variables.put(result.get("predicate"), result.get("object"));

    return variables;
  }

}
