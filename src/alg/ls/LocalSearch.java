package alg.ls;

import alg.BaseSMetaheuristic;
import alg.InitialSolutionGenerator;
import alg.IterationBasedTC;
import alg.TerminalCondition;
import problem.OptimizationProblem;
import problem.ProblemModel;
import problem.SimpleOptimizationProblem;
import representation.IntegerPermutation;
import representation.Representation;
import representation.Solution;

public class LocalSearch<PM extends ProblemModel<R>, R extends Representation> extends BaseSMetaheuristic<PM,R> {

    InitialSolutionGenerator<PM, R> isg;

    NeighborGenerator<PM, R> neighborGenerator;

    public LocalSearch(TerminalCondition<PM, R> terminalCondition, InitialSolutionGenerator<PM, R> isg, NeighborGenerator<PM, R> neighborGenerator) {
        super(terminalCondition);
        this.isg = isg;
        this.neighborGenerator = neighborGenerator;
    }

    @Override
    protected Solution _perform(OptimizationProblem<PM, R> problem) {

        while (!terminalCondition.isSatisfied(this, problem)) {
            Solution neighbor = generateNext(problem);

            updateBest(problem, neighbor);
            iterationCount++;

            if (neighbor == null) {
                break;
            }

            if (neighbor != null &&
                    problem.getObjective().type().betterThan(neighbor.value(), currentSolution.value())) {
                currentSolution = neighbor;
            }
        }

        return bestSolution;
    }

    private Solution generateNext(OptimizationProblem<PM, R> problem) { // We are applying First-Improvement Neighbor Selection

        neighborGenerator.init(problem, currentSolution);

        while (neighborGenerator.hasNext()) {
            Solution neighbor = neighborGenerator.next(problem);
            if (problem.getObjective().type().betterThan(neighbor.value(), currentSolution.value())) {
                return neighbor;
            }

        }
        return null;
    }

    @Override
    protected void _init(OptimizationProblem<PM, R> problem) {
        iterationCount = 0;
        bestSolution = null;
        currentSolution = null;
        updateBest(problem, currentSolution);
    }

    @Override
    public String toString() {
        return "LS " + isg + " " + neighborGenerator;
    }
}
