package Main;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main 
{
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException 
	{
	//	System.out.println("Saikat!");
		
		
		final Solver solver = new Solver();
		//solver.initialise();
		Thread thread1=new Thread() {			
			@Override
			public void run() {
				try {
					/*System.out.println("Enter Elitist Ratio for LocalImprovement: ");
					Scanner scan = new Scanner(System.in);
					Solver.LocalImprovementElitistRation = Double.parseDouble(scan.next());
					while(Solver.LocalImprovementElitistRation >= 0.32){
						System.out.println("Enter Elitist Ratio for LocalImprovement Again: ");
						Solver.LocalImprovementElitistRation = Double.parseDouble(scan.next());
					}*/
					
					for(int i=0;i<Solver.instanceFiles.length;i++)
					{
						solver.solve(Solver.instanceFiles[i]);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}
		};
		//Thread thread2=new visualize();
		
		thread1.start();
		//thread2.start();
		

		
	}

}
