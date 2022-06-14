import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;


public class ServidorFlujo extends Thread {
    
    private int puerto;
    private String rutaDescarga;
    
    
    public ServidorFlujo(int puerto, String rutaDescarga)
    {
        this.puerto = puerto;
        this.rutaDescarga = rutaDescarga;
    }
    
   
    
    @Override
    public void run()
    {
        try
        {
            ServerSocket ss = new ServerSocket(puerto);
            ss.setReuseAddress(true);
            
            while(true)
            {
                Socket servidor = ss.accept();
                DataInputStream dis = new DataInputStream(servidor.getInputStream());
                String nombre = dis.readUTF();
                long tam = dis.readLong();
                DataOutputStream dos =  new DataOutputStream(new FileOutputStream(rutaDescarga + nombre));
                
                long recibidos = 0;
                int l = 0, porcentaje = 0;

                while (recibidos < tam) 
                {
                    byte[] b = new byte[8192];
                    l = dis.read(b);
                    if(l == -1)
                        break;
                    dos.write(b, 0, l);
                    dos.flush();
                    recibidos = recibidos + l;
                    porcentaje = (int) ((recibidos * 100) / tam);
                    //System.out.print("\rEnviando el " + porcentaje + " % del archivo");
                }
                dos.close();
                dis.close();
                servidor.close();
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    
    
    }
    
}
