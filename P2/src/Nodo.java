/**
 * Clase que representa un nodo en el algoritmo A*
 * 
 * Cada nodo tiene una fila y una columna que representan su posición en el tablero
 * Además, tiene un puntero al nodo padre, que es el nodo desde el cual se llegó a este nodo
 * 
 * También tiene un costoG, que es el costo desde el inicio hasta este nodo.
 * Y un costoH, que es una heurística que estima el costo desde este nodo hasta el objetivo
 * 
 * El costoTotal es la suma de costoG y costoH
 */

public class Nodo implements Comparable<Nodo> {
    private int fila;
    private int columna;
    private Nodo padre;
    private double costoG; // Costo desde el inicio hasta este nodo
    private double costoH; // Heurística (estimación del costo desde este nodo hasta el objetivo)
    private double costoTotal; // costoTotal = costoG + costoH

    /*
     * Constructor de la clase Nodo
     */
    public Nodo(int fila, int columna, Nodo padre, double costoG, double costoH) {
        this.fila = fila;
        this.columna = columna;
        this.padre = padre;
        this.costoG = costoG;
        this.costoH = costoH;
        this.costoTotal = costoG + costoH;
    }

    /**
     * Metodo que devuelve la fila del nodo
     * @return fila del nodo
     */
    public int getFila() {
        return fila;
    }

    /**
     * Metodo que devuelve la columna del nodo
     * @return columna del nodo
     */
    public int getColumna() {
        return columna;
    }

    /**
     * Metodo que devuelve el nodo padre
     * @return nodo padre
     */
    public Nodo getPadre() {
        return padre;
    }

    /**
     * Metodo que devuelve el costo para llegar a este nodo desde el inicio
     * @return  costo para llegar a este nodo desde el inicio
     */
    public double getCostoG() {
        return costoG;
    }

    /**
     * Metodo que devuelve el costo de la heurística desde este nodo hasta el objetivo
     * @return costo de la heurística desde este nodo hasta el objetivo
     */
    public double getCostoH() {
        return costoH;
    }

    /**
     * Metodo que permite comparar dos nodos
     */
    @Override
    public int compareTo(Nodo otro) {
        return Double.compare(this.costoTotal, otro.costoTotal);
    }

    /**
     * Metodo que permite comparar si dos nodos son iguales
     * @return true si los nodos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Nodo) {
            Nodo otro = (Nodo) obj;
            return this.fila == otro.fila && this.columna == otro.columna;
        }
        return false;
    }

    /**
     * Metodo que permite obtener el hashcode de un nodo, sirve para ordenar los nodos
     */
    @Override
    public int hashCode() {
        return fila * 31 + columna;
    }
}
