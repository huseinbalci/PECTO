package problem;


import representation.Representation;

public interface ObjectiveFunction<R extends Representation, PM extends ProblemModel<R>> {
    ObjectiveType type();
    double value(PM pm , R r);
}
