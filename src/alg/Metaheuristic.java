package alg;

import problem.OptimizationProblem;
import problem.ProblemModel;
import representation.Representation;
import representation.Solution;

public interface Metaheuristic<PM extends ProblemModel<R>, R extends Representation> {
    Solution solve(OptimizationProblem<PM,R> problem);
    long iterationCount();
    Solution getBestSolution();
    long getIterationCount();
    long getBestAchieveTime();
    long getBestAchieveIteration();
    long getEndTime();
    long getStartTime();
}