import java.io.*;
import java.net.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Servidor{

    ServerSocket s;
    File f;
    String ruta, rutaDescarga;

    public Servidor(int puerto){
        try{
            s = new ServerSocket(puerto);
            s.setReuseAddress(true);

            File aux = new File("");
            ruta = aux.getAbsolutePath() + "\\archivos\\";
            rutaDescarga = aux.getAbsolutePath() + "\\localArchivos\\";
            f = new File(ruta);
            f.mkdirs();
            f.setReadable(true);
            f.setWritable(true);
        }catch(IOException ex){}
    }

    public void descomprimirZip(String archivo, String directorio) throws IOException
    {
        File carpeta =  new File(directorio);
        if(!carpeta.exists())
            carpeta.mkdir();
        
        try
        {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(archivo));
            ZipEntry ze =  zis.getNextEntry();
            byte[] buffer = new byte[8192];
            while(ze != null)
            {
                String nombreArchivo =  ze.getName();
                File archivoNuevo = new File(directorio + File.separator + nombreArchivo);
                System.out.println("Carpeta descomprimida: "+ archivoNuevo.getAbsoluteFile());

                new File(archivoNuevo.getParent()).mkdirs();
                FileOutputStream fos =  new FileOutputStream(archivoNuevo);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
                
            }
            zis.closeEntry();
            System.out.println("Archivos descomprimidos");
            //Borramos la carpeta
            File f = new File(directorio + ".zip");
            if(f.exists()){
                f.delete();
            
            }
            

        }catch(IOException ex){
            ex.printStackTrace();
        }
        
    }

    public static void mandarInfo(ObjectOutputStream oos, File[] archivos, String opcion) throws IOException
    {
            Datos d =  new Datos(opcion, archivos);
            oos.writeObject(d);
            oos.flush();
    }



    public void enviarArchivos(int numArchivos) throws IOException
    {
        System.out.println("Cantidad de archivos a enviar: "+ numArchivos);

        for(int i = 0; i<numArchivos; i++){
            Socket socket = s.accept();
            System.out.println("Archivos enviados por el cliente " + socket.getInetAddress() + ": "+ socket.getPort());
            //System.out.println("Mando archivos a la rura: "+rutaDescarga);
            //Leyendo los datos provenientes del socket
            DataInputStream dis =  new DataInputStream(socket.getInputStream());
            String nombre = dis.readUTF();
            long tam = dis.readLong();

            System.out.println("Enviando archivo "+ nombre + "de " + tam +" bytes");
            DataOutputStream dos =  new DataOutputStream(new FileOutputStream(rutaDescarga + nombre));

            long recibidos = 0;
            int l = 0, porcentaje = 0;

            while (recibidos < tam) {
                byte[] b = new byte[8192];
                l = dis.read(b);
                if(l == -1)
                    break;
                dos.write(b, 0, l);
                dos.flush();
                recibidos = recibidos + l;
                porcentaje = (int) ((recibidos * 100) / tam);
                System.out.print("\rEnviando el " + porcentaje + " % del archivo");
            }

            System.out.println("\nArchivo enviado...\n");
            dos.close();
            dis.close();
            socket.close();

            //Verificamos si tenemos que descomplimir el ZIP
            String[] nombreArchivo = nombre.split("\\.");
            if (nombreArchivo[nombreArchivo.length - 1].equals("zip"))
            {
                String nomaux =  nombreArchivo[0];
                descomprimirZip(rutaDescarga + nombre, rutaDescarga + nomaux);
                File f =  new File(rutaDescarga+nombre);
                f.delete();
            }
        }
    }

    public void recibirArchivos(int numArchivos) throws IOException
    {
        System.out.println("Cantidad de archivos a recibir: "+ numArchivos);
        for(int i = 0; i<numArchivos; i++)
        {
           Socket socket = s.accept();
           System.out.println("Recibiendo archivos del cliente: " + socket.getInetAddress() + ": "+ socket.getPort());
            //Leyendo los datos provenientes del socket
            DataInputStream dis =  new DataInputStream(socket.getInputStream());
            String nombre = dis.readUTF();
            long tam = dis.readLong();

            System.out.println("Descargando archivos "+ nombre + "de " + tam +" bytes");
            DataOutputStream dos =  new DataOutputStream(new FileOutputStream(ruta + nombre));

            long recibidos = 0;
            int l = 0, porcentaje = 0;

            while (recibidos < tam) {
                byte[] b = new byte[8192];
                l = dis.read(b);
                if(l == -1)
                    break;
                dos.write(b, 0, l);
                dos.flush();
                recibidos = recibidos + l;
                porcentaje = (int) ((recibidos * 100) / tam);
                System.out.print("\rDescargando el " + porcentaje + " % del archivo");
            }

            System.out.println("\nArchivo reciido...\n");
            dos.close();
            dis.close();
            socket.close();

            //Verificamos si tenemos que descomplimir el ZIP
            String[] nombreArchivo = nombre.split("\\.");
            if (nombreArchivo[nombreArchivo.length - 1].equals("zip"))
            {
                String nomaux =  nombreArchivo[0];
                descomprimirZip(ruta + nombre, ruta + nomaux);
                File f =  new File(ruta+nombre);
                f.delete();
            }
        }  
    }

    public void eliminarArchivos(int numArchivos)throws IOException
    {
        System.out.println(numArchivos);
        if(numArchivos < 2)
        {
            Socket socket = s.accept();
            DataInputStream dis =  new DataInputStream(socket.getInputStream());
            String nombre = dis.readUTF();
            System.out.println(nombre);
            long tam = dis.readLong();

            File archivo = new File(nombre);
            if(archivo.exists()){
                archivo.delete();
            }
            socket.close();
        }

        else
        {
            for(int i = 0; i<numArchivos; i++)
            {
                Socket socket = s.accept();
                DataInputStream dis =  new DataInputStream(socket.getInputStream());
                String nombre = dis.readUTF();
                long tam = dis.readLong();
                System.out.println(nombre);
                File archivo = new File(nombre);
                if(archivo.exists()){
                    archivo.delete();
                }
                socket.close();
            }
        }
    }


    public static void main(String[] args) throws ClassNotFoundException
    {
        try
        {
            Servidor servidor = new Servidor(8000);
            Servidor servidorPaLosDatos = new Servidor(8080);
            System.out.println("Servidor iniciado en el puerto " + servidor.s.getLocalPort());
            System.out.println("Servidor iniciado para la transferencia de datos en el puerto " + servidorPaLosDatos.s.getLocalPort());

            for(;;)
            {
                //Se conceta al servidor
                System.out.println("Esperando al cliente...");
                Socket cl1 = servidor.s.accept();
                ObjectInputStream ois = new ObjectInputStream(cl1.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(cl1.getOutputStream());
                System.out.println("Cliente " + cl1.getInetAddress()+ ":" + cl1.getPort() + " conectado");

                while(!cl1.isClosed())
                {
                    //AQUI SE REALIZAN TODAS LAS OPCIONES QUE PUEDE HACER EL SERVIDOR
                    Datos opciones = (Datos)ois.readObject();
                    {
                        switch(opciones.opcion())
                        {
                            case "subir":
                                System.out.println("Hemos recibido que se desea subir archivos\n");
                                servidorPaLosDatos.recibirArchivos(opciones.numArchivos());
                                break;
                            
                            case "listar":
                                System.out.println("Hemos recibido que desea ver los archivos en nuestro server\n");
                                File [] archivos = servidor.f.listFiles();
                                mandarInfo(oos, archivos, "listar");
                                break;

                            case "descargar":
                                System.out.println("Hemos recibido que se desea descargar archivos de nuestro server");
                                servidorPaLosDatos.enviarArchivos(opciones.numArchivos());
                                break;
                            
                            case "eliminar":
                                System.out.println("Hemos recibido que se desea eliminar archivos de nuestro server");
                                servidorPaLosDatos.eliminarArchivos(opciones.numArchivos());
                                break;
                        }
                    }
                }

            }


        }catch(IOException ex){
            ex.printStackTrace();
        }
    }


}
