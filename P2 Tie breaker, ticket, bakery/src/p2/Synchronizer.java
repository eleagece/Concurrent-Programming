package p2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Synchronizer 
	{
	////////////////
	// Attributes //
	////////////////
	// Constants that identify which algorithm enables mutual exclusion
    static final int TIE_BREAKER=1;
    static final int TICKET=2;
    static final int BAKERY=3;
	// Auxiliary attributes which don't change during execution
    private int threadCount;
    private int algorithm;
    // Tie-Breaker (Peterson) attributes
    volatile public/*private*/ int[] level;
	volatile public/*private*/ int[] last;
    // Ticket attributes
    volatile public/*private*/ int nowServing;
    volatile public/*private*/ int nextTicket;
    Lock ticketLock;
    // Bakery (Lamport) attributes
    volatile public int[] ticket;
    volatile public boolean[] entering;
    
    /////////////////
    // Constructor //
    /////////////////
    public Synchronizer (int algorithm, int threadCount)
    	{
        this.threadCount=threadCount;
        this.algorithm=algorithm;
        switch (this.algorithm)
            {
            case TIE_BREAKER:
                level=new int[this.threadCount];
                last=new int[this.threadCount]; 
                for (int i=0; i<this.threadCount; i++) 
                    {
                    level[i]=-1;
                    last[i]=-1;
                    }
                break; 
            case TICKET:
                nowServing=0;
                nextTicket=0;
                ticketLock=new ReentrantLock();
                break; 
            case BAKERY:
                ticket=new int[this.threadCount];
                entering=new boolean[this.threadCount]; 
                for (int i=0; i<this.threadCount; i++) 
                    {
                    ticket[i]=0;
                    entering[i]=false;
                    }
                break;
            default:
            	break;
            }
    	}

    /////////////////////
    // General methods //
    /////////////////////
    public void entryCS(int id)
        {
    	switch (this.algorithm)
	        {
	        case TIE_BREAKER:
	            tieBreakerEntryCS(id);
	            break; 
	        case TICKET:
	            ticketEntryCS();
	            break; 
	        case BAKERY:
	        	bakeryEntryCS(id);
	            break;
	        default:
	        	System.out.println("Not an actual algorithm");
	        	break;
	        }
        }

    public void exitCS(int id)
        {
    	switch (this.algorithm)
        	{
	        case TIE_BREAKER:
	            tieBreakerExitCS(id);
	            break; 
	        case TICKET:
	            ticketExitCS();
	            break; 
	        case BAKERY:
	        	bakeryExitCS(id);
	            break;
	        default:
	        	System.out.println("Not an actual algorithm");
	        	break;
	        }
        }

    ////////////////////////////////////
    // Tie-Breaker (Peterson) methods //
    ////////////////////////////////////
    // https://en.wikipedia.org/wiki/Peterson%27s_algorithm
    private void tieBreakerEntryCS(int id)
    	{
    	for (int j=0; j<threadCount; j++)
    		{
    		level[id]=j;  // El hilo 'id' intenta subir al nivel 'j'
    		last[j]=id;  // El nivel 'j' fue intentado acceder por ultima vez por el hilo 'id' (linea anterior)
    		for (int k=0; k<threadCount; k++)  // 'id' se compara con el resto de procesos
    			{
    			if (k!=id)  // El hilo 'id' no se compara consigo mismo
    				{
    				/* Mientras 
    				 * --------
    				 * 1. El nivel al que intenta acceder el hilo 'id' sea menor o igual 
    				 * que al que intenta acceder cualquier otro hilo y...
    				 * 2. Ese nivel al que intenta acceder el hilo 'id' no se ha tratado
    				 * de acceder por otro hilo
    				 * Entonces
    				 * --------
    				 * Permanece en espera activa
    				 */
    				while (level[k]>=level[id] && last[j]==id)  // Es lo mismo que decir: 'while (level[k]>=j && last[j]==id)'
    					{}
    				}
    			}
    		}
    	printTieBreakerInfo();
    	}
    
    private void tieBreakerExitCS(int id)
    	{
    	level[id] = -1;
    	}
    
    private void printLevel()
    	{
    	System.out.print("level=[");
    	int i;
    	for (i=0; i<threadCount-1; i++)
    		{
    		System.out.print(level[i]+",");
    		}
    	System.out.print(level[i]+"]");
    	}
    
    private void printLast()
		{
		System.out.print("last=[");
		int i;
		for (i=0; i<threadCount-1; i++)
			{
			System.out.print(last[i]+",");
			}
		System.out.print(last[i]+"]");
		}
    
    private void printTieBreakerInfo()
    	{
    	printLevel();
    	System.out.print(" | ");
    	printLast();
    	System.out.println();
    	}

    ////////////////////
    // Ticket methods //
    ////////////////////
    // Algorithm: https://en.wikipedia.org/wiki/Ticket_lock
    // 'Lock' usage: http://jrlq.blogspot.com.es/2013/05/concurrencia-en-java-parte-3.html
    private void ticketEntryCS()
        {
        int myTicket = takeMyTicketAndIncrement();
        while(nowServing!=myTicket) 
            {}
        }

    private int takeMyTicketAndIncrement()
        {
    	ticketLock.lock();
        int myTicket=nextTicket;
        nextTicket++;
        ticketLock.unlock();
        return myTicket;
        }

    private void ticketExitCS()
        {
        nowServing++;
        }

    //////////////////////////////
    // Bakery (Lamport) methods //
    //////////////////////////////
    // https://en.wikipedia.org/wiki/Lamport%27s_bakery_algorithm
    public void bakeryEntryCS(int id)
    	{
    	entering[id]=true;
    	int max=0;
    	for (int i=0; i<threadCount; i++)
    		{
    		int current=ticket[i];
    		if (current>max)
    			max=current;
    		}
    	ticket[id]=1+max;
    	entering[id]=false;
    	for (int i=0; i<threadCount; i++)
    		{
    		if (i!=id)
    			{
    			while (entering[i])
    				{
    				//Thread.yield();
    				}
    			while (ticket[i]!=0 && (ticket[id]>ticket[i] || (ticket[id]==ticket[i] && id>i)))
    				{
    				//Thread.yield();
    				}
    			}
    		}
    	printBakeryInfo();
    	}

    public void bakeryExitCS(int id)
		{
    	ticket[id]=0;
		}
  
    private void printTicket()
		{
		System.out.print("ticket=[");
		int i;
		for (i=0; i<threadCount-1; i++)
			{
			System.out.print(ticket[i]+",");
			}
		System.out.print(ticket[i]+"]");
		}

	private void printEntering()
		{
		System.out.print("entering=[");
		int i;
		for (i=0; i<threadCount-1; i++)
			{
			System.out.print(entering[i]+",");
			}
		System.out.print(entering[i]+"]");
		}
	
	private void printBakeryInfo()
		{
		printTicket();
		System.out.print(" | ");
		printEntering();
		System.out.println();
		}
 
	}
