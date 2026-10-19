package alg;

import problem.OptimizationProblem;
import problem.ProblemModel;
import representation.Representation;
import representation.Solution;

import java.util.List;

public abstract class BaseMetaheuristic<PM extends ProblemModel<R>, R extends Representation> implements Metaheuristic<PM,R> {

    protected TerminalCondition<PM,R> terminalCondition;
    protected long iterationCount;

    protected Solution bestSolution;

    long startTime;
    long endTime;
    long bestAchieveTime;
    long bestAchieveIteration;

    public BaseMetaheuristic(TerminalCondition<PM, R> terminalCondition) {
        this.terminalCondition = terminalCondition;
    }

    public Solution solve(OptimizationProblem<PM,R> problem){
        startTime = System.currentTimeMillis();
        _init(problem);
        Solution result = _perform(problem);
        endTime = System.currentTimeMillis();
        return result;
    }

    @Override
    public long iterationCount() {
        return iterationCount;
    }

    public void updateBest(OptimizationProblem<PM, R> problem, Solution s)
    {
        if (s==null)
            return;
        if ( bestSolution == null || problem.getObjective().type().betterThan(s.value(), bestSolution.value()))
        {
            bestSolution = s.copy();
            bestAchieveTime = System.currentTimeMillis() - startTime;
            bestAchieveIteration = getIterationCount();
        }
    }

    protected void updateBest(OptimizationProblem<PM,R> problem , List<Solution> solutions)
    {
        solutions.forEach((s)-> updateBest(problem,s));
    }

    protected abstract Solution _perform(OptimizationProblem<PM, R> pm);

    protected abstract void _init(OptimizationProblem<PM, R> pm);

    @Override
    public Solution getBestSolution() {
        return bestSolution;
    }

    @Override
    public long getIterationCount() {
        return iterationCount;
    }

    @Override
    public long getBestAchieveTime() {
        return bestAchieveTime;
    }

    @Override
    public long getBestAchieveIteration() {
        return bestAchieveIteration;
    }

    public void setBestAchieveTime(long bestAchieveTime) {
        this.bestAchieveTime = bestAchieveTime;
    }

    public void setBestAchieveIteration(long bestAchieveIteration) {
        this.bestAchieveIteration = bestAchieveIteration;
    }

    public TerminalCondition<PM, R> getTerminalCondition() {
        return terminalCondition;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }
}
