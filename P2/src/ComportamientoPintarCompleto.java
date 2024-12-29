import jade.core.behaviours.TickerBehaviour;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Comportamiento que se encarga de mover el agente por todos los pasos
 * del camino calculado y de pintar el camino en la interfaz gráfica
 * 
 * Extiende de TickerBehaviour ya que el agente se moverá cada cierto tiempo por
 * los pasos del camino calculado
 */
public class ComportamientoPintarCompleto extends TickerBehaviour {
    String modo;
    AgenteMovil ag;
    List<int[]> camino;
    Interfaz interfaz;
    int[] posicionAgente;
    int[] posicionObjetivo;
    
    /**
     * Constructor de la clase
     */
    ComportamientoPintarCompleto(AgenteMovil ag, List<int[]> camino) {
        super(ag, 500);
        this.ag = ag;
        this.camino = camino;
        
        this.interfaz = ag.getInterfaz();
        this.posicionAgente = ag.getPosicionAgente();
    }

    /**
     * Método que se ejecuta cada vez que el comportamiento es activado
     */
    @Override
    protected void onTick() {
        //Si no se ha llegado al final del camino, mover el agente
        if (camino != null && !camino.isEmpty()) {
            posicionAgente = camino.remove(0);
            ag.moverAgente(posicionAgente);
            interfaz.pintarCaminoRealizado(posicionAgente[0], posicionAgente[1]);
        } 
        //Si se ha llegado al final del camino, actualizar la interfaz y terminar el agente
        else {
            SwingUtilities.invokeLater(() -> {
                interfaz.setPosicionAgente(posicionAgente);
                interfaz.setPosicionObjetivo(null);
                interfaz.repaintMapa();
                JOptionPane.showMessageDialog(null, "¡El agente ha alcanzado el objetivo!");
            });
            ag.doDelete();
        }
    }
}
