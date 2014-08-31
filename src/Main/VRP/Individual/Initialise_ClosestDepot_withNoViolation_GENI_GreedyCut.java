package Main.VRP.Individual;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.MutationOperators.GENI;

/*closest depot, greedy cut*/
public class Initialise_ClosestDepot_withNoViolation_GENI_GreedyCut 
{
	
	
	public static void initialise(Individual individual) 
	{
		// TODO Auto-generated method stub		
		InitialisePeriodAssigmentUniformly.initialise(individual);
		ClosestDepot_GENI_RouteWithGreedyCut(individual);
		individual.calculateCostAndPenalty();
	}

	private static void ClosestDepot_GENI_RouteWithGreedyCut(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		//Assign customer to route
		
		
		//allocate array lists big routes
		individual.bigRoutes = new ArrayList<ArrayList<ArrayList<Integer>>>();
		
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			individual.bigRoutes.add(new ArrayList<ArrayList<Integer>>());
			
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				individual.bigRoutes.get(period).add(new ArrayList<Integer>());
			}
		}
		
		//create big routes
		GENI.initialiseBigRouteWithClosestClients(individual);
		
		//now cut the routes and distribute to vehicles
		for(int period=0; period<problemInstance.periodCount;period++)
		{
			for(int depot=0; depot<problemInstance.depotCount;depot++)
			{

				greedyCutWithMinimumViolation(individual,period, depot);
				/*int vehicle = problemInstance.vehiclesUnderThisDepot.get(depot).get(0);
				ArrayList<Integer >route = routes.get(period).get(vehicle);
				route.clear();
				route.addAll(bigRoutes.get(period).get(depot));*/
				
			}
		}
	}

	private static void greedyCutWithMinimumViolation(Individual individual,int period,int depot) 
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> bigRoute = individual.bigRoutes.get(period).get(depot);		
		ArrayList<Integer> vehicles = problemInstance.vehiclesUnderThisDepot.get(depot);
		
		
		int currentVehicleIndex = 0;
		double currentLoad=0;
		
		while(!bigRoute.isEmpty() && currentVehicleIndex <vehicles.size())
		{
			int vehicle = vehicles.get(currentVehicleIndex);
			int client = bigRoute.get(0);
			
			double thisCapacity = problemInstance.loadCapacity[vehicle];
			double thisClientDemand = problemInstance.demand[client];
			
			double loadViolation = (currentLoad+thisClientDemand) - (thisCapacity);
			
			if(loadViolation <= 0) //add this client to this vehicle route, update info
			{
				individual.routes.get(period).get(vehicle).add(client);
				currentLoad += thisClientDemand;
				bigRoute.remove(0);
			}
			else
			{
				currentVehicleIndex++;
				currentLoad=0;
			}
		}
		
		if(!bigRoute.isEmpty())
		{
			
			
			//System.out.println("LEFT : "+bigRoute.size());
			while(!bigRoute.isEmpty())
			{
				int client = bigRoute.get(0);
				int vehicle = vehicles.get(vehicles.size()-1);
				
				individual.routes.get(period).get(vehicle).add(client);
				bigRoute.remove(0);
			}
		}
	}

}

