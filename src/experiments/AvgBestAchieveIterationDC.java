package experiments;

import alg.Metaheuristic;

public class AvgBestAchieveIterationDC implements DataCollector{

    double bestAchieveIteration = 0;
    int count=0;

    @Override
    public void collect(Metaheuristic alg) {
        double val = alg.getBestAchieveIteration();
        count++;
        bestAchieveIteration +=val;
    }

    @Override
    public void reset() {
        count = 0;
        bestAchieveIteration = 0;
    }

    @Override
    public String resultString() {
        if (count==0)
            count++;
        double avg = bestAchieveIteration /count;
        return String.format("BEST_IT:%.2f", avg);
    }
}
