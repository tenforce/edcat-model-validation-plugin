package eu.lod2.edcat.plugins.modelValidation.constraints.sparqlConstraints;

/**
 * This error indicates that a model did not pass the validation of a supplied sparql constraint.
 */
@SuppressWarnings("UnusedDeclaration")
public class SparqlConstraintValidationException extends Exception {

  /** The constraint which did not hold. */
  private SparqlConstraint constraint;

  /**
   * Returns the constraint which failed on the supplied model.
   *
   * @return Failed constraint.
   */
  public SparqlConstraint getConstraint() {
    return constraint;
  }

  /**
   * Simple constructor.
   *
   * @param constraint SparqlConstraint which failed.
   */
  public SparqlConstraintValidationException( SparqlConstraint constraint ) {
    super( "" +
        "Sparql constraint failed.  The check was: " + constraint.getDescription() +
        " with sparql query " + constraint.getQuery()
    );

    this.constraint = constraint;
  }

}