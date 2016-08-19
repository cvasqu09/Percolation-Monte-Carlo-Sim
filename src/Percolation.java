public class Percolation {
	Site[][] sites;																//N by N grid
	final int N;																//Size of grid
	Site virtualTop = new Site(Integer.MAX_VALUE, Integer.MAX_VALUE);			//Virtual top site w/ special id of Integer.MAX_VALUE
	Site virtualBottom = new Site(Integer.MIN_VALUE, Integer.MIN_VALUE);		//Virtual bottom site w/ special id Integer.MIN_VALUE
	
	//Constructor
	public Percolation(final int N) {
		if(N <= 0){														//Invalid size of grid
			throw new IllegalArgumentException();
		}
		
		this.N = N;													
		sites = new Site[N][N];
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				int siteID = getSiteID(i, j);							//Calculate the siteID given indices
				sites[i][j] = new Site(siteID, siteID);					//Create the site with the given indices and rooted at itself
			}
		}
	}
	
	/* open: Integer x Integer -> void
	 * Given valid indices in an N by N grid, this function opens the site in the grid
	 * indexed at row i, column j.
	 */
	public void open(int i, int j){
		if(i == 0){													//If opening a site in the first row
			sites[i][j].open();										//Open the site
			sites[i][j].setRoot(virtualTop.getId());				//Set the root to the virtual top
			virtualTop.setSize(virtualTop.getSize() + 1);			//Increase the size of elements rooted at virtual top.
		} else if(i == N-1){										//Same process for last row except use virtual bottom instead
			sites[i][j].open();										
			sites[i][j].setRoot(virtualBottom.getId());
			virtualBottom.setSize(virtualBottom.getSize() + 1);
		} else {			
			sites[i][j].open();										//Otherwise simply open the site.
		}
	}
	

	/* root: Integer x Integer -> Site
	 * Given valid indices in an N by N grid, this function returns the Site corresponding to the site at
	 * the given indices. Note that i and j should be in the range of [0, N). 
	 */
	public Site root(int i, int j){
		Site rootSite = sites[i][j];
		if(sites[i][j].getRoot() == Integer.MAX_VALUE){				//If id of root is Integer.MAX_Value
			rootSite = virtualTop;									//start rootSite at virtual top
		} else if(sites[i][j].getRoot() == Integer.MIN_VALUE){		//If id of root is Integer.MIN_Value
			rootSite = virtualBottom;								//start rootSite at virtual bottom
		}
	
		while(rootSite.getId() != rootSite.getRoot()){				//The actual root is when rootSite's id equals the id returned from .getRoot()
				rootSite = getSite(rootSite.getRoot());				//Get the root of rootSite and check again until the equality is satisfied.
		}
		
		return rootSite;
	}
	
	/* root: Integer -> Site
	 * Given a valid siteID (including Integer.MAX_VALUE and Integer.MIN_VALUE indicating the virtual top and virtual
	 * bottom sites respectively) this function returns the root of the given siteID.
	 */
	public Site root(int siteID){
		Site rootSite = getSite(siteID);
		
		while(rootSite.getId() != rootSite.getRoot()){			//Same while loop as the other root function
			rootSite = getSite(rootSite.getRoot());
		}
	
		return rootSite;	
	}
	
	/* union: Integer x Integer x Integer x Integer -> void
	 * Given valid indices for each argument which should be in the range [0, N^2), this function
	 * calculates the union of the site indexed at i1, j1 with the site indexed at i2, j2. Note that
	 * this is a weighted union to make sure that the smaller trees are rooted at larger trees to 
	 * preserve a better balance and optimized operations.
	 */
	public void union(int i1, int j1, int i2, int j2){
		if(!sites[i2][j2].isOpen()){							//If not open no union needed to be performed
			return;
		}
		
		Site root1 = root(i1,j1);								//Calculate the root of first site
		Site root2 = root(i2,j2);								//Calculate root of second site
		if(root1.getId() == root2.getId()){						//If the ids are the same no union needed to be performed
			return;
		} else if(root1.getSize() > root2.getSize()){			//If more elements rooted at root1 than root2
			root2.setRoot(root1.getId());						//Set root2 to be root1
			root1.setSize(root1.getSize() + root2.getSize());	//Update size of tree
		} else {							
			root1.setRoot(root2.getId());						//Otherwise set root1 to be root2
			root2.setSize(root2.getSize() + root1.getSize());	//Update size of tree
		};
	}
	
	/* unionWithNeighbors: Integer x Integer -> void
	 * Given valid indices in an N by N grid (in the interval [0, N^2) the function will
	 * performed union operations with each of the neighbors in the grid. 
	 */
	public void unionWithNeighbors(int i, int j){
		if(N == 1){								//Special case when N is 1
			return;
		} else if(i == 0 && j == 0){
			//Case: Left corner
			union(i, j, i, j + 1);				//Union with right site
			union(i, j, i + 1, j);				//Union with bottom site
		} else if(i == 0 && j == N-1){			
			//Case: Right corner
			union(i, j, i, j-1);				//Union with left site
			union(i, j, i + 1, j);				//Union with bottom site
		} else if(i == N-1 && j == 0){			
			//Case: Left corner
			union(i, j, i - 1, j);				//Union with top site
			union(i, j, i, j + 1);				//Union with right site
		} else if(i == N-1 && j == N-1){
			//Case: Right corner
			union(i, j, i - 1, j);				//Union with top site
			union(i, j, i, j - 1);				//Union with left site
		} else if(i == 0){
			//Case: Top edge
			union(i, j, i, j - 1);				//Union with left site
			union(i, j, i, j + 1);				//Union with right site
			union(i, j, i + 1, j);				//Union with bottom site
		} else if(j == 0){
			//Case: Left edge
			union(i, j, i - 1, j);				//Union with top site
			union(i, j, i + 1, j);				//Union with bottom site
			union(i, j, i, j + 1);				//Union with right site
		} else if(i == N-1){
			//Case: Bottom edge
			union(i, j, i, j + 1);				//Union with right site
			union(i, j, i, j - 1);				//Union with left site
			union(i, j, i-1, j);				//Union top left site
		} else if(j == N-1){
			//Case: Right edge
			union(i, j, i-1, j);				//Union with top site
			union(i, j, i+1, j);				//Union with bottom site
			union(i, j, i, j-1);				//Union with left site
		} else {
			//Case: Non-Edge, Non-Corner
			union(i, j, i, j + 1);				//Union with right site
			union(i, j, i, j - 1);				//Union with left site
			union(i, j, i-1, j);				//Union with top site
			union(i, j, i+1, j);				//Union with bottom site
		}
}
	
	/* connected: Integer x Integer x Integer x Integer -> boolean
	 * Given valid indices in range [0, N^2), this function returns whether or not
	 * site indexed at i1, j1 is connected to the site indexed by i2, j2 by checking
	 * if the roots have the same id.
	 */
	public boolean connected(int i1, int j1, int i2, int j2){
		Site root1 = root(i1, j1);					//Root of site 1
		Site root2 = root(i2, j2);					//Root of site 2
		return root1.getId() == root2.getId();		//Compare ids
	}
	
	/* isOpen: Integer x Integer -> boolean
	 * If i and j are valid indices in range [0, N^2) then isOpen(i, j) returns whether
	 * or not the site indexed at row i, column j is open.
	 */
	public boolean isOpen(int i, int j){
		return sites[i][j].isOpen();
	}
	
	/* percolates: void -> boolean
	 * Returns whether or not the system percolates by checking if the virtual top and
	 * virtual bottom sites have the same root.
	 */
	public boolean percolates(){
		if(N == 1 && sites[0][0].isOpen()){				//Special case when N == 1
			return true;
		} else {			
			return root(virtualTop.getRoot()) == root(virtualBottom.getRoot());		//Check if virtual bottom and top are connected	
		}
	}
	
	/*getSiteID: Integer x Integer -> Integer
	 * Returns the site id of the site indexed at row i, column j given that i and j are
	 * in the interval [0, N^2).
	 */
	public int getSiteID(int i, int j){
		return i * N + j + 1;
	}
	
	/* getSite: Integer -> Site
	 * Given a valid siteID the function returns the site that is referenced by the 
	 * given siteID.
	 */
	public Site getSite(int siteID){
		if(siteID == Integer.MAX_VALUE){			//Integer.MAX_VALUE indicates virtual top
			return virtualTop;
		} else if(siteID == Integer.MIN_VALUE){		//Integer.MIN_VALUE indicates virtual bottom
			return virtualBottom;
		} else {									
			int i = (siteID - 1) / N;				//Calculate row index
			int j = (siteID - 1) % N;				//Calculate column index
			return sites[i][j];					
		}
	}
}
