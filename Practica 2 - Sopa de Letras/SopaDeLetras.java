import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

class SopaDeLetras {
    String concepto;
    String[] conceptos, definiciones;
    Boolean[][] ocupacion;
    String[][] tablero;
    int LONGITUD = 15;
    int totalPalabras;
    int totalEncontradas;
    Boolean terminado;
    Map <Integer, Direccion> direcciones;
    Map <String, Palabra> palabras;
    List <String> palabrasEncontradas;

    SopaDeLetras(String concepto, String[] conceptos, String[] definiciones){
        this.concepto = concepto;
        this.conceptos = conceptos;
        this.definiciones = definiciones;
        this.totalPalabras = conceptos.length;

        this.terminado = false;
        this.totalEncontradas = 0;
        tablero = new String [this.LONGITUD][this.LONGITUD];
        ocupacion = new Boolean [this.LONGITUD][this.LONGITUD];

        for(int i = 0; i < this.LONGITUD; i++){
            for(int j = 0; j < this.LONGITUD; j++){
                tablero[i][j] = "-";
                ocupacion[i][j] = false;
            }
        }

        palabras = new HashMap <> ();
        palabrasEncontradas = new ArrayList <String>();
        direcciones = new HashMap <> ();
        direcciones.put(0, new Direccion("NORTE", 0, 1));
        direcciones.put(1, new Direccion("NORESTE", 1, 1));
        direcciones.put(2, new Direccion("ESTE", 1, 0));
        direcciones.put(3, new Direccion("SURESTE", 1, -1));
        direcciones.put(4, new Direccion("SUR", 0, -1));
        direcciones.put(5, new Direccion("SUROESTE", -1, -1));
        direcciones.put(6, new Direccion("OESTE", -1, 0));
        direcciones.put(7, new Direccion("NOROESTE", -1, 1));
            
        this.construirTablero();
    }

    public void construirTablero(){
        for(String concepto : this.conceptos){
            this.colocarPalabra(concepto);
        }

        // Se termina de rellenar la tabla
        for(int i = 0; i < this.LONGITUD; i++){
            for(int j = 0; j < this.LONGITUD; j++){
                if(!ocupacion[i][j]){
                    int assci = randomInt(97, 122);
                    char relleno = (char) assci;
                    tablero[i][j] = Character.toString(relleno);
                }
            }
        }
    }

    private void colocarPalabra(String concepto){
        Boolean posicionCorrecta = false;
        int minX, maxX, minY, maxY; 
        int longitud = concepto.length();
        Direccion direccion = null;
        Coordenadas inicio = new Coordenadas();

        minX = minY = 0;
        maxX = maxY = this.LONGITUD - 1;
        
        while(!posicionCorrecta){

            // Se supone que se genera una posicion correcta
            posicionCorrecta = true;
            direccion = this.direcciones.get(randomInt(0, 7));
            inicio = inicio.generarCoordenadas(minX, maxX, minY, maxY);

            // Revisar correcto solapamiento
            for(int i = 0; i < longitud; i++){
                int movX = inicio.x + (direccion.movX * i);
                int movY = inicio.y + (direccion.movY * i);

                if(movX >= this.LONGITUD || movX < 0){
                    posicionCorrecta = false;
                    break;
                }

                if(movY >= this.LONGITUD || movY < 0){
                    posicionCorrecta = false;
                    break;
                }

                if(ocupacion[movX][movY]){
                    if(!tablero[movX][movY].equals(concepto.substring(i, i + 1))){
                        posicionCorrecta = false;
                        break;
                    }
                }
            }

            // Se revisa que no haya una misma palabra con las misma coordenadas de inicio y misma direccion
            for (Map.Entry <String, Palabra> palabra : palabras.entrySet()) {
                Palabra guardada = palabra.getValue();

                if(direccion.mismaDireccion(guardada.direccion) && inicio.mismasCoords(guardada.coordsInicio)){
                    posicionCorrecta = false;
                    break;
                }
		    }
        }

        // Se ocupa el tablero
        int movX = 0;
        int movY = 0;

        for(int i = 0; i < longitud; i++){
            movX = inicio.x + (direccion.movX * i);
            movY = inicio.y + (direccion.movY * i);

            ocupacion[movX][movY] = true;
            tablero[movX][movY] = concepto.substring(i, i + 1);
        }

        // Se guarda la palabra
        int coordX = movX;
        int coordY = movY;
        Coordenadas coordsFinal = new Coordenadas(coordX, coordY);
        palabras.put(concepto, new Palabra(concepto, direccion, inicio, coordsFinal));
    }

    private void imprimirTablero(){

        System.out.println("S O P A   D E   L E T R A S\n");
        System.out.println("Concepto - " + this.concepto + "\n");

        System.out.print("   ");
        for (int i = 0; i < this.LONGITUD; i++){
            System.out.format("%3d", i);
        }

        System.out.println("");

        for(int i = 0; i < this.LONGITUD; i++){
            System.out.format("%3d ", i);

            for(int j = 0; j < this.LONGITUD; j++){
                System.out.print(" " + tablero[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    public String comenzarJuego() throws Exception {
        int opc = 1;
        Scanner teclado;
        String palabra;
        int inicioX, inicioY, finalX, finalY;
        String estats = "";

        while(opc != 2){
            limpiarPantalla();
            this.imprimirTablero();

            System.out.println("\nOpciones");
            System.out.println("\t1. Descubrir palabra");
            System.out.println("\t2. Salir");
            System.out.print("Elegir opcion: ");

            teclado = new Scanner(System.in);
            opc = teclado.nextInt();

            switch (opc){
                case 1:
                    System.out.print("\nEscribe la palabra: ");
                    teclado = new Scanner(System.in);
                    palabra = teclado.nextLine();

                    System.out.print("Escribe la coordenada x de inicio: ");
                    teclado = new Scanner(System.in);
                    inicioX = teclado.nextInt();

                    System.out.print("Escribe la coordenada y de inicio: ");
                    teclado = new Scanner(System.in);
                    inicioY = teclado.nextInt();

                    System.out.print("Escribe la coordenada x de final: ");
                    teclado = new Scanner(System.in);
                    finalX = teclado.nextInt();

                    System.out.print("Escribe la coordenada y de final: ");
                    teclado = new Scanner(System.in);
                    finalY = teclado.nextInt();

                    this.procesarEleccion(palabra, inicioY, inicioX, finalY, finalX);

                    if(this.comprobarFin()){
                        limpiarPantalla();
                        System.out.println("\nHAS GANADO!!!!\n");
                        opc = 2;
                        pausa("Presiona una tecla para continuar... ");
                    }

                    break;
                case 2:
                    break;
            }
        }
        estats += this.totalEncontradas + "-";
        estats += this.totalPalabras + "-";
        estats += this.terminado;

        return estats;
    }

    private void procesarEleccion(String palabra, int inicioX, int inicioY, int finalX, int finalY){
        if(this.eleccionValida(palabra, inicioX, inicioY, finalX, finalY)){

            // Se guarda la palabra como encontrada
            palabrasEncontradas.add(palabra);

            // Se extrae palabra para obtener movimiento
            Palabra p = palabras.get(palabra);

            // "\033[0;101m"

            for(int i = 0; i < palabra.length(); i++){
                int movX = p.coordsInicio.x + (p.direccion.movX * i);
                int movY = p.coordsInicio.y + (p.direccion.movY * i);

                tablero[movX][movY] = "\033[0;101m" + tablero[movX][movY] + "\033[0m";
            }

            System.out.println("\nCorrecto!!!!\n");
            pausa("Presiona una tecla para continuar... ");
        }
        else {
            System.out.println("\nEstas equivocado :((\n");
            pausa("Presiona una tecla para continuar... ");
        }
    }

    private Boolean eleccionValida(String palabra, int inicioX, int inicioY, int finalX, int finalY){

        Palabra p = palabras.get(palabra);

        if (p == null){
            return false;
        }
        else if (p.coordsInicio.mismasCoords(new Coordenadas(inicioX, inicioY)) && p.coordsFinal.mismasCoords(new Coordenadas(finalX, finalY))){
            return !palabrasEncontradas.contains(palabra);  
        }              
        else
            return false;
    }

    private Boolean comprobarFin(){
        if(palabrasEncontradas.size() == this.totalPalabras){
            this.terminado = true;
            this.totalEncontradas = palabrasEncontradas.size();
            return true;
        }
        else{
            this.totalEncontradas = palabrasEncontradas.size();
            return false;
        }
    }

    public int randomInt(int min, int max){
        Random random = new Random();
		return random.nextInt(max - min + 1) + min;
    }

    public void limpiarPantalla() throws Exception {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    public void pausa(String mensaje){
        System.out.print(mensaje);

        Scanner teclado = new Scanner(System.in);
        String cadena = teclado.nextLine();
    }

    public class Palabra {
        String palabra;
        Direccion direccion;
        Coordenadas coordsInicio;
        Coordenadas coordsFinal;

        Palabra(String palabra, Direccion direccion, Coordenadas coordsInicio, Coordenadas coordsFinal){
            this.palabra = palabra;
            this.direccion = direccion;
            this.coordsInicio = coordsInicio;
            this.coordsFinal = coordsFinal;
        }
    }

    public class Coordenadas{
        int x;
        int y;

        Coordenadas(int x, int y){
            this.x = x;
            this.y = y;
        }

        Coordenadas(){

        }

        public Coordenadas generarCoordenadas(int minX, int maxX, int minY, int maxY){
            return new Coordenadas(randomInt(minX, maxX), randomInt(minY, maxY));
        }

        public Boolean mismasCoords(Coordenadas c){
            return c.x == this.x && c.y == this.y;
        }
    }

    public class Direccion {
        String direccion;
        int movX;
        int movY;

        Direccion(String direccion, int movX, int movY){
            this.direccion = direccion;
            this.movX = movX;
            this.movY = movY;
        }

        public Boolean mismaDireccion(Direccion d){
            return this.direccion.equals(d.direccion);
        }
    }
}