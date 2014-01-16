package eu.lod2.edcat.plugins.modelValidation;

import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.constraints.Priority;
import eu.lod2.hooks.contexts.CatalogInstallationContext;
import eu.lod2.hooks.contexts.InstallationContext;
import eu.lod2.hooks.handlers.dcat.CatalogInstallationHandler;
import eu.lod2.hooks.handlers.dcat.InstallationHandler;
import eu.lod2.hooks.util.ActionAbortException;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Installation for the ModelValidator.
 * <p/>
 * This ensures that all currently known validation rules are stored in the configuration graph of
 * the database.
 */
public class Installation implements InstallationHandler, CatalogInstallationHandler {

  // --- HOOKS

  @Override
  public void handleInstall( InstallationContext context ) throws ActionAbortException {
    setupDatabase( context.getEngine() );
  }

  @Override
  public void handleCatalogInstall( CatalogInstallationContext context ) throws ActionAbortException {
    setupCatalog( context.getEngine(), context.getCatalogURI() );
  }


  // --- PRIORITIES

  @Override
  public Collection<Priority> getConstraints( String hook ) {
    return null;
  }


  // --- IMPLEMENTATION

  /**
   * Installs the rules which may be used for validating the model in the config graph.
   * <p/>
   * Once these rules have been installed, they may be enabled and disabled on a per-catalog basis
   * by connecting the Catalog to the the rule through the cterms:validatedBy predicate.
   *
   * @param engine Connection to the RDF store in which the validation rules need to be described.
   */
  private void setupDatabase( SparqlEngine engine ) {
    // fetch file
    InputStream configFileInput = null;
    try {
      configFileInput = Constants.VALIDATION_RULES_GRAPH_FILE_PATH.openStream();
      // parse file
      Model validationRules = Rio.parse( configFileInput, Constants.CONFIG_BASE_URI, Constants.RULE_FILE_FORMAT );
      // add statements
      engine.addStatements( validationRules, new URIImpl( Constants.CONFIG_BASE_URI ) );
      configFileInput.close();
      // catch any errors
    } catch ( IOException e ) {
      LoggerFactory
          .getLogger( "validation.Installation" )
          .error( "IO exception when reading " + Constants.VALIDATION_RULES_GRAPH_FILE_PATH );
      if ( configFileInput != null )
        try {
          configFileInput.close();
        } catch ( IOException e1 ) {
          LoggerFactory
              .getLogger( "validation.Installation" )
              .error( "And couldn't clean up file descriptor for " + Constants.VALIDATION_RULES_GRAPH_FILE_PATH );
        }
    } catch ( RDFParseException e ) {
      LoggerFactory
          .getLogger( "validation.Installation" )
          .error( "Failed to parse " + Constants.VALIDATION_RULES_GRAPH_PACKAGE_PATH );
    }
  }

  /**
   * Sets up a new catalog, linking it to all currently known rules.
   *
   * @param engine     Connection to the RDF store in which the rule connections will be inserted.
   * @param catalogUri URI of the newly inserted catalog.
   */
  private void setupCatalog( SparqlEngine engine, URI catalogUri ) {
    String query = "" +
        Constants.SPARQL_PREFIXES +
        " SELECT ?rule \n" +
        " FROM <" + Constants.CONFIG_BASE_URI + "> \n" +
        " WHERE {\n" +
        "   ?rule a cterms:ValidationRule.\n" +
        " }";

    QueryResult rules = engine.sparqlSelect( query );

    Model statementConnections = new LinkedHashModel();

    for ( Map<String, String> ruleMap : rules )
      statementConnections.add(
          catalogUri,
          new URIImpl( Constants.CTERMS + "validatedBy" ),
          new URIImpl( ruleMap.get( "rule" ) ) );

    engine.addStatements( statementConnections, new URIImpl( Constants.CONFIG_BASE_URI ) );
  }

}