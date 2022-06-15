import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.file.*;

import javax.swing.JFileChooser;



public class Cliente{

    Socket socket;
    File file;
    String ruta;
    String rutaArhcivosServidor = "";
    String rutaArchivosLocal = "";
    public ArrayList<String> listaArchivos = new ArrayList<String>();

    public Cliente(int puerto)
    {
        try
        {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), puerto);
            socket.setReuseAddress(true);
            File aux = new File("");
            ruta = aux.getAbsolutePath() + "\\localArchivos\\";
            rutaArchivosLocal = aux.getAbsolutePath() + "\\localArchivos\\";
            rutaArhcivosServidor = aux.getAbsolutePath() + "\\archivos\\";
            file = new File(ruta);
            file.mkdirs();
            file.setReadable(true);
            file.setWritable(true);
        }catch(IOException ex){}
    }

   

    public void Menu()
    {
        int opcion;
        do{
            
            Scanner sca = new Scanner(System.in);
            System.out.println("Bienvenido, seleccione alguna de las siguientes opciones:");
            System.out.println("1. Subir archivos o carptea:");
            System.out.println("2. Descargar archivo");
            System.out.println("3. Listar conenido");
            System.out.println("4. Eliminar archivos");
            System.out.println("5. Salir");
            System.out.print("Su opcion:");
            opcion = Integer.parseInt(sca.nextLine());

            switch(opcion)
            {
                case 1:
                    Subir();
                    break;

                case 2:
                    Descargar();
                    break;

                case 3:
                    Listar();
                    break;

                case 4:
                    eliminarArchivos();
                    break;
                
                case 5:
                    System.out.println("Cerrando conexion");
                    break;
                
                default:
                    System.out.println("Introduzca un valor del 1-5");
            }
        }while(opcion != 5);
        
    
    }
    
    public void generarZIP(String file, String directorio)
    {
        byte[] buffer = new byte[8192];
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ZipOutputStream zoz = new ZipOutputStream(fos);
            System.out.println("Generando zip " + file);
            System.out.println("Cantidad de archivos en la carpeta: "+listaArchivos.size());

            for(String archivo: listaArchivos)
            {
                ZipEntry ze =  new ZipEntry(archivo);
                zoz.putNextEntry(ze);
                FileInputStream fis =  new FileInputStream(""+directorio + File.separator + archivo);
                int len;
                while((len = fis.read(buffer)) > 0){
                    zoz.write(buffer, 0 , len);
                }
                fis.close();
            }
            zoz.closeEntry();
            System.out.println("Archivo ZIP generado\n\n");



        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public String genZip(String archivo, String directorio)
    {
        return archivo.substring(directorio.length(), archivo.length());
            
    }


    public void generarListaArchivos(File f)
    {
        if(f.isFile())
        {
            listaArchivos.add(f.getName());
        }

        if(f.isDirectory()){
            String [] archivos = f.list();
            for(String nombreArhivo: archivos){
                generarListaArchivos(new File(f, nombreArhivo));
            }
        }
    }

    public void eliminarArchivos()
    {
        Scanner sca = new Scanner(System.in);
        int opcion;
        System.out.println("¿De que carpeta quieres borrar archivos?");
        System.out.println("1. Carpeta Servidor");
        System.out.println("2. Carpeta local");
        System.out.println("3. Cancelar");
        System.out.println("Su opcion: ");
        opcion = Integer.parseInt(sca.nextLine());
        File f = new File("");
        String rutaServer = f.getAbsolutePath() + "\\archivos\\";
        String ruta = f.getAbsolutePath() + "\\localArchivos\\";
        int contador = 0;
        switch(opcion)
        {
            case 1:
                try{
                    Cliente cl1 = new Cliente(8000);
                    JFileChooser jf1 = new JFileChooser(rutaServer);
                    jf1.setMultiSelectionEnabled(true);
                    jf1.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    jf1.showOpenDialog(null);
                    jf1.setRequestFocusEnabled(true);

                    File []seleccionadosSer = jf1.getSelectedFiles();
                    ObjectOutputStream oss = new ObjectOutputStream(cl1.socket.getOutputStream());
                    Datos d =  new Datos(seleccionadosSer, "eliminar");
                    oss.writeObject(d);
                    oss.flush();
                    //Escribimos cada uno de los archivos a enviar
                    for(File fi: seleccionadosSer)
                    {
                        Socket cl2 = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
                        System.out.println("Conectando con el servidor para eliminar datos...");
                        String nombre =  fi.getName();
                        String path = fi.getAbsolutePath();
                        long tam =  fi.length();
                        System.out.println("Preparando "+ path + " de " + tam + " bytes\n\n");
                        DataOutputStream dos = new DataOutputStream(cl2.getOutputStream());
                        DataInputStream dis = new DataInputStream(new FileInputStream(path));
                        dos.writeUTF(path);
                        dos.flush();
                        dos.writeLong(tam);
                        dos.flush();
                        dis.close();
                        dos.close();
                        cl2.close();
                    }

                }catch(IOException ex){
                    ex.printStackTrace();
                }
                
                break;
                
            case 2:
                JFileChooser jf = new JFileChooser(ruta);
                jf.setMultiSelectionEnabled(true);
                jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jf.showOpenDialog(null);
                jf.setRequestFocusEnabled(true);
                
                //Creamos un arreglo para guardar los archivos que seleccione
                File[] seleccionados = jf.getSelectedFiles();
                for(File fi: seleccionados)
                {
                    if(fi.delete())
                        contador++;
                }
                System.out.println("Hemos borrado "+contador+" archivos");
                break;
        }



    }

    public void Subir() 
    {    
        listaArchivos.clear();   
        //Agarramos los archivos del JFilechooser
        JFileChooser jf = new JFileChooser();
        jf.setMultiSelectionEnabled(true);
        jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jf.showOpenDialog(null);
        jf.setRequestFocusEnabled(true);
        
        //Creamos un arreglo para guardar los archivos que seleccione
        File[] seleccionados = jf.getSelectedFiles();
        
        //Mandamos la informacion a nuestro socket
        try
        {
            HashSet<File> archivosEnviar = new HashSet<>();
            HashSet<File> archivosZipEnviar = new HashSet<>();
            String ZipPath = "";
            Cliente cl1 = new Cliente(8000);
            ObjectOutputStream oss = new ObjectOutputStream(cl1.socket.getOutputStream());
            Datos d =  new Datos(seleccionados, "subir");
            oss.writeObject(d);
            oss.flush();

            //Ahora enviamos los archivos
            //Vamos añadiendo cada uno de los archivos HashSet dependiendo si es un archivo o un ZIP
            for(File f: seleccionados)
            {
                //Si el archivo es un directorio creamos el ZIP
                if(f.isDirectory())
                {
                    ZipPath = f.getAbsolutePath() + ".zip";
                    //System.out.println("" + ZipPath);
                    generarListaArchivos(f);
                    generarZIP(ZipPath, f.getAbsolutePath());
                    File zip = new File(ZipPath);
                    archivosEnviar.add(zip);
                    archivosZipEnviar.add(zip);
                }
                else
                    archivosEnviar.add(f);
            }

            //Ahora eviamos el hash al server
            for(File f : archivosEnviar)
            {
                Socket cl2 = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
                System.out.println("Conectando con el servidor para enviar datos...");
                String nombre =  f.getName();
                String path = f.getAbsolutePath();
                long tam =  f.length();
                System.out.println("Preparando "+ path + " de " + tam + " bytes\n\n");
                DataOutputStream dos = new DataOutputStream(cl2.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(path));
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
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
                    System.out.println("\rEnviado el " + porcentaje + " % del archivo");
                }
                System.out.println("Archivo enviado..\n");
                dis.close();
                dos.close();
                cl2.close();

            }

            for(File f: archivosZipEnviar){
                f.delete();
            }

            //Borramos el ZIP que creamos
            File ZIP = new File(ZipPath);
            if(ZIP.exists())
            {
                ZIP.delete();
                System.out.println("Se borro");
            }
                

        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }

    public void Descargar()
    {
        //Obtenemos la ruta donde estan almacenados los archivos del servidor
        File aux = new File("");
        String ruta = aux.getAbsolutePath() + "\\archivos\\";

        //Agarramos los archivos del JFilechooser en la carpeta de archivos
        JFileChooser jf = new JFileChooser(ruta);
        jf.setMultiSelectionEnabled(true);
        jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jf.showOpenDialog(null);
        jf.setRequestFocusEnabled(true);
        
        //Creamos un arreglo para guardar los archivos que seleccione
        File[] seleccionados = jf.getSelectedFiles();
        //Mandamos la informacion a nuestro socket
        try
        {
            HashSet<File> archivosEnviar = new HashSet<>();
            HashSet<File> archivosZipEnviar = new HashSet<>();
            Cliente cl1 = new Cliente(8000);
            ObjectOutputStream oss = new ObjectOutputStream(cl1.socket.getOutputStream());
            Datos d =  new Datos(seleccionados, "descargar");
            oss.writeObject(d);
            oss.flush();

            //Ahora enviamos los archivos
            //Vamos añadiendo cada uno de los archivos HashSet dependiendo si es un archivo o un ZIP
            for(File f: seleccionados)
            {
                //Si el archivo es un directorio creamos el ZIP
                if(f.isDirectory())
                {
                    String ZipPath = f.getAbsolutePath() + ".zip";
                    System.out.println("" + ZipPath);
                    generarListaArchivos(f);
                    generarZIP(ZipPath, f.getAbsolutePath());
                    File zip = new File(ZipPath);
                    archivosEnviar.add(zip);
                    archivosZipEnviar.add(zip);
                }
                else
                    archivosEnviar.add(f);
            }

            //Ahora eviamos el hash al server
            for(File f : archivosEnviar)
            {
                Socket cl2 = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
                System.out.println("Conectando con el servidor para descagar datos...");
                String nombre =  f.getName();
                String path = f.getAbsolutePath();
                long tam =  f.length();
                System.out.println("Preparando "+ path + " de " + tam + " bytes\n\n");
                DataOutputStream dos = new DataOutputStream(cl2.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(path));
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
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
                    System.out.println("\rDescargando el " + porcentaje + " % del archivo");
                }
                System.out.println("Archivo descargado...\n");
                dis.close();
                dos.close();
                cl2.close();

            }

            for(File f: archivosZipEnviar){
                f.delete();
            }

        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    public void Listar()
    {
        try
        {
            //Se conceta al servidor y enviamos la la opcion de listar contenido
            Cliente cl1 = new Cliente(8000);
            ObjectOutputStream oss = new ObjectOutputStream(cl1.socket.getOutputStream());
            Datos d =  new Datos("listar");
            oss.writeObject(d);
            oss.flush();
            //Se crea el aux para obtener la ruta de los archivos
            File aux = new File("");
            String ruta = aux.getAbsolutePath() + "\\archivos\\";

            //Empieza a listar los archivos que se encuentran en la carpeta
            DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(ruta));
            for(Path rutas : ds){
                System.out.println(rutas.getFileName());
            }
            
        }catch(IOException ex){
            ex.printStackTrace();
        }
       
    }

    public void Conectar(){
        try
        {
            System.out.println("Conexion con el servidor");
            Menu();
        }
        catch(Exception ex){
            System.out.println("No se conecto");
            ex.printStackTrace(); 
        }
    }


    public static void main(String[] args) {
        Cliente cl = new Cliente(1000);
        cl.Conectar();
    }


}