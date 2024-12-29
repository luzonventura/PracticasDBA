import java.util.List;

/**
 * La clase Entorno representa el entorno inmediato que rodea al agente.
 * Esta clase permite al agente "ver" las celdas contiguas
 * y actualizar su percepción sobre el mundo.
 */
public class Entorno {
    private Mapa mapa;
    private int[] posicionAgente;

    /**
     * Constructor que inicializa el entorno con el mapa y la posición inicial del agente.
     *
     * @param mapa El mapa del mundo en el que se mueve el agente.
     * @param posicionInicial Posición inicial del agente en el mapa
     */
    public Entorno(Mapa mapa, int[] posicionInicial) {
        this.mapa = mapa;
        this.posicionAgente = posicionInicial;
    }

    /**
     * Método que permite cambiar la posición actual del agente.
     * 
     * Al usar este metodo podriamos acceder a posiciones fuera de los sensores del agente
     * por lo que este método solo puede ser usado en nodos que se hayan añadido a una lista de 
     * nodos vecinos de algun nodo ya visitado, por lo que para evitar errores se comprueba que
     * la celda a la que se quiere mover es vecina de alguna de las celdas visitadas.
     *
     * @param nuevaPosicion La nueva posición del agente
     */
    public void actualizarPosicion(int[] nuevaPosicion, List<int[]> visitados) {
        if (!esVecino(visitados, nuevaPosicion)) {
            throw new IllegalArgumentException("La celda no es vecina");
        }
        this.posicionAgente = nuevaPosicion;
    }

    /**
     * Método que permite ver las celdas contiguas al agente. 
     * Y las devuelve como lista de sus posiciones.
     *
     * @return Lista de las celdas contiguas al agente.
     */
    public List<int[]> see() {
        int fila = posicionAgente[0];
        int columna = posicionAgente[1];

        List<int[]> celdasContiguas = List.of(
            new int[] {fila - 1, columna}, // Arriba
            new int[] {fila + 1, columna}, // Abajo
            new int[] {fila, columna - 1}, // Izquierda
            new int[] {fila, columna + 1} // Derecha
        );

        return celdasContiguas;
    }

    /**
     * Método que permite comprobar si una celda es accesible.
     * Para evitar que se intenten acceder celdas que no son vecinas,
     * se lanza una excepción si la celda no es vecina.
     * 
     * @return true si la celda es accesible, false en otro caso.
     */
    public boolean esCeldaAccesible(int fila, int columna) {
        List<int[]> celdasVecinas = see();
        for (int[] celda : celdasVecinas) {
            if (celda[0] == fila && celda[1] == columna) {
                return mapa.esCeldaAccesible(fila, columna);
            }
        }
        
        // Si la celda no es vecina, da error
        throw new IllegalArgumentException("La celda no es vecina");
        
    }

    /**
     * Método que permite obtener el número de filas del mapa
     * Usado para desvincular el agente del mapa totalmente
     * 
     * @return Número de filas del mapa
     */
    public int getFilas() {
        return mapa.getFilas();
    }

    /**
     * Método que permite obtener el número de columnas del mapa
     * Usado para desvincular el agente del mapa totalmente
     * 
     * @return Número de columnas del mapa
     */
    public int getColumnas() {
        return mapa.getColumnas();
    }

    /**
     * Metodo que con una lista de posiciones y una posicion comprueba
     * si esa posicion es vecina de alguna de las posiciones de la lista
     * 
     * @param posiciones Lista de posiciones
     * @param posicion Posicion a comprobar si es vecina
     * @return true si es vecina o es la propia celda, false en otro caso
     */
    public boolean esVecino(List<int[]> posiciones, int[] posicion) {
        for (int[] pos : posiciones) {
            if (pos[0] == posicion[0]+1 && pos[1] == posicion[1] 
                || pos[0] == posicion[0]-1 && pos[1] == posicion[1] 
                || pos[0] == posicion[0] && pos[1] == posicion[1]+1  
                || pos[0] == posicion[0] && pos[1] == posicion[1]-1
                || pos[0] == posicion[0] && pos[1] == posicion[1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método que permite inicializar la posición del agente.
     */	
    public void inicializaPosicion(int[] posicionInicial) {
        this.posicionAgente = posicionInicial;
    }

    public int[] getPosicionAgente() {
        return posicionAgente;
    }
}