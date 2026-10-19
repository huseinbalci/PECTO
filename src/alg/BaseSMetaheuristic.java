package alg;

import problem.ProblemModel;
import representation.Representation;
import representation.Solution;

public abstract class BaseSMetaheuristic<PM extends ProblemModel<R>, R extends Representation> extends BaseMetaheuristic<PM,R> {
    protected Solution currentSolution;

    public BaseSMetaheuristic(TerminalCondition<PM, R> terminalCondition) {
        super(terminalCondition);
    }
}
