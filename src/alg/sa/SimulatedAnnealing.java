package alg.sa;

import alg.BaseSMetaheuristic;
import alg.TerminalCondition;
import alg.TimeBasedTC;
import alg.greedy.pecto.PECTOGreedyHeuristic;
import alg.greedy.pecto.PECTONearestParticipantEvaluator;
import alg.ls.NeighborGenerator;
import alg.sa.pecto.PECTORandomSwapNG;
import problem.OptimizationProblem;
import problem.ProblemModel;
import problem.SimpleOptimizationProblem;
import problem.pecto.PECTOMinimumTimeOF;
import problem.pecto.PECTOModel2;
import problem.pecto.PECTORandomISG;
import representation.IntegerPermutation;
import representation.PectoSolution;
import representation.Representation;
import representation.Solution;
import utils.RandUtils;

public class SimulatedAnnealing<PM extends ProblemModel<R>, R extends Representation> extends BaseSMetaheuristic<PM, R> {
    private final NeighborGenerator<PM, R> neighborGenerator;
    private final CoolingFunction coolingFunction;
    private Solution initialSolution;
    private final double initialTemperature;
    private double temperature;
    private final PECTOGreedyHeuristic pectoGreedyHeuristic;

    public SimulatedAnnealing(TerminalCondition<PM, R> terminalCondition,
                              Solution initialSolution,
                              NeighborGenerator<PM, R> neighborGenerator,
                              double initialTemperature,
                              CoolingFunction coolingFunction) {
        super(terminalCondition);
        this.initialSolution = initialSolution;
        this.neighborGenerator = neighborGenerator;
        this.temperature = initialTemperature;
        this.initialTemperature = initialTemperature;
        this.coolingFunction = coolingFunction;
        pectoGreedyHeuristic = new PECTOGreedyHeuristic(new PECTONearestParticipantEvaluator(), null);
    }

    @Override
    public Solution _perform(OptimizationProblem problem) {
//        long startTime = System.currentTimeMillis();
//        System.out.println("I: " + startTime);
        currentSolution = initialSolution.copy();
        neighborGenerator.init(problem, currentSolution);
//        System.out.println("INIT: " + initialSolution.value());
        updateBest(problem, currentSolution);

        while (temperature >= 1) {
            int count = 0;
            int failureLimit = 10;

            while (count <= failureLimit) {
                double delta = ((PECTORandomSwapNG)neighborGenerator).applyMove(problem);

                if (delta > 0) {
                    double prob = Math.exp(- delta / temperature);

                    if (!RandUtils.rollDice(prob)) {
                        ((PECTORandomSwapNG) neighborGenerator).undoMove();
                    }
                }

                count++;
                iterationCount++;
                updateBest(problem, currentSolution);
            }

            temperature = coolingFunction.updateTemperature(temperature, iterationCount);
        }

//        long finishTime = System.currentTimeMillis() - startTime;
//        System.out.println("F: " + finishTime);
//        System.out.println("BEST: " + bestSolution.value());
        return bestSolution;
    }

    @Override
    protected void _init (OptimizationProblem problem) {
        bestSolution = null;
        initialSolution = pectoGreedyHeuristic.solve((PECTOModel2) problem.model(),
                (SimpleOptimizationProblem<PECTOModel2, IntegerPermutation>) problem);
        temperature = initialTemperature;
        updateBest(problem, currentSolution);
        iterationCount = 0;
    }

    public void reset (Solution newSolution) {
        this.initialSolution = newSolution;
        temperature = initialTemperature;
        bestSolution = null;
        iterationCount = 0;
    }

    @Override
    public String toString() {
        return "SA TEMP:" + initialTemperature + " " + coolingFunction;
    }

    public static void main(String[] args) {
        PECTOModel2 pectoModel2 = new PECTOModel2(200, 5163, 64);
        SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem =
                new SimpleOptimizationProblem<>(pectoModel2, new PECTOMinimumTimeOF());
        PECTORandomISG pectoRandomISG = new PECTORandomISG(pectoModel2);
        PECTOGreedyHeuristic pectoGreedyHeuristic = new PECTOGreedyHeuristic(new PECTONearestParticipantEvaluator(), null);
        PectoSolution pectoSolution1 = (PectoSolution) pectoRandomISG.generate(problem);

        SimulatedAnnealing sa = new SimulatedAnnealing<>(
                new TimeBasedTC<>(60000),
                null,
                new PECTORandomSwapNG(),
                75,
                new GeometricUpdate(0.95)
        );

        for (int i = 0; i < 1; i++) {

            PectoSolution pectoSolution2 = (PectoSolution) pectoGreedyHeuristic.solve(pectoModel2, problem);
            System.out.println(pectoSolution2);
//            System.out.println("T1: " + pectoSolution1.transportPenalty());
//            System.out.println("V1: " + pectoSolution1.variancePenalty());
//            System.out.println("C1: " + pectoSolution1.getTotalCost());
            sa.reset(pectoSolution2);
            pectoSolution2 = (PectoSolution) sa._perform(problem);

            System.out.println(pectoSolution2);
//            System.out.println("T2: " + pectoSolution1.transportPenalty());
//            System.out.println("V2: " + pectoSolution1.variancePenalty());
//            System.out.println("C2: " + pectoSolution1.getTotalCost());
        }


    }
}
