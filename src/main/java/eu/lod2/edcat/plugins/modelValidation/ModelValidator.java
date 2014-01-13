package eu.lod2.edcat.plugins.modelValidation;

import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.constraints.Priority;
import eu.lod2.hooks.handlers.dcat.AtCreateHandler;
import eu.lod2.hooks.handlers.dcat.AtUpdateHandler;
import eu.lod2.hooks.util.ActionAbortException;
import org.openrdf.model.Model;

import java.util.Collection;


/**
 * The ModelValidator retrieves sparql expressions from the main graph and
 * verifies that the data to insert into the graph is correct.
 *
 * This validation runs both on the insertion of a new DataSet or when a
 * DataSet is to be updated.
 */
public class ModelValidator implements AtCreateHandler, AtUpdateHandler {

  @Override
  public Collection<Priority> getConstraints(String hook) {
    return null;
  }

  @Override
  public Model handleAtCreate(Catalog catalog, Model statements, SparqlEngine engine) throws ActionAbortException {
    return null;
  }

  @Override
  public void handleAtUpdate(Catalog catalog, Model statements, SparqlEngine engine) throws ActionAbortException {

  }
}
