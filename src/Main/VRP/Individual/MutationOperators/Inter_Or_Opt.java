package Main.VRP.Individual.MutationOperators;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import javax.lang.model.element.NestingKind;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class Inter_Or_Opt {

	public static int apply = 0;	
	public static double totalSec=0;

	public static void mutate(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		int period = Utility.randomIntInclusive(problemInstance.periodCount-1);
		int vehicle1 = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
		
		int vehicle2= vehicle1;
		while(vehicle2 == vehicle1) vehicle2 = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
			
		
/*		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double cb = individual.costWithPenalty;
*/		
		boolean success = mutateRouteBy_Or_Opt_withFirstBetterMove(individual, period, vehicle1,vehicle2,loadPenaltyFactor,routeTimePenaltyFactor);
		
/*		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double ca = individual.costWithPenalty;
*/		
		
/*		System.out.format("Cost before: %f, After:%f\n",cb,ca);
		if(success) System.out.println("Successful");
		else System.out.println("Fail");
*/		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}

	

	public static void mutateSpecificClientByCheckingHOS(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		
		int period=-1,client=-1;
	

		if(print)
		{
			System.out.print("HOS BEFORE: ");
			individual.printHOS_PC();
		}
		while(true)
		{
			period = Utility.randomIntInclusive(problemInstance.periodCount-1);
			client = Utility.randomIntExclusive(problemInstance.customerCount);
			
			while (individual.periodAssignment[period][client]==false)
				client = Utility.randomIntExclusive(problemInstance.customerCount);
			
			if(print)System.out.format("Seleceted PC : %d, %d\n",period,client);
			
			if(individual.isInHallOfShamePC(period, client)==false) break;
			
			if(print)System.err.println("IN HOS, Retry");

			//new Scanner(System.in).nextLine();
		}
		
		if(print)
		{	TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			double cb = individual.costWithPenalty;
			System.out.format("Cost before: %f\n",cb);
		}
		
		boolean success = mutateClientRouteBy_Or_Opt_withFirstBetterMove(individual, period, client,loadPenaltyFactor,routeTimePenaltyFactor);

		if(!success)
		{
			individual.addToHOS_PC(period, client);
		}

		if(print)
		{
			TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			double ca = individual.costWithPenalty;
			System.out.format("Cost After:%f\n",ca);
			if(success) System.out.println("Successful");
			else System.out.println("Fail");

			System.out.print("HOS AFTER: ");
			individual.printHOS_PC();

		}
		
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		
		//System.exit(1);
	}


	
	public static void mutateSpecificClient(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		
		
		int period = Utility.randomIntInclusive(problemInstance.periodCount-1);

		int client = Utility.randomIntExclusive(problemInstance.customerCount);
		
		while (individual.periodAssignment[period][client]==false)
			client = Utility.randomIntExclusive(problemInstance.customerCount);
		
		
/*		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double cb = individual.costWithPenalty;
*/		
		boolean success = mutateClientRouteBy_Or_Opt_withFirstBetterMove(individual, period, client,loadPenaltyFactor,routeTimePenaltyFactor);
		
/*		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double ca = individual.costWithPenalty;
*/		
		
/*		System.out.format("Cost before: %f, After:%f\n",cb,ca);
		if(success) System.out.println("Successful");
		else System.out.println("Fail");
*/		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}

	public static boolean mutateClientRouteBy_Or_Opt_withFirstBetterMove(Individual individual, int period, int client, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		boolean print=false;
		
		if(print) System.out.println("CLient: "+client);
		
		int vehicle1 =  RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);				
		int route1Size = individual.routes.get(period).get(vehicle1).size();
		
		//random vehicle choice
		int vehicle2 = vehicle1;
		
		while(vehicle2 == vehicle1) vehicle2 = Utility.randomIntExclusive(problemInstance.vehicleCount);
		
		int route2Size = individual.routes.get(period).get(vehicle2).size();

		
		int clientPostion = individual.routes.get(period).get(vehicle1).indexOf(client);
		if(print)System.out.println("Client Position: "+clientPostion);
	
		
		double oldCost1 = individual.calculateCostWithPenalty(period, vehicle1, loadPenaltyFactor, routeTimePenaltyFactor);
		double oldCost2 = individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor);

		
		if(print)
		{
			System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
			System.out.println("Old cost1: "+oldCost1);
			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
			System.out.println("Old cost2: "+oldCost2);			
		}

		for(int k=3;k>=1;k--)
		{
			if(route1Size<k)continue;
						
			if(print) System.out.println("k: "+k);			
						

			for(int i=Math.max(0,clientPostion-k+1); i <=clientPostion && i+k-1<route1Size; i++)
			{

				ArrayList<Integer> modifiedRoute1 = individual.routes.get(period).get(vehicle1);
				ArrayList<Integer> savedRoute1 = new ArrayList<Integer>(modifiedRoute1);
				
				ArrayList<Integer> relocatedChain = new ArrayList<Integer>();
				
				for(int tmp=0;tmp<k;tmp++)
				{
					relocatedChain.add(0,modifiedRoute1.remove(i));
				}
				
				double newCost1 = individual.calculateCostWithPenalty(period, vehicle1, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);

				if(print) 
				{
					System.out.println("Removed "+relocatedChain+" from "+i+" yielding "+modifiedRoute1.toString()+"\n New Cost1: "+newCost1);
				}

				ArrayList<Integer> modifiedRoute2 = individual.routes.get(period).get(vehicle2);
				ArrayList<Integer> savedRoute2 = new ArrayList<>(modifiedRoute2);
				
				
				for(int j=0;j<=route2Size;j++)
				{
					modifiedRoute2.addAll(j,relocatedChain);
					double newCost2 = individual.calculateCostWithPenalty(period, vehicle2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
					
					if(print) System.out.println("Inserted in "+j+": "+modifiedRoute2.toString()+"\n New Cost2: "+newCost2);

					
					if((oldCost1+oldCost2-newCost1-newCost2)>0)
					{
						if(print)System.out.println("FOUND IMPROVEMENT");
						return true;
					}
					
					modifiedRoute2.clear();
					modifiedRoute2.addAll(savedRoute2);
					
					Collections.reverse(relocatedChain);
					modifiedRoute2.addAll(j,relocatedChain);
					newCost2 = individual.calculateCostWithPenalty(period, vehicle2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
					
					if(print) System.out.println("Inserted in "+j+"(Reveresed): "+modifiedRoute2.toString()+"\n New Cost2: "+newCost2);

					
					if((oldCost1+oldCost2-newCost1-newCost2)>0)
					{
						if(print)System.out.println("FOUND IMPROVEMENT");
						return true;
					}
					
					modifiedRoute2.clear();
					modifiedRoute2.addAll(savedRoute2);
					Collections.reverse(relocatedChain);

				}
				
				
				modifiedRoute1.clear();
				modifiedRoute1.addAll(savedRoute1);
			}
		}

		if(print)
		{
			System.out.println("After Inter Route Or-Opt\nOriginal Route1: "+individual.routes.get(period).get(vehicle1).toString());
			System.out.println("Old cost1: "+oldCost1);
			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
			System.out.println("Old cost2: "+oldCost2);			
		}

		return false;
	}
	

	/**
	 * 
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRouteBy_Or_Opt_withFirstBetterMove(Individual individual, int period, int vehicle1, int vehicle2, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		
		int coin = Utility.randomIntInclusive(1);
		if(coin == 1) Collections.reverse(individual.routes.get(period).get(vehicle1));		

		int route1Size = individual.routes.get(period).get(vehicle1).size();
		int route2Size = individual.routes.get(period).get(vehicle2).size();

		double oldCost1 = individual.calculateCostWithPenalty(period, vehicle1, loadPenaltyFactor, routeTimePenaltyFactor);
		double oldCost2 = individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor);

		
		if(print)
		{
			System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
			System.out.println("Old cost1: "+oldCost1);
			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
			System.out.println("Old cost2: "+oldCost2);			
		}

		for(int k=3;k>=1;k--)
		{
			if(route1Size<k)continue;
						
			if(print) System.out.println("k: "+k);			
						
			for(int i=0; i+k-1 < route1Size; i++)
			{
				ArrayList<Integer> modifiedRoute1 = individual.routes.get(period).get(vehicle1);
				ArrayList<Integer> savedRoute1 = new ArrayList<Integer>(modifiedRoute1);
				
				ArrayList<Integer> relocatedChain = new ArrayList<Integer>();
				
				for(int tmp=0;tmp<k;tmp++)
				{
					relocatedChain.add(0,modifiedRoute1.remove(i));
				}
				
				double newCost1 = individual.calculateCostWithPenalty(period, vehicle1, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);

				if(print) System.out.println("Removed from "+i+": "+modifiedRoute1.toString()+"\n New Cost1: "+newCost1);

				ArrayList<Integer> modifiedRoute2 = individual.routes.get(period).get(vehicle2);
				ArrayList<Integer> savedRoute2 = new ArrayList<>(modifiedRoute2);
				
				
				for(int j=0;j<=route2Size;j++)
				{
					modifiedRoute2.addAll(j,relocatedChain);
					double newCost2 = individual.calculateCostWithPenalty(period, vehicle2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
					
					if(print) System.out.println("Inserted in "+j+": "+modifiedRoute2.toString()+"\n New Cost2: "+newCost2);

					
					if((oldCost1+oldCost2-newCost1-newCost2)>0)
					{
						if(print)System.out.println("FOUND IMPROVEMENT");
						return true;
					}
					
					modifiedRoute2.clear();
					modifiedRoute2.addAll(savedRoute2);
					
					Collections.reverse(relocatedChain);
					modifiedRoute2.addAll(j,relocatedChain);
					newCost2 = individual.calculateCostWithPenalty(period, vehicle2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
					
					if(print) System.out.println("Inserted in "+j+"(Reveresed): "+modifiedRoute2.toString()+"\n New Cost2: "+newCost2);

					
					if((oldCost1+oldCost2-newCost1-newCost2)>0)
					{
						if(print)System.out.println("FOUND IMPROVEMENT");
						return true;
					}
					
					modifiedRoute2.clear();
					modifiedRoute2.addAll(savedRoute2);
					Collections.reverse(relocatedChain);

				}
				
				
				modifiedRoute1.clear();
				modifiedRoute1.addAll(savedRoute1);
			}
		}

		if(print)
		{
			System.out.println("After Inter Route Or-Opt\nOriginal Route1: "+individual.routes.get(period).get(vehicle1).toString());
			System.out.println("Old cost1: "+oldCost1);
			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
			System.out.println("Old cost2: "+oldCost2);			
		}
		return false;
	}
	

}
