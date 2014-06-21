package Main.VRP.Individual;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;

public class RandomInitialisation 
{
	
	public static void initialiseRandom(Individual individual) 
	{
		// NOW INITIALISE WITH VALUES
		//initialize period assignment

		int freq,allocated,random;
		//Randomly allocate period to clients equal to their frequencies

		ProblemInstance problemInstance = individual.problemInstance;
		InitialisePeriodAssigmentUniformly.initialise(individual);
		//Assign customer to route
		for(int clientNo=0;clientNo<problemInstance.customerCount;clientNo++)
		{
			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				if(individual.periodAssignment[period][clientNo]==false)continue;

				int vehicle = Utility.randomIntInclusive(problemInstance.vehicleCount-1);				
				individual.routes.get(period).get(vehicle).add(clientNo);
			}
		}


		//randomize the pattern for each route
		//adjacent swap
		int coin;
		int ran;

		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			ran = Utility.randomIntInclusive(3);

			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				ArrayList<Integer> route = individual.routes.get(period).get(vehicle);

				if(ran==0 || ran==1)   // knuth shuffle
				{
					for( int i = route.size()-1;i>=1;i--)
				    {
						int j = Utility.randomIntInclusive(0, i);
						int tmp = route.get(j);
						route.set(j, route.get(i));
						route.set(i, tmp);
				    } 
				}
				else if(ran==2)
				{
					for(int i=1;i<route.size();i++)
					{
						coin = Utility.randomIntInclusive(1);
						if(coin==1)
						{
							int tmp = route.get(i-1);
							route.set(i-1, route.get(i));
							route.set(i, tmp);
						}
					}
				}
				else
				{
					for(int i=route.size()-1;i>0;i--)
					{
						coin = Utility.randomIntInclusive(1);
						if(coin==1)
						{
							int tmp = route.get(i-1);
							route.set(i-1, route.get(i));
							route.set(i, tmp);
						}
					}
				}
			}
		}

		individual.calculateCostAndPenalty();
	}


}
