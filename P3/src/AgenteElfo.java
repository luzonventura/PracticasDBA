import jade.core.Agent;


/**
 * El Agente traduce mensajes entre santa y el buscador
 * 
 * Si santa es el que le ha enviado el mensaje le cambia: "Rakas Joulupukki" por "Bro" y "Kiitos" por "En Plan"
 * Si el buscador es el que le ha enviado el mensaje le cambia: "Bro" por "Rakas Joulupukki" y "En Plan" por "Kiitos"
 * 
 */

public class AgenteElfo extends Agent {

    @Override
    protected void setup() {
        System.out.println("El Elfo Traductor está listo.");
        addBehaviour(new ComportamientoTraduccion(this));
    }
}
