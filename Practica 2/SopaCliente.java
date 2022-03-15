import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

class SopaCliente {

    static long tiempo;
    String direccion, juego;
    int puerto;
    DatagramSocket cl;
    String[] concepto, definicion;
    boolean terminado = false;
    int encontradas = 0;
    int aEncontrar = 15;

    SopaCliente(String direccion, int puerto) throws Exception{
        this.direccion = direccion;
        this.puerto = puerto;

        this.cl = new DatagramSocket();
        this.cl.setBroadcast(true);
    }

    public String[] getJuegos() throws Exception {

        // Se piden los juegos
        this.enviarPeticion("getJuegos");

        // Se reciben los juegos
        DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
        this.cl.receive(p);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p.getData()));
        String[] juegos = (String[]) ois.readObject();

        return juegos;
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

    public String[] obtenerPalabras() throws Exception {

        // Se piden los conceptos
        this.enviarPeticion("juego-" + this.juego);

        // Se reciben los conceptos
        DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
        this.cl.receive(p);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p.getData()));
        String[] conceptos = (String[]) ois.readObject();

        return conceptos;
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

    private void enviarPeticion(String msj) throws Exception {
        InetAddress dst = InetAddress.getByName(this.direccion);
        byte[] b = msj.getBytes();
        DatagramSocket cl = new DatagramSocket();
        this.cl.setBroadcast(true);
        DatagramPacket p = new DatagramPacket(b, b.length, dst, this.puerto);
        this.cl.send(p);
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

    public String[] obtenerEstadisticas() throws Exception {

        // Se hace la peticion de las estadisticas
        this.enviarPeticion("estats-" + this.juego);

        // Se obtienen las estats
        DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
        this.cl.receive(p);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p.getData()));
        String[] estats = (String[]) ois.readObject();

        return estats;
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

    public void iniciarJuego() throws Exception {
        limpiarPantalla();

        String[] juegos = this.getJuegos();
        this.setJuego(this.elegirJuego(juegos));
        String[] conceptos = this.obtenerPalabras();
        this.descomponerConceptos(conceptos);
        this.jugar();
        this.guardarEstadisticas();
        String[] estats = this.obtenerEstadisticas();
        this.mostrarEstadisticas(estats);
    }

    public void limpiarPantalla() throws Exception {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    public static void main(String[] args){
        String direccion;
        String[] juegos;
        int puerto;

        try {

            System.out.println("\nC O N F I G U R A C I O N   D E L   J U E G O\n");
            System.out.print("Ingresa la direccion IP: ");
            Scanner teclado = new Scanner(System.in);
            direccion = teclado.nextLine();

            System.out.print("Ingresa el puerto: ");
            teclado = new Scanner(System.in);
            puerto = teclado.nextInt();

        
            SopaCliente sc = new SopaCliente(direccion, puerto);
            sc.iniciarJuego();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}