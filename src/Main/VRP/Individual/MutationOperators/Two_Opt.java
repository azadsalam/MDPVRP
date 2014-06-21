package Main.VRP.Individual.MutationOperators;
import java.lang.reflect.Array;
import java.util.ArrayList;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class Two_Opt {

	public static void mutateRandomRoute(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		//boolean success = false;
		//do
		//{
			int period = Utility.randomIntInclusive(problemInstance.periodCount-1);
			int vehicle = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
			mutateRouteBy2_Opt(individual, period, vehicle);
			
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
				mutateRouteBy2_Opt(individual, period, vehicle);
			}
		}
	}
	
	/**
	 * 
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRouteBy2_Opt(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		
		boolean retry = true;
		boolean exit = false;
		while(retry) 
		{
			exit = false;
			route = individual.routes.get(period).get(vehicle);
			double best_distance = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
			
			
			for (int i = 0; i < route.size(); i++) 
			{
	    	   if(exit) break;
	           for (int k = i + 1; k < route.size(); k++) 
	           {
	               ArrayList<Integer> new_route = twoOptSwap(route, i, k);
	               double new_distance = RouteUtilities.costForThisRoute(problemInstance, new_route, vehicle);
	               //System.out.println(new_distance);
	               if (new_distance < best_distance) 
	               {
	            	   //update individual
	                   //existing_route = new_route
	            	  // System.out.println("Previous ROute : "+route.toString()+" Cost : "+best_distance);
	            	   
	            	   ArrayList<Integer> updatedRoute = individual.routes.get(period).get(vehicle);
	            	   
	            	   updatedRoute.clear();
	            	   
	            	   for(int cur=0;cur<new_route.size();cur++)
	            	   {
	            		   updatedRoute.add(new_route.get(cur));
	            		   
	            	   }
	            	   
	            	   //System.out.println(updatedRoute.toString());
	            	   
	            	   //System.out.println("Updated ROute : "+updatedRoute.toString()+" Cost : "+new_distance);
	       			
	                   exit=true;
	                   break;
	               }
	           }
	       }
			if(!exit)
			retry = false;
		 }
		return true;
	}

	public static ArrayList<Integer> twoOptSwap(ArrayList<Integer> route, int i, int k) 
	{
		ArrayList<Integer> new_route = new ArrayList<>();

//       1. take route[0] to route[i-1] and add them in order to new_route
		for(int cur=0;cur <= i-1;cur++)
		{
			new_route.add(route.get(cur));
		}
		
		//2. take route[i] to route[k] and add them in reverse order to new_route
		for(int cur=k;cur >= i;cur--)
		{
			new_route.add(route.get(cur));
		}
		
	//       3. take route[k+1] to end and add them in order to new_route
		for(int cur=k+1;cur < route.size();cur++)
		{
			new_route.add(route.get(cur));
		}
	       return new_route;
	}
	
}
