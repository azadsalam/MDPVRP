package Main.VRP.GeneticAlgorithm;
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


public class Mutation implements MutationInterface
{	
	//ProblemInstance problemInstance;
	
	
	/*
	public Mutation(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
	}
	*/
	
	
	public void applyMutation(Individual offspring)
	{
		int rand = 9;
		if(offspring.problemInstance.periodCount==1)rand-=2;
		
		int selectedMutationOperator = Utility.randomIntInclusive(rand);
		
		if(selectedMutationOperator==0)
		{
			//greedy //intra
			IntraRouteGreedyInsertion.mutate(offspring);
		}
		else if (selectedMutationOperator == 1)
		{			
			//greedy //inter
			GreedyVehicleReAssignment.mutate(offspring);
		}
		else if (selectedMutationOperator == 2)
		{
			//random //inter
			OneZeroExchange.mutate(offspring);
//			offspring.mutateRouteWithInsertion();
		}
		else if (selectedMutationOperator == 3)
		{
			//random //intra
			IntraRouteRandomInsertion.mutate(offspring);
		}
		else if (selectedMutationOperator == 4)
		{
			//intra       
			Two_Opt.mutateRandomRoute(offspring);
		}
		else if (selectedMutationOperator == 5)
		{
			//random+greedy       //inter
			OneOneExchange.mutate(offspring);
		}
		else if (selectedMutationOperator == 6)
		{
			//greedy       //inra	
			Or_Opt.mutateRandomRoute(offspring);
		}
		else if (selectedMutationOperator == 7)
		{
			//greedy       //inra	
			Three_Opt.mutateRandomRoute(offspring);
		}
		
		else if (selectedMutationOperator == 8)
		{
			//greedy       //inter period	
			PatternImprovement.patternImprovement(offspring);
		}
		
		else 
		{
			//random //inter
		   MutatePeriodAssignment.mutatePeriodAssignment(offspring);
		}
		
		offspring.calculateCostAndPenalty();
		
	}

	@Override
	public void updateWeights() {
		// TODO Auto-generated method stub
		
	}

}
