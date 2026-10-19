package experiments;

import alg.Metaheuristic;

public class AvgBestDC implements DataCollector{

    double best;
    int count=0;

    @Override
    public void collect(Metaheuristic alg) {
        double val = alg.getBestSolution().value();
        count++;
        best +=val;
    }

    @Override
    public void reset() {
        count = 0;
        best = 0;
    }

    @Override
    public String resultString() {
        if (count==0)
            count++;
        double avg = best/count;
        return String.format("BEST:%.2f", avg);
    }
}
