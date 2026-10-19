package experiments;

import alg.Metaheuristic;

public class AvgRunTimeDC implements DataCollector{

    double runTime;
    int count=0;

    @Override
    public void collect(Metaheuristic alg) {
        double val = alg.getEndTime()-alg.getStartTime();
        count++;
        runTime +=val;
    }

    @Override
    public void reset() {
        count = 0;
        runTime = 0;
    }

    @Override
    public String resultString() {
        if (count==0)
            count++;
        double avg = runTime /count;
        return String.format("RUNTIME:%.2f", avg);
    }
}
