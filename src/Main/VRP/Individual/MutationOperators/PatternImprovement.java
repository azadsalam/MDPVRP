package Main.VRP.Individual.MutationOperators;

import java.security.AllPermission;
import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.Mutation_Grouped;
import Main.VRP.GeneticAlgorithm.Scheme6;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class PatternImprovement {

	
	public static void patternImprovement(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		int chosenClient = Utility.randomIntInclusive(problemInstance.customerCount-1);
		
		int noOfPossiblePatterns = problemInstance.allPossibleVisitCombinations.get(chosenClient).size();
		
		double min = Double.MAX_VALUE;
		
		int chosenVisitPattern = -1; 
		
		for(int i=0;i<noOfPossiblePatterns ;i++)
		{
			changeVisitPattern(individual, chosenClient, problemInstance.allPossibleVisitCombinations.get(chosenClient).get(i),loadPenaltyFactor,routeTimePenaltyFactor,true);
			TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			if(individual.costWithPenalty<min)
			{
				chosenVisitPattern = problemInstance.allPossibleVisitCombinations.get(chosenClient).get(i);
				min = individual.costWithPenalty;
			}
		}
		
		changeVisitPattern(individual, chosenClient, chosenVisitPattern,loadPenaltyFactor,routeTimePenaltyFactor,true);
	
	}
	
	
	public static void patternImprovementOfAllClients(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		//int totalcustomer = problemInstance.customerCount;
		
		for(int chosenClient=0;chosenClient<problemInstance.customerCount;chosenClient++)
		{
//			int chosenClient = Utility.randomIntInclusive(problemInstance.customerCount-1);
			
			int noOfPossiblePatterns = problemInstance.allPossibleVisitCombinations.get(chosenClient).size();
			
			
			double min = individual.costWithPenalty;
			int chosenVisitPattern = individual.visitCombination[chosenClient]; 
			for(int i=0;i<noOfPossiblePatterns ;i++)
			{
				changeVisitPattern(individual, chosenClient, problemInstance.allPossibleVisitCombinations.get(chosenClient).get(i),loadPenaltyFactor,routeTimePenaltyFactor,false);
				TotalCostCalculator.calculateCost(individual,loadPenaltyFactor,routeTimePenaltyFactor);
				if(individual.costWithPenalty<min)
				{
					chosenVisitPattern = problemInstance.allPossibleVisitCombinations.get(chosenClient).get(i);
					min = individual.costWithPenalty;
				}
			}
			
			changeVisitPattern(individual, chosenClient, chosenVisitPattern,loadPenaltyFactor,routeTimePenaltyFactor,true);
		}
	}

	

	//change the visit pattern of individual to newVisitCombination 
	private static void changeVisitPattern(Individual individual, int clientNo, int newVisitCombination, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean improveResultantRoute)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		int previousCombination = individual.visitCombination[clientNo];
		int newCombination = newVisitCombination;
				
		individual.visitCombination[clientNo] = newCombination; 
		
		//remove the client from previous combinations
		int[] bitArrayPrev = problemInstance.toBinaryArray(previousCombination);
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(individual.periodAssignment[period][clientNo]) 
			{
				removeClientFromPeriod(individual, period, clientNo,improveResultantRoute);
			}	
		}
		
		// add client to new combinations
		int[] newBitArray = problemInstance.toBinaryArray(newCombination);
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(newBitArray[period]==1) 
			{
				addClientIntoPeriodGreedy(individual, period, clientNo,loadPenaltyFactor,routeTimePenaltyFactor,improveResultantRoute);
				individual.periodAssignment[period][clientNo] = true;
			}	
			else
			{
				individual.periodAssignment[period][clientNo] = false;
			}
		}
	}
	
	/**
	 * 
	 * @param individual
	 * @param period
	 * @param client
	 */
	public static void addClientIntoPeriodGreedy(Individual individual, int period, int client, double loadPenaltyFactor, double routeTimePenaltyFactor,boolean improveResultantRoute)
	{

		MinimumCostInsertionInfo min = new  MinimumCostInsertionInfo();
		MinimumCostInsertionInfo newInfo;
		min.increaseInCost=99999999;
		min.loadViolationContribution = 999999999;
		min.insertPosition=-1;
		
		for(int vehicle = 0;vehicle<Individual.problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			newInfo= RouteUtilities.minimumCostInsertionPosition(Individual.problemInstance, vehicle, client, route);
			
			double minCostWithPenalty = min.increaseInCost + min.loadViolationContribution * loadPenaltyFactor;
			double newCostWithPenalty = newInfo.increaseInCost + newInfo.loadViolationContribution * loadPenaltyFactor;
			
			
			//just checking the cost WITHOUT PENALTY 
			/*double minCostWithPenalty = min.increaseInCost ;
			double newCostWithPenalty = newInfo.increaseInCost ;
			*/
			
			if(newCostWithPenalty < minCostWithPenalty)
			{
				min = newInfo;
			}
			else if (newCostWithPenalty == minCostWithPenalty)
			{
				int coin = Utility.randomIntInclusive(1);
				if(coin==1)
					min=newInfo;
			}
		}		
		individual.routes.get(period).get(min.vehicle).add(min.insertPosition, client);	
		
		//improve new route
		if(improveResultantRoute)
			Mutation_Grouped.improveRoute(individual, period, min.vehicle);
	}
	
	/** Removes client from that periods route
	 * 
	 * @param period
	 * @param client
	 * @return number of the vehicle, of which route it was present.. <br/> -1 if it wasnt present in any route
	 */
	public static int removeClientFromPeriod(Individual individual, int period, int client,boolean improveResultantRoute)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			if(route.contains(client))
			{
				route.remove(new Integer(client));
				//improve route
				if(improveResultantRoute)Mutation_Grouped.improveRoute(individual, period, vehicle);
				return vehicle;
			}
		}
		return -1;
	}
	
}
