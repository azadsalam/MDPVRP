package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;

import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class OneZeroExchange 
{
	static int fail=0;
	//MDPVRP pr04 5530.768632709098
	//MDPVRP pr06 7270.992149
	/**
	 * Randomly selects a client,period 
	 * <br/> Inserts the client  in another random route with minimum cost increase heuristics
	 * <br/> Improvements can be done in selecting the other route..
	 * <br/> Like - we can select the order the routes according to avg distance from this client and assign this client to the  next/prev route 
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
			success = mutateVehicleAssignmentRandom(individual,period,client);
			retry++;
			
		}while(success==false && retry<3);
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	private static boolean mutateVehicleAssignmentRandom(Individual individual,int period,int client)
	{
		
		MinimumCostInsertionInfo min = new  MinimumCostInsertionInfo();
		MinimumCostInsertionInfo newInfo;
		min.cost=9999999;
		
		//ei test er kono dorkar nai.. vehicle count 2 er kom hole ei operator call korar kono mane e nai
		if(individual.problemInstance.vehicleCount<2) return false;
			
		int assigendVehicle = RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);
		int position = individual.routes.get(period).get(assigendVehicle).indexOf(client);
		
		//remove the client from route
		individual.routes.get(period).get(assigendVehicle).remove(position);
		
	
		int vehicle = assigendVehicle;		
		while(vehicle==assigendVehicle)
		{
			vehicle = Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);
		}
		
		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
		newInfo= RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, client, route);
				
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		
		individual.routes.get(period).get(min.vehicle).add(min.insertPosition, client);
		
		if(min.vehicle==assigendVehicle && min.insertPosition==position) return false;
		else
			return true;
	}
	

}
