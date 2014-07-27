package Main.VRP.Individual.MutationOperators;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.lang.model.element.NestingKind;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class Or_Opt {

	//mdpvrp pr10 -> 10865
	
	public static void mutateRandomRoute(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		//boolean success = false;
		//do
		//{
		int period = Utility.randomIntInclusive(problemInstance.periodCount-1);
		int vehicle = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
			
			//mutateRouteBy_Or_Opt(individual, 0, 0);
		mutateRouteBy_Or_Opt_withBestMove(individual, period, vehicle);
			
			//success = mutateRouteBy2_Opt(individual,period, vehicle);
		//}while(success==false);
	}
	
	public static void onAllROute(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
	//	mutateRouteBy2_Opt(individual, 0, 1);if(true)return;
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle = 0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				mutateRouteBy_Or_Opt_withBestMove(individual, period, vehicle);
			}
		}
	}
	
	public static boolean mutateRouteBy_Or_Opt_withBestMove(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		int routeSize = individual.routes.get(period).get(vehicle).size();
		int k = 3;
		for(k=3;k>=1;k--)
		{			
			
			route = individual.routes.get(period).get(vehicle);
			ArrayList<Integer> bestRoute=route;
			double oldCost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
			double bestCost=oldCost;
			
			int startNode=0;
			for(startNode=0; startNode+k-1< routeSize; startNode++)
			{
				
				ArrayList<Integer> routeAfterCut = new ArrayList<>(route);
				ArrayList<Integer> cutPortion = new ArrayList<Integer>();
				
				for(int i=0;i<k;i++)
				{
					cutPortion.add(routeAfterCut.remove(startNode));
				}
				
				for(int insertIndex=0;insertIndex<=routeAfterCut.size();insertIndex++)
				{
					ArrayList<Integer> modifiedRoute = new ArrayList<Integer>(routeAfterCut);
					modifiedRoute.addAll(insertIndex, cutPortion);
					
					double newCost = RouteUtilities.costForThisRoute(problemInstance, modifiedRoute, vehicle);
					if(newCost<bestCost)
					{
						bestCost= newCost;
						bestRoute = modifiedRoute;
					}
					else if(newCost == bestCost)
					{
						int coin = Utility.randomIntInclusive(1);
						if(coin==1) bestRoute = modifiedRoute;
					}					
				}
			}
			
			if(bestRoute != route)
			{
				route.clear();
				route.addAll(bestRoute);
			}
		}
		
		return true;
	}

	/**
	 * 
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRouteBy_Or_Opt_withFirstBetterMove(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		int routeSize = individual.routes.get(period).get(vehicle).size();
		int k = 3;
		for(k=3;k>=1;k--)
		{
			
			int startNode=0;
			boolean improved = false;
			for(startNode=0; startNode+k-1< routeSize; startNode++)
			{
				if(improved) break;
				
				route = individual.routes.get(period).get(vehicle);
				double oldCost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
				
				ArrayList<Integer> routeAfterCut = new ArrayList<>(route);
				ArrayList<Integer> cutPortion = new ArrayList<Integer>();
				
				for(int i=0;i<k;i++)
				{
					cutPortion.add(routeAfterCut.remove(startNode));
				}
				
				/*System.out.println("Initial : "+route.toString());
				System.out.println("Cost : "+oldCost);
				System.out.println("routeAfterCut : "+routeAfterCut.toString());
				System.out.println("cut portion : "+cutPortion.toString());
				*/
				for(int insertIndex=0;insertIndex<=routeAfterCut.size();insertIndex++)
				{
					ArrayList<Integer> modifiedRoute = new ArrayList<Integer>(routeAfterCut);
					modifiedRoute.addAll(insertIndex, cutPortion);
					
					double newCost = RouteUtilities.costForThisRoute(problemInstance, modifiedRoute, vehicle);
					if(newCost<oldCost)
					{
						route.clear();
						route.addAll(modifiedRoute);
						improved=true;
						
				/*		System.out.println("Modified Route : "+modifiedRoute.toString());
						System.out.println("Cost : "+newCost);
				*/		break;
					}
				}
			}
		}
		
		return true;
	}

	
}
