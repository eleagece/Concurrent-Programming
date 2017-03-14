package p2;

public class Main
	{
	public static void main(String[] args) throws InterruptedException
		{
		// Número de hilos (threadsCount/2 aumentan y threadsCount/2 decrementan)
		int threadsCount=10;
	    // Objeto que comparten los hilos y sobre el que tienen que acceder con exclusión mutúa
	    Shared sharedInt = new Shared();
	    // Objeto que controla exclusión mutúa mediante el algoritmo especificado en args[0]. TIE_BREAKER=1, TICKET=2, BAKERY=3
	    Synchronizer synchronizer = new Synchronizer(Integer.parseInt(args[0]),threadsCount);
		// Datos para cada hilo
	    int sleepTime=0;  // Milisegundos que han de dormir los hilos (0=no dormir)
	    int a=1;  // Número de aumentos (o decrementos) que hace el hilo sobre 'sharedInt'
	    // Creación de todos los hilos
	    Thread[] threads=new Thread[threadsCount];
	    for (int id=0; id<threadsCount; id++) 
	    	{
	    	if (id%2==0)  // Los hilos pares son los incrementadores:  
	    		{
	    		// Se crea un nuevo hilo de identificador 'id', de duración 'sleepTime', que hace
	    		// 'n' aumentos (sabemos que aumenta por 1 en primer argumento) en la variable
	    		// compartida 'sharedInt' de tipo Shared mediante el Synchronizer 'synchronizer'
		        threads[id] = new Thread(new UpDownThread(1,id,sleepTime,a,synchronizer,sharedInt));
	    		System.out.println("<"+id+",START>");
		        threads[id].start();
	    		}
	    	else if (id%2==1)  // Los hilos impares son los decrementadores:
	    		{
	    		// Se crea un nuevo hilo de identificador 'id', de duración 'sleepTime', que hace
	    		// 'n' decrementos (sabemos que decrementa por 1 en primer argumento) en la
	    		// variable compartida 'sharedInt' de tipo Shared mediante el Synchronizer 'synchronizer'
		        threads[id] = new Thread(new UpDownThread(0,id,sleepTime,a,synchronizer,sharedInt));
	    		System.out.println("<"+id+",START>");
		        threads[id].start();
	    		}
	    	}
	    for (int id=0; id<threadsCount; id++) 
			{ 
	    	threads[id].join();
	    	System.out.println("<"+id+",JOIN>");
			}
	    System.out.println("END EXECUTION");
		}	
	}


