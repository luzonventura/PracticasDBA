import java.util.ArrayList;
import java.awt.Point;
import jade.core.Agent;

/**
 *  El agente Rudolf conoce las posiciones de todos los renos y de Santa.
 *  Este agente se va a encargar de comunicar a nuestro agente buscador si es digno o no.
 *  En el caso de ser digno nos irá comunicando las posiciones de los renos, hasta que no queden más.
 */
public class AgenteRudolf extends Agent{
    private static final String CODIGO_DIGNO = "95";
    private int renosEncontrados = 0;
    private ArrayList<Point> puntos;

    /**
     * Devuelve el número de renos encontrados, es decir el numero de renos pedidos al agente Rudolf
     * @return Número de renos encontrados
     */
    public int getRenosEncontrados() {
        return renosEncontrados;
    }

    /**
     * Devuelve la posición de un reno en concreto
     * @param i Número del reno
     * @return Posición del reno
     */
    public Point getRenoPos(int i){
        return puntos.get(i);
    }

    /**
     * Incrementa el número de renos encontrados
     */
    public void incrementarRenosEncontrados() {
        renosEncontrados++;
    }

    /**
     * Comprueba si se han encontrado todos los renos
     * @return True si se han encontrado todos los renos, false en caso contrario
     */
    public boolean todosRenosEncontrados() {
        return (puntos.size() <= renosEncontrados);
    }

    /**
     * Devuelve el código secreto
     * @return Código secreto
     */
    public String getCODIGO_DIGNO() {
        return CODIGO_DIGNO.toString();
    }

    /**
     * Inicializa el agente Rudolf
     */
    @Override
    protected void setup() {
        //Obtiene los argumentos pasados al agente
        Object[] args = getArguments();

        //Si se han pasado puntos de reno los guarda en la lista de puntos
        if (args != null && args.length > 0) {
            puntos = new ArrayList<>();
            for (Object obj : (Object[]) args[0]) {
                if (obj instanceof Point) {
                    puntos.add((Point) obj);
                }
            }
        }
        //Si no se han pasado puntos de reno se añaden puntos por defecto
        else {
            System.out.println("No se han pasado puntos de reno.");
            puntos = new ArrayList<>();
            puntos.add(new Point(0, 0));
            puntos.add(new Point(0, 1));
            puntos.add(new Point(0, 2));                    
        }

        // Inicializa el agente Rudolf usando su comportamiento
        System.out.println("Rudolph está listo.");
        addBehaviour(new ComportamientoRudolf(this));
    }
}