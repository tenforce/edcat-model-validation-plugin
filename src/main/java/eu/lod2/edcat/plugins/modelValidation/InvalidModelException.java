package eu.lod2.edcat.plugins.modelValidation;

/**
 * Indicates that the Model was invalid.
 *
 * @see eu.lod2.edcat.plugins.modelValidation.ModelValidator
 */
public class InvalidModelException extends Exception {

  public InvalidModelException(Object model, Object rule){
    super("Model " + model.toString() + " was invalid because " + rule.toString() + " did not hold.");
  }

}