import jade.core.Agent;
import javax.swing.*;
import java.util.*;

/**
 * Clase que representa un agente móvil que se mueve en un mapa para alcanzar un objetivo
 * El agente se mueve cada 0.5 segundos y consume 1 unidad de energía por paso.
 * El agente se mueve en 4 direcciones (arriba, abajo, izquierda, derecha).
 * 
 * El movimiento del agente se realiza mediante comportamientos
 */
public class AgenteMovil extends Agent {
    ///////////////////////////////////////////////////////////////////
    /// ATRIBUTOS DE LA CLASE /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    private int[] posicionAgente;        
    private int[] posicionObjetivo;                     
    private Interfaz interfaz;           
    private double direccionAgente = 0;  

    private Entorno entorno;
    private String tipo;
    private String modo;

    private Nodo nodoActual;
    private Nodo mejorNodo = null;
    private List<Nodo> posicionesAbiertas = new ArrayList<>();
    private Set<String> posicionesCerradas = new HashSet<>();
    private List<int[]> posicionesDescubiertas = new ArrayList<>();

    ///////////////////////////////////////////////////////////////////
    /// MÉTODO DE INICIALIZACIÓN //////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    protected void setup() {
        
        // Obtener los argumentos pasados al agente
        Object[] args = getArguments();
        
        if (args != null && args.length == 9) {

            modo = (String) args[0];
            posicionAgente = new int[]{(int) args[3], (int) args[4]};
            posicionObjetivo = new int[]{(int) args[5], (int) args[6]};
            tipo = (String) args[7];

            entorno = (Entorno) args[1];
            entorno.inicializaPosicion(posicionAgente);
            interfaz = (Interfaz) args[2];
            
        } else {
            JOptionPane.showMessageDialog(null, "No se proporcionaron los argumentos necesarios.");
            doDelete();
            return;
        }

        
        // Mostrar las posiciones en la interfaz gráfica
        interfaz.setPosicionAgente(posicionAgente);
        interfaz.setPosicionObjetivo(posicionObjetivo);
        interfaz.setDireccionAgente(0);
        interfaz.repaintMapa();

        // Inicializar las posiciones conocidas o extraerlas de la memoria si ya existen
        if(interfaz.getNodoActual() != null){
            nodoActual = interfaz.getNodoActual();
        }
        else{
            nodoActual = new Nodo(posicionAgente[0], posicionAgente[1], null, 0, calcularHeuristica(posicionAgente));
        }
        if(interfaz.getPosicionesCerradas() != null && !interfaz.getPosicionesCerradas().isEmpty()){
            posicionesCerradas = interfaz.getPosicionesCerradas();
        }
        else{
            posicionesCerradas.add(posicionAgente[0] + "," + posicionAgente[1]);
        }
        if(interfaz.getPosiciones() != null && !interfaz.getPosiciones().isEmpty()){
            posicionesDescubiertas = interfaz.getPosiciones();
        }
        else{
            posicionesDescubiertas.add(posicionAgente);
        }
        if(interfaz.getPosicionesAbiertas() != null && !interfaz.getPosicionesAbiertas().isEmpty()){
            posicionesAbiertas = interfaz.getPosicionesAbiertas();
        }
        else{
            if(posicionesCerradas.stream().noneMatch(cerrada -> cerrada.equals(nodoActual.getFila() + "," + nodoActual.getColumna()))){
                posicionesAbiertas.add(nodoActual);
            }
        }
        
        mejorNodo = nodoActual;
        
        
        // Lanza el comportamiento del agente
        addBehaviour(new ComportamientoAgente(this));
    } 

    
    ///////////////////////////////////////////////////////////////////
    /// HEURÍSTICA DISTANCIA EUCLIDIANA ///////////////////////////////
    ///////////////////////////////////////////////////////////////////
    private double calcularHeuristica(int[] posicion) {
        int dx = Math.abs(posicion[0] - posicionObjetivo[0]);
        int dy = Math.abs(posicion[1] - posicionObjetivo[1]);
        
        
        return Math.sqrt(dx * dx + dy * dy); 
    }
    
    
    ///////////////////////////////////////////////////////////////////
    /// A*       //////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    /****************************************************************
    * Para calcular el A* vamos explorando las celdas y su vecinas 
    * accesibles y una vez encontramos el objetivo, reconstruimos el
    * camino desde el nodo objetivo hasta el nodo inicial usando los 
    * nodos padres de cada nodo, para encontrar el camino óptimo.
    *
    * En el caso de las restricciones el metodo es usado para llegar 
    * de la mejor manera posible a la celda con la mejor heurística
    * conocida, pasando solo por celdas conocidas
    ****************************************************************/
    
    
    /**
     * Método para realizar un A* teniendo en cuenta que el agente
     * no puede ver todo el mapa, sino solo las celdas contiguas a su posición.
     * 
     * Por lo que es necesario ir actualizando la posición del entorno para el cálculo del A*
     * Este metodo no es el usado ya que este es un A* completo y no uno parcial, pero se puede
     * seleccionar desde el menu de la interfaz
     */

    public List<int[]> buscarCamino() {
        Set<Nodo> cerrados = new HashSet<>();
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();

        // Crear el nodo de inicio y añadirlo a la lista de abiertos
        Nodo inicio = new Nodo(posicionAgente[0], posicionAgente[1], null, 0, calcularHeuristica(posicionAgente));
        abiertos.add(inicio);

        while (!abiertos.isEmpty()) {

            Nodo actual = abiertos.poll(); 
            // Agregar el nodo actual a la lista de nodos cerrados
            cerrados.add(actual);

            // Actualizar la posición del entorno para poder ver los vecinos de ese nodo
            entorno.actualizarPosicion(new int[]{actual.getFila(), actual.getColumna()}, cerrados.stream().map(n -> new int[]{n.getFila(), n.getColumna()}).toList());

            // Comprobar si ha llegado al objetivo
            if (actual.getFila() == posicionObjetivo[0] && actual.getColumna() == posicionObjetivo[1]) {
                return reconstruirCamino(actual);
            }

            // Obtener la lista de vecinos del entorno
            for (int[] vecino : entorno.see()) {
                // Si la celda no es accesible, no la consideramos
                if (!entorno.esCeldaAccesible(vecino[0], vecino[1])) {
                    continue;
                }

                Nodo nodoVecino = new Nodo(vecino[0], vecino[1], actual, actual.getCostoG() + 1, calcularHeuristica(vecino));
                // Si el vecino ya ha sido explorado, no lo consideramos
                if (cerrados.contains(nodoVecino)) {
                    continue;
                }

                Optional<Nodo> nodoAbierto = abiertos.stream().filter(n -> n.equals(nodoVecino)).findFirst();
                // Si el vecino ya está en la lista de abiertos y se encuentra un camino más corto, se actualiza el nodo
                if (nodoAbierto.isPresent()) {
                    if (nodoVecino.getCostoG() < nodoAbierto.get().getCostoG()) {
                        abiertos.remove(nodoAbierto.get());
                        abiertos.add(nodoVecino);
                    }
                } else {
                    // Si no se encuentra en abiertos, se añade a la lista
                    abiertos.add(nodoVecino);
                }
            }
        }

        return null; // No se encontró un camino al objetivo
    } 

    /**
     * Método para realizar un A* parcial desde la posición del agente hasta una posición objetivo,
     * pudiento pasar solo por las posiciones conocidas.
     * 
     * @param posicionAgente La posición actual del agente
     * @param Objetivo Coordenadas de la posición a la que queremos llegar
     * @param posicionesConocidas Lista de posiciones conocidas por el agente
     * 
     * @return Lista de posiciones que representan el camino desde la posición del agente hasta la posición objetivo.
     */
    public List<int[]> buscarCaminoRestriccion(int[] posicionAgente, int[] Objetivo, List<int[]> posicionesConocidas) {

        Set<Nodo> cerrados = new HashSet<>();
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        // Crear el nodo de inicio y añadirlo a la lista de abiertos
        Nodo inicio = new Nodo(posicionAgente[0], posicionAgente[1], null, 0, calcularHeuristica(posicionAgente));
        abiertos.add(inicio);
    
        while (!abiertos.isEmpty()) {
            Nodo actual = abiertos.poll(); 
            cerrados.add(actual);
    
            // Comprobar si ha llegado al objetivo
            if (actual.getFila() == Objetivo[0] && actual.getColumna() == Objetivo[1]) {
                return reconstruirCamino(actual);
            }
            entorno.actualizarPosicion(new int[]{actual.getFila(), actual.getColumna()}, posicionesConocidas);
            // Obtener la lista de vecinos del entorno
            for (int[] vecino : entorno.see()) {
                // Si la celda no es accesible, no la consideramos
                if (!entorno.esCeldaAccesible(vecino[0], vecino[1])) {
                    continue;
                }
                // Asegurarse de que el vecino esté en posicionesConocidas
                boolean contiene = false;

                for (int[] conocida : posicionesConocidas) {
                    if (Arrays.equals(conocida, vecino)) {
                        contiene = true;
                        break;
                    }
                }
                if (!contiene) {
                    continue;
                }
    
                Nodo nodoVecino = new Nodo(vecino[0], vecino[1], actual, actual.getCostoG() + 1, calcularHeuristica(vecino));
                // Si el vecino ya ha sido explorado, no lo consideramos
                if (cerrados.contains(nodoVecino)) {
                    continue;
                }
    
                Optional<Nodo> nodoAbierto = abiertos.stream().filter(n -> n.equals(nodoVecino)).findFirst();
                // Si el vecino ya está en la lista de abiertos y se encuentra un camino más corto, se actualiza el nodo
                if (nodoAbierto.isPresent()) {
                    if (nodoVecino.getCostoG() < nodoAbierto.get().getCostoG()) {
                        abiertos.remove(nodoAbierto.get());
                        abiertos.add(nodoVecino);
                    }
                } else {
                    // Si no se encuentra en abiertos, se añade a la lista
                    abiertos.add(nodoVecino);
                }
            }
        }
    
        return null; // No se encontró un camino al objetivo
    }

    /**
     * Método para reconstruir el camino desde el nodo objetivo hasta el nodo inicial, encontrando el camino óptimo.
     * 
     * @param nodoObjetivo El nodo objetivo al que se quiere llegar.
     * @return Lista de posiciones que representan el camino desde el nodo inicial hasta el nodo objetivo.
     */
    private List<int[]> reconstruirCamino(Nodo nodoObjetivo) {
        List<int[]> camino = new ArrayList<>();
        Nodo actual = nodoObjetivo;

        // Recorrer los nodos padres para reconstruir el camino
        while (actual != null) {
            camino.add(0, new int[]{actual.getFila(), actual.getColumna()}); 
            actual = actual.getPadre();
        }

        return camino;
    }


    //////////////////////////////////////////////////////////////////////
    /// METODO PARA LLEGAR AL OBJETIVO ITERANDO     //////////////////////
    //////////////////////////////////////////////////////////////////////
    /****************************************************************
     * Este método se encarga de realizar una iteración del algoritmo
     * encargado de llegar al objetivo, en cada iteración: 
     * - Se comprueba si el agente ha llegado al objetivo
     * - Se comprueban los vecinos de la posición actual y se añaden
     *      a la lista de posiciones abiertas si no están en la lista
     *      de posiciones cerradas
     * - Se busca el mejor nodo de la lista de posiciones abiertas
     * - Se busca el camino al siguiente nodo
     * - Se actualiza al agente para ir hacia esta posición
     * 
     ****************************************************************/
    
    /**
     * Método para realizar una iteración del algoritmo de búsqueda del camino al objetivo.
     */
    public List<int[]> Iteracion(){
        //Si el agente ha llegado al objetivo, se termina
        if(posicionAgente[0] == posicionObjetivo[0] && posicionAgente[1] == posicionObjetivo[1]){
            return null;
        }
        
        //Si el mejor nodo es igual al nodo actual, se reinicia el mejor nodo
        if(nodoActual.getFila() == mejorNodo.getFila() && nodoActual.getColumna() == mejorNodo.getColumna()){
            mejorNodo = null;
        }

        //Se recorren los vecinos de la posición actual
        for (int[] celda : entorno.see()) {
            String celdaKey = celda[0] + "," + celda[1];
            if (entorno.esCeldaAccesible(celda[0], celda[1]) && !posicionesCerradas.contains(celdaKey)) {
                Nodo nodoVecino = new Nodo(celda[0], celda[1], nodoActual, nodoActual.getCostoG()+1, calcularHeuristica(celda));

                //Si no está en la lista de posiciones abiertas, se añade
                if(!posicionesAbiertas.contains(nodoVecino)){
                    posicionesAbiertas.add(nodoVecino);
                }
                
                //Si no está en la lista de posiciones conocidas, se añade
                if (posicionesDescubiertas.stream().noneMatch(conocida -> Arrays.equals(conocida, celda))) {
                    posicionesDescubiertas.add(celda);
                }
            }
        }

        //De la lista de posiciones abiertas, se busca el mejor nodo
        for(Nodo nodo : posicionesAbiertas){
            if(mejorNodo == null || nodo.getCostoH() < mejorNodo.getCostoH() ){
                mejorNodo = nodo;
            }
        }

        //Usando el método buscarCaminoRestriccion, se busca el mejor camino al siguiente mejor nodo, puede pasar solo por las posciones conocidas
        List<int[]> camino = buscarCaminoRestriccion(posicionAgente,new int[]{mejorNodo.getFila(), mejorNodo.getColumna()}, posicionesDescubiertas);
        
        //Si no se encuentra un camino, se termina el agente informando que no se encontró un camino
        if (camino == null) {
            JOptionPane.showMessageDialog(null, "No se encontró un camino al objetivo.");
            doDelete();
            return null;
        }
        
        //Se actualiza la posición del agente al siguiente nodo, posicion 1 ya que la 0 es la posición actual
        Nodo nodoSiguiente = new Nodo(camino.get(1)[0], camino.get(1)[1], nodoActual, nodoActual.getCostoG() + 1, calcularHeuristica(camino.get(1)));
        nodoActual = nodoSiguiente;
        
        //Se actualiza la posición del agente y se actualizan las posiciones abiertas y cerradas
        posicionesAbiertas.remove(nodoActual);
        posicionesCerradas.add(nodoActual.getFila() + "," + nodoActual.getColumna());
        entorno.actualizarPosicion(new int[]{nodoActual.getFila(), nodoActual.getColumna()}, posicionesDescubiertas);
        return camino;
    }


    ///////////////////////////////////////////////////////////////////
    /// METODO MOVIMIENTO  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////

    /**
     * Método para mover al agente a una nueva posición en el mapa.
     * Se encarga de actualizar la posición del agente en el entorno y en la interfaz.
     * 
     * @param nuevaPosicion La nueva posición a la que se moverá el agente.
     */
    public void moverAgente(int[] nuevaPosicion) {
        int[] posicionAnterior = posicionAgente.clone();
        posicionAgente = nuevaPosicion;

        // Calcular la dirección del agente y permite pintarlo en la interfaz con la dirección correcta
        int deltaY = posicionAgente[0] - posicionAnterior[0];
        int deltaX = posicionAgente[1] - posicionAnterior[1];
        direccionAgente = Math.toDegrees(Math.atan2(deltaY, deltaX));

        if (direccionAgente < 0) {
            direccionAgente += 360;
        }


        // Actualizar la interfaz
        SwingUtilities.invokeLater(() -> {
            interfaz.setPosicionAgente(posicionAgente);
            interfaz.setDireccionAgente(direccionAgente);
            interfaz.actualizarEnergia(interfaz.getEnergiaConsumida() + 1);
            interfaz.repaintMapa();
        });

    }

    ///////////////////////////////////////////////////////////////////
    /// Proceso de cálculo del A* Parcial       ///////////////////////
    ///////////////////////////////////////////////////////////////////
    /****************************************************************
    * Esta sección se encarga de representar visualmente el proceso
    * de cálculo del A* parcial, realiza saltos que el personaje en
    * una ejecución real no haría, pero es útil para visualizar el
    * proceso de cálculo del algoritmo
    ****************************************************************/
    /*En el caso de querer usar este modo de visualización, se debe descomentar el código 
    * y borrar del interior del metodo las lineas que recrean estas variables en el 
    * interior del método
    */

    //Set<Nodo> cerrados = new HashSet<>();
    //PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
    //private boolean busquedaCompleta = false;

    private void ejecutarPasoBusqueda() {
        
        Set<Nodo> cerrados = new HashSet<>();
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        boolean busquedaCompleta = false;
        
        Nodo inicio = new Nodo(posicionAgente[0], posicionAgente[1], null, 0, calcularHeuristica(posicionAgente));
        abiertos.add(inicio);
    
        while (!abiertos.isEmpty()) {
            Nodo actual = abiertos.poll();

            cerrados.add(actual); // Agregar el nodo actual a nodos cerrados
            
            entorno.actualizarPosicion(new int[]{actual.getFila(), actual.getColumna()}, abiertos.stream().map(n -> new int[]{n.getFila(), n.getColumna()}).toList());
    
            // Comprobar si ha llegado al objetivo, si es así, termina la búsqueda
            if (actual.getFila() == posicionObjetivo[0] && actual.getColumna() == posicionObjetivo[1]) {
                busquedaCompleta = true;
                return; 
            }
            
            
            
            // Obtener la lista de vecinos del entorno
            for (int[] vecino : entorno.see()) {

                // Si la celda no es accesible, no la consideramos
                if (!entorno.esCeldaAccesible(vecino[0], vecino[1])) {
                    continue; 
                }
                
                Nodo nodoVecino = new Nodo(vecino[0], vecino[1], actual, actual.getCostoG() + 1, calcularHeuristica(vecino));
                
                // Si el vecino ya ha sido explorado, no lo consideramos
                if (cerrados.contains(nodoVecino)) {
                    continue;
                }
    
                // Verificar si el vecino ya está en la lista de abiertos
                Optional<Nodo> nodoAbierto = abiertos.stream().filter(n -> n.equals(nodoVecino)).findFirst();
    
                if (nodoAbierto.isPresent()) {
                    // Si se encuentra en abiertos y se encuentra un camino más corto, se actualiza el nodo
                    if (nodoVecino.getCostoG() < nodoAbierto.get().getCostoG()) {
                        abiertos.remove(nodoAbierto.get());
                        abiertos.add(nodoVecino);
                    }
                } else {
                    // Si no se encuentra en abiertos, se añade a la lista
                    abiertos.add(nodoVecino);
                }
            }
    
            // El agente se mueve a la siguiente posición
            if (!abiertos.isEmpty()) {
                
                Nodo siguienteNodo = abiertos.poll();
                int[] nuevaPosicion = new int[]{siguienteNodo.getFila(), siguienteNodo.getColumna()};
                moverAgente(nuevaPosicion); // Mover al agente a la nueva posición
                
                return; // Finaliza el método después de mover
            }
        }
    
    }
    
    //////////////////////////////////////////////////////////////////////
    /// METODOS PARA LOS COMPORTAMIENTOS DEL AGENTE //////////////////////
    //////////////////////////////////////////////////////////////////////
    /**
     * Metodo para obtener la posición del agente
     */
    public int[] getPosicionAgente() {
        return posicionAgente;
    }

    /**
     * Metodo para obtener la posición del objetivo
     */
    public int[] getPosicionObjetivo() {
        return posicionObjetivo;
    }

    /**
     * Metodo para obtener la interfaz
     */
    public Interfaz getInterfaz() {
        return interfaz;
    }

    /**
     * Metodo para obtener el modo de búsqueda
     */
    public String getModo() {
        return modo;
    }

    /**
     * Metodo para obtener el tipo de agente
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Metodo para obtener las posiciones descubiertas
     */
    public List<int[]> getPosicionesDescubiertas() {
        return posicionesDescubiertas;
    }

    /**
     * Metodo para establecer las posiciones descubiertas
     */
    public void setPosicionesDescubiertas(List<int[]> posicionesDescubiertas) {
        this.posicionesDescubiertas = posicionesDescubiertas;
    }

    /**
     * Metodo para obtener el nodo actual
     */
    public Nodo getNodoActual() {
        return nodoActual;
    }

    /**
     * Metodo para obtener las posiciones cerradas
     */
    public Set<String> getPosicionesCerradas() {
        return posicionesCerradas;
    }

    /**
     * Metodo para cambiar las posiciones cerradas
     */
    public void setPosicionesCerradas(Set<String> posicionesCerradas) {
        this.posicionesCerradas = posicionesCerradas;
    }

    /**
     * Metodo para cambiar el nodo actual
     */
    public void setNodoActual(Nodo nodo) {
        this.nodoActual = nodo;
    }

    /**
     * Metodo para cambiar las posiciones abiertas
     */
    public void setPosicionesAbiertas(List<Nodo> posicionesAbiertas) {
        this.posicionesAbiertas = posicionesAbiertas;
    }

    /**
     * Metodo para obtener las posiciones abiertas
     */
    public List<Nodo> getPosicionesAbiertas() {
        return posicionesAbiertas;
    }
}



