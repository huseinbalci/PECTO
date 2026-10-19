package alg;

import problem.OptimizationProblem;
import problem.ProblemModel;
import representation.Representation;

public class IterationBasedTC<PM extends ProblemModel<R>, R extends Representation> implements TerminalCondition<PM,R>{

    long maxIteration;

    public IterationBasedTC(long maxIteration) {
        this.maxIteration = maxIteration;
    }

    @Override
    public boolean isSatisfied(Metaheuristic<PM, R> alg, OptimizationProblem<PM, R> problem) {

        return maxIteration <= alg.iterationCount()  ;
    }
}
