package experiments;

import alg.Metaheuristic;

public class AvgIterationDC implements DataCollector{

    double iteration;
    int count=0;

    @Override
    public void collect(Metaheuristic alg) {
        double val = alg.getIterationCount();
        count++;
        iteration +=val;
    }

    @Override
    public void reset() {
        count = 0;
        iteration = 0;
    }

    @Override
    public String resultString() {
        if (count==0)
            count++;
        double avg = iteration/count;
        return String.format("TOT_IT:%.2f", avg);
    }
}
