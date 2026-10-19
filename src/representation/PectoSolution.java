package representation;

import problem.SimpleOptimizationProblem;
import problem.pecto.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static problem.pecto.PECTOModel2.*;

public class PectoSolution implements Solution{
    private final IntegerPermutation integerPermutation;
    private final PECTOModel2 pectoModel;
    private final SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem;
    private final List<List<Integer>> routes;
    private double[] routeCosts;
    private final double[] routeTransportPenalties;
    private double totalCost;
    public int[] hospitalOccupancy;
    private int age;
    private final double lambda = 0.0;

    public PectoSolution(List<List<Integer>> routes, int[] hospitalOccupancy, SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem) {
        this.routes = routes;
        routeTransportPenalties = new double[routes.size()];
        sortRoutes();
        this.hospitalOccupancy = hospitalOccupancy;
        this.integerPermutation = IntegerPermutation.fromRoutes(routes);
        this.pectoModel = problem.model();
        this.problem = problem;
        this.totalCost = 0;
        age = 0;
        calculateRouteCosts();
    }

    public double[] getRouteCosts() {
        return routeCosts;
    }

    public PectoSolution(IntegerPermutation integerPermutation, SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem) {
        this.integerPermutation = integerPermutation;
        this.pectoModel = problem.model();
        routeTransportPenalties = new double[pectoModel.getNumAmbulances()];
        this.problem = problem;
        this.totalCost = 0;
        this.age = 0;
        this.routes = new ArrayList<>();
        this.hospitalOccupancy = new int[pectoModel.getNumHospitals()];
        buildRoutesFromPermutation();
        sortRoutes();
        calculateRouteCosts();
    }

    public int getRouteStartIndex(int routeIndex) {
        int start = 0;
        for (int i = 0; i < routeIndex; i++) {
            start += routes.get(i).size();
        }
        return start;
    }

    public void syncFromRoute(int routeIndex) {
        int start = getRouteStartIndex(routeIndex);
        for (int r = routeIndex; r < routes.size(); r++) {
            for (int id : routes.get(r)) {
                integerPermutation.set(start++, id);
            }
        }
    }

    private void sortRoutes() {
        routes.sort(Comparator.comparingInt(List::getFirst));
    }

    private void buildRoutesFromPermutation() {
        int ambulanceIndex = -1;

        for (int i = 0; i < integerPermutation.size(); i++) {
            int id = integerPermutation.get(i);
            ParticipantType type = pectoModel.getParticipantType(id);

            if (type == ParticipantType.AMBULANCE) {
                ambulanceIndex++;
                List<Integer> route = new ArrayList<>();
                route.add(id);
                routes.add(route);
            } else {
                routes.get(ambulanceIndex).add(id);

                if (type == ParticipantType.HOSPITAL) {
                    int hIdx = id - pectoModel.getNumAmbulances() - pectoModel.getNumPatients();
                    hospitalOccupancy[hIdx]++;
                }
            }
        }
    }

    public void incrementAge() {
        age++;
    }

    public int getAge() {
        return age;
    }

    public double calculateDeltaWeightedSum(int route1, int route2, double delta1, double delta2) {
        double newTotalCost = totalCost + delta1 + delta2;
        int numAmbulances = pectoModel.getNumAmbulances();
        double newMean = newTotalCost / numAmbulances;
        double newVariance = 0;

        for (int r = 0; r < numAmbulances; r++) {
            double cost = r == route1 ? routeCosts[r] + delta1 :
                            r == route2 ? routeCosts[r] + delta2 :
                                    routeCosts[r];
            newVariance += Math.pow(cost - newMean, 2);
        }
        newVariance /= numAmbulances;
        double newWeightedSum = (newTotalCost * (1 - lambda)) + (lambda * Math.sqrt(newVariance));

        return newWeightedSum - value();
    }

    public double standardDeviationPenalty() {
        double mean = totalCost / routes.size();
        double variance = 0;
        for (double routeCost : routeCosts) {
            variance += Math.pow((routeCost - mean), 2);
        }
        variance /= routes.size();

        return lambda * Math.sqrt(variance);
    }

    private double routePenalty(int route) {
        double distance = 0.0;
        double penalty = 0.0;
        double[][] distances = pectoModel.getDistanceMatrix();

        for (int i = 0; i < routes.get(route).size() - 1; i++) {
            int index = routes.get(route).get(i);
            distance += distances[index][routes.get(route).get(i + 1)];

            if (pectoModel.getParticipantType(index).equals(ParticipantType.PATIENT)) {
                if (pectoModel.getPatientUrgencyTransportTime(index) < distance) {
                    penalty += Math.abs(pectoModel.getPatientUrgencyTransportTime(index) - distance);
                }
            }
        }

        return penalty;
    }

    public double transportPenalty() {
        double penalty = 0.0;

        for (double d : routeTransportPenalties) {
            penalty += d;
        }

        return lambda * penalty;
    }

    public double getTotalCost() {
        return (totalCost * (1 - (lambda * 2)));
    }

    @Override
    public Representation getRepresentation() {
        return integerPermutation;
    }

    @Override
    public double value() {
        return (totalCost * (1 - (lambda * 2))) + standardDeviationPenalty() + transportPenalty();
    }

    @Override
    public double value(int index) {
        return routeCosts[index];
    }

    @Override
    public double[] values() {
        return routeCosts;
    }

    @Override
    public Solution copy() {
        int[] newHospitalOccupancy = hospitalOccupancy.clone();
        List<List<Integer>> copiedRoutes = new ArrayList<>();

        for (List<Integer> route : this.routes) {
            copiedRoutes.add(new ArrayList<>(route));
        }

        return new PectoSolution(copiedRoutes, newHospitalOccupancy, problem);
    }

    public void updateRouteCost(int route, double value) {
        routeCosts[route] += value;
        routeTransportPenalties[route] = routePenalty(route);
        updateValue(value);
    }

    public void updateValue(double value) {
        totalCost += value;
    }

    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < integerPermutation.size(); i++) {
            list.add(integerPermutation.get(i));
        }
        return list;
    }

    public int size() {
        return integerPermutation.size();
    }

    private void calculateRouteCosts() {
        routeCosts = new double[routes.size()];
        totalCost = 0;

        for (int r = 0; r < routes.size(); r++) {
            IntegerPermutation routePermutation = new IntegerPermutation(routes.get(r));
            routeCosts[r] = problem.getObjective().value(pectoModel, routePermutation);
            routeTransportPenalties[r] = routePenalty(r);
            totalCost += routeCosts[r];
        }
    }

    public List<List<Integer>> getRoutes() {
        return routes;
    }

    public String printHos() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("HO: ");
        for (int i : hospitalOccupancy) {
            stringBuilder.append(i + " ");
        }

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return
//                printDistanceMatrix() +
//                printHos() +
                printSolution();
//                printAmbulanceRoutes();
//                printTotalCost();
    }

    private String printDistanceMatrix() {
        StringBuilder stringBuilder = new StringBuilder();
        double[][] matrix = pectoModel.getDistanceMatrix();

        stringBuilder.append("\n=====     DISTANCE MATRIX     =====\n\n");

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                stringBuilder.append(matrix[i][j]).append("\t");
            }
            stringBuilder.append("\n");
        }

        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private String printSolution() {
        return "\n=====     SOLUTION     =====\n\nR: " + integerPermutation + printTotalCost();
    }

    private String printTotalCost() {
        return "\nTotal Cost: [" + String.format("%.2f", value()) + "]\n\n";
    }

    private String printAmbulanceRoutes() {
        return printHeader() +
                printRoutes();
    }

    private String printHeader() {
        return """
                \n=====     AMBULANCE ROUTES     =====

                A: Ambulance    P: Patient      H: Hospital
                L: Light        H: Heavy        C: Critical
                           
                """;
    }

    private String printRoutes() {
        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < routes.size(); r++) {
            List<Integer> route = routes.get(r);
            if (route.isEmpty()) continue;

            int ambId = route.get(0);
            sb.append("A").append(ambId).append(" = [");

            boolean first = true;
            for (int i = 1; i < route.size(); i++) {
                int id = route.get(i);
                ParticipantType type = pectoModel.getParticipantType(id);

                if (!first) sb.append(" -> ");
                first = false;

                sb.append(printParticipant(id, type));
            }

            sb.append("]  V: [").append(String.format("%.2f", value(r))).append("]\n");
        }

        return sb.toString();
    }

    private String printParticipant(int id, ParticipantType pType) {
        return switch (pType) {
            case ParticipantType.PATIENT -> printPatient(id);
            case ParticipantType.HOSPITAL -> printHospital(id);
            default -> "";
        };
    }

    private String printPatient(int id) {
        char urgency = 0;

        if (pectoModel.getPatientUrgencyMultiplier(id) == 1.0) {
            urgency = 'S';
        } else if (pectoModel.getPatientUrgencyMultiplier(id) == 0.8) {
            urgency = 'L';
        } else if (pectoModel.getPatientUrgencyMultiplier(id) == 0.6) {
            urgency = 'M';
        } else if (pectoModel.getPatientUrgencyMultiplier(id) == 0.4) {
            urgency = 'H';
        } else if (pectoModel.getPatientUrgencyMultiplier(id) == 0.2) {
            urgency = 'C';
        }

        int patientNumber = id - pectoModel.getNumAmbulances();
        return "P" + patientNumber + "(" + urgency + ")";
    }

    private String printHospital(int id) {
        int hospitalNumber = id - pectoModel.getNumAmbulances() - pectoModel.getNumPatients();
        return "H" + hospitalNumber;
    }
}


