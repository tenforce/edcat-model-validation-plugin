package eu.lod2.edcat.plugins.modelValidation;

import eu.lod2.hooks.util.ActionAbortException;

/**
 * Indicates that the Model was invalid.
 *
 * @see eu.lod2.edcat.plugins.modelValidation.ModelValidator
 */
@SuppressWarnings( "UnusedDeclaration" )
// Currently not used, but a nice interface for future applications.
public class ConstraintFailedException extends ActionAbortException {

  /**
   * Simple constructor for the ConstraintFailedException
   *
   * @param model       The model which couldn't be verified.
   * @param rule        The rule which failed.
   * @param description Human description of why there was a failure.
   */
  public ConstraintFailedException( Object model, Object rule, String description ) {
    super( "" +
        "Model " + model.toString() + " was invalid because " + rule.toString() + "did not hold. " +
        "Explanation: " + description );
  }

}