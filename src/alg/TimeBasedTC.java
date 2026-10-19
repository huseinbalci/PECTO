package alg;

import problem.OptimizationProblem;
import problem.ProblemModel;
import representation.Representation;

public class TimeBasedTC<PM extends ProblemModel<R>, R extends Representation> implements TerminalCondition<PM, R> {
    private final long maxMillis;

    public TimeBasedTC(long maxMillis) {
        this.maxMillis = maxMillis;
    }

    @Override
    public boolean isSatisfied(Metaheuristic<PM, R> alg, OptimizationProblem<PM, R> problem) {
        long algStartTime = alg.getStartTime();
        return (System.currentTimeMillis() - algStartTime) >= maxMillis;
    }
}
