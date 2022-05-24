import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PoolServer {
	
    public static void main(String[] args) {
    	int pto, tamPool;

        try {

			pto = 8000;
			tamPool = 1000;

        	ExecutorService pool = Executors.newFixedThreadPool(tamPool);

	        ServerSocket s = new ServerSocket(pto);
	        System.out.println("Servidor iniciado: http://localhost:" + pto);
	        System.out.println("Esperando a clientes...");

	        for( ; ; ) { 
	            Socket cl = s.accept();
	            Manejador manejador = new Manejador(cl);
	            pool.execute(manejador);
	        }
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }
}