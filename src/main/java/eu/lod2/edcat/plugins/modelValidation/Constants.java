package eu.lod2.edcat.plugins.modelValidation;

import org.openrdf.rio.RDFFormat;

import java.net.URL;

/**
 * All constants which are used in the modelValidationPlugin are contained within this file. Most of
 * these constants should be diverted to a more generic Constant class.
 */
public class Constants {

  /** Resource path to the validation rules graph */
  final public static String VALIDATION_RULES_GRAPH_PACKAGE_PATH
      = "/eu/lod2/edcat/plugins/modelValidation/validationRules.ttl";

  /** Resource file path to the validation rules graph */
  final public static URL VALIDATION_RULES_GRAPH_FILE_PATH
      = Installation.class.getResource( Constants.VALIDATION_RULES_GRAPH_PACKAGE_PATH );

  /** Base URI for the config graph. */
  final public static String CONFIG_BASE_URI = "http://lod2.tenforce.com/edcat/example/config/";

  /** Input format for the Graph */
  final public static RDFFormat RULE_FILE_FORMAT = RDFFormat.TURTLE;

  /** String of prefixes which may be used in SPARQL queries */
  final public static String SPARQL_PREFIXES = " " +
      "PREFIX : <http://lod2.tenforce.com/edcat/example/config/> \n" +
      "PREFIX edcat: <http://lod2.tenforce.com/edcat/terms/> \n" +
      "PREFIX cterms: <http://lod2.tenforce.com/edcat/terms/config/> \n" +
      "PREFIX catalogs: <http://lod2.tenforce.com/edcat/catalogs/> \n";

  /** Namespace which contains the terms of the ModelValidator */
  final public static String CTERMS = "http://lod2.tenforce.com/edcat/terms/config/";

  /**
   * Contains the URI of each MatchPredicate.
   * <p/>
   * This makes the lookup code cleaner.
   */
  public enum CtermsMatchPredicate {
    EXACTLY( Constants.CTERMS + "matchExactly"),
    LESS_THAN( Constants.CTERMS + "matchLessThan"),
    MORE_THAN( Constants.CTERMS + "matchMoreThan");

    public String uri;
    private CtermsMatchPredicate(String uri) { this.uri = uri; }
  }
}