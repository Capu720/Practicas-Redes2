import java.nio.channels.*;
import java.nio.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

class Mensaje implements Serializable {
  String peticion;
  String [] arreglo;

  public Mensaje(String peticion, String[] arreglo){
      this.peticion = peticion;
      this.arreglo = arreglo;
  }
}//class

public class ClienteSopa {
    String juego;
    String [] concepto;
    String [] definicion;
    boolean terminado = false;
    int encontradas = 0;
    int aEncontrar = 15;
    static long tiempo;

    public void procesarRespuesta(Mensaje m) throws Exception
    {
        // Se escoge el juego y se piden los conceptos
        if (m.peticion.contains("getJuegos"))
        {
            this.limpiarPantalla();
            this.setJuego(this.elegirJuego(m.arreglo));
        }
        // Se piden los conceptos
        else if (m.peticion.contains("juego"))
        { 
            this.descomponerConceptos(m.arreglo);
        }
        // Se piden las estadisticas
        else if (m.peticion.contains("guardarEstadisticas"))
        {
            System.out.println("Guardado con exito");
        }
        else if (m.peticion.contains("estats"))
        {
            this.mostrarEstadisticas(m.arreglo);
        }
    }

    public void descomponerConceptos(String[] conceptos){
        List<String> listaConceptos = new ArrayList<String>();
        List<String> listaDefiniciones = new ArrayList<String>();

        for(int i = 0; i < conceptos.length - 1; i += 2){
            listaConceptos.add(conceptos[i]);
            listaDefiniciones.add(conceptos[i + 1]);
        }

        this.concepto = listaConceptos.toArray(new String[0]);
        this.definicion = listaDefiniciones.toArray(new String[0]);
    }

    public String elegirJuego(String[] juegos){
       System.out.println("J U E G O S   D I S P O N I B L E S\n");

        for(int i = 0; i < juegos.length; i++)
            System.out.println((i + 1) + ".- " + juegos[i]);

        System.out.print("\nElige un juego: ");
        Scanner teclado = new Scanner(System.in);
        int pos = teclado.nextInt() - 1;

        return juegos[pos];
    }

    public void setJuego(String juego){
        this.juego = juego;
    }

    public void enviarPeticion(String peticion) throws Exception
    {
        SocketChannel cl = SocketChannel.open();
        cl.configureBlocking(false);
        Selector sel = Selector.open();
        cl.register(sel, SelectionKey.OP_CONNECT);
        cl.connect(new InetSocketAddress("127.0.0.1", 9999));

        Boolean terminar = true;

        while(terminar)
        {
            sel.select();
            Iterator <SelectionKey> it = sel.selectedKeys().iterator();

            while(it.hasNext())
            {
                SelectionKey k = (SelectionKey) it.next();
                it.remove();

                if(k.isConnectable())
                {
                    SocketChannel ch = (SocketChannel) k.channel();

                    if(ch.isConnectionPending())
                    {
                        try
                        {   
                            ch.finishConnect();
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }//catch
                    }//if

                    ch.configureBlocking(false);
                    ch.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    continue;
                }

                // ESCRITURA
                if (k.isWritable()) 
                {
                    Mensaje m = new Mensaje(peticion, null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(m);
                    oos.flush();
                    ByteBuffer b = ByteBuffer.wrap(baos.toByteArray());
                    SocketChannel sc = (SocketChannel) k.channel();
                    sc.write(b);
                }

                // LECTURA 
                else if(k.isReadable())
                {
                    ByteBuffer b = ByteBuffer.allocate(4000);
                    b.clear();
                    SocketChannel ch = (SocketChannel) k.channel();
                    ch.read(b);
                    b.flip();
                    if(b.hasArray()) 
                    {
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b.array()));
                        Mensaje m = (Mensaje) ois.readObject();
                        ch.register(sel, SelectionKey.OP_WRITE);
                        this.procesarRespuesta(m);
                        terminar = false;
                    }//if
                }
            } // while
        }//while*/
    }

    public void prepararJuego() throws Exception 
    {
        this.enviarPeticion("getJuegos");
        this.enviarPeticion("juego-" + this.juego);
        this.jugar();
        this.guardarEstadisticas();
        this.enviarPeticion("estats-" + this.juego);
    }

    public void jugar() throws Exception{
        // Se crea la sopa de letras
        SopaDeLetras sdl = new SopaDeLetras(this.juego, this.concepto, this.definicion);

        // Se toma el inicio del tiempo para jugar
        long tiempoInicial = System.nanoTime();
        String estats = sdl.comenzarJuego();

        long tiempoFinal = System.nanoTime();
        this.tiempo = (tiempoFinal - tiempoInicial) / 1000000000;

        // Se sacan las estadisticas del juego reciente terminado
        String[] juegoReciente = estats.split("-");

        this.encontradas = Integer.parseInt(juegoReciente[0]);
        this.aEncontrar = Integer.parseInt(juegoReciente[1]);
        this.terminado = Boolean.parseBoolean(juegoReciente[2]);
    }

    public void guardarEstadisticas() throws Exception {
        limpiarPantalla();
        System.out.println("G U A R D A   T U S   E S T A D I S T I C A S\n");
        
        System.out.print("Dale un nombre a estas estadisticas: ");
        Scanner teclado = new Scanner(System.in);
        String nombre = teclado.nextLine();
        
        String estats = this.juego + "-";
        estats += nombre + "-";
        estats += this.tiempo + "-";
        estats += this.terminado + "-";
        estats += this.encontradas + "-";
        estats += this.aEncontrar;

        this.enviarPeticion(estats);
    }

    private void mostrarEstadisticas(String[] estats) throws Exception {
        limpiarPantalla();
        System.out.println("E S T A D I S T I C A S   D E L   J U E G O\n");
        System.out.println("Juego: " + this.juego + "\n");

        for(String estatsJuego : estats){
            this.mostrarDatos(estatsJuego.split("-"));
            System.out.println("");
        }
    }

    private void mostrarDatos(String[] estats) {
        System.out.println("Nombre: " + estats[0]);
        System.out.println("Tiempo: " + estats[1]);
        String terminado = Boolean.parseBoolean(estats[2]) ? "TERMINADO" : "NO TERMINADO";
        System.out.println(terminado);
        System.out.println("Palabras encontradas: " + estats[3] + " de " + estats[4]);   
    }

    public void limpiarPantalla() throws Exception {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    public static void main(String[] args){
       try{
           ClienteSopa c = new ClienteSopa();
           c.prepararJuego();
           
       }catch(Exception e){
           e.printStackTrace();
       }//catch
   }//main
}
