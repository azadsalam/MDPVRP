package Main.VRP.Individual;
import java.util.ArrayList;

import Main.VRP.ProblemInstance;



public class RouteUtilities 
{
	
	/**
	 * Checks if the client is present in any route or not,  for the specified period
	 * @param problemInstance
	 * @param individual
	 * @param period
	 * @param client
	 * @return true if client is present in some route <br/> else false
	 */
	public static boolean doesRouteContainThisClient(ProblemInstance problemInstance, Individual individual, int period, int client)
	{

		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			if(individual.routes.get(period).get(vehicle).contains(client))
			{
				return true;
			}
		}	
		return false;
	}


	/**
	 * Returns the vehicleNumber which serves the client  
	 * @param individual
	 * @param client
	 * @param period
	 * @param problemInstance
	 * @return vehicleNumber, v ; if the client is present in the period
	 * <br/> -1 if not present
	 */	
	public static int assignedVehicle(Individual individual, int client, int period,ProblemInstance problemInstance)
	{
		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			if(route.contains(client)) return vehicle;
		}	
		return -1;
	}


	/**
	 * returns the index in which the client can be inserted with minimum cost increase,
	 *  should be used for big route, when the vehicles are still unassigned
	 * @param problemInstance
	 * @param client
	 * @param route
	 * @return
	 */
	public static MinimumCostInsertionInfo minimumCostInsertionPosition2(ProblemInstance problemInstance,int depot,int client,ArrayList<Integer> route)
	{
		double min = Double.MAX_VALUE;
		int chosenInsertPosition =- 1;
		double cost;
		
		double [][]costMatrix = problemInstance.costMatrix;
		int depotCount = problemInstance.depotCount;
		//int depot = problemInstance.depotAllocation[vehicle];
		
		
		if(route.size()==0)
		{
		//	route.add(client);	
			
			MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
			info.increaseInCost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depot];
			info.insertPosition=0;
			info.loadViolation= -1;
			//info.vehicle = vehicle;
			return info;
		}
		
		cost=0;
		cost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(0)];
		cost -= (costMatrix[depot][depotCount+route.get(0)]);
		if(cost<min)
		{
			min=cost;
			chosenInsertPosition = 0;
		}
		
		
		for(int insertPosition=1;insertPosition<route.size();insertPosition++)
		{
			//insert the client between insertPosition-1 and insertPosition and check 
			cost = costMatrix[depotCount+route.get(insertPosition-1)][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(insertPosition)];
			cost -= (costMatrix[depotCount+route.get(insertPosition-1)][depotCount+route.get(insertPosition)]);
			if(cost<min)
			{
				min=cost;
				chosenInsertPosition = insertPosition;
			}
		}
		
		cost = costMatrix[depotCount+route.get(route.size()-1)][depotCount+client] + costMatrix[depotCount+client][depot];
		cost-=(costMatrix[depotCount+route.get(route.size()-1)][depot]);
		
		if(cost<min)
		{
			min=cost;
			chosenInsertPosition = route.size();
		}
		
		MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
		info.increaseInCost = min;
		info.insertPosition=chosenInsertPosition;
		info.loadViolation= -1;
		//info.vehicle = vehicle;
		return info;
	
	}

	
	
	/**
	 * returns the index in which the client can be inserted with minimum cost increase
	 * 
	 * @param problemInstance
	 * @param vehicle
	 * @param client
	 * @param route
	 * @return
	 */
	public static MinimumCostInsertionInfo minimumCostInsertionPosition(ProblemInstance problemInstance,int vehicle,int client,ArrayList<Integer> route)
	{
		double min = Double.MAX_VALUE;
		int chosenInsertPosition =- 1;
		double cost;
		
		double [][]costMatrix = problemInstance.costMatrix;
		int depotCount = problemInstance.depotCount;
		int depot = problemInstance.depotAllocation[vehicle];
		
		
		if(route.size()==0)
		{
		//	route.add(client);				
			MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
			
			info.increaseInCost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depot];
			info.insertPosition=0;
			
			info.loadViolation= problemInstance.demand[client] - problemInstance.loadCapacity[vehicle];
			
			if(info.loadViolation>0) info.loadViolationContribution = info.loadViolation;
			else info.loadViolationContribution=0;
			
			info.vehicle = vehicle;
			return info;
		}
		
		cost=0;
		cost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(0)];
		cost -= (costMatrix[depot][depotCount+route.get(0)]);
		if(cost<min)
		{
			min=cost;
			chosenInsertPosition = 0;
		}
		
		for(int insertPosition=1;insertPosition<route.size();insertPosition++)
		{
			//insert the client between insertPosition-1 and insertPosition and check 
			cost = costMatrix[depotCount+route.get(insertPosition-1)][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(insertPosition)];
			cost -= (costMatrix[depotCount+route.get(insertPosition-1)][depotCount+route.get(insertPosition)]);
			if(cost<min)
			{
				min=cost;
				chosenInsertPosition = insertPosition;
			}
		}
		
		cost = costMatrix[depotCount+route.get(route.size()-1)][depotCount+client] + costMatrix[depotCount+client][depot];
		cost-=(costMatrix[depotCount+route.get(route.size()-1)][depot]);
		
		if(cost<min)
		{
			min=cost;
			chosenInsertPosition = route.size();
		}
		
		//calculate load violation
		//calculate total load
		double totalLoad=0,previousLoad=0;
		
		for(int i=0;i<route.size();i++)
		{
			int node = route.get(i);
			previousLoad += problemInstance.demand[node];
		}
		totalLoad = previousLoad + problemInstance.demand[client];
		
		double vehicleCapacity = problemInstance.loadCapacity[vehicle];
		double totalLoadViolation = totalLoad - vehicleCapacity;
		
		double loadViolationContribution=0;
		if(previousLoad > vehicleCapacity) loadViolationContribution = problemInstance.demand[client];
		else
		{ 
			if(totalLoadViolation>0)loadViolationContribution = totalLoadViolation;
			else loadViolationContribution = 0;
		} 
		
		
		MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
		info.increaseInCost = min;
		info.insertPosition = chosenInsertPosition;
		info.loadViolation = totalLoadViolation;
		info.loadViolationContribution = loadViolationContribution;
		info.vehicle = vehicle;
		return info;
	
	}

	
	public static double costForSingleRoute(Individual individual, int period, int vehicle)
	{
		int assignedDepot;		
		int clientNode,previous;

		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
		assignedDepot = individual.problemInstance.depotAllocation[vehicle];
		ProblemInstance problemInstance = individual.problemInstance;
		if(route.isEmpty())return 0;

		double costForPV = 0;
		
		for(int i=1;i<route.size();i++)
		{
			clientNode = route.get(i);
			previous = route.get(i-1);
			costForPV +=	problemInstance.costMatrix[previous+problemInstance.depotCount][clientNode+problemInstance.depotCount];
		}

        costForPV += problemInstance.costMatrix[assignedDepot][route.get(0)+problemInstance.depotCount];
        costForPV += problemInstance.costMatrix[route.get(route.size()-1)+problemInstance.depotCount][assignedDepot];

		return costForPV;

	}
	
	public static double costForThisRoute(ProblemInstance problemInstance, ArrayList<Integer> route, int vehicle)
	{
		int assignedDepot;		
		int clientNode,previous;

		assignedDepot = problemInstance.depotAllocation[vehicle];
		if(route.isEmpty())return 0;

		double costForPV = 0;
		
		for(int i=1;i<route.size();i++)
		{
			clientNode = route.get(i);
			previous = route.get(i-1);
			costForPV +=	problemInstance.costMatrix[previous+problemInstance.depotCount][clientNode+problemInstance.depotCount];
		}

        costForPV += problemInstance.costMatrix[assignedDepot][route.get(0)+problemInstance.depotCount];
        costForPV += problemInstance.costMatrix[route.get(route.size()-1)+problemInstance.depotCount][assignedDepot];

		return costForPV;

	}
	
	public static int closestDepot(int client)
	{
		ProblemInstance problemInstance = Individual.getProblemInstance();
		int clientIndex = problemInstance.depotCount + client;
		
		int selectedDepot=0;
		double minDistance = problemInstance.costMatrix[0][clientIndex] + problemInstance.costMatrix[clientIndex][0];
		minDistance /=2;
		
		for(int depot=1;depot<problemInstance.depotCount;depot++)
		{
			double distance = problemInstance.costMatrix[depot][clientIndex] + problemInstance.costMatrix[clientIndex][depot];
			distance /= 2;
			if(distance <= minDistance)
			{
				selectedDepot = depot;
				minDistance = distance;
			}
		}
		return selectedDepot ;
	}
	
	/**
	 * Returns K depots, sorted in ascending order with distance to customer client
	 * @param client
	 * @param k
	 * @return
	 */
	public static ArrayList<Integer> closestDepots(int client)
	{
		ProblemInstance problemInstance = Individual.getProblemInstance();
		int clientIndex = problemInstance.depotCount + client;
		
		ArrayList<Integer> selectedDepot= new ArrayList<Integer>();
		ArrayList<Double> distances = new ArrayList<Double>();
		//double minDistance = problemInstance.costMatrix[0][clientIndex] + problemInstance.costMatrix[clientIndex][0];
		//minDistance /=2;
		
		double minDistance = Double.MAX_VALUE;
		for(int depot=0;depot<problemInstance.depotCount;depot++)
		{
			double distance = problemInstance.costMatrix[depot][clientIndex] + problemInstance.costMatrix[clientIndex][depot];
			distance /= 2;
			
			int i=0;
			while(i<distances.size() && distances.get(i) <= distance) i++;
			
			distances.add(i, distance);
			selectedDepot.add(i,depot);
		}
		return selectedDepot ;
	}
}
