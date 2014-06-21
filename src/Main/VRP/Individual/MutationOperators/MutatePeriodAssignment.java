package Main.VRP.Individual.MutationOperators;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class MutatePeriodAssignment {

	
	/** do not updates cost + penalty
	 if sobgula client er frequency = period hoy tahole, period assignment mutation er kono effect nai
	*/
	public static void mutatePeriodAssignment(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		boolean success;
		int clientNo;
		int total = problemInstance.customerCount;
		do
		{
			clientNo = Utility.randomIntInclusive(problemInstance.customerCount-1);
			success = mutatePeriodAssignment(individual,clientNo);
			total--;
		}while(success==false && total>0);
	
	}
	
	//returns 0 if it couldnt mutate as period == freq
	//need to edit this- must repair 
	private static boolean mutatePeriodAssignment(Individual individual, int clientNo)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		ArrayList<Integer> allPossibleCombinationsForThisClient =  problemInstance.allPossibleVisitCombinations.get(clientNo);
		
		int size = allPossibleCombinationsForThisClient.size();
		if( size == 1) return false;
		
		
		//problemInstance.out.println("Client "+clientNo);
		
		int previousCombination = individual.visitCombination[clientNo];
		int newCombination = previousCombination;
		//int size = 
		
		while(newCombination == previousCombination)
		{
			int ran = Utility.randomIntInclusive(size-1);
			newCombination = allPossibleCombinationsForThisClient.get(ran);
		}
		
		individual.visitCombination[clientNo] = newCombination; 
		
		//remove the client from previous combinations
		// add client to new combinations
		int[] bitArrayPrev = problemInstance.toBinaryArray(previousCombination);
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(individual.periodAssignment[period][clientNo]) 
			{
				removeClientFromPeriod(individual, period, clientNo);
				//individual.periodAssignment[period][clientNo] = false;
			}	
		}
		
		int[] newBitArray = problemInstance.toBinaryArray(newCombination);
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(newBitArray[period]==1) 
			{
				addClientIntoPeriodGreedy(individual, period, clientNo);
				individual.periodAssignment[period][clientNo] = true;
			}	
			else
			{
				individual.periodAssignment[period][clientNo] = false;
			}
		}
		
		
/*		//no way to mutate per. ass. as freq. == period
		if(problemInstance.frequencyAllocation[clientNo] == problemInstance.periodCount) return false;
		if(problemInstance.frequencyAllocation[clientNo] == 0) return false;		

		int previouslyAssigned; // one period that was assigned to client
		do
		{
			previouslyAssigned = Utility.randomIntInclusive(problemInstance.periodCount-1);
		} while (individual.periodAssignment[previouslyAssigned][clientNo]==false);

		int previouslyUnassigned;//one period that was NOT assigned to client
		do
		{
			previouslyUnassigned = Utility.randomIntInclusive(problemInstance.periodCount-1);
		} while (individual.periodAssignment[previouslyUnassigned][clientNo]==true);

		individual.periodAssignment[previouslyAssigned][clientNo] = false;
		individual.periodAssignment[previouslyUnassigned][clientNo]= true;

		removeClientFromPeriod(individual,previouslyAssigned,clientNo);
		addClientIntoPeriodGreedy(individual, previouslyUnassigned,clientNo);
*/
		//problemInstance.out.println("previouslyAssigned Period : "+previouslyAssigned+"previouslyUnassigned : "+previouslyUnassigned+" vehicle  : "+vehicle+" client "+clientNo);

		return true;
	}
	
	
	private static void addClientIntoPeriodGreedy(Individual individual, int period, int client)
	{

		MinimumCostInsertionInfo min = new  MinimumCostInsertionInfo();
		MinimumCostInsertionInfo newInfo;
		min.cost=9999999;
		
				
		for(int vehicle = 0;vehicle<individual.problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			newInfo= RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, client, route);
			
			if(newInfo.cost <= min.cost)
			{
				min = newInfo;
			}
		}
		
		individual.routes.get(period).get(min.vehicle).add(min.insertPosition, client);
		
	}
	
	private static void addClientIntoPeriod(int period, int vehicle, int client)
	{
		// ArrayList<Integer> route = routes.get(period).get(vehicle);
		
		// int position = Utility.randomIntInclusive(route.size());
		// route.add(position,client);
	}
	/** Removes client from that periods route
	 * 
	 * @param period
	 * @param client
	 * @return number of the vehicle, of which route it was present.. <br/> -1 if it wasnt present in any route
	 */
	private static int removeClientFromPeriod(Individual individual, int period, int client)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			if(route.contains(client))
			{
				route.remove(new Integer(client));
				return vehicle;
			}
		}
		return -1;
	}
	
}
