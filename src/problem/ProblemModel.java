package problem;

import representation.Representation;

public interface ProblemModel<R extends Representation> {
    boolean isFeasable( R r);
}