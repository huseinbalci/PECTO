package alg.greedy.pecto;

import problem.pecto.*;

import java.util.List;

import static problem.pecto.PECTOModel2.*;

public class PECTONearestParticipantEvaluator implements PECTOEvaluator{
    @Override
    public int choose(PECTOModel2 pectoModel, List<Integer> participantList, List<Integer> route) {
        double[][] distanceMatrix = pectoModel.getDistanceMatrix();
        int closest = participantList.getFirst();
        double minCost = Double.MAX_VALUE;
        ParticipantType participantType = pectoModel.getParticipantType(closest);
        double urgencyWeight = 1.0;

        for (int participant : participantList) {
            switch (participantType) {
                case ParticipantType.PATIENT -> {
                    urgencyWeight = pectoModel.getPatientUrgencyMultiplier(participant);
                }
                case ParticipantType.HOSPITAL -> {
                    urgencyWeight = pectoModel.getPatientUrgencyMultiplier(route.getLast());
                }
            }

            double cost = urgencyWeight * (distanceMatrix[route.getLast()][participant]);

            if (cost < minCost) {
                minCost = cost;
                closest = participant;
            }
        }
        return closest;
    }
}
