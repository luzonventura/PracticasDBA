import java.io.*;
import java.nio.file.*;
import java.util.Random;

/**
 * Clase encargada de generar un mapa aleatorio y moverlo a la carpeta de mapas
 */
public class GeneradorMapa {

    /**
     * Se encarga de generar un mapa aleatorio y moverlo a la carpeta de mapas
     * @param probabilidadObstaculo Probabilidad de que una celda sea un obstáculo (-1), sobre 1
     */
    public static void Crear_mover(float probabilidadObstaculo) {
        // Dimensiones del mapa
        int filas = 10;
        int columnas = 10;

        // Directorio inicial, nombre del archivo y ruta de origen
        String carpetaOrigen = System.getProperty("user.dir");
        String nombreArchivo = "Random" + probabilidadObstaculo + ".txt";

        String rutaOrigen = carpetaOrigen + File.separator + nombreArchivo;

        // Ruta de destino
        Path rutaDestino = Paths.get(carpetaOrigen).resolve("maps").resolve(nombreArchivo);
        
        try {
            // Generar el mapa
            int[][] mapa = generarMapa(filas, columnas, probabilidadObstaculo);

            //Guardar el mapa en un archivo
            guardarMapaEnArchivo(mapa, rutaOrigen);

            // Mover el archivo a la carpeta de mapas
            moverArchivo(rutaOrigen, rutaDestino.toString());

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Genera un mapa aleatorio con las dimensiones y probabilidad especificadas
     *
     * @param filas Número de filas del mapa
     * @param columnas Número de columnas del mapa
     * @param probabilidadObstaculo Probabilidad de que una celda sea un obstáculo (-1)
     * @return Array bidimensional con el mapa generado
     */
    private static int[][] generarMapa(int filas, int columnas, double probabilidadObstaculo) {
        Random random = new Random();
        int[][] mapa = new int[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                // Genera un -1 con una probabilidad igual a probabilidadObstaculo, o 0 en otro caso.
                mapa[i][j] = random.nextDouble() < probabilidadObstaculo ? -1 : 0;
            }
        }

        return mapa;
    }

    /**
     * Guarda el mapa en un archivo de texto
     *
     * @param mapa El mapa a guardar
     * @param rutaArchivo Ruta completa del archivo de salida
     */
    private static void guardarMapaEnArchivo(int[][] mapa, String rutaArchivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            int filas = mapa.length;
            int columnas = mapa[0].length;

            // Escribir las dimensiones
            writer.write(filas + "\n");
            writer.write(columnas + "\n");

            // Escribir el contenido del mapa
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    writer.write(mapa[i][j] + (j < columnas - 1 ? "\t" : ""));
                }
                writer.newLine();
            }
        }
    }


    /**
     * Mueve un archivo de una ubicación a otra
     *
     * @param rutaOrigen Ruta completa del archivo origen
     * @param rutaDestino Ruta completa del archivo destino
     */
    private static void moverArchivo(String rutaOrigen, String rutaDestino) throws IOException {
        Path origen = Paths.get(rutaOrigen);
        Path destino = Paths.get(rutaDestino);

        Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
    }
}