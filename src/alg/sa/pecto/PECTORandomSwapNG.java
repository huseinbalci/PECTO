package alg.sa.pecto;

import alg.ls.NeighborGenerator;
import problem.OptimizationProblem;
import problem.pecto.PECTOModel2;
import representation.IntegerPermutation;
import representation.PectoSolution;
import representation.Solution;

import java.util.ArrayList;
import java.util.List;

import static utils.RandUtils.generateRandomInt;

public class PECTORandomSwapNG implements NeighborGenerator<PECTOModel2, IntegerPermutation> {
    PectoSolution currentSolution;
    PECTOModel2 pectoModel;
    int lastRoute1;
    int lastRoute2;
    int lastIndex1;
    int lastIndex2;
    int lastSolutionIndex1;
    int lastSolutionIndex2;
    double lastDelta1;
    double lastDelta2;

    @Override
    public void init(OptimizationProblem<PECTOModel2, IntegerPermutation> problem, Solution currentSolution) {
        this.currentSolution = (PectoSolution) currentSolution;
        pectoModel = problem.model();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Solution next(OptimizationProblem<PECTOModel2, IntegerPermutation> problem) {
        return currentSolution;
    }

    public double applyMove(OptimizationProblem<PECTOModel2, IntegerPermutation> problem) {
        int route1 = generateRandomInt(pectoModel.getNumAmbulances());
        int route2 = generateRandomInt(pectoModel.getNumAmbulances());

        List<Integer> ambulance1 = currentSolution.getRoutes().get(route1);

        while(ambulance1.size() == 1) {
            route1 = (route1 + 1) % pectoModel.getNumAmbulances();
            ambulance1 = currentSolution.getRoutes().get(route1);
        }

        List<Integer> ambulance2 = currentSolution.getRoutes().get(route2);

        while(ambulance2.size() == 1) {
            route2 = (route2 + 1) % pectoModel.getNumAmbulances();
            ambulance2 = currentSolution.getRoutes().get(route2);
        }

        int index1 = 1 + generateRandomInt(ambulance1.size() - 1);
        int index2 = 1 + generateRandomInt(ambulance2.size() - 1);

        int solutionIndex1 = findSolutionIndex(currentSolution, route1, index1);
        int solutionIndex2 = findSolutionIndex(currentSolution, route2, index2);

        while (!pectoModel.getParticipantType(((IntegerPermutation)(currentSolution.getRepresentation())).get(solutionIndex1))
                .equals(pectoModel.getParticipantType(((IntegerPermutation)(currentSolution.getRepresentation())).get(solutionIndex2)))) {
            index2 = 1 + generateRandomInt(ambulance2.size() - 1);
            solutionIndex2 = findSolutionIndex(currentSolution, route2, index2);
        }

        int participant1 = ambulance1.get(index1);
        int participant2 = ambulance2.get(index2);

        ambulance1.set(index1, participant2);
        ambulance2.set(index2, participant1);

        List<Integer> initialList1 = extractSegment(currentSolution, solutionIndex1, solutionIndex1);
        List<Integer> finalList1 = extractSegment(currentSolution, solutionIndex1, solutionIndex2);
        List<Integer> initialList2 = extractSegment(currentSolution, solutionIndex2, solutionIndex2);
        List<Integer> finalList2 = extractSegment(currentSolution, solutionIndex2, solutionIndex1);

        double initialValue1 = problem.getObjective().value(problem.model(), new IntegerPermutation(initialList1));
        double finalValue1   = problem.getObjective().value(problem.model(), new IntegerPermutation(finalList1));
        double initialValue2 = problem.getObjective().value(problem.model(), new IntegerPermutation(initialList2));
        double finalValue2   = problem.getObjective().value(problem.model(), new IntegerPermutation(finalList2));

        lastRoute1 = route1;
        lastRoute2 = route2;
        lastIndex1 = index1;
        lastIndex2 = index2;
        lastSolutionIndex1 = solutionIndex1;
        lastSolutionIndex2 = solutionIndex2;

        ((IntegerPermutation)(currentSolution.getRepresentation())).swap(solutionIndex1, solutionIndex2);
        double delta1 = finalValue1 - initialValue1;
        currentSolution.updateRouteCost(route1, delta1);
        double delta2 = finalValue2 - initialValue2;
        currentSolution.updateRouteCost(route2, delta2);

        lastDelta1 = delta1;
        lastDelta2 = delta2;

        return delta1 + delta2;
    }

    public void undoMove() {
        List<Integer> ambulance1 = currentSolution.getRoutes().get(lastRoute1);
        List<Integer> ambulance2 = currentSolution.getRoutes().get(lastRoute2);

        int p1 = ambulance1.get(lastIndex1);
        int p2 = ambulance2.get(lastIndex2);

        ambulance1.set(lastIndex1, p2);
        ambulance2.set(lastIndex2, p1);

        ((IntegerPermutation) currentSolution.getRepresentation())
                .swap(lastSolutionIndex1, lastSolutionIndex2);

        currentSolution.updateRouteCost(lastRoute1, -lastDelta1);
        currentSolution.updateRouteCost(lastRoute2, -lastDelta2);
    }

    private List<Integer> extractSegment(Solution s, int index1, int index2) {
        List<Integer> list = new ArrayList<>();

        if ((index1 - 1) >= 0) {
            list.add(((IntegerPermutation)(s.getRepresentation())).get(index1 - 1));
        }

        list.add(((IntegerPermutation)(s.getRepresentation())).get(index2));

        if ((index1 + 1) < ((PectoSolution) s).size()) {
            list.add(((IntegerPermutation)(s.getRepresentation())).get(index1 + 1));
        }

        return list;
    }

    private int findSolutionIndex(PectoSolution pectoSolution, int route, int index) {
        int solutionIndex = index;

        for (int i = 0; i < route; i++) {
            solutionIndex += pectoSolution.getRoutes().get(i).size();
        }

        return solutionIndex;
    }

    @Override
    public String toString() {
        return "PECTO-Swap-NG";
    }
}
