package alg.greedy.pecto;

import alg.BaseSMetaheuristic;
import alg.TerminalCondition;
import alg.greedy.GreedyHeuristic;
import problem.OptimizationProblem;
import problem.SimpleOptimizationProblem;
import problem.pecto.*;
import representation.IntegerPermutation;
import representation.PectoSolution;
import representation.Solution;

import java.util.*;

public class PECTOGreedyHeuristic extends BaseSMetaheuristic<PECTOModel2, IntegerPermutation>
                                    implements GreedyHeuristic<PECTOModel2, IntegerPermutation>
{
    private final PECTOEvaluator evaluator;

    public PECTOGreedyHeuristic(PECTOEvaluator pectoEvaluator, TerminalCondition<PECTOModel2, IntegerPermutation> terminalCondition) {
        super(terminalCondition);
        this.evaluator = pectoEvaluator;
    }

    private int findNearestParticipant(PECTOModel2 pectoModel, List<Integer> participant, List<Integer> route) {
        return evaluator.choose(pectoModel, participant, route);
    }

    @Override
    public Solution solve(PECTOModel2 pectoModel, SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem) {
        List<Integer> notTransportedPatients = new ArrayList<>(pectoModel.getPatientList());
        List<Integer> availableHospitals = new ArrayList<>(pectoModel.getHospitalList());
        List<Integer> ambulanceList = pectoModel.getAmbulanceList();
        Collections.shuffle(ambulanceList);

        List<List<Integer>> routes = new ArrayList<>();
        int[] hospitalOccupancy = new int[availableHospitals.size()];
        Arrays.fill(hospitalOccupancy, 0);

        for (int ambId : ambulanceList) {
            List<Integer> route = new ArrayList<>();
            route.add(ambId);
            routes.add(route);
        }

        while (!notTransportedPatients.isEmpty() && !availableHospitals.isEmpty()) {
            for (int ambulance : ambulanceList) {
                int patient = findNearestParticipant(pectoModel, notTransportedPatients, routes.get(ambulance));
                routes.get(ambulance).add(patient);
                notTransportedPatients.remove(Integer.valueOf(patient));


                int hospital = findNearestParticipant(pectoModel, availableHospitals, routes.get(ambulance));
                routes.get(ambulance).add(hospital);
                int hIdx = hospital - pectoModel.getNumAmbulances() - pectoModel.getNumPatients();
                hospitalOccupancy[hIdx]++;

                if (!(hospitalOccupancy[hIdx] < pectoModel.getHospitalCapacity())) {
                    availableHospitals.remove(Integer.valueOf(hospital));
                }

                if (notTransportedPatients.isEmpty() || availableHospitals.isEmpty()) {
                    break;
                }
            }
        }

        return new PectoSolution(routes, hospitalOccupancy, problem);
    }

    @Override
    protected Solution _perform(OptimizationProblem<PECTOModel2, IntegerPermutation> pm) {
        SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem = (SimpleOptimizationProblem<PECTOModel2, IntegerPermutation>) pm;

        while( !terminalCondition.isSatisfied(this, problem) )
        {
            PectoSolution pectoSolution = (PectoSolution) solve(problem.model(), (SimpleOptimizationProblem) pm);

            updateBest(problem,pectoSolution);
            iterationCount++;

        }
        return bestSolution;
    }

    @Override
    protected void _init(OptimizationProblem<PECTOModel2, IntegerPermutation> pm) {
        bestSolution = null;
        iterationCount = 0;
    }

    @Override
    public String toString() {
        return "GR ";
    }

    public static void main(String[] args) {
        PECTOGreedyHeuristic heuristic= new PECTOGreedyHeuristic(new PECTONearestParticipantEvaluator(), (alg, pm) -> false);
        PECTOModel2 pectoModel = new PECTOModel2(5,15,2) ;
        SimpleOptimizationProblem<PECTOModel2, IntegerPermutation> problem =  new SimpleOptimizationProblem<>(pectoModel, new PECTOMinimumTimeOF());

        PectoSolution solution1 = (PectoSolution) heuristic.solve(pectoModel, problem);
        System.out.println("S1: " + solution1);
        PectoSolution solution2 = (PectoSolution) heuristic.solve(pectoModel, problem);
        System.out.println("S2: " + solution2);
    }
}
