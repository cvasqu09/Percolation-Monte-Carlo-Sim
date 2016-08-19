public class Site {
	private int root;					//ID of the given site's root element.
	private final int id;				//Position of given site (doesn't change)
	private int size;					//Number of items rooted at this site (including the site itself)
	private boolean isOpen = false;		//Initially all sites will be closed
	
	//Constructor
	public Site(final int id, int root) {
		this.id = id;
		this.root = root;
		this.size = 1;
	}
	

	public void open(){
		if(isOpen())
			return;
		else{
			isOpen = true;
		}
	}
	
	public boolean isOpen(){
		return isOpen;
	}
		
	public int getRoot() {
		return root;
	}
	
	public int getId() {
		return id;
	}
	
	public void setRoot(int root) {
		this.root = root;
	}
	

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
