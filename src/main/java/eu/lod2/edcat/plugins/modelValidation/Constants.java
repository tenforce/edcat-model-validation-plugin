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

  /** Input format for the Graph */
  final public static RDFFormat RULE_FILE_FORMAT = RDFFormat.TURTLE;
}