package eu.lod2.edcat.plugins.modelValidation;

import eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints.QueryResultConstraint;
import eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints.QueryResultConstraintBuilder;
import eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints.UnknownQueryResultConstraintException;
import eu.lod2.edcat.plugins.modelValidation.constraints.sparqlConstraints.SparqlConstraint;
import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.constraints.Constraint;
import eu.lod2.hooks.constraints.Priority;
import eu.lod2.hooks.contexts.AtContext;
import eu.lod2.hooks.handlers.dcat.AtCreateHandler;
import eu.lod2.hooks.handlers.dcat.AtUpdateHandler;
import eu.lod2.hooks.util.ActionAbortException;
import org.apache.commons.logging.LogFactory;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.memory.MemoryStore;

import java.util.*;


/**
 * The ModelValidator retrieves sparql expressions from the main graph and verifies that the data to
 * insert into the graph is correct.
 * <p/>
 * This validation runs both on the insertion of a new DataSet or when a DataSet is to be updated.
 */
public class ModelValidator implements AtCreateHandler, AtUpdateHandler {

  // --- PRIORITY

  @Override
  public Collection<Priority> getConstraints( String hook ) {
    return Arrays.asList( Constraint.LATE );
  }


  // --- HOOKS

  @Override
  public void handleAtCreate( AtContext context ) throws ActionAbortException {
    handleValidation( context );
  }

  @Override
  public void handleAtUpdate( AtContext context ) throws ActionAbortException {
    handleValidation( context );
  }


  // --- VALIDATION LOGIC

  /**
   * Handles the validation of the current model and aborts the connection if the model was not
   * valid.
   *
   * @param context Contains the context of which we validate the model.
   * @throws ActionAbortException Throws an ActionAbortException if the model is not valid, thereby
   *                              canceling the request.
   */
  private void handleValidation( AtContext context ) throws ActionAbortException {
    try {
      assertValidatedModel( context );
    } catch ( InvalidModelException e ) {
      throw new ActionAbortException( "Invalid model, rule did not hold" );
    }
  }

  /**
   * Asserts that the new Model is in a valid state.
   * <p/>
   * We assume the repository is in a valid state if all SPARQL queries which have been assigned as
   * checks on this model hold up. .
   *
   * @param ctx Context which contains the model to validate.
   * @throws InvalidModelException Thrown iff the model is discovered to be invalid.
   */
  private void assertValidatedModel( AtContext ctx ) throws InvalidModelException {
    try {
      setupTmpRepository();
      this.tmpRepositoryConnection.add( ctx.getStatements() );
      verifyAllConstraints( ctx );
    } catch ( RepositoryException e ) {
      e.printStackTrace();
    } finally {
      destroyTmpRepository();
    }
  }


  // --- MANAGING THE REPOSITORY

  /**
   * The repository used for temporarily storing the triples.
   */
  private SailRepository tmpRepository;
  /**
   * Connection for temporarily storing the triples through.
   */
  private SailRepositoryConnection tmpRepositoryConnection;

  /**
   * Sets up a temporary repository and its connection.
   *
   * @throws RepositoryException Thrown when the repository could not be initialized.
   */
  private void setupTmpRepository() throws RepositoryException {
    this.tmpRepository = new SailRepository( new MemoryStore() );
    this.tmpRepository.initialize();
    this.tmpRepositoryConnection = this.tmpRepository.getConnection();
  }

  /**
   * Handles everything needed to destroy the temporary repository.
   */
  private void destroyTmpRepository() {
    // nothing to do here, tmpRepository destroys itself
    tmpRepository.isInitialized(); // operate on tmpRepository so code analysis believes it to be needed.
  }


  // --- MANAGING THE CONSTRAINTS

  /**
   * Finds and verifies all constraints which apply to context.
   *
   * @param context Context for which the constraints will be verified.
   */
  private void verifyAllConstraints( AtContext context ) throws InvalidModelException {
    for ( SparqlConstraint constraint : getSparqlConstraints( context.getEngine(), context.getCatalog() ) )
      if ( !verifySparqlConstraint( constraint.getQuery(), constraint.getConstraint() ) )
        throw new InvalidModelException( context.getCatalog(), constraint.getConstraint() );
  }

  /**
   * Fetches all constraints which apply to catalog from engine.
   *
   * @param engine  Engine which has a connection to the configuration graph.
   * @param catalog Catalog for which we want to retrieve the constraints.
   * @return Collection of SparqlConstraints which aught to be verified.
   */
  private Collection<SparqlConstraint> getSparqlConstraints( SparqlEngine engine, Catalog catalog ) {

    String query = "" +
        Constants.SPARQL_PREFIXES +
        " SELECT ?rule, ?sparqlQuery \n" +
        " WHERE {\n" +
        "   ?rule a                   cterms:ValidationRule;\n" +
        "         cterms:sparqlQuery  ?sparqlQuery;\n" +
        "         ^cterms:validatedBy <" + catalog.getURI().toString() + ">.\n" +
        " }";

    QueryResult results = engine.sparqlSelect( query );

    Collection<SparqlConstraint> constraints = new ArrayList<SparqlConstraint>();
    for ( Map<String, String> result : results )
      try {
        QueryResultConstraint constraint = QueryResultConstraintBuilder.fromSparql( result.get( "rule" ), engine );
        String sparqlQuery = result.get( "sparqlQuery" );
        // we ignore the constraint property for now, as it's always Exactly.NOTHING
        constraints.add( new SparqlConstraint( constraint, sparqlQuery ) );
      } catch ( UnknownQueryResultConstraintException e ) {
        LogFactory.getLog( "ModelValidator" ).error( e.getMessage() );
      }

    return constraints;
  }

  /**
   * Verify the results of the supplied SPARQL constraint to be within the accepted boundaries.
   *
   * @param query      SPARQL query which will yield the results.
   * @param validators Set of constraints to which the query should abide.
   * @return true iff each of the constraints was valid for the supplied query.
   */
  private boolean verifySparqlConstraint( String query, QueryResultConstraint... validators ) {
    try {
      TupleQueryResult sparqlResult = this.tmpRepositoryConnection.prepareTupleQuery( QueryLanguage.SPARQL, query ).evaluate();

      // convert sparqlResult to QueryResult
      QueryResult results = new QueryResult();
      while ( sparqlResult.hasNext() ) {
        BindingSet set = sparqlResult.next();
        Map<String, String> currentRow = new HashMap<String, String>();
        for ( String name : set.getBindingNames() )
          currentRow.put( name, set.getValue( name ).stringValue() );
        results.add( currentRow );
      }

      // verify QueryResult
      for ( QueryResultConstraint validator : validators )
        if ( !validator.valid( results ) )
          return false;

      return true;
    } catch ( RepositoryException e ) {
      throw new IllegalStateException( e );
    } catch ( MalformedQueryException e ) {
      throw new IllegalArgumentException( e );
    } catch ( QueryEvaluationException e ) {
      throw new IllegalArgumentException( e );
    }
  }
}
