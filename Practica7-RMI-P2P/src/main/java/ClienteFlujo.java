
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;


public class ClienteFlujo extends Thread {
   private int puerto;
   private long tam;
   private String path;
   private String nombre;
   String host = "localhost";
   
   public ClienteFlujo(String path, String nombre, int puerto, long tam) 
   {
       this.path = path;
       this.puerto = puerto;
       this.tam = tam;
       this.nombre = nombre;
   }
   
    @Override
    public void run()
    {
        try
        {
            Socket cliente = new Socket(host, puerto);
            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
            System.out.println(path);
            DataInputStream dis = new DataInputStream(new FileInputStream(path));
            
            dos.writeUTF(this.nombre);
            dos.flush();
            dos.writeLong(this.tam);
            dos.flush();
            
            long enviados = 0;
            int l = 0, porcentaje = 0;
            byte[] b = null;
            while(enviados < tam || (l=dis.read(b))!= -1)
            {
                b = new byte[8192];
                l = dis.read(b);
                if(l == -1)
                    break;
                dos.write(b,0,l);
                dos.flush();
                enviados = enviados + 1;
                porcentaje = (int) ((enviados * 100) / tam);
                //System.out.println("\rDescargando el " + porcentaje + " % del archivo");
            }
            
            
            dis.close();
            dos.close();
            
        }catch(Exception ex){
            ex.printStackTrace();
        } 
    }
}
