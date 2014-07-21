package Main.VRP.GeneticAlgorithm;
import javax.swing.plaf.SliderUI;

import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomInsertion;
import Main.VRP.Individual.MutationOperators.IntraRouteGreedyInsertion;
import Main.VRP.Individual.MutationOperators.MutatePeriodAssignment;
import Main.VRP.Individual.MutationOperators.GreedyVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomSwap;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.OneOneExchange;
import Main.VRP.Individual.MutationOperators.OneZeroExchange;
import Main.VRP.Individual.MutationOperators.Or_Opt;
import Main.VRP.Individual.MutationOperators.PatternImprovement;
import Main.VRP.Individual.MutationOperators.Three_Opt;
import Main.VRP.Individual.MutationOperators.Two_Opt;


public class Mutation_Grouped implements MutationInterface
{	
	//ProblemInstance problemInstance;
	
	
	/*
	public Mutation(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
	}
	*/
	
	//category 3 
	// 1 - intra route improvement
	// 2 - inter route -> 1) same period
	//					  2) different period
	
	public void applyMutation(Individual offspring)
	{
		int totalCategory = 2;
		if(offspring.problemInstance.periodCount==1)totalCategory-=1;
		
		int selectedCategory = Utility.randomIntExclusive(totalCategory);
		
		//System.out.println("MUTATION WITH GROUPING");
		
		if(selectedCategory==0) mutateRouteAssignment(offspring);
		else mutatePeriodAssignment(offspring);
		
		mutateRoute(offspring);
		
			
		offspring.calculateCostAndPenalty();
		
	}

	
	public void mutateRoute(Individual offspring)
	{
		int totalRouteImprovementOperators = 4;
		int selectedMutationOperator = Utility.randomIntExclusive(totalRouteImprovementOperators);
		if(selectedMutationOperator==0)
		{
			//intra       
			Two_Opt.mutateRandomRoute(offspring);
		}
		else if (selectedMutationOperator == 1)
		{			
			//greedy       //intra	
			Three_Opt.mutateRandomRoute(offspring);
		}
		else if (selectedMutationOperator == 2)
		{
			//greedy       //intra	
			Or_Opt.mutateRandomRoute(offspring);
		}
		else if (selectedMutationOperator == 3)
		{
			//random //intra 
			IntraRouteRandomInsertion.mutate(offspring); 
		}		
		
		//not used currently
		//greedy //intra
		//IntraRouteGreedyInsertion.mutate(offspring);

	}
	
	public void mutatePeriodAssignment(Individual offspring)
	{
		int totalOperators = 2;
		int selectedMutationOperator = Utility.randomIntExclusive(totalOperators);
		
		if (selectedMutationOperator == 0)
		{
			//greedy       //inter period	
			PatternImprovement.patternImprovement(offspring);
		}
		else 
		{
			//random //inter period
		    MutatePeriodAssignment.mutatePeriodAssignment(offspring);
		}
	}
	
	public void mutateRouteAssignment(Individual offspring)
	{
		int totalOperators = 3;
		int selectedMutationOperator = Utility.randomIntExclusive(totalOperators);

		if (selectedMutationOperator == 0)
		{
			//greedy //inter
			GreedyVehicleReAssignment.mutate(offspring);
		}
		else if (selectedMutationOperator == 1)
		{
			//random //inter
			OneZeroExchange.mutate(offspring);
		}
		else if (selectedMutationOperator == 2)
		{
			//random+greedy       //inter
			OneOneExchange.mutate(offspring);
		}
		
	}
	
	@Override
 	public void updateWeights() {
		// TODO Auto-generated method stub
		
	}

}
