package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import javax.rmi.CORBA.Tie;

import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.GeneticAlgorithm;
import Main.VRP.GeneticAlgorithm.Scheme6;
import Main.VRP.GeneticAlgorithm.Scheme6_With_Binary_Tournament;
import Main.VRP.GeneticAlgorithm.Scheme6_with_normal_mutation;
import Main.VRP.GeneticAlgorithm.Scheme7_with_weighted_mutation_withFaltuCrossover;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.GeneticAlgorithm.bulkInitialization;
import Main.VRP.GeneticAlgorithm.TestAlgo.MutationTest;
import Main.VRP.GeneticAlgorithm.TestAlgo.TestAlgo;
import Main.VRP.GeneticAlgorithm.TestAlgo.TestDistance;
import Main.VRP.GeneticAlgorithm.TestAlgo.Tester_Initiator;
import Main.VRP.Individual.Individual;


public class Solver 
{
	
	
	public static int totalNumberofSingleRun = 1;
	public static double loadPenaltyFactor = 500;
	public static double routeTimePenaltyFactor = 500;	
	public static boolean singleRun = true;
	public static boolean writeToExcel=true;
	public static String[] instanceFiles={"benchmark/PVRP/pr02"};

	
	//FOR OUTPUT TRACE //TEST ALGORITHM
	static public boolean outputTrace = true;
	int aggregate_report_run_size=3;
	public static boolean printFinalSolutionToFile=false; // output the final population in file
	static public PrintWriter outputTraceWriter;
	/////////FOR WEIGHTING SCHEME
	static public int numberOfmutationOperator=10;
	static public int episodeSize = 5;
	////////////////
	static public Visualiser visualiser;
	public static boolean showViz=false;
	public static boolean printProblemInstance= false;
	public static boolean onTest=false;
	
	public static String singleInputFileName = "benchmark/MDPVRP/pr01";
	public static double ServivorElitistRation = 0.10;
	public static double LocalImprovementElitistRation = 0.10;
	
	public static String weightingSchemeOutputFileName = "parameters/weighting Scheme/"+singleInputFileName.substring(singleInputFileName.indexOf('/'))+".csv";
	
	String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(Calendar.getInstance().getTime());
	String singleOutputFileName = singleInputFileName+" "+timeStamp+"-out.txt";
	
	
	String reportFileName = "reports/report_"+timeStamp+".csv"; 
	
	File inputFile,outputFile;	
	Scanner input;
	PrintWriter output;
	
	public static ExportToCSV exportToCsv;
		
	//ProblemInstance problemInstance;
	
	
	public static boolean generateAggregatedReport;
	
	public static int mutateRouteOfTwoDiefferentFailed=0;

	public static double weightingSchemeAlpha=0.7;
	public static String elitistRatioFileName = "parameters/elitistRatio.txt";
	public static String weightingSchemeFileName = "parameters/weightingScheme.txt";
	public static String elitistRatioOutputFileName = "parameters/elitistRatio/"+singleInputFileName.substring(singleInputFileName.indexOf('/'))+".csv";
	
	//String[] instanceFiles={"benchmark/PVRP/p01"};
	/*String[] instanceFiles={"benchmark/PVRP/p01","benchmark/PVRP/p13","benchmark/PVRP/p32","benchmark/PVRP/pr06","benchmark/PVRP/pr10"
			,"benchmark/MDVRP/p01","benchmark/MDVRP/p14","benchmark/MDVRP/p23","benchmark/MDVRP/pr01","benchmark/MDVRP/pr05","benchmark/MDVRP/pr10"
			,"benchmark/MDPVRP/pr03","benchmark/MDPVRP/pr07","benchmark/MDPVRP/pr10"};
	
	*/
	
	//All mdpvrp
		
	String[] BKSFiles={"benchmark/MDVRP/solution/p01","benchmark/MDVRP/solution/p02","benchmark/MDVRP/solution/p03","benchmark/MDVRP/solution/p04",
			"benchmark/MDVRP/solution/p05","benchmark/MDVRP/solution/p06","benchmark/MDVRP/solution/p07","benchmark/MDVRP/solution/p08","benchmark/MDVRP/solution/p09",
			"benchmark/MDVRP/solution/p10","benchmark/MDVRP/solution/p11","benchmark/MDVRP/solution/p12","benchmark/MDVRP/solution/p13","benchmark/MDVRP/solution/p14",
			"benchmark/MDVRP/solution/p15","benchmark/MDVRP/solution/p16","benchmark/MDVRP/solution/p17","benchmark/MDVRP/solution/p18","benchmark/MDVRP/solution/p19",
			"benchmark/MDVRP/solution/p20","benchmark/MDVRP/solution/p21","benchmark/MDVRP/solution/pr01","benchmark/MDVRP/solution/pr02","benchmark/MDVRP/solution/pr03",
			"benchmark/MDVRP/solution/pr04","benchmark/MDVRP/solution/pr05","benchmark/MDVRP/solution/pr06","benchmark/MDVRP/solution/pr07","benchmark/MDVRP/solution/pr08",
			"benchmark/MDVRP/solution/pr09","benchmark/MDVRP/solution/pr10"
			};
	
	public static double BKSValue;
	public static boolean BKSValueSet=false;
	
	//

	

	
 	public ProblemInstance createProblemInstance(String inputFileName, String outputFileName)
	{
		ProblemInstance problemInstance=null;
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
			
			if(writeToExcel) exportToCsv = new ExportToCSV(inputFileName);
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
	
	public void solve(String singleInputFileName) throws Exception 
	{
		
		elitistRatioOutputFileName = "parameters/elitistRatio/"+singleInputFileName.substring(singleInputFileName.indexOf('/'))+"-"+Solver.LocalImprovementElitistRation+".csv";
		Scanner weightFile=new Scanner(new File(weightingSchemeFileName));
		Scanner elitistRatioFile = new Scanner(new File(elitistRatioFileName));
		
		// singlerun = true when excel needs to be generated or output checked for testing
		// sigleRun = false when aggregated report is to be generated
		Individual bestResult = new Individual();
		double bestResultCost = 0;
		
		PrintWriter weightOutput = new PrintWriter(new File(weightingSchemeOutputFileName));
		PrintWriter elitistRatioOutput = new PrintWriter(new File(elitistRatioOutputFileName));
		elitistRatioOutput.println("Local Improvement Ratio,Survivor Selection Ratio, Average Cost");
		elitistRatioOutput.flush();
		weightOutput.println("Weight Alpha, Average Cost");
		weightOutput.flush();
		
		
		
		while(elitistRatioFile.hasNextLine()){
			
			//Solver.weightingSchemeAlpha=Double.parseDouble(weightFile.nextLine().trim());
			Solver.ServivorElitistRation = Double.parseDouble(elitistRatioFile.nextLine().trim());
			bestResultCost = 0;
			long start,end;
			
			start = System.currentTimeMillis();
			
			//writeToExcel = singleRun;
			//outputToFile = singleRun;
			generateAggregatedReport = !singleRun;
			
			
			if(generateAggregatedReport)
				generateAggregatedReport();
			
			//single run
			if(singleRun) 
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
						for(int j=50;j<=ga.getNumberOfGeeration();j+=50)
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
				
				outputTraceWriter.flush();
				outputTraceWriter.close();
				if(writeToExcel)exportToCsv.createCSV();
			}
			
			//System.out.println("mutateTwoDifferentRouteBySwapping Failed : "+mutateRouteOfTwoDiefferentFailed);
			if(printFinalSolutionToFile)output.close();
			
			end= System.currentTimeMillis();
			
			long duration = (end-start) / 1000;
			long minute =  duration/ 60;
			long seconds = duration % 60;
			System.out.println("ELAPSED TIME : " + minute+ " minutes "+seconds+" seconds");
			
			elitistRatioOutput.println(Solver.LocalImprovementElitistRation+","+Solver.ServivorElitistRation+","+bestResultCost/Solver.totalNumberofSingleRun);
			elitistRatioOutput.flush();
			
		}
		
		elitistRatioOutput.close();
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
	
	public void generateAggregatedReport() throws Exception
	{
	
		long start,end;
		
		start = System.currentTimeMillis();
		
		File reportFile = new File(reportFileName);
		PrintWriter reportOut=null;
		
		boolean once= true;
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
		
		for(int instanceNo=0;instanceNo<instanceFiles.length;instanceNo++)
		{
			ProblemInstance problemInstance = createProblemInstance(instanceFiles[instanceNo], singleOutputFileName);
			
			//////////////////////////////////////////////////////////////////////////////////////////////////
			if(Solver.BKSValueSet){
				String BKSFileName= BKSFiles[instanceNo]+".res";
				Scanner BKSfile=new Scanner(new File(BKSFileName));
				Solver.BKSValue=Double.parseDouble(BKSfile.nextLine());
			}
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			
			
			Scheme6_with_normal_mutation ga = new Scheme6_with_normal_mutation(problemInstance);
			//Scheme7_with_weighted_mutation_withFaltuCrossover ga = new Scheme7_with_weighted_mutation_withFaltuCrossover(problemInstance);
			
			if(once)
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
				Individual sol = ga.run();
				
				if(sol.isFeasible==true)
				{
					feasibleCount++;
				}
				sum += sol.costWithPenalty;
				if(sol.costWithPenalty>max) max = sol.costWithPenalty;
				if(sol.costWithPenalty<min) min = sol.costWithPenalty;
				
					
			}
			avg = sum/aggregate_report_run_size;
		
			reportOut.format("%s, %f, %f, %f, %d \n",instanceFiles[instanceNo],min,avg,max,feasibleCount);
			reportOut.flush();
			System.out.format("%s, %f, %f, %f, %d \n",instanceFiles[instanceNo],min,avg,max,feasibleCount);
		}
		
		end= System.currentTimeMillis();
		
		long duration = (end-start) / 1000;
		long minute =  duration/ 60;
		long seconds = duration % 60;
		
		reportOut.println("\nELAPSED TIME : " + minute+ " minutes "+seconds+" seconds");
		reportOut.flush();
		reportOut.close();
	}
	
	
}
