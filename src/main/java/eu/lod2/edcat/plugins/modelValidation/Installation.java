package eu.lod2.edcat.plugins.modelValidation;

import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.hooks.constraints.Priority;
import eu.lod2.hooks.contexts.CatalogInstallationContext;
import eu.lod2.hooks.contexts.InstallationContext;
import eu.lod2.hooks.handlers.dcat.ActionAbortException;
import eu.lod2.hooks.handlers.dcat.CatalogInstallationHandler;
import eu.lod2.hooks.handlers.dcat.InstallationHandler;
import eu.lod2.query.Db;
import eu.lod2.query.Sparql;
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
    setupDatabase( );
  }

  @Override
  public void handleCatalogInstall( CatalogInstallationContext context ) throws ActionAbortException {
    setupCatalog( context.getCatalogURI() );
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
   */
  private void setupDatabase( ) {
    // fetch file
    InputStream configFileInput = null;
    try {
      configFileInput = Constants.VALIDATION_RULES_GRAPH_FILE_PATH.openStream();
      // parse file
      Model validationRules = Rio.parse(
        configFileInput,
        Constants.RULES_GRAPH.stringValue(),
        Constants.RULE_FILE_FORMAT );
      // add statements
      Db.add( validationRules, Constants.RULES_GRAPH );
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
   * @param catalogUri URI of the newly inserted catalog.
   */
  private void setupCatalog( URI catalogUri ) {
    QueryResult rules = Db.query( "" +
      " @PREFIX " +
      " SELECT ?rule" +
      " FROM $rulesGraph" +
      " WHERE {" +
      "   ?rule a cterms:ValidationRule." +
      " }",
      "rulesGraph", Constants.RULES_GRAPH );

    Model statementConnections = new LinkedHashModel();

    for ( Map<String, String> ruleMap : rules )
      statementConnections.add(
        catalogUri,
        Sparql.namespaced( "cterms", "validatedBy" ),
        new URIImpl( ruleMap.get( "rule" ) ) );

    Db.add( statementConnections, Constants.RULES_GRAPH );
  }

}