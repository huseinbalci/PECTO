package experiments;

import alg.Metaheuristic;

public class AvgBestAchieveTimeDC implements DataCollector{

    double bestAchieveTime = 0;
    int count=0;

    @Override
    public void collect(Metaheuristic alg) {
        double val = alg.getBestAchieveTime();
        count++;
        bestAchieveTime +=val;
    }

    @Override
    public void reset() {
        bestAchieveTime = 0;
        count = 0;
    }

    @Override
    public String resultString() {
        if (count==0)
            count++;
        double avg = bestAchieveTime /count;
        return String.format("BEST_TIME:%.2f", avg);
    }
}
