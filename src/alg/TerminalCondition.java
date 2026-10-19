package alg;

import problem.OptimizationProblem;
import problem.ProblemModel;
import representation.Representation;

public interface TerminalCondition<PM extends ProblemModel<R>, R extends Representation> {

    boolean isSatisfied(Metaheuristic<PM,R> alg, OptimizationProblem<PM,R> pm);
}
