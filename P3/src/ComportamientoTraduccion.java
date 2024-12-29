
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


/**
 * Comportamiento de traducción de mensajes entre santa y el buscador
 */
public class ComportamientoTraduccion extends Behaviour {
    AgenteElfo ag;

    public ComportamientoTraduccion(AgenteElfo a) {
        ag = a;
    }

    @Override
    public void action() {
        //El elfo solo puede recibir mensajes de tipo REQUEST
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage msg = myAgent.blockingReceive(mt);

        String content = msg.getContent();
        ACLMessage reply = msg.createReply(ACLMessage.INFORM);

        // Si el mensaje es de Santa, lo adaptamos al buscador sino lo adaptamos a Santa
        if("ConversacionElfoSanta".equals(msg.getConversationId())){
            System.out.println("Traduciendo mensaje de Santa: " + content);
            
            reply.setContent(translateSanta(content));
            myAgent.send(reply);

        } else if("ConversacionElfoAgente".equals(msg.getConversationId())){
            System.out.println("Traduciendo mensaje del Buscador: " + content);
            
            reply.setContent(translateBuscador(content));
            System.out.println("Mensaje traducido: " + reply.getContent() +"\n\n");
            myAgent.send(reply);
        }
        
    }

    private String translateSanta(String content) {
        // Implementa aquí la lógica de traducción
        return content.replace("Hyvää joulua","Bro")
                      .replace("Nähdään pian", "En plan");
    }

    private String translateBuscador(String content) {
        // Implementa aquí la lógica de traducción
        return content.replace("Bro", "Rakas Joulupukki")
                      .replace("En Plan", "Kiitos");

    }

    @Override
    public boolean done() {
        return false;
    }
}