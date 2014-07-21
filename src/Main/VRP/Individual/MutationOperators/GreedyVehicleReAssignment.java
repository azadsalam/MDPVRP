package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;

import Main.Solver;
import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class GreedyVehicleReAssignment 
{
	static int fail=0;
	
	/**
	 * Randomly selects a client,period 
	 * <br/>Inserts the client in the route, which cause minimum cost increase taking account of load violation
	 * @param individual
	 */
	public static void mutate(Individual individual)
	{
		int retry = 0;
		int period,client;
		boolean success=false;
		do
		{
			
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			client = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);

			if(individual.periodAssignment[period][client] == false) continue;
			success = mutateVehicleAssignmentGreedy(individual,period,client);
			retry++;
			
		}while(success==false && retry<3);
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	private static boolean mutateVehicleAssignmentGreedy(Individual individual,int period,int client)
	{
		MinimumCostInsertionInfo min = new  MinimumCostInsertionInfo();
		MinimumCostInsertionInfo newInfo;
		min.increaseInCost = Double.MAX_VALUE;
		min.loadViolation = Double.MAX_VALUE;
		
		int assigendVehicle = RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);
		int position = individual.routes.get(period).get(assigendVehicle).indexOf(client);
		
		//remove the client from route
		individual.routes.get(period).get(assigendVehicle).remove(position);
		
		
		for(int vehicle = 0;vehicle<individual.problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			
			
			newInfo= RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, client, route);
			
			
			double minCostContribution = min.increaseInCost + min.loadViolationContribution * Solver.loadPenaltyFactor ;
			double newCostContribution = newInfo.increaseInCost + newInfo.loadViolationContribution * Solver.loadPenaltyFactor ;
			
			if(newCostContribution < minCostContribution)
			{
				min = newInfo;					
			}
			else if (newCostContribution == minCostContribution)
			{
				int coin = Utility.randomIntInclusive(1);
				if(coin==1)
					min=newInfo;
			}
			
		}
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		
		individual.routes.get(period).get(min.vehicle).add(min.insertPosition, client);
		
		if(min.vehicle==assigendVehicle && min.insertPosition==position) return false;
		else
			return true;
	}
	

}
