import java.util.List;
import jade.core.behaviours.TickerBehaviour;

/**
 * Clase que implementa el comportamiento de iteración del agente
 * Al ser una iteración, se ejecuta cada 0.5s, se encarga de calcular el camino
 * hasta el mejor nodo con las posiciones que conoce y llamar al 
 * comportamiento correspondiente para pintar el camino
 * 
 * Hemos optado por un TickerBehaviour ya que al realizar iteraciones y no un 
 * plan completo creemos que es la mejor opción
 */
public class ComportamientoIteracion extends TickerBehaviour {
    String modo;
    AgenteMovil ag;
    List<int[]> camino;
    Interfaz interfaz;
    int[] posicionAgente;
    int[] posicionObjetivo;


    /**
     * Constructor de la clase
     * @param ag AgenteMovil
     */	
    ComportamientoIteracion( AgenteMovil ag) {
        super(ag, 500);

        this.ag = ag; 

        this.modo = ag.getModo();
        this.interfaz = ag.getInterfaz();
        this.posicionAgente = ag.getPosicionAgente();
        this.posicionObjetivo = ag.getPosicionObjetivo();
    }

    /**
     * Metodo que se ejecuta al iniciar el comportamiento
     */
    @Override
    protected void onTick() {
        
        // En caso de estar en modo iterativo se recuoera la información de la interfaz
        if(modo == "Paso"){
            if(interfaz.getPosiciones() != null){
                ag.setPosicionesDescubiertas(interfaz.getPosiciones());
            }
            if(interfaz.getNodoActual() != null){
                ag.setNodoActual(interfaz.getNodoActual());
            }
            if(interfaz.getPosicionesCerradas() != null){
                ag.setPosicionesCerradas(interfaz.getPosicionesCerradas());
            }
            if(interfaz.getPosicionesAbiertas() != null){
                ag.setPosicionesAbiertas(interfaz.getPosicionesAbiertas());
            }

        }
        // Se calcula el camino
        camino = ag.Iteracion();
        
        // Pintamos la primera posición del camino que es de la que estamos partiendo
        if(camino != null && !camino.isEmpty()) {
            camino.remove(0);
            interfaz.pintarCaminoRealizado(posicionAgente[0], posicionAgente[1]);
        }
        else{
            throw new IllegalArgumentException("No se ha encontrado camino");
        }
    
        // En caso de estar en modo iterativo se guarda la información en la interfaz
        // para poder recuperarla en la siguiente iteración
        if(modo == "Paso"){
            interfaz.savePosiciones(ag.getPosicionesDescubiertas());
            interfaz.saveNodoActual(ag.getNodoActual());
            interfaz.savePosicionesCerradas(ag.getPosicionesCerradas());
            interfaz.savePosicionesAbiertas(ag.getPosicionesAbiertas());

        }
        
        // Pintar el camino
        if(modo == "Auto" ){ 
            ag.addBehaviour(new ComportamientoPintarPaso(ag, camino));
        }
        else if(modo == "Paso"){
            ag.addBehaviour(new ComportamientoPintarPaso(ag, camino));
            
        }
        else{
            throw new IllegalArgumentException("Modo no reconocido");
        }
    }    
}