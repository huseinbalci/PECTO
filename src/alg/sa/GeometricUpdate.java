package alg.sa;

public class GeometricUpdate implements CoolingFunction{
    private final double alpha;

    public GeometricUpdate(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double updateTemperature(double currentTemperature, long iteration) {
        return currentTemperature * alpha;
    }

    @Override
    public String toString() {
        return "GEO-Update ALPHA:" + alpha;
    }
}
