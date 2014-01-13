package eu.lod2.edcat.plugins.modelValidation;

import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.constraints.Constraint;
import eu.lod2.hooks.constraints.Priority;
import eu.lod2.hooks.contexts.AtContext;
import eu.lod2.hooks.handlers.dcat.AtCreateHandler;
import eu.lod2.hooks.handlers.dcat.AtUpdateHandler;
import eu.lod2.hooks.util.ActionAbortException;
import org.apache.commons.lang3.ArrayUtils;
import org.openrdf.model.Model;

import java.util.Arrays;
import java.util.Collection;


/**
 * The ModelValidator retrieves sparql expressions from the main graph and
 * verifies that the data to insert into the graph is correct.
 *
 * This validation runs both on the insertion of a new DataSet or when a
 * DataSet is to be updated.
 */
public class ModelValidator implements AtCreateHandler, AtUpdateHandler {

  // --- PRIORITY

  @Override
  public Collection<Priority> getConstraints(String hook) {
    return Arrays.asList(Constraint.LATE);
  }

  // --- HOOKS

  @Override
  public void handleAtCreate(AtContext context) throws ActionAbortException {

  }

  @Override
  public void handleAtUpdate(AtContext context) throws ActionAbortException {

  }
}
