
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServidorRMI extends Thread{
    
    private int puerto;
    ArrayList <Archivo> encontrados = new ArrayList();
    
    public ServidorRMI(){}
    
    public ServidorRMI(int puerto)
    {   
        this.puerto = puerto;
    }
    
    private String obtenerHash(File archivo, MessageDigest md) throws FileNotFoundException, IOException
    {
        FileInputStream fis = new FileInputStream(archivo);
        byte[] bytes = new byte[1024];
        int bcount = 0;
        
        while((bcount = fis.read(bytes)) != -1)
        {
            md.update(bytes,0,bcount);
        }
        fis.close();
        
        byte[] bytesMes = md.digest();
        
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<bytesMes.length; i++)
        {
            sb.append(Integer.toString((bytesMes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    
    public ArrayList<Archivo> busca(String archivo, String dir) throws RemoteException, NoSuchAlgorithmException, IOException
    {
        String direccion = dir;
        System.out.println("Buscando en la direccion: "+ direccion);
        File directorio = new File(direccion);
        File[] coincidentes = directorio.listFiles(new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.startsWith(archivo);
        }
        });

        MessageDigest md = MessageDigest.getInstance("MD5");
        for(File match: coincidentes)
        {
            String rutaArchivo = match.getAbsolutePath();
            String hash = obtenerHash(match, md);
            long tam = match.length();

            Archivo arch = new Archivo();
            arch.setArchivo(rutaArchivo);
            arch.setHash(hash);
            arch.setTam(tam);
            encontrados.add(arch);
        }
        return encontrados;

    }
    
    public void run()
    {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(this.puerto); //puerto default del rmiregistry
            System.out.println("RMI registry ready.");
	} catch (Exception e) {
            System.out.println("Exception starting RMI registry:");
            e.printStackTrace();
	}//catch
        
        
        
         try{
            System.setProperty("java.rmi.server.codebase","file:G:\\permisos.policy"); ///file:///f:\\redes2\\RMI\\RMI2
	    ServidorRMI obj = new ServidorRMI();
	    InterfazBusc stub = (InterfazBusc) UnicastRemoteObject.exportObject((Remote) obj, 0);
            
	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry(this.puerto);
	    registry.bind("ListasArchivos", stub);

	    System.err.println("Servidor listo...");
        }catch(Exception e){
            e.printStackTrace();
        }
    
    
    }
    
    
    
     
     
     
     
    
    
    
        
    

    
    
}
