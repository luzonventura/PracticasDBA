import jade.core.behaviours.OneShotBehaviour;


/**
 * Clase que define el comportamiento del agente, este comportamiento
 * es el encargado de filtrar por tipo de agente y llamar al comportamiento
 * correspondiente
 * 
 * Hemos optado por usar un OneShotBehaviour ya que este comportamiento se 
 * encarga unicamente de filtar por tipo de agente y llamar al comportamiento
 * correspondiente, por lo que no es necesario que se ejecute en cada tick
 */
public class ComportamientoAgente extends OneShotBehaviour {
    AgenteMovil ag;
    String tipo;
    /**
     * Constructor de la clase
     * @param ag AgenteMovil
     */
    ComportamientoAgente( AgenteMovil ag) {
        super();

        this.ag = ag; 
        this.tipo = ag.getTipo();
    }

    /**
     * Metodo que se ejecuta al iniciar el comportamiento
     * Llama al comportamiento correspondiente segun el tipo de agente
     */
    @Override
    public void action() {
        switch (tipo) {
            case "Directo":
                ag.addBehaviour(new ComportamientoDirecto(ag));
                break;
        
            case "Iteracion":
                ag.addBehaviour(new ComportamientoIteracion(ag));
                break;
            default:
                break;
        }
    }
}
