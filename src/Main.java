
public class Main {
	/* The following program expects that the TestPngs package is downloaded 
	 * with the project. You may add different tests if you wish however they must
	 * follow the following format:
	 * 1. The first integer that is read in is the size of the grid N.
	 * 2. The numbers following are whitespace delimited and are valid locations 
	 * on the grid of sites to open. Each index should be in range [1, N].
	 * Ex: 
	 * 4
	 * 1 1
	 * 2 2
	 * 
	 * The example results in a four by four grid with the site at row 1 column 1 opened
	 * and the site at row 2 column 2 opened.
	 * 
	 * The program itself expects 1-2 arguments. The first argument should be a valid and properly
	 * formatted .txt file located in the TestPngs package. This is the test file that will be run for
	 * the simulations. The second argument is the number of simulations to perform on the given .txt that
	 * was passed in as the first argument. If no second argument is given the simulation will only be run once.
	 */
	public static void main(String args[]){
		PercolationStats.run(args);
		return;
	}

}
