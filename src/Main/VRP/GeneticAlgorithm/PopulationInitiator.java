package Main.VRP.GeneticAlgorithm;
import java.util.Random;

import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.Initialise_ClosestDepotWithGreedyCut;
import Main.VRP.Individual.Initialise_ClosestDepot_GENI_GreedyCut;
import Main.VRP.Individual.Initialise_ClosestDepot_UniformCut;
import Main.VRP.Individual.Initialise_ClosestDepot_withNoLoadViolation_Greedy_cut;
import Main.VRP.Individual.Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut;
import Main.VRP.Individual.RandomHeuristicInterleavedInitialisation;
import Main.VRP.Individual.RandomInitialisation;
import Main.VRP.Individual.RandomInitialisationWithCyclicVehicleAssignment;

public class PopulationInitiator 
{
	public static void initialisePopulationRandom(Individual[] population,int populationSize,ProblemInstance problemInstance)
	{
		//out.print("Initial population : \n");
		for(int i=0; i<populationSize; i++)
		{
			population[i] = new Individual(problemInstance);
			
			RandomInitialisationWithCyclicVehicleAssignment.initialiseRandom(population[i]);

		}
	}

	public static void initialisePopulation(Individual[] population,int populationSize,ProblemInstance problemInstance)
	{
		//out.print("Initial population : \n");
		for(int i=0; i<populationSize; i++)
		{
			population[i] = new Individual(problemInstance);
			
			//Initialise_ClosestDepotWithGreedyCut.initialise(population[i]);
			
			/*if(i%2==0)
				Initialise_ClosestDepot_GENI_GreedyCut.initialise(population[i]);
			else*/
			//	Initialise_ClosestDepot_withNoLoadViolation_Greedy_cut.initiialise(population[i]);
			
			//10427 ->pr10
			//with all 3
			if(i%2 == 0)
				RandomInitialisationWithCyclicVehicleAssignment.initialiseRandom(population[i]);
			else
				Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut.initiialise(population[i]);
			
			
			//RandomHeuristicInterleavedInitialisation.initialise(population[i]);
			/* if(i%2 == 0)
					Initialise_ClosestDepot_GENI_GreedyCut.initialise(population[i]);
			 else 
					Initialise_ClosestDepot_withNoLoadViolation_Greedy_cut.initiialise(population[i]);
			*/
//			if(i%2 ==0)

		//	Initialise_ClosestDepot_withNoLoadViolation_Greedy_cut.initiialise(population[i]);
			
/*			if(i%2 == 1)
				Initialise_ClosestDepot_GENI_GreedyCut.initialise(population[i]);
			else
				Initialise_ClosestDepot_withNoLoadViolation_Greedy_cut.initiialise(population[i]);
*/
			
/*			else
				Initialise_ClosestDepot_withNoLoadViolation_Greedy_cut.initiialise(population[i]);*/
			//problemInstance.out.println("Printing individual "+ i +" : \n");
			//population[i].print();
		}
	}
}
