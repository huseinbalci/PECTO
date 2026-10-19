package problem;

interface SolutionEvaluator {
    boolean betterThan(double v1, double v2);
}

public enum ObjectiveType {
    Minimization( (v1,v2)-> v1<v2), Maximization( (v1,v2)-> v1>v2);

    SolutionEvaluator evaluator;

    public boolean betterThan(double v1, double v2)
    {
        return evaluator.betterThan(v1,v2);
    }

    ObjectiveType(SolutionEvaluator evaluator)
    {
        this.evaluator = evaluator;
    }

}
