package problem;


import representation.Representation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleOptimizationProblem <PM extends ProblemModel<R>, R extends Representation> implements OptimizationProblem<PM,R> {
    PM model;
    List<ObjectiveFunction<R,PM>> objectives;

    public SimpleOptimizationProblem(PM pm, ObjectiveFunction<R,PM> of) {
        model = pm;
        objectives = new ArrayList<>();
        objectives.add(of);
    }

    @Override
    public PM model() {
        return model;
    }

    @Override
    public List<ObjectiveFunction<R,PM>> objectives() {
        return objectives;
    }

    @Override
    public void addObjective(ObjectiveFunction<R, PM> objectiveFunction) {
        objectives.add(objectiveFunction);
    }

    @Override
    public String toString() {
        return ""+ model;
    }
}