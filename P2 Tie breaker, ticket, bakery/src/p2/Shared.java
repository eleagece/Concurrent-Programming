package p2;

public class Shared 
	{
	///////////////
	// Atributos //
	///////////////
    volatile private int sharedInt;
    
    /////////////////
    // Constructor //
    /////////////////
    public Shared()
    	{
        sharedInt=0;
    	}

    /////////////
    // Setters //
    /////////////
    public void up(int n)
    	{
    	sharedInt=sharedInt+n;
    	}
    
    public void down(int n)
    	{
    	sharedInt=sharedInt-n;
    	}

    /////////////
    // Getters //
    /////////////
    public int getInt()
    	{
    	return sharedInt;
    	}
	}
