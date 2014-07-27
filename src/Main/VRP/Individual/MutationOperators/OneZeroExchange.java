package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;

import Main.Utility;
import Main.VRP.GeneticAlgorithm.Mutation_Grouped;
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
		//vehicle count 2 er kom hole ei operator call korar kono mane e nai
		if(individual.problemInstance.vehicleCount<2) return;

		int period,client;
		do
		{
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			client = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);		
		}while(individual.periodAssignment[period][client] == false);
		
		mutateVehicleAssignmentRandom(individual,period,client);

	}
	
	private static void mutateVehicleAssignmentRandom(Individual individual,int period,int client)
	{
		
		int assigendVehicle = RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);
		int position = individual.routes.get(period).get(assigendVehicle).indexOf(client);
		
		//remove the client from old route - with vehicle == assignedVehicle
		individual.routes.get(period).get(assigendVehicle).remove(position);		
	
		int vehicle = assigendVehicle;		
		while(vehicle==assigendVehicle)
		{
			vehicle = Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);
		}
		
		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
		MinimumCostInsertionInfo newInfo= RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, client, route);
				
		
		// add client to new route - with vehicle == vehicle
		individual.routes.get(period).get(vehicle).add(newInfo.insertPosition, client);
		
		//Mutation_Grouped.mutateRouteAssignment(individual, loadPenaltyFactor, routeTimePenaltyFactor)
		
		Mutation_Grouped.improveRoute(individual, period, assigendVehicle); //improveOldRoute
		Mutation_Grouped.improveRoute(individual, period, vehicle); //improve new route
		
	}
	

}
