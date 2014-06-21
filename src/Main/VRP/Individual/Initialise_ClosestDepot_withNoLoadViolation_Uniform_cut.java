package Main.VRP.Individual;

import java.util.ArrayList;
import java.util.Arrays;

import Main.Utility;
import Main.VRP.ProblemInstance;

public class Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut {

	public static void initiialise(Individual individual) 
	{
		// TODO Auto-generated method stub
		//System.out.println("HERE");
		InitialisePeriodAssigmentUniformly.initialise(individual);
		bigClosestDepot_withNoLoadViolation_Uniform_cut(individual);
		individual.calculateCostAndPenalty();
	}


	public static void bigClosestDepot_withNoLoadViolation_Uniform_cut(Individual individual)
	{
		ProblemInstance problemInstance = Individual.problemInstance;
		//Assign customer to route
		boolean[] clientMap = new boolean[problemInstance.customerCount];
		
		
		//create empty lists
		individual.bigRoutes = new ArrayList<ArrayList<ArrayList<Integer>>>();		
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			individual.bigRoutes.add(new ArrayList<ArrayList<Integer>>());
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				individual.bigRoutes.get(period).add(new ArrayList<Integer>());
			}
		}
		
		//capacity for each depots
		double[][] currentCapcityOfDepots = new double[problemInstance.periodCount][problemInstance.depotCount];
		
		for(int depot=0;depot < problemInstance.depotCount;depot++)
		{
			ArrayList<Integer> vehicles = problemInstance.vehiclesUnderThisDepot.get(depot);
			double capacity=0;
			for(int i=0; i < vehicles.size() ;i++)
			{
				int vehicle = vehicles.get(i);
				capacity += problemInstance.loadCapacity[vehicle];
			}
			for(int period=0;period<problemInstance.periodCount;period++)
			{
					currentCapcityOfDepots[period][depot] = capacity;	
//				System.out.print(currentCapcityOfDepots[period][depot] +" ");
			}	
//			System.out.println();
		}
		
		int assigned=0;
			
		//create big routes
		while(assigned<problemInstance.customerCount)
		{
			int clientNo = Utility.randomIntInclusive(problemInstance.customerCount-1);
			if(clientMap[clientNo]) continue;
			clientMap[clientNo]=true;
			assigned++;
			
			
			ArrayList<Integer> closestDepots = RouteUtilities.closestDepots(clientNo);	
			double demand = problemInstance.demand[clientNo];
			//System.out.println("CLient : "+clientNo+" "+closestDepots.toString());
			
			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				if(individual.periodAssignment[period][clientNo]==false)continue;

				// add this to the closest depot if no load violation
				// else add to the next closest
				int depot;
				int i;
				for(i=0;i<closestDepots.size();i++)
				{
					depot = closestDepots.get(i);
					if(currentCapcityOfDepots[period][depot] >= demand)
					{
						currentCapcityOfDepots[period][depot] -= demand;
						individual.insertIntoBigClosestDepotRoute(clientNo, depot, period);
						break;
					}
				}
				
				if(i == problemInstance.depotCount)
				{
					individual.insertIntoBigClosestDepotRoute(clientNo, closestDepots.get(0), period);
				}
				
				//
			}			
		}
		
	/*	for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				System.out.print(" "+currentCapcityOfDepots[period][depot]);
			}
			System.out.println();
		}
	*/	
		
		/*Re insert all clients */
	/*	for(int period=0; period<problemInstance.periodCount;period++)
		{
			for(int depot=0; depot<problemInstance.depotCount;depot++)
			{
				ArrayList<Integer> bigRoute = individual.bigRoutes.get(period).get(depot);		

				//make a copy
				ArrayList<Integer> copy = new ArrayList<Integer> ();
				for(int i=0;i<bigRoute.size();i++)copy.add(bigRoute.get(i));
				
				//remove and add again every client serially  
				for(int i=0; i<copy.size();i++)
				{
					int clientNo = copy.get(i);
					
					int index = bigRoute.indexOf(clientNo); //remove
					bigRoute.remove(index);
					
					//insert again
					MinimumCostInsertionInfo minPos = RouteUtilities.minimumCostInsertionPosition2(problemInstance, depot, clientNo, bigRoute);
					bigRoute.add(minPos.insertPosition, clientNo);
				}
				
			}
		}
*/		
		//now cut the routes and distribute to vehicles
		for(int period=0; period<problemInstance.periodCount;period++)
		{
			for(int depot=0; depot<problemInstance.depotCount;depot++)
			{
				greedyCutWithMinimumViolation(individual,period, depot);				
			}
		}
	}
	
	public static void greedyCutWithMinimumViolation(Individual individual,int period,int depot) 
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> bigRoute = individual.bigRoutes.get(period).get(depot);		
		ArrayList<Integer> vehicles = problemInstance.vehiclesUnderThisDepot.get(depot);
		
//		double currentCapacity[] = new double[vehicles.size];
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
