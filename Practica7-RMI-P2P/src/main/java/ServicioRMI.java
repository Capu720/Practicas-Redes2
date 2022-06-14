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


public class ServicioRMI extends Thread implements ListaArchivos{
    
    private int puerto;
    private String ruta;
    
    public ServicioRMI(){}
    
    public ServicioRMI(int puerto)
    {
        this.puerto = puerto;
    }
    
    public ServicioRMI(int puerto, String ruta)
    {
        this.puerto = puerto;
        this.ruta = ruta;
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
     
    public  ArrayList <Archivo> busquedaArchivos(String archivo, String dir) throws RemoteException
    {
        ArrayList<Archivo> encontrados = new ArrayList();
        File path = new File(dir);
        System.out.println("Buscando " +archivo + " en la direccion: "+dir);
        int bandera = 0;
        
        File[] coincide = path.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String nombre){
                return nombre.startsWith(archivo);
            }
        });
        
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for(File match: coincide)
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
        }catch(NoSuchAlgorithmException ex) {
            Logger.getLogger(ServicioRMI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServicioRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encontrados; 
    }
    
    @Override
    public void run()
    {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(this.puerto);
            System.out.println("RMI registry ready");
        } catch (RemoteException ex) {
            Logger.getLogger(ServicioRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
   
        try
        {
        
            System.setProperty("java.rmi.server.codebase","file:G:\\permisos.policy");
	    ServicioRMI obj = new ServicioRMI();       
	    ListaArchivos stub = (ListaArchivos) UnicastRemoteObject.exportObject((Remote) this, 0);
            
	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry(this.puerto);
            System.out.println("Servidor rmi en el puerto: "+this.puerto);
	    registry.bind("ListasArchivos", stub);

	    System.err.println("Servidor listo...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
    }

    
}
