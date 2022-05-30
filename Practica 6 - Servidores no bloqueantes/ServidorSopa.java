import java.nio.channels.*;
import java.nio.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// CLASE DE MENSAJE
class Mensaje implements Serializable {
  String peticion;
  String [] arreglo;

  public Mensaje(String peticion, String[] arreglo){
      this.peticion = peticion;
      this.arreglo = arreglo;
  }
}

// CLASE PRINCIPAL

public class ServidorSopa {
    String rutaJuegos = "Servidor/juegos";
    SelectionKey k;
    Selector sel;

    private String[] getJuegos(){
        File carpeta = new File(this.rutaJuegos);
        return carpeta.list();
    }

    public void enviarDatos(Mensaje m) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(m);
        oos.flush();
        ByteBuffer b = ByteBuffer.wrap(baos.toByteArray());
        SocketChannel sc = (SocketChannel) k.channel();
        sc.write(b);
    }

    public String[] leerArchivoJuego(String juego) throws Exception {
        String rutaArchivo = rutaJuegos + "/" + juego + "/";
        String nombreArchivo = juego + ".txt";
        List<String> conceptos = new ArrayList<String>();

        File juegoArchivo = new File(rutaArchivo + nombreArchivo);
        Scanner lector = new Scanner(juegoArchivo);

        while (lector.hasNextLine()){
            conceptos.add(lector.nextLine());
        }

        return conceptos.toArray(new String[0]);
    }

    private void procesarPeticion(String peticion) throws Exception {
        // Se pide la lista de juegos
        if(peticion.equals("getJuegos")){
            String[] juegos = this.getJuegos();
            System.out.println("\nSe ha recibido una peticion para consultar las sopas disponibles...");
            this.enviarDatos(new Mensaje("getJuegos", juegos));
            System.out.println("Se enviaron los juegos disponibles...");
        }
        // Se pide un juego
        else if (peticion.contains("juego-")){
            String juego = peticion.substring(6);
        System.out.println("\nSe ha recibido una peticion para enviar los conceptos y definiciones del juego " + juego + "...");
            String[] conceptos = this.leerArchivoJuego(juego);
            this.enviarDatos(new Mensaje("juego", conceptos));
            System.out.println("Se enviaron los conceptos y definiciones del juego " + juego + "...");
        }
        // Se mandan las estadisticas
        else if (peticion.contains("estats-")){
            String juego = peticion.substring(7);
            System.out.println("\nSe ha recibido una peticion para enviar las estdisticas del juego " + juego + "...");
            String[] estats = this.obtenerEstats(juego);
            this.enviarDatos(new Mensaje("estats", estats));
            System.out.println("Se enviaron las estadisticas del juego " + juego + "...");
        }
        // Se envian las estadisticas
        else {
            System.out.println("\nSe ha recibido una peticion para guardar las estadisticas de un juego...");
            String[] estats = peticion.split("-");
            this.guardarEstadisticas(estats);
            this.enviarDatos(new Mensaje("guardarEstadisticas", null));
            System.out.println("Se guardaron las estadisticas del juego...");
        }
    }

    public void iniciarServidor() throws Exception {

        ServerSocketChannel s = ServerSocketChannel.open();
           s.configureBlocking(false);
           s.socket().bind(new InetSocketAddress(9999));
           sel = Selector.open();
           s.register(sel, SelectionKey.OP_ACCEPT);
           this.limpiarPantalla();
           System.out.println("S E R V I D O R   L I S T O");
           System.out.println("Esperando clientes...");
           while(true){
               sel.select();
               Iterator<SelectionKey> it = sel.selectedKeys().iterator();
               while(it.hasNext()){
                   k = (SelectionKey) it.next();
                   it.remove();

                   // CONEXION
                   if(k.isAcceptable()){
                       ServerSocketChannel sch = (ServerSocketChannel)k.channel();
                       SocketChannel cl = sch.accept();
                       System.out.println("\n\nCliente conectado desde: " + cl.socket().getInetAddress() + ":" + cl.socket().getPort());
                       cl.configureBlocking(false);
                       cl.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                       continue;
                   }

                   // LECTURA
                   if(k.isReadable()){
                       ByteBuffer b = ByteBuffer.allocate(16000);
                       b.clear();
                       SocketChannel ch = (SocketChannel) k.channel();
                       ch.read(b);
                       b.flip();

                       if(b.hasArray())
                       {
                           ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b.array()));
                           Mensaje m = (Mensaje) ois.readObject();
                           this.procesarPeticion(m.peticion);
                           k.cancel();
                       }

                   }else if (k.isWritable()){

                   }//else
               }//while
           }//while
    }

    private String[] obtenerEstats(String juego) throws Exception{
        List<String> estatsTodos = new ArrayList<String>();
        String estats = ""; 
        File estatsArchivo;
        Scanner lector;

        String ruta = rutaJuegos + "/" + juego + "/estadisticas/";
        File carpeta = new File(ruta);
        String[] archivos = carpeta.list();

        for(String archivo : archivos){
            estatsArchivo = new File(ruta + archivo);
            lector = new Scanner(estatsArchivo);

            while (lector.hasNextLine()){
                estats += lector.nextLine();

                if(lector.hasNextLine()){
                    estats += "-";
                }
            }

            //estatsArchivo.close();
            estatsTodos.add(estats);
            estats = "";
        }

        return estatsTodos.toArray(new String[0]);
    }

    private void guardarEstadisticas(String[] estats) throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
        String horario = dtf.format(LocalDateTime.now());
        String carpeta = rutaJuegos + "/" + estats[0] + "/estadisticas/";

        File file = new File(carpeta + estats[1] + horario + ".txt");

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(estats[1]);
        bw.newLine();
        bw.write(estats[2]);
        bw.newLine();
        bw.write(estats[3]);
        bw.newLine();
        bw.write(estats[4]);
        bw.newLine();
        bw.write(estats[5]);
        bw.close();
    }

    public void limpiarPantalla() throws Exception {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    public static void main(String[] args) {
       try {
           ServidorSopa s = new ServidorSopa();
           s.iniciarServidor();

       } catch(Exception e){
           e.printStackTrace();
       }//catch
   }//main
}
