package Main.VRP.Individual.Crossover;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;


public class PIX 
{
	private static  int DEPOT; 
	static ProblemInstance problemInstance;
	
	
	
	public static void crossOver(ProblemInstance pi,Individual parent1,Individual parent2,Individual child)
	{
		problemInstance = pi;
		DEPOT = problemInstance.customerCount;
			
		UniformCrossoverPeriodAssigment.uniformCrossoverForPeriodAssignment(child,parent1, parent2,problemInstance);	
		
		reAlignParentRoutes(parent1, parent2);
		
		applyPIX(child, parent1, parent2);
		
		//update cost and penalty
		child.calculateCostAndPenalty();
//		if(Solver.gatherCrossoverStat) CrossoverStatistics.gatherData(parent1, parent2, child);

	}
	
	
	
	public static void reAlignParentRoutes(Individual parent1, Individual parent2)
	{
		//for each depot re assign similar routes to same serial number vehicle
		
		boolean printn=false;

		for(int period=0;period<problemInstance.periodCount;period++)
		{			 
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				if(printn)System.out.println("Period"+period+" Depot: "+depot);
				
				ArrayList<Integer> vehiclesUnderThisDepot = problemInstance.vehiclesUnderThisDepot.get(depot);
				int vehicleUnderThisDepotCount = vehiclesUnderThisDepot.size();
				
				
				if(printn)
				{	
					System.out.println("Before\nParent1");
					for(int j=0;j<vehicleUnderThisDepotCount;j++)
					{
						int vehicle = vehiclesUnderThisDepot.get(j);
						ArrayList<Integer> route = parent1.routes.get(period).get(vehicle);
						System.out.println("Route "+vehicle+ " : "+route);
					}
					
					System.out.println("Parent2");
					for(int j=0;j<vehicleUnderThisDepotCount;j++)
					{
						int vehicle = vehiclesUnderThisDepot.get(j);
						ArrayList<Integer> route = parent2.routes.get(period).get(vehicle);
						System.out.println("Route "+vehicle+ " : "+route);
					}
					System.out.println();					
					
					System.out.println();						System.out.println();						System.out.println();
				
				}
				for(int i=0;i<vehicleUnderThisDepotCount-1;i++)
				{
					int selectedVehicle=-1;
					int maxMatch = -1;
					
					int vehicle1 = vehiclesUnderThisDepot.get(i);
					ArrayList<Integer> route1 = parent1.routes.get(period).get(vehicle1);
					int size1 = route1.size();
					
					if(printn)
					{	
						System.out.println("Vehicle1: "+vehicle1);
						System.out.println("Route1: "+route1);
					}
					
	
					for(int j=i;j<vehicleUnderThisDepotCount;j++)
					{
						int vehicle2 = vehiclesUnderThisDepot.get(j);
						ArrayList<Integer> route2 = parent2.routes.get(period).get(vehicle2);
						int size2 = route2.size();
						
						HashSet<Integer> union = new HashSet<>();
						union.addAll(route1);
						union.addAll(route2);
						
						int common = (size1+size2)-union.size();
						if(common>maxMatch)
						{
							maxMatch=common;
							selectedVehicle = vehicle2;
						}
						
						if(printn)
						{	
							System.out.println("Vehicle2: "+vehicle2);
							System.out.println("Route2: "+route2);
							System.out.println("Matching: "+common);
							System.out.println();
						}
					
							
					}
					
					if(printn)
					{
						System.out.println("Selected Vehicle: "+selectedVehicle);
						System.out.println();
						
						if(i==selectedVehicle)
						{
							System.out.println("\n\nafter\nParent1");
							for(int j=0;j<vehicleUnderThisDepotCount;j++)
							{
								int vehicle = vehiclesUnderThisDepot.get(j);
								ArrayList<Integer> route = parent1.routes.get(period).get(vehicle);
								System.out.println("Route "+vehicle+ " : "+route);
							}
							
							System.out.println("Parent2");
							for(int j=0;j<vehicleUnderThisDepotCount;j++)
							{
								int vehicle = vehiclesUnderThisDepot.get(j);
								ArrayList<Integer> route = parent2.routes.get(period).get(vehicle);
								System.out.println("Route "+vehicle+ " : "+route);
							}
							System.out.println();					
							
							System.out.println();						System.out.println();						System.out.println();
						}
					}
						
					//swap the route in i with route selectedVehicle in parent 2
					if(i==selectedVehicle) continue;
					
					ArrayList<Integer> route21 = parent2.routes.get(period).get(vehiclesUnderThisDepot.get(i));
					ArrayList<Integer> route22 = parent2.routes.get(period).get(selectedVehicle);
					
					ArrayList<Integer> temp = new ArrayList<Integer>(route21);
					
					route21.clear();
					route21.addAll(route22);
					
					route22.clear();
					route22.addAll(temp);
					
					if(printn)
					{	
						System.out.println("\n\nafter\nParent1");
						for(int j=0;j<vehicleUnderThisDepotCount;j++)
						{
							int vehicle = vehiclesUnderThisDepot.get(j);
							ArrayList<Integer> route = parent1.routes.get(period).get(vehicle);
							System.out.println("Route "+vehicle+ " : "+route);
						}
						
						System.out.println("Parent2");
						for(int j=0;j<vehicleUnderThisDepotCount;j++)
						{
							int vehicle = vehiclesUnderThisDepot.get(j);
							ArrayList<Integer> route = parent2.routes.get(period).get(vehicle);
							System.out.println("Route "+vehicle+ " : "+route);
						}
						System.out.println();											
						System.out.println();						
						System.out.println();						
						System.out.println();
					}
				
				}
			}
		}
		
	}
	private static void applyPIX(Individual child, Individual parent1, Individual parent2)
	{
		TotalCostCalculator.calculateCost(parent1, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		TotalCostCalculator.calculateCost(parent2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);

		double cost1 = parent1.costWithPenalty;
		double cost2 = parent2.costWithPenalty;
		
		if(cost1>cost2)
		{
			Individual temp = parent1;
			parent1 = parent2;
			parent2 = temp;
		}
		
		int coin;
		boolean print=false;
		for(int period=0;period<problemInstance.periodCount;period++)
		{			 
			//should make this inclusive
			int r1 =  Utility.randomIntExclusive(problemInstance.vehicleCount );
			int r2 =  Utility.randomIntExclusive(problemInstance.vehicleCount - r1 );			
			int r3 = (problemInstance.vehicleCount - r1 - r2 > 0) ? problemInstance.vehicleCount - r1 - r2 : 0 ;
			
			int arr[] = {r1,r2,r3};			
			Arrays.sort(arr);
			r1 = arr[2];
			r2 = arr[1];
			r3 = arr[0];
			if(print)System.out.println(r1+" "+r2+" "+r3);
			
			
			boolean clientsMap[] = new boolean[problemInstance.customerCount];
			boolean vehiclesMap[] = new boolean[problemInstance.vehicleCount];
			
			ArrayList<Integer> p1 = new ArrayList<>();
			ArrayList<Integer> p2 = new ArrayList<>();
			ArrayList<Integer> p3 = new ArrayList<>();
			
			int taken = 0;
			while(taken<r1)
			{
				int rv = Utility.randomIntExclusive(problemInstance.vehicleCount);
				if(vehiclesMap[rv]) continue;
				vehiclesMap[rv] = true;
				p1.add(rv);
				taken++;
			}
			
			taken = 0;
			while(taken<r2)
			{
				int rv = Utility.randomIntExclusive(problemInstance.vehicleCount);
				if(vehiclesMap[rv]) continue;
				vehiclesMap[rv] = true;
				p2.add(rv);
				taken++;
			}
			
			taken = 0;
			while(taken<r3)
			{
				int rv = Utility.randomIntExclusive(problemInstance.vehicleCount);
				if(vehiclesMap[rv]) continue;
				vehiclesMap[rv] = true;
				p3.add(rv);
				taken++;
			}
			
			if(print)
			{
				System.out.println("Parent1 : "+ p1.toString());
				System.out.println("Common : "+ p2.toString());
				System.out.println("Parent2 : "+ p3.toString());
			}
			
			//if(true) return;			
			//inherit routes from parent 1 completely ,, r1 routes, listed in p1		
			for(int i=0;i<p1.size();i++)
			{
				int vehicle = p1.get(i);				
				ArrayList<Integer> parentRoute = parent1.routes.get(period).get(vehicle);
				ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);
				
				for(int j=0;j<parentRoute.size();j++)
				{
					int client = parentRoute.get(j);
					if(child.periodAssignment[period][client]==false || clientsMap[client]==true) continue;
					
					childRoute.add(client);
					clientsMap[client]=true;
				}
				
				if(print)
				{
					System.out.println("Parent1 Route: "+vehicle+" : "+parentRoute);
					System.out.println("Child Route (complete copy): "+childRoute);
				}
			}
			
			//inherit routes from parent 1 partially , r2 routes, listed in p2 ,  partially		
			for(int i=0;i<p2.size();i++)
			{
				int vehicle = p2.get(i);				
				ArrayList<Integer> parentRoute = parent1.routes.get(period).get(vehicle);
				ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);
				
				if(parentRoute.size()==0)return;
				int lt = Utility.randomIntExclusive(parentRoute.size());
				int rt = Utility.randomIntExclusive(parentRoute.size());
				
				if(rt<lt)
				{
					int tmp = rt;
					rt = lt;
					lt = tmp;
				}
				
				for(int j=lt;j<=rt;j++)
				{
					int client = parentRoute.get(j);
					if(child.periodAssignment[period][client]==false || clientsMap[client]==true) continue;
					
					childRoute.add(client);
					clientsMap[client]=true;
				}
				
				if(print)
				{
					System.out.format("Left: %d, Right: %d\n",lt,rt);
					System.out.println("Parent1 Route: "+vehicle+" : "+parentRoute);
					System.out.println("Child Route (partial inheritence): "+childRoute);
				}
				
			}

			//inherit routes from parent 2 completely ,, r3 routes, listed in p3	
			//shuffle p3
			//Collections.shuffle(p3); // already shuffled, by the way it was created
			for(int i=0;i<p3.size();i++)
			{
				int vehicle = p3.get(i);				
				ArrayList<Integer> parentRoute = parent2.routes.get(period).get(vehicle);
				ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);
				
				for(int j=0;j<parentRoute.size();j++)
				{
					int client = parentRoute.get(j);
					if(child.periodAssignment[period][client]==false || clientsMap[client]==true) continue;
					
					childRoute.add(client);
					clientsMap[client]=true;
				}
				
				if(print)
				{
					System.out.println("Parent2 Route: "+vehicle+" : "+parentRoute);
					System.out.println("Child Route(complete copy): "+childRoute);
				}
			}
			//System.out.println("HERE");
			
			//inherit routes from parent 2 partially ,, r2 routes, listed in p2	
			
			for(int i=0;i<p2.size();i++)
			{
				int vehicle = p2.get(i);				
				ArrayList<Integer> parentRoute = parent2.routes.get(period).get(vehicle);
				ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);
				
				for(int j=0;j<parentRoute.size();j++)
				{
					int client = parentRoute.get(j);
					if(child.periodAssignment[period][client]==false || clientsMap[client]==true) continue;
					
					childRoute.add(client);
					clientsMap[client]=true;
				}
				
				if(print)
				{
					System.out.println("Parent2 Route: "+vehicle+" : "+parentRoute);
					System.out.println("Child Route(partial copy): "+childRoute);
				}
			}

			
			// fill missing visits
			for(int i=0;i<p3.size();i++)
			{
				int vehicle = p3.get(i);				
				ArrayList<Integer> parent1Route = parent1.routes.get(period).get(vehicle);
				ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);
				
				for(int j=0;j<parent1Route.size();j++)
				{
					int client = parent1Route.get(j);
					if(child.periodAssignment[period][client]==false
							|| clientsMap[client]==true) continue;
					
					childRoute.add(client);
					clientsMap[client]=true;
				}
				
				if(print)
				{
					System.out.println("Parent1 Route: "+vehicle+" : "+parent1Route);
					System.out.println("Child Route(missing filling): "+childRoute);
				}

			}

			for(int i=0;i<p2.size();i++)
			{
				int vehicle = p2.get(i);				
				ArrayList<Integer> parent1Route = parent1.routes.get(period).get(vehicle);
				ArrayList<Integer> parent2Route = parent2.routes.get(period).get(vehicle);

				ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);
				
				for(int j=0;j<parent1Route.size();j++)
				{
					int client = parent1Route.get(j);
					if(child.periodAssignment[period][client]==false
							|| clientsMap[client]==true) continue;
					
					childRoute.add(client);
					clientsMap[client]=true;
				}
				
				for(int j=0;j<parent2Route.size();j++)
				{
					int client = parent2Route.get(j);
					if(child.periodAssignment[period][client]==false
							|| clientsMap[client]==true) continue;
					
					childRoute.add(client);
					clientsMap[client]=true;
				}
				
				if(print)
				{
					System.out.println("Parent1 Route: "+vehicle+" : "+parent1Route);
					System.out.println("Parent2 Route: "+parent2Route);
					System.out.println("Child Route: "+childRoute);
				}

			}
			
			for(int i=0;i<p1.size();i++)
			{
				int vehicle = p1.get(i);				
				ArrayList<Integer> parentRoute = parent2.routes.get(period).get(vehicle);
				ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);
				
				for(int j=0;j<parentRoute.size();j++)
				{
					int client = parentRoute.get(j);
					if(child.periodAssignment[period][client]==false
							|| clientsMap[client]==true) continue;
					
					childRoute.add(client);
					clientsMap[client]=true;
				}
				
				if(print)
				{
					System.out.println("Parent2 Route: "+vehicle+" : "+parentRoute);
					System.out.println("Child Route(missing filling): "+childRoute);
				}
			}

		}
		


	
		
	}
	
	
	
}
