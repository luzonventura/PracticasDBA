import jade.core.Agent;
import java.awt.Point;

/**
 * El agente Santa se encarga de determinar si el buscador es digno o no de recibir el código secreto
 * Y que, si el buscador es digno y ha encontrado a todos los renos le envie su posición 
 * 
 * Para los mensajes tendrá que comunicarse con el agente Elfo para despues encviar el mensaje al buscador
 */
public class AgenteSanta extends Agent {
    private Point posicion;
    private static final int CODIGO_DIGNO = 95;

    /**
     * Devuelve la posición de Santa
     * @return Posición de Santa
     */
    public Point getPosicion() {
        return posicion;
    }
    
    /**
     * Devuelve el código secreto
     * @return Código secreto
     */
    public int getCODIGO_DIGNO() {
        return CODIGO_DIGNO;
    }

    
    /**
     * Inicializa el agente Santa
     */
    @Override
    protected void setup() {
        // Obtiene los argumentos pasados al agente
        Object[] args = getArguments();
        // Si se ha pasado la posición de Santa como argumento, la recogemos
        if (args != null && args.length > 0) {
            if (args[0] instanceof Point) {
                posicion = (Point) args[0];
            }
        }
        // Si no se ha pasado la posición de Santa, la inicializamos a (0, 0) 
        else {
            System.out.println("No se ha pasado la posición de Santa.");
            posicion = new Point(0, 0);
            
        }

        // Inicializa el agente Santa usando su comportamiento
        System.out.println("Santa Claus está listo.");
        addBehaviour(new ComportamientoSanta(this));
    }
}