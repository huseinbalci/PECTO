package alg.ls.pecto;

import alg.InitialSolutionGenerator;
import alg.IterationBasedTC;
import alg.TerminalCondition;
import alg.greedy.pecto.PECTOGreedyHeuristic;
import alg.greedy.pecto.PECTONearestParticipantEvaluator;
import alg.ls.LocalSearch;
import alg.ls.NeighborGenerator;
import alg.sa.pecto.PECTORandomSwapNG;
import problem.OptimizationProblem;
import problem.SimpleOptimizationProblem;
import problem.pecto.PECTOMinimumTimeOF;
import problem.pecto.PECTOModel2;
import problem.pecto.PECTORandomISG;
import representation.IntegerPermutation;
import representation.PectoSolution;
import representation.Solution;

import java.util.Arrays;

public class PECTOLocalSearch extends LocalSearch<PECTOModel2, IntegerPermutation> {
    private final InitialSolutionGenerator<PECTOModel2, IntegerPermutation> isg;
    private final NeighborGenerator<PECTOModel2, IntegerPermutation> neighborGenerator;
    private final Selection selection;
    private Solution initialSolution;
    public enum Selection {FIRST_IMP, BEST_IMP, LAHC};
    private final PECTOGreedyHeuristic pectoGreedyHeuristic;
    private static final int LAHC_HISTORY_LENGTH = 10;

    public PECTOLocalSearch(TerminalCondition<PECTOModel2, IntegerPermutation> terminalCondition,
                            Solution initialSolution,
                            InitialSolutionGenerator<PECTOModel2, IntegerPermutation> isg,
                            NeighborGenerator<PECTOModel2, IntegerPermutation> neighborGenerator,
                            Selection selection) {
        super(terminalCondition, isg, neighborGenerator);
        this.isg = isg;
        this.neighborGenerator = neighborGenerator;
        this.selection = selection;
        this.initialSolution = initialSolution;
        pectoGreedyHeuristic = new PECTOGreedyHeuristic(new PECTONearestParticipantEvaluator(), null);
    }

    @Override
    public Solution _perform(OptimizationProblem problem) {
        currentSolution = initialSolution.copy();
        neighborGenerator.init(problem, currentSolution);
        updateBest(problem, currentSolution);
        Solution neighbor;

        if (selection == Selection.LAHC) {
            return performLAHC(problem);
        }
        
        while (!terminalCondition.isSatisfied(this, problem)) {
            neighbor = generateNextFirstImp(problem);

            if (neighbor != null) {
                currentSolution = neighbor;
                neighborGenerator.init(problem, currentSolution);
                updateBest(problem, neighbor);
            } else {
                // Local Minimum reached! Stop the search.
                break;
            }

            iterationCount++;
        }
//        long finishTime = System.currentTimeMillis() - startTime;
//        System.out.println("F: " + finishTime);
        return bestSolution != null ? bestSolution : currentSolution;
    }

    private Solution performLAHC(OptimizationProblem problem) {
        double[] history = new double[LAHC_HISTORY_LENGTH];
        Arrays.fill(history, Double.MAX_VALUE);
        int historyIdx = 0;

        while (!terminalCondition.isSatisfied(this, problem)) {
            double delta = ((PECTORandomSwapNG) neighborGenerator).applyMove(problem);
            double neighborValue = currentSolution.value() + delta;

            // Accept if neighbor is better than ANY of the last L recorded values
            boolean accept = false;
            for (int i = 0; i < LAHC_HISTORY_LENGTH; i++) {
                if (neighborValue < history[i]) {
                    accept = true;
                    break;
                }
            }

            if (accept) {
                history[historyIdx] = neighborValue;
                historyIdx = (historyIdx + 1) % LAHC_HISTORY_LENGTH;
                updateBest(problem, currentSolution);
            } else {
                ((PECTORandomSwapNG) neighborGenerator).undoMove();
            }

            iterationCount++;
        }

        return bestSolution != null ? bestSolution : currentSolution;
    }

    @Override
    protected void _init(OptimizationProblem<PECTOModel2, IntegerPermutation> problem) {
        super._init(problem);
        initialSolution = pectoGreedyHeuristic.solve(problem.model(),
                (SimpleOptimizationProblem<PECTOModel2, IntegerPermutation>) problem);
    }

    public void reset (Solution newSolution) {
        this.initialSolution = newSolution.copy();
        bestSolution = null;
        iterationCount = 0;
    }

    private Solution generateNextFirstImp(OptimizationProblem<PECTOModel2, IntegerPermutation> problem) {
        int count = 0;
        int failureLimit = 500;

        while (count <= failureLimit) {
            double delta = ((PECTORandomSwapNG) neighborGenerator).applyMove(problem);
            if (delta >= 0) {
                ((PECTORandomSwapNG) neighborGenerator).undoMove();
                count++;
            } else {
                return currentSolution;
            }
        }
        return null;
    }

    private Solution generateNextBestImp(OptimizationProblem<PECTOModel2, IntegerPermutation> problem) {
        neighborGenerator.init(problem,currentSolution);
        Solution bestImprovedNeighbor = null;
        int count = 0;
        int maxNeighborLimit = 2000;

        while (neighborGenerator.hasNext() && count < maxNeighborLimit)
        {
            Solution neighbor = neighborGenerator.next(problem);
            count++;

            if (problem.getObjective().type().betterThan( neighbor.value(), currentSolution.value()))
            {
                if (bestImprovedNeighbor == null || problem.getObjective().type().betterThan(neighbor.value(), bestImprovedNeighbor.value())) {
                    bestImprovedNeighbor = neighbor;
                }
            }
        }
        return bestImprovedNeighbor;
    }

    @Override
    public String toString() {
        return "LS " + neighborGenerator + " " + selection;
    }

    public static void main(String[] args) {
        PECTOModel2 model = new PECTOModel2(200, 5163, 64);

        SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem =
                new SimpleOptimizationProblem<>(model, new PECTOMinimumTimeOF());

        PectoSolution pectoSolution1 = (PectoSolution) new PECTORandomISG(model).generate(problem);

        PECTOGreedyHeuristic pectoGreedyHeuristic =
                new PECTOGreedyHeuristic(new PECTONearestParticipantEvaluator(), new IterationBasedTC<>(60000));

        PectoSolution pectoSolution2 = (PectoSolution) pectoGreedyHeuristic.solve(problem.model(), problem);
        System.out.println("INIT VALUE: " + pectoSolution2.value());

        PECTORandomSwapNG neighborGenerator = new PECTORandomSwapNG();
        TerminalCondition<PECTOModel2, IntegerPermutation> tc = new IterationBasedTC<>(1);

        PECTOLocalSearch ls = new PECTOLocalSearch(
                tc,
                pectoSolution2,
                new PECTORandomISG(model),
                neighborGenerator,
                Selection.FIRST_IMP
        );

        ls.reset(pectoSolution2);
        Solution best = ls._perform(problem);

        if (best != null) {
            System.out.println("BEST VALUE: " + best.value());
        }
    }
}
