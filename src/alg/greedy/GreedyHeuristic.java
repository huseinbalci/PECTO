package alg.greedy;

import problem.ProblemModel;
import problem.SimpleOptimizationProblem;
import problem.pecto.PECTOModel2;
import representation.IntegerPermutation;
import representation.Representation;
import representation.Solution;

public interface GreedyHeuristic<PM extends ProblemModel<R>, R extends Representation> {
    Solution solve(PECTOModel2 pectoModel, SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem);
}
