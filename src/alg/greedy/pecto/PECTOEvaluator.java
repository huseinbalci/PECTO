package alg.greedy.pecto;

import problem.pecto.PECTOModel;
import problem.pecto.PECTOModel2;

import java.util.List;

public interface PECTOEvaluator {
    int choose(PECTOModel2 pectoModel, List<Integer> participant, List<Integer> route);
}
