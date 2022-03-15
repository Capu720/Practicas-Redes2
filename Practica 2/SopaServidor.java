import java.io.*;
import java.net.*;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.FileWriter;

class SopaServidor {
    int puerto;
    String rutaJuegos = "Servidor/juegos";
    int LONGITUD = 65535;
    DatagramPacket p;
    DatagramSocket s;

    SopaServidor(int puerto) throws Exception{
        this.puerto = puerto;
        this.p = new DatagramPacket(new byte[LONGITUD], LONGITUD);

        this.s = new DatagramSocket(this.puerto);
        this.s.setReuseAddress(true);
        this.s.setBroadcast(true); 
    }

    private String[] getJuegos(){
        File carpeta = new File(this.rutaJuegos);
        return carpeta.list();
    }

    private void enviarJuegos(String[] juegos) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(juegos);
        oos.flush();
        byte[] b = baos.toByteArray();
        DatagramPacket p = new DatagramPacket(b, b.length, this.p.getAddress(), this.p.getPort());
        this.s.send(p);
    }

    private void enviarConceptos(String[] conceptos) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(conceptos);
        oos.flush();
        byte[] b = baos.toByteArray();
        DatagramPacket p = new DatagramPacket(b, b.length, this.p.getAddress(), this.p.getPort());
        this.s.send(p);
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

    private void enviarEstats(String[] estats) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(estats);
        oos.flush();
        byte[] b = baos.toByteArray();
        DatagramPacket p = new DatagramPacket(b, b.length, this.p.getAddress(), this.p.getPort());
        this.s.send(p);
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

    private void iniciarServidor() throws Exception{
        while(true){
            this.s.receive(this.p);
            String peticion = new String(this.p.getData(), 0, this.p.getLength());
            this.procesarPeticion(peticion);
        }
    }

    private void procesarPeticion(String peticion) throws Exception{
        // Se pide la lista de juegos
        if(peticion.equals("getJuegos")){
            String[] juegos = this.getJuegos();
            System.out.println("Se ha recibido una peticion para consultar las sopas disponibles de " + p.getAddress() + "...");
            this.enviarJuegos(juegos);
            System.out.println("Se enviaron los juegos disponibles a " + p.getAddress() + "...");
        }
        // Se pide un juego
        else if (peticion.contains("juego-")){
            String juego = peticion.substring(6);
            System.out.println("Se ha recibido una peticion para enviar los conceptos y definiciones del juego " + juego + " de " + p.getAddress() + "...");
            String[] conceptos = this.leerArchivoJuego(juego);
            this.enviarConceptos(conceptos);
            System.out.println("Se enviaron los conceptos y definiciones del juego " + juego + " a " + p.getAddress() + "...");
        }
        // Se mandan las estadisticas
        else if (peticion.contains("estats-")){
            String juego = peticion.substring(7);
            System.out.println("Se ha recibido una peticion para enviar las estdisticas del juego " + juego + " de " + p.getAddress() + "...");
            String[] estats = this.obtenerEstats(juego);
            this.enviarEstats(estats);
            System.out.println("Se enviaron las estadisticas del juego " + juego + " a " + p.getAddress() + "...");
        }
        // Se envian las estadisticas
        else {
            System.out.println("Se ha recibido una peticion para guardar las estadisticas de un juego de " + p.getAddress() + "...");
            String[] estats = peticion.split("-");
            this.guardarEstadisticas(estats);
            System.out.println("Se guardaron las estadisticas del juego de " + p.getAddress() + "...");
        }
    }

    public static void main(String[] args){
        try {
            SopaServidor ss = new SopaServidor(Integer.parseInt(args[0]));
            ss.iniciarServidor();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}