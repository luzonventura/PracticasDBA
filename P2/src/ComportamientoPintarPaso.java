import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Clase que implementa el comportamiento de pintar un paso del agente por el mapa
 * 
 * Es un OneShotBehaviour ya que este comportamiento unicamente pinta un paso del 
 * agente por el mapa
 */
public class ComportamientoPintarPaso extends OneShotBehaviour{

    AgenteMovil ag;
    Interfaz interfaz;
    List<int[]> camino;
    int[] posicionAgente;
    int[] posicionObjetivo;

    /**
     * Constructor de la clase
     * @param ag AgenteMovil
     * @param camino List<int[]> camino a recorrer
     */
    ComportamientoPintarPaso(AgenteMovil ag, List<int[]> camino) {
        super();
        this.ag = ag;
        this.camino = camino;
        
        this.interfaz = ag.getInterfaz();
        this.posicionAgente = ag.getPosicionAgente();
        this.posicionObjetivo = ag.getPosicionObjetivo();
    }

    /**
     * Metodo que se ejecuta en cada tick del TickerBehaviour
     */
    @Override
    public void action() {
        //Si no se ha llegado al final del camino, mover el agente
        if (camino != null && !camino.isEmpty()) {
            posicionAgente = camino.remove(0);
            ag.moverAgente(posicionAgente);
            interfaz.pintarCaminoRealizado(posicionAgente[0], posicionAgente[1]);
            
            if (posicionAgente[0] == posicionObjetivo[0] && posicionAgente[1] == posicionObjetivo[1]) {
                SwingUtilities.invokeLater(() -> {
                    interfaz.setPosicionAgente(posicionAgente);
                    interfaz.setPosicionObjetivo(null);
                    interfaz.repaintMapa();
                    JOptionPane.showMessageDialog(null, "¡El agente ha alcanzado el objetivo!");
                });
                ag.doDelete();
                
            }
        } 
        //Si se ha llegado al final del camino, actualizar la interfaz y terminar el agente
        else {
            throw new IllegalArgumentException("No hay más posiciones en el camino");
        }

        //Si el agente esta en modo paso, se elimina el agente para en el caso que venga desde un 
        //TickerBehaviour, se detenga el agente
        if(ag.getModo() == "Paso"){
            ag.doDelete();
        }
    }
    
}
