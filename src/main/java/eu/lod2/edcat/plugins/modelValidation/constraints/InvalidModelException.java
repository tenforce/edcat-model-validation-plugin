package eu.lod2.edcat.plugins.modelValidation.constraints;

import eu.lod2.edcat.plugins.modelValidation.constraints.sparqlConstraints.SparqlConstraint;

import java.util.Collection;

/**
 * Indicates that the model was not valid.
 * <p/>
 * There may be multiple constraints which caused this validation to fail.  Each of these
 * constraints can be accessed through this exception.
 */
public class InvalidModelException extends Exception {

  /** Collection of all constraints which failed on the model. */
  private Collection<SparqlConstraint> failedConstraints;

  /**
   * Returns all constraints which failed to validate.
   *
   * @return Collection of {@link SparqlConstraint}s which failed to validate.
   */
  public Collection<SparqlConstraint> getFailedConstraints() {
    return failedConstraints;
  }

  /**
   * Simple constructor for the failed constraints, supplying all constraints which have failed.
   *
   * @param failedConstraints Collection of all constraints which failed to validate.
   */
  public InvalidModelException( Collection<SparqlConstraint> failedConstraints ) {
    super( "Failed to validate model, " + failedConstraints.size() + " constraints failed to validate" );
    this.failedConstraints = failedConstraints;
  }
}