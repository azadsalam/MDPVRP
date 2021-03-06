package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import javax.rmi.CORBA.Tie;

import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.BasicGeneticAlgorithm;
import Main.VRP.GeneticAlgorithm.BasicSimulatedAnnealing;
import Main.VRP.GeneticAlgorithm.GeneticAlgorithm;
import Main.VRP.GeneticAlgorithm.Scheme6;
import Main.VRP.GeneticAlgorithm.Scheme6_With_Binary_Tournament;
import Main.VRP.GeneticAlgorithm.Scheme6_dynamic_penalty_factor;
import Main.VRP.GeneticAlgorithm.Scheme6_with_crossover_only;
import Main.VRP.GeneticAlgorithm.Scheme6_with_normal_mutation;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.GeneticAlgorithm.bulkInitialization;
import Main.VRP.GeneticAlgorithm.TestAlgo.MutationTest;
import Main.VRP.GeneticAlgorithm.TestAlgo.TestAlgo;
import Main.VRP.GeneticAlgorithm.TestAlgo.TestDistance;
import Main.VRP.GeneticAlgorithm.TestAlgo.Tester_Crossover;
import Main.VRP.GeneticAlgorithm.TestAlgo.Tester_Initiator;
import Main.VRP.Individual.Individual;
import Main.VRP.LocalImprovement.SimulatedAnnealing;


public class Solver 
{

	public static String errorString="";
	//public static int totalNumberofSingleRun = 1;
	public static double loadPenaltyFactor = 1000;
	public static double routeTimePenaltyFactor = 1000;	
	//public static boolean singleRun = true;

	public static int HallOfShamePCSize=-1;
	
	int aggregate_report_run_size=1;
	public static boolean writeToExcel=false;
	public static boolean generateAggregatedReport=false;
	public static boolean printEveryGeneration = true;
	public static boolean printFinalSolutionToFile=true; // output the final population in file
	public static boolean showViz=false;
	public static boolean checkForInvalidity=true;
	
	public static boolean improveRouteAfterInterRouteOperation= true;
	
	public static boolean gatherCrossoverStat=false;
	
	
	public static String[] instanceFiles={"benchmark/MDPVRP/pr03"};

	
	//all mdpvrp
	/*public static String[] instanceFiles={"benchmark/MDPVRP/pr01","benchmark/MDPVRP/pr02","benchmark/MDPVRP/pr03"
		,"benchmark/MDPVRP/pr04","benchmark/MDPVRP/pr05","benchmark/MDPVRP/pr06"
		,"benchmark/MDPVRP/pr07","benchmark/MDPVRP/pr08","benchmark/MDPVRP/pr09"
		,"benchmark/MDPVRP/pr10"
		};*/

	//all mdvrp - pr01- pr10
	/*public static String[] instanceFiles={"benchmark/MDVRP/pr01","benchmark/MDVRP/pr02","benchmark/MDVRP/pr03"
		,"benchmark/MDVRP/pr04","benchmark/MDVRP/pr05","benchmark/MDVRP/pr06"
		,"benchmark/MDVRP/pr07","benchmark/MDVRP/pr08","benchmark/MDVRP/pr09"
		,"benchmark/MDVRP/pr10"
		};
	*/
	 
	//all pvrp - pr01 - pr 10
/*	public static String[] instanceFiles={"benchmark/PVRP/pr01","benchmark/PVRP/pr02","benchmark/PVRP/pr03"
		,"benchmark/PVRP/pr04","benchmark/PVRP/pr05","benchmark/PVRP/pr06"
		,"benchmark/PVRP/pr07","benchmark/PVRP/pr08","benchmark/PVRP/pr09"
		,"benchmark/PVRP/pr10"
		};*/
	
	// selected instances
	/*public static String[] instanceFiles={"benchmark/MDVRP/p11","benchmark/MDVRP/p21"
		,"benchmark/MDVRP/pr05","benchmark/MDVRP/pr10"
		,"benchmark/MDPVRP/pr05","benchmark/MDPVRP/pr06"
		,"benchmark/MDPVRP/pr08","benchmark/MDPVRP/pr10"
		};
	*/
	
	
//	public static String[] instanceFiles={"benchmark/MDPVRP/pr01","benchmark/MDPVRP/pr02","benchmark/MDPVRP/pr03"};

	
	//FOR OUTPUT TRACE //TEST ALGORITHM
	static public boolean outputTrace = false; //prints solutions cost after each interval, runs multiple times
	static public int outputTracePrintStep = 2;
	
	static public PrintWriter outputTraceWriter;
	/////////FOR WEIGHTING SCHEME
	static public int numberOfmutationOperator=10;
	static public int episodeSize = 5;
	////////////////
	static public Visualiser visualiser;
	
	public static boolean printProblemInstance= true;
	public static boolean onTest=false;
	
	//public static String singleInputFileName = "benchmark/MDPVRP/pr01";
	public static double ServivorElitistRation = 0.10;
	public static double LocalImprovementElitistRation = 0.1;
	
	//public static String weightingSchemeOutputFileName = "parameters/weighting Scheme/"+singleInputFileName.substring(singleInputFileName.indexOf('/'))+".csv";
	
	String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(Calendar.getInstance().getTime());
	//String singleOutputFileName = singleInputFileName+" "+timeStamp+"-out.txt";
	
	String reportFileName = "reports/report_"+timeStamp+".csv"; 
	
	File inputFile,outputFile;	
	Scanner input;
	PrintWriter output;
	
	public static ExportToCSV exportToCsv;
		
	//ProblemInstance problemInstance;
	public static int mutateRouteOfTwoDiefferentFailed=0;

	public static String elitistRatioFileName = "parameters/elitistRatio.txt";
	public static String weightingSchemeFileName = "parameters/weightingScheme.txt";
	//public static String elitistRatioOutputFileName = "parameters/elitistRatio/"+singleInputFileName.substring(singleInputFileName.indexOf('/'))+".csv";
	
	//String[] instanceFiles={"benchmark/PVRP/p01"};
	/*String[] instanceFiles={"benchmark/PVRP/p01","benchmark/PVRP/p13","benchmark/PVRP/p32","benchmark/PVRP/pr06","benchmark/PVRP/pr10"
			,"benchmark/MDVRP/p01","benchmark/MDVRP/p14","benchmark/MDVRP/p23","benchmark/MDVRP/pr01","benchmark/MDVRP/pr05","benchmark/MDVRP/pr10"
			,"benchmark/MDPVRP/pr03","benchmark/MDPVRP/pr07","benchmark/MDPVRP/pr10"};
	
	*/
	private static ProblemInstance problemInstance=null;
		
	/*
 	public static ProblemInstance getProblemInstance() {
		return problemInstance;
	}*/

	public ProblemInstance createProblemInstance(String inputFileName, String outputFileName)
	{
		
		try
		{
			inputFile = new File(inputFileName);
			input = new Scanner(inputFile);
			
			if(printFinalSolutionToFile)
			{
				outputFile = new File(outputFileName);
				//output = new PrintWriter(System.out);//for console output
				output = new PrintWriter(outputFile);//for file output
			}			
			
			int testCases = 1;			
			
			//exportToCsv.createCSV(10);
			
			if(inputFileName.startsWith("benchmark"))
				problemInstance = new ProblemInstance(input,output,true);
			else
			{
				testCases = input.nextInt(); 
				input.nextLine(); // escaping comment
				// FOR NOW IGNORE TEST CASES, ASSUME ONLY ONE TEST CASE
				//output.println("Test cases (Now ignored): "+ testCases);
				//output.flush();
				problemInstance = new ProblemInstance(input,output);
				if(problemInstance.periodCount==1) numberOfmutationOperator--;
			}
		}
		catch (FileNotFoundException e)
		{
			System.out.println("FILE DOESNT EXIST !! EXCEPTION!!\n");
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("EXCEPTION!!\n");
			e.printStackTrace();
		}
		return problemInstance;
	}
	
	public void solve() throws Exception 
	{
		
		
				
		long start,end;
		
		start = System.currentTimeMillis();
		
		runGA();
	
		
/*		//single run
		else //if (singleRun) 
		{
			ProblemInstance problemInstance = createProblemInstance(singleInputFileName,singleOutputFileName);

			if(printProblemInstance)
				problemInstance.print();
			
			if(showViz)
			{
				String visInputFileName = "original/"+singleInputFileName.substring(singleInputFileName.indexOf("/")+1, singleInputFileName.length()); 
				//System.out.println("Visual input file name : "+visInputFileName);
				visualiser = new Visualiser(visInputFileName,problemInstance);
			}
			GeneticAlgorithm ga;
			
			
			if(Solver.outputTrace)
			{
				File outputTraceFile = new File("OutputTrace/"+ singleInputFileName.substring(singleInputFileName.indexOf('/')+1)+"_timestamp_"+timeStamp+".csv");
				try 
				{
					outputTraceWriter = new PrintWriter(outputTraceFile);
					//outputTraceWriter.println();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			for(int i=0;i<Solver.totalNumberofSingleRun;i++)
			{
				
				if(!onTest)
					ga = new Scheme6_with_normal_mutation(problemInstance);
					//ga= new bulkInitialization(problemInstance);					
				else
					ga = new MutationTest(problemInstance);
				
				if(i==0 && outputTrace)
				{
					for(int j=outputTracePrintStep;j<=ga.getNumberOfGeeration();j+=outputTracePrintStep)
					{
						outputTraceWriter.print(j + ", ");
					}
					outputTraceWriter.println();
					outputTraceWriter.flush();
				}
				
				if(writeToExcel)
					Solver.exportToCsv.init(ga.getNumberOfGeeration()+1);	
				
				bestResult = ga.run();
				bestResultCost += bestResult.costWithPenalty;
			}
			
			if(outputTrace)
			{	
				outputTraceWriter.flush();
				outputTraceWriter.close();
			}
			
			if(writeToExcel)exportToCsv.createCSV();
		}
		*/
		//System.out.println("mutateTwoDifferentRouteBySwapping Failed : "+mutateRouteOfTwoDiefferentFailed);
		if(printFinalSolutionToFile)output.close();
		
		end= System.currentTimeMillis();
		
		long duration = (end-start) / 1000;
		long minute =  duration/ 60;
		long seconds = duration % 60;
		System.out.println("ELAPSED TIME : " + minute+ " minutes "+seconds+" seconds");
		
		
	
	}
	
	
	/**
	 * gathers the min,avg and max cost \n
	 * Assumes all individuals cost+penalty is evaluated already
	 * Also assumes actual population is in [0,populationSize)
	 * @param population
	 * @param populationSize
	 * @param generation
	 */
	public static void gatherExcelData(Individual[] population,int populationSize,int generation)
	{
		if(writeToExcel)
		{
			
			double sum=0,avg,penalty;
			double min =0xFFFFFFF;
			double max = -1;
			int feasibleCount = 0;
			
			for(int i=0; i<populationSize; i++)
			{
				sum += population[i].costWithPenalty;
				if(population[i].costWithPenalty > max) max = population[i].costWithPenalty;
				if(population[i].costWithPenalty < min) min = population[i].costWithPenalty;
				if(population[i].isFeasible) feasibleCount++;
			}
			
			avg = sum / populationSize;

		
			exportToCsv.min[generation] = min;
			exportToCsv.avg[generation] = avg;
			exportToCsv.max[generation] = max;
			exportToCsv.feasibleCount[generation] = feasibleCount;
		}
	}
	
	public void runGA() throws Exception
	{
		boolean once=false;
		File reportFile;
		PrintWriter reportOut=null;
		long start=0,end;


		if(generateAggregatedReport)
		{
			
			start = System.currentTimeMillis();	
			once = true;
			reportFile = new File(reportFileName);
			reportOut=null;
			
			try 
			{
				reportOut = new PrintWriter(reportFile);
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println(timeStamp );
			reportOut.println("Report Generation Time , "+timeStamp);
		}
		
		

		for(int instanceNo=0;instanceNo<instanceFiles.length;instanceNo++)
		{
			problemInstance = createProblemInstance(instanceFiles[instanceNo], instanceFiles[instanceNo]+"_"+timeStamp+"_out.txt");
			
			if(printProblemInstance)
				problemInstance.print();

			if(showViz)
			{
				visualiser = new Visualiser(problemInstance);
			}
			
//			if(!onTest)
			
			//Scheme6_dynamic_penalty_factor ga = new Scheme6_dynamic_penalty_factor(problemInstance);
			Scheme6_with_normal_mutation ga = new Scheme6_with_normal_mutation(problemInstance);
			//BasicGeneticAlgorithm ga = new BasicGeneticAlgorithm(problemInstance);
			//BasicSimulatedAnnealing ga = new BasicSimulatedAnnealing(problemInstance);
			//Scheme6_with_crossover_only ga = new Scheme6_with_crossover_only(problemInstance);
			//Tester_Initiator ga = new Tester_Initiator(problemInstance);
			//Tester_Crossover ga = new Tester_Crossover(problemInstance);
			
			
			if(once && generateAggregatedReport)
			{
				once=false;
				reportOut.format("Number Of Generation, Population Size, Offspring Population Size, LoadPenalty, RouteTime Penalty\n");
				reportOut.format("%d, %d, %d, %f, %f\n",ga.NUMBER_OF_GENERATION,ga.POPULATION_SIZE,ga.NUMBER_OF_OFFSPRING,loadPenaltyFactor,routeTimePenaltyFactor);
				reportOut.println();
				reportOut.println();
				reportOut.format("Instance Name, Min, Avg, Max, Feasible \n");

			}
			
			double min = 0xFFFFFF;
			double max = -1;
			double sum = 0;
			double avg;
			int feasibleCount=0;
	
			for(int i=0; i<aggregate_report_run_size; i++)
			{			

				if(writeToExcel)
				{
					exportToCsv = new ExportToCSV(instanceFiles[instanceNo]);
					Solver.exportToCsv.init(ga.getNumberOfGeeration()+1);
				}
				
				Individual sol = ga.run();
				
				
				if(writeToExcel)
					exportToCsv.createCSV();
				
				if(sol.isFeasible==true)
				{
					feasibleCount++;
				}
				sum += sol.costWithPenalty;
				if(sol.costWithPenalty>max) max = sol.costWithPenalty;
				if(sol.costWithPenalty<min) min = sol.costWithPenalty;
				
				System.out.format("%s, Run: %d -> Solution cost: %f",instanceFiles[instanceNo], i+1,sol.costWithPenalty);
				if(sol.isFeasible) System.out.println(" - Feasible");
				else System.out.println(" - Infeasible");
			}
			avg = sum/aggregate_report_run_size;
			
			if(generateAggregatedReport)
			{
				reportOut.format("%s, %f, %f, %f, %d \n",instanceFiles[instanceNo],min,avg,max,feasibleCount);
				reportOut.flush();
			}
			System.out.format("%s, %f, %f, %f, %d \n",instanceFiles[instanceNo],min,avg,max,feasibleCount);
		}
		
		if(generateAggregatedReport)
		{
			end= System.currentTimeMillis();
			
			long duration = (end-start) / 1000;
			long minute =  duration/ 60;
			long seconds = duration % 60;
			
			reportOut.println("\nELAPSED TIME : " + minute+ " minutes "+seconds+" seconds");
			reportOut.flush();
			reportOut.close();
		}
	}
	
	
}
