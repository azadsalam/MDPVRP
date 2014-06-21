package Main.VRP.GeneticAlgorithm;
import java.util.Random;

import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.Initialise_ClosestDepot_GreedyCut;
import Main.VRP.Individual.Initialise_ClosestDepot_UniformCut;
import Main.VRP.Individual.Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut;
import Main.VRP.Individual.RandomInitialisation;

public class PopulationInitiator 
{
	public static void initialisePopulation(Individual[] population,int populationSize,ProblemInstance problemInstance)
	{
		//out.print("Initial population : \n");
		for(int i=0; i<populationSize; i++)
		{
			population[i] = new Individual(problemInstance);
			
			if(i%3 == 0)
				RandomInitialisation.initialiseRandom(population[i]);
			else if(i%3 == 1)
				Initialise_ClosestDepot_UniformCut.initiialise(population[i]);
			else 
				//Initialise_ClosestDepot_GreedyCut.initialise(population[i]);
				Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut.initiialise(population[i]);
			
			//problemInstance.out.println("Printing individual "+ i +" : \n");
			//population[i].print();
		}
	}
}
