package eu.lod2.edcat.plugins.modelValidation.constraints.resultConstraints;

/**
 * This error shows that the specification for a QueryResultConstraint could not be parsed into a
 * valid QueryResultConstraint.  This occurs when the supplied information was not sufficient or was
 * invalid.
 */
public class UnknownQueryResultConstraintException extends Exception {

  /**
   * Constructs a new QueryResultConstraintException with a String supplied as a reference.
   *
   * @param source String representation of the specification which couldn't be converted.
   */
  public UnknownQueryResultConstraintException( String source ) {
    super( "QueryResultConstraint " + source + " could not be converted to a valid QueryResultConstraint" );
  }

}
