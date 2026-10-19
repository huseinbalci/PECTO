package alg.ls;

import problem.OptimizationProblem;
import problem.ProblemModel;
import representation.Representation;
import representation.Solution;

public interface NeighborGenerator<PM extends ProblemModel<R>, R extends Representation> {
    void init(OptimizationProblem<PM, R> problem, Solution currentSolution);

    boolean hasNext();

    Solution next(OptimizationProblem<PM, R> problem);
}
