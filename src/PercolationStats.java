import java.util.Random;
import java.util.Scanner;

public class PercolationStats {
	final int N;			//Size of grid
	final int T;			//Number of tests in simulation
	double thresholds[];	//Thresholds of each simulation
	int threshIndex;		//Index for next item in thresholds array
	double mean;			
	double stddev;
	double confidenceLo;
	double confidenceHi;
	
	//Constructor
	public PercolationStats(final int N, final int T) {
		if(N <= 0 || T <= 0){
			throw new IllegalArgumentException();
		}
		this.N = N;
		this.T = T;
		thresholds = new double[T];
		threshIndex = 0;
		mean = 0.0;
		stddev = 0.0;
		confidenceLo = 0.0;
		confidenceHi = 0.0;
	}
	
	/* run: String[] -> void
	 * This function takes the command line arguments as an argument and performs the necessary
	 * number of simulations and produces the statistics that result from the information 
	 * gathered from the simulations. 
	 */
	public static void run(String args[]){
		Scanner scanner;
		PercolationStats stats;
		Percolation p;
		int N;
		int T;
		
		
		if(args.length == 1){					//Default case if no T argument is given
			System.out.println("Lenght is 1");
			T = 1;
		} else {
			T = Integer.parseInt(args[1]);		//T should be the second argument passed into program.
		}
		
		
		try{
			//Read the first integer which should be N and reset the scanner.
			scanner = new Scanner(PercolationStats.class.getResourceAsStream("TestPngs/" + args[0]));
			N = scanner.nextInt();
			scanner.close();
			
			//Create PercolationStats object now that we read in N
			stats = new PercolationStats(N, T);
			for(int iter = 0; iter < T; iter++){
				scanner = new Scanner(PercolationStats.class.getResourceAsStream("TestPngs/" + args[0]));
				//Ignore the first value which is N (it has already been read in)
				scanner.nextInt();	
				
				p = new Percolation(N);
				int i, j;
				int openSites = 0;
				
				//Reading values from input file.
				while(scanner.hasNextInt()){			//Assumes that there are at least two integers if one exists
					if(p.percolates()){
						stats.addThreshold(openSites);	//Add openSites to the array
						p = null;						//Allow GC to remove
						scanner.close();				//Close scanner to avoid leaks
						break;
					}
					
					i = scanner.nextInt();				//Read the next two integers
					j = scanner.nextInt();				
					p.open(i-1, j-1);					//Open the site
					p.unionWithNeighbors(i-1, j-1);		//Union with all neighboring sites
					openSites++;						//Increment threshold value
				}
				
				/*------------------------------------------------------------------------------------------
				//For debugging
				for(int c = 0; c < N; c++){
					for(int d = 0; d < N; d++){
						System.out.println("Site: " + p.getSiteID(c, d) + " root: " + p.root(c, d).getRoot());
					}
				}
				System.out.println("virtual top root: " + p.virtualTop.getRoot());
				System.out.println("vb root: " + p.virtualBottom.getRoot());
				-------------------------------------------------------------------------------------------*/
				
				
				//Generating random numbers if the system doesn't percolate yet
				Random rand = new Random();
				while(!(p.percolates())){
					int randomSite = rand.nextInt(N * N) + 1;				//Generate random number in [1, N^2]
					int row, col;
					row = (randomSite - 1) / N;								//Calculate row index
					col = (randomSite - 1) % N;								//Calculate column index
					
					if(!(p.isOpen(row, col))){								//Only perform if site is not already open
						p.open(row, col);									//Open site
						p.unionWithNeighbors(row, col);						//Union with all neighboring sites
						openSites++;										//Increment threshold value
					}
				}
				stats.addThreshold(openSites);								//Add number of open sites to thresholds array								
				p = null;													//Allow GC to reallocate resources
				scanner.close();											
			}
			//Print out all the statistics
			System.out.println("Mean: " + stats.mean());
			System.out.println("Standard deviation: " + stats.stddev());
			System.out.println("Confidence lo: " + stats.confidenceLow());
			System.out.println("Confidence hi: " + stats.confidenceHigh());
			
		} catch (NullPointerException e){
			System.out.println("The file '" + args[0] + "' could not be found.");
		}
	}
	
	/* calculateMean(): void -> void
	 * This function calculates the mean of the threshold values (number of open sites)
	 * that were found in the simulations performed. This function assumes that the thresholds 
	 * array is of size T and that all T values have already been stored. As a result the variable
	 * 'mean' should contain the mean value after this function is called.
	 */
	public void calculateMean(){
		double sum = 0.0;
		for(int i = 0; i < T; i++){					//Sum up all threshold values
			sum += thresholds[i];
		}
		mean = sum / (double) T;					//Divide by the number of simulations
	}
	
	/* calculateStdDev(): void -> void
	 * Assuming that the thresholds array is of size T and all T values have been stored, 
	 * calculateStdDev stores the standard deviation of the calculated thresholds in the variable
	 * 'stddev'.
	 */
	public void calculateStdDev(){					
		double variance = 0.0;
		double numerator = 0.0;
		for(int i = 0; i < T; i++){					//Sum up the squares of the deviations from the mean
			numerator += Math.pow((thresholds[i] - mean), 2);
		}
		variance = numerator / (T - 1);				//Divide by the number of simulations - 1
		stddev = Math.sqrt(variance);
	}
	
	/* calculateConfidence(): void -> void
	 * Assuming mean and stddev have already been calculated then this function stores
	 * both the low and high confidence endpoints for a 95% confidence interval into
	 * the variable 'confidenceLo' and 'confidenceHi'.
	 */
	public void calculateConfidence(){
		confidenceLo = mean - (1.96 * stddev / Math.sqrt(T));
		confidenceHi = mean + (1.96 * stddev / Math.sqrt(T));
	}
	
	/* addThreshold: Integer -> void
	 * Takes in a threshold value and adds it to the threshold array and updates the threshIndex
	 * to have the next available index to store the next threshold value. If threshIndex equals T
	 * (signaling all simulations have been performed) then the mean, standard deviation, and 
	 * 95% confidence Interval is calculated.
	 */
	public void addThreshold(int threshold){
		thresholds[threshIndex++] = (double) threshold;
		//When the final simulation is performed calculate all the statistics
		if(threshIndex == T){
			calculateMean();
			calculateStdDev();
			calculateConfidence();
		}
	}
	
	public double mean(){
		return mean;
	}
	
	public double stddev(){
		return stddev;
	}
	
	public double confidenceLow(){
		return confidenceLo;
	}
	
	public double confidenceHigh(){
		return confidenceHi;
	}
}
