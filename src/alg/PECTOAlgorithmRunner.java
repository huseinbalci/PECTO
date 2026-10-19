package alg;

import alg.ga.FitnessBasedVictimSelector;
import alg.ga.MutationOperation;
import alg.ga.pecto.*;
import alg.greedy.pecto.PECTOGreedyHeuristic;
import alg.greedy.pecto.PECTONearestParticipantEvaluator;
import alg.ls.pecto.PECTOLocalSearch;
import alg.sa.GeometricUpdate;
import alg.sa.SimulatedAnnealing;
import alg.sa.pecto.PECTORandomSwapNG;
import experiments.*;
import problem.SimpleOptimizationProblem;
import problem.pecto.*;
import representation.IntegerPermutation;
import representation.PectoSolution;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PECTOAlgorithmRunner {
    private ResultProcessor resultProcessor;
    private final PECTOModel2[] pectoModels;
    private int[] populationSizes;
    private final long timeLimitMillis;
    private final int repeat;
    private double crossOverRate = 0.75;
    private double mutationRate = 0.05;
    private double initialTemperature = 25;
    private double coolingRate = 0.95;
    private int stagnationPeriod = 750;

    public PECTOAlgorithmRunner(PECTOModel2[] pectoModels, int[] populationSizes, long timeLimitMillis, int repeat) {
        this.pectoModels = pectoModels;
        this.populationSizes = populationSizes;
        this.timeLimitMillis = timeLimitMillis;
        this.repeat = repeat;
    }

    public void run() {
        for (PECTOModel2 model : pectoModels) {
            for (int popSize : populationSizes) {
                SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem =
                        new SimpleOptimizationProblem<>(model, new PECTOMinimumTimeOF());
                resultProcessor = new ToTextFileRP("./data/output/exp.txt");

                PectoSolution pectoSolution = (PectoSolution) new PECTORandomISG(model).generate(problem);

                PECTOGreedyHeuristic pectoGreedyHeuristic = new PECTOGreedyHeuristic(new PECTONearestParticipantEvaluator(), new IterationBasedTC<>(repeat));
                PectoSolution pectoSolution2 = (PectoSolution) pectoGreedyHeuristic.solve(problem.model(), problem);

                SimulatedAnnealing<PECTOModel2, IntegerPermutation> pectoSimulatedAnnealing = new SimulatedAnnealing<>(
                        null,
                        pectoSolution2,
                        new PECTORandomSwapNG(),
                        initialTemperature,
                        new GeometricUpdate(coolingRate)
                );

                PECTOLocalSearch pectoLocalSearch = new PECTOLocalSearch(
                        new TimeBasedTC<>(timeLimitMillis),
                        pectoSolution2,
                        new PECTORandomISG(problem.model()),
                        new PECTORandomSwapNG(),
                        PECTOLocalSearch.Selection.FIRST_IMP
                );

                PECTOGA<PECTOModel2, IntegerPermutation> standardGA = new PECTOGA<>(new TimeBasedTC<>(timeLimitMillis), false);
                getIntegerPermutationGA(standardGA, model, popSize, new PECTOMutationMove(true));
                PECTOGA<PECTOModel2, IntegerPermutation> hybridGA = new PECTOGA<>(new TimeBasedTC<>(timeLimitMillis), true);
                getIntegerPermutationGA(hybridGA, model, popSize, new PECTOMutationMove(true));

                List<DataCollector> dataCollectors = Arrays.asList(
                        new AvgBestDC(),
                        new AvgBestAchieveTimeDC(),
                        new AvgBestAchieveIterationDC(),
                        new AvgIterationDC(),
                        new AvgRunTimeDC());

                ExperimentRunner runner = new ExperimentRunner(
                        repeat,
                        resultProcessor,
                        Collections.singletonList(problem),
                        Arrays.asList(hybridGA),
                        dataCollectors
                );
                runner.run();
            }
        }
    }

    private void getIntegerPermutationGA(PECTOGA<PECTOModel2, IntegerPermutation> pectoga, PECTOModel2 pectoModel2,
                                         int popSize, MutationOperation mutationOperation) {
        pectoga.setIsg(new PECTORandomISG(pectoModel2));
        pectoga.setPopulationSize(popSize);
        pectoga.setVictimSelector(new FitnessBasedVictimSelector());
        pectoga.setCrossOverRate(crossOverRate);
        pectoga.setStagnationPeriod(stagnationPeriod);
        pectoga.setMutationRate(mutationRate);
        pectoga.setMoveMutation(mutationOperation);
    }

    private void setCrossOverRate(double crossOverRate) {
        this.crossOverRate = crossOverRate;
    }

    private void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public void setInitialTemperature(double initialTemperature) {
        this.initialTemperature = initialTemperature;
    }

    public void setCoolingRate(double coolingRate) {
        this.coolingRate = coolingRate;
    }

    public void setStagnationPeriod(int stagnationPeriod) {
        this.stagnationPeriod = stagnationPeriod;
    }

    public void tunePopulationSize() {
//        resultProcessor = new ToTextFileRP("./data/output/pop_tune.txt");
        resultProcessor = new ToTextFileRP("./data/output/exp.txt");
        run();
    }

    public void tuneCrossOverRate(int[] populationSizes, double[] crossOverRates) {
        resultProcessor = new ToTextFileRP("./data/output/cr_tune.txt");
        this.populationSizes = populationSizes;

        for (Double rate : crossOverRates) {
            setCrossOverRate(rate);
            run();
        }
    }

    public void tuneStagnationPeriod(int[] populationSizes, int[] stagnationPeriods) {
        resultProcessor = new ToTextFileRP("./data/output/sp_tune.txt");
        this.populationSizes = populationSizes;

        for (int s : stagnationPeriods) {
            setStagnationPeriod(s);
            run();
        }
    }

    public void tuneMutationRate(int[] populationSizes, double bestCrossOverRate, double[] mutationRates) {
        resultProcessor = new ToTextFileRP("./data/output/mr_tune.txt");
        this.populationSizes = populationSizes;
        setCrossOverRate(bestCrossOverRate);

        for (Double rate : mutationRates) {
            setMutationRate(rate);
            run();
        }
    }

    public void tuneInitialTemperature(int[] populationSizes, double[] temperatures) {
        resultProcessor = new ToTextFileRP("./data/output/temp_tune.txt");
        this.populationSizes = populationSizes;

        for (Double temp : temperatures) {
            setInitialTemperature(temp);
            run();
        }
    }

    public void tuneAlpha(int[] populationSizes, double temperature, double[] alphas) {
        resultProcessor = new ToTextFileRP("./data/output/alpha_tune.txt");
        this.populationSizes = populationSizes;
        this.initialTemperature = temperature;

        for (Double a : alphas) {
            setCoolingRate(a);
            run();
        }
    }

    public static void main(String[] args) {
        int patientSize = 3797;

        PECTOModel2[] pectoModels = {
                new PECTOModel2(200, (int)(patientSize * 1.36), 64),  // 5163
                new PECTOModel2(200, (int)(patientSize * 1.28), 64),  // 4860
                new PECTOModel2(200, (int)(patientSize * 1.20), 64),  // 4556
                new PECTOModel2(200, (int)(patientSize * 1.12), 64),  // 4252
                new PECTOModel2(200, (int)(patientSize * 1.04), 64),  // 3948
                new PECTOModel2(200, (int)(patientSize * 0.96), 64),  // 3645
                new PECTOModel2(200, (int)(patientSize * 0.88), 64),  // 3341
                new PECTOModel2(200, (int)(patientSize * 0.80), 64),  // 3037
                new PECTOModel2(200, (int)(patientSize * 0.72), 64),  // 2733
                new PECTOModel2(200, (int)(patientSize * 0.64), 64),  // 2430
        };

        int[] populationSizes = {40, 80, 120, 160, 200};
        double[] crossOverRates = {0.60, 0.75, 0.90};
        double[] mutationRates = {0.05, 0.10, 0.15};
        double[] temperatures = {25, 50, 75, 100, 150, 200, 250, 500};
        double[] alphas = {0.80, 0.85, 0.90, 0.95};
        int[] stagnationPeriods = {250, 500, 750, 1000};

        int[] bestPopulationSize = {40};
        double bestCrossOverRate = 0.75;
        double bestMutationRate = 0.05;
        double bestTemperature = 25;
        double bestAlpha = 0.95;

//        PECTOAlgorithmRunner pectoParameterTuning = new PECTOAlgorithmRunner(pectoModels, bestPopulationSize, 60000, 0);
//        pectoParameterTuning.tunePopulationSize();
//        pectoParameterTuning.tuneCrossOverRate(bestPopulationSize, crossOverRates);
//        pectoParameterTuning.tuneMutationRate(bestPopulationSize, bestCrossOverRate, mutationRates);
//        pectoParameterTuning.tuneInitialTemperature(bestPopulationSize, temperatures);
//        pectoParameterTuning.tuneAlpha(bestPopulationSize, bestTemperature, alphas);
//        pectoParameterTuning.tuneStagnationPeriod(bestPopulationSize, stagnationPeriods);

        PECTOAlgorithmRunner pectoAlgorithmComparison = new PECTOAlgorithmRunner(pectoModels, bestPopulationSize, 60000, 20);
//        pectoAlgorithmComparison.setCrossOverRate(bestCrossOverRate)
//        pectoAlgorithmComparison.setMutationRate(bestMutationRate);
        pectoAlgorithmComparison.run();
    }
}
