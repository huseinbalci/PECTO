package experiments;

import alg.IterationBasedTC;
import alg.Metaheuristic;
import problem.OptimizationProblem;
import problem.SimpleOptimizationProblem;
import representation.IntegerPermutation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExperimentRunner implements Runnable {

    int repeatCount;
    ResultProcessor resultProcessor;
    Iterable<OptimizationProblem> problemProvider;
    Iterable<Metaheuristic> algorithmProvider;
    List<DataCollector> dataCollectors;

    public ExperimentRunner(int repeatCount, ResultProcessor resultProcessor, Iterable<OptimizationProblem> problemProvider, Iterable<Metaheuristic> algorithmProvider, List<DataCollector> dataCollectors) {
        this.repeatCount = repeatCount;
        this.resultProcessor = resultProcessor;
        this.problemProvider = problemProvider;
        this.algorithmProvider = algorithmProvider;
        this.dataCollectors = dataCollectors;
    }

    @Override
    public void run() {
        System.out.println("<<START OF EXPERIMENTS>>");
        for (Metaheuristic alg : algorithmProvider) {
            for (OptimizationProblem problem : problemProvider) {
                performRepeatedRun(alg, problem);
            }
        }
    }

    private void performRepeatedRun(Metaheuristic alg, OptimizationProblem problem) {
        dataCollectors.forEach(DataCollector::reset);

        for (int repeat = 0; repeat < repeatCount; repeat++) {
            alg.solve(problem);
            dataCollectors.forEach((dc) -> dc.collect(alg));
        }
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("[" + alg + "] (" + problem + ") ");
        dataCollectors.forEach(dc -> resultBuilder.append(dc.resultString()).append(" "));
        resultProcessor.process(resultBuilder.toString());
    }
}