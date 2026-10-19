package alg;

import problem.OptimizationProblem;
import problem.ProblemModel;
import representation.Representation;
import representation.Solution;

public interface InitialSolutionGenerator<PM extends ProblemModel<R>, R extends Representation> {

    Solution generate(OptimizationProblem<PM,R> problem);
}
