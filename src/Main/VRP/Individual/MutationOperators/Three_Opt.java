package Main.VRP.Individual.MutationOperators;
import java.lang.reflect.Array;
import java.util.ArrayList;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class Three_Opt {

	//mdpvrp pr10 -> 
	
	public static int apply = 0;	
	public static double totalSec=0;
	
	public static void mutateRandomRoute(Individual individual)
	{
		
		
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		int period = Utility.randomIntInclusive(problemInstance.periodCount-1);
		int vehicle = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
			
		mutateRouteBy_Three_Opt_with_first_better_move(individual, period, vehicle);
			
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}
	
	public static void onAllROute(Individual individual)
	{
	//	System.err.println("in 3 opt");
		ProblemInstance problemInstance = individual.problemInstance;
		
	//	mutateRouteBy2_Opt(individual, 0, 1);if(true)return;
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle = 0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				mutateRouteBy_Three_Opt_with_best_move(individual, period, vehicle);
			}
		}
	}
	
	/**
	 * Repeatedly applies the best 3 opt move, until it gives a lower cost route
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return
	 */
	public static boolean mutateRouteBy_Three_Opt_with_best_move(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
		boolean improved = true;
		while(improved)
		{
			route  = individual.routes.get(period).get(vehicle);
			double oldCost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
			double bestCost = oldCost;
			ArrayList<Integer> bestRoute = route;
			
			for(int i=0;i<route.size();i++) // i th node er previous edge
			{
				for(int j=i ; j<route.size();j++) // j th and k th node er porer edge
				{
					for(int k=j+1;k<route.size();k++)
					{
						double newCost;
						int selected=-1;
						combinations.add(combination1(i, j, k, route));
						combinations.add(combination2(i, j, k, route));
						combinations.add(combination3(i, j, k, route));
						combinations.add(combination4(i, j, k, route));
						
						//System.out.println("\ncurrent "+" cost : "+cost);
						for(int p=0;p<4;p++)
						{
							ArrayList<Integer> newRoute = combinations.get(p);
							newCost = RouteUtilities.costForThisRoute(problemInstance, newRoute, vehicle);
							//System.out.println("comb "+ p+" cost : "+costThis);
							if(newCost<bestCost)
							{
								bestCost=newCost;
								bestRoute = newRoute;
							}
							else if(newCost==bestCost)
							{
			            	   int coin = Utility.randomIntInclusive(1);
			            	   if(coin==1) bestRoute = newRoute;	
							}
						}						
					}
				}
			}
			
			if(bestRoute != route)
			{
				route.clear();
				route.addAll(bestRoute);
			}
			
			if(bestCost == oldCost) improved=false;			
		}		
		return true;
	}


	/**
	 * Improves the selected route by repeated 3 opt moves(the first 3 opt moves that gives a better route)
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRouteBy_Three_Opt_with_first_better_move(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
		boolean improved = true;
		while(improved)
		{
			improved = false;
			route  = individual.routes.get(period).get(vehicle);
			double cost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
		//	System.out.println("\n"+route.toString());
			
			for(int i=0;i<route.size();i++) // i th node er previous edge
			{
				if(improved)break;
				for(int j=i ; j<route.size();j++) // j th and k th node er porer edge
				{
					if(improved)break;
					for(int k=j+1;k<route.size();k++)
					{
						double min=cost,costThis;
						int selected=-1;
						combinations.add(combination1(i, j, k, route));
						combinations.add(combination2(i, j, k, route));
						combinations.add(combination3(i, j, k, route));
						combinations.add(combination4(i, j, k, route));
						
						//System.out.println("\ncurrent "+" cost : "+cost);
						for(int p=0;p<4;p++)
						{
							ArrayList<Integer> routeCOmb = combinations.get(p);
							costThis = RouteUtilities.costForThisRoute(problemInstance, routeCOmb, vehicle);
							//System.out.println("comb "+ p+" cost : "+costThis);
							if(costThis<min)
							{
								min = costThis;
								selected = p;
							}
						}
						if(selected != -1)
						{
							ArrayList<Integer> selectedComb = combinations.get(selected);
							//System.out.println("Selected : "+selected);
							route.clear();
							route.addAll(selectedComb);
							improved=true;
							break;
						}
						
					}
				}
			}
			//for testing only
			
		}		
		return true;
	}

	/*private static boolean update(ArrayList<Integer> route, double costOfCurrentRoute, ArrayList<Integer> modifiedRoute) 
	{
		
	}*/
	private static ArrayList<Integer> combination1(int i, int j, int k, ArrayList<Integer> route)
	{
		ArrayList<Integer> modifiedRoute = new ArrayList<Integer>();
		
		for(int index=0;index<i;index++)
			modifiedRoute.add(route.get(index));
		
		for(int index = j+1; index <= k;index++)	
			modifiedRoute.add(route.get(index));
		
		for(int index = j; index >= i;index--)	
			modifiedRoute.add(route.get(index));
	
		for(int index = k+1; index < route.size();index++)	
			modifiedRoute.add(route.get(index));

//		System.out.printf("i: %d j: %d k: %d original -> %s \n",i,j,k,route.toString());
//		System.out.printf("i: %d j: %d k: %d Comb 1 -> %s \n",i,j,k,modifiedRoute.toString());
		return modifiedRoute;

	}
	
	private static ArrayList<Integer> combination2(int i, int j, int k, ArrayList<Integer> route)
	{
		ArrayList<Integer> modifiedRoute = new ArrayList<Integer>();
		
		for(int index=0;index<i;index++)
			modifiedRoute.add(route.get(index));
		
		for(int index=k;index>j;index-- )
			modifiedRoute.add(route.get(index));

		for(int index=i;index<=j;index++)
			modifiedRoute.add(route.get(index));

		for(int index = k+1; index < route.size();index++)	
			modifiedRoute.add(route.get(index));

//		System.out.printf("i: %d j: %d k: %d original -> %s \n",i,j,k,route.toString());
//		System.out.printf("i: %d j: %d k: %d Comb 2 -> %s \n",i,j,k,modifiedRoute.toString());
		return modifiedRoute;

	}

	private static ArrayList<Integer> combination3(int i, int j, int k, ArrayList<Integer> route)
	{
		ArrayList<Integer> modifiedRoute = new ArrayList<Integer>();
		
		for(int index=0;index<i;index++)
			modifiedRoute.add(route.get(index));
		
		for(int index=j;index>=i;index--)
			modifiedRoute.add(route.get(index));

		for(int index=k;index> j ; index--)
			modifiedRoute.add(route.get(index));

		for(int index = k+1; index < route.size();index++)	
			modifiedRoute.add(route.get(index));

		//System.out.printf("i: %d j: %d k: %d original -> %s \n",i,j,k,route.toString());
		//System.out.printf("i: %d j: %d k: %d Comb 3 -> %s \n",i,j,k,modifiedRoute.toString());
		return modifiedRoute;

	}

	private static ArrayList<Integer> combination4(int i, int j, int k, ArrayList<Integer> route)
	{
		ArrayList<Integer> modifiedRoute = new ArrayList<Integer>();
		
		for(int index=0;index<i;index++)
			modifiedRoute.add(route.get(index));
		
		for(int index = j+1;index<=k ; index++)
			modifiedRoute.add(route.get(index));
				
		for(int index = i;index<=j;index++)
			modifiedRoute.add(route.get(index));
				
		for(int index = k+1; index < route.size();index++)	
			modifiedRoute.add(route.get(index));

	//	System.out.printf("i: %d j: %d k: %d original -> %s \n",i,j,k,route.toString());
	//	System.out.printf("i: %d j: %d k: %d Comb 4 -> %s \n",i,j,k,modifiedRoute.toString());
		return modifiedRoute;

	}
}
