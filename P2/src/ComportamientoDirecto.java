import jade.core.behaviours.OneShotBehaviour;
import java.util.List;

/**
 * Clase que implementa el comportamiento de movimiento directo del agente
 * 
 * Hemos optado por un OneShotBehaviour ya que en el modo directo el agente
 * calcula el camino completo y una vez lo tiene completo selecciona el 
 * comportamiento que lo representara por pantalla
 */
public class ComportamientoDirecto extends OneShotBehaviour {
    String modo;
    AgenteMovil ag;
    List<int[]> camino;

    /**
     * Constructor de la clase
     * @param ag AgenteMovil
     */
    ComportamientoDirecto( AgenteMovil ag) {
        super();

        this.ag = ag; 
        this.modo = ag.getModo();
    }

    /**
     * Metodo que se ejecuta al iniciar el comportamiento
     */
    @Override
    public void action() {
        camino = ag.buscarCamino();
        // Pintamos la primera posición del camino que es de la que estamos partiendo
        if(camino != null && !camino.isEmpty()) {
            camino.remove(0);
            ag.getInterfaz().pintarCaminoRealizado(ag.getPosicionAgente()[0], ag.getPosicionAgente()[1]);
        }
        else{
            throw new IllegalArgumentException("No se ha encontrado camino");
        }

        // Ejecutar el comportamiento correspondiente
        switch (modo) {
            case "Auto":
                ag.addBehaviour(new ComportamientoPintarCompleto(ag, camino));
                break;
        
            case "Paso":
                ag.addBehaviour(new ComportamientoPintarPaso(ag,camino));
                break;

            default:
                throw new IllegalArgumentException("Modo no reconocido");
        }
    }

}
