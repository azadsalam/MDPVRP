
package Main.VRP.Individual.Crossover;

import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut;

public class FaltuSystemCrossOver {
	public static void crossOver(ProblemInstance p,Individual parent1,Individual parent2,Individual child1,Individual child2){
		UniformCrossoverPeriodAssigment.uniformCrossoverForPeriodAssignment(child1, child2, parent1, parent2, p);
		Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut.bigClosestDepot_withNoLoadViolation_Uniform_cut(child1);
		Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut.bigClosestDepot_withNoLoadViolation_Uniform_cut(child2);
		child1.calculateCostAndPenalty();
		child2.calculateCostAndPenalty();
	}
}
