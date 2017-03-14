package p2;

public class UpDownThread implements Runnable 
	{
	// Atributos
    private int upOrDown;  // Indica si el hilo aumenta 'sharedInt' (1) o lo decrementa (0)
    private int id;  // Identificador del hilo
    private int sleepTime;  // Tiempo que duerme el hilo después de hacer su tarea
    private int a;  // Número de aumentos (o decrementos) que hará este hilo (operación en exclusión mutúa) sobre 'sharedInt'
    private Synchronizer synchronizer;  // El sincronizador de hilos para que accedan en exlusión mútua a 'sharedInt'
    private Shared sharedInt;  // El entero compartido por todos los hilos
    
    // Constructores
    public UpDownThread(int upOrDown, int id, int sleepTime, int a, Synchronizer synchronizer, Shared sharedInt)
    	{
        // OJO. RECORDEMOS QUE EN JAVA HACER '=' NO ES HACER UNA COPIA DEL OBJETO, ¡¡¡¡ES UNA REFERENCIA!!!!
        this.upOrDown = upOrDown;
        this.id = id;
        this.sleepTime = sleepTime;
        this.a = a;
        this.synchronizer = synchronizer;
        this.sharedInt = sharedInt;
    	}

    // Métodos    
    public void run() 
    	{
        synchronizer.entryCS(id);
        criticalSection();
        synchronizer.exitCS(id);
        }
    
    private void criticalSection()
	    {
        if (upOrDown==0)
            {
            sharedInt.down(a);
            System.out.println("<"+this.id+",DOWN,"+this.sharedInt.getInt()+">");
            }
        else
            {
            sharedInt.up(a);
            System.out.println("<"+this.id+",UP,"+this.sharedInt.getInt()+">");
            }
        if (sleepTime>0)
        	{
	        try 
	        	{
				java.lang.Thread.sleep(sleepTime);
	        	} 
	        catch (InterruptedException e) 
	        	{
				e.printStackTrace();
	        	}
        	}
	    }
	}