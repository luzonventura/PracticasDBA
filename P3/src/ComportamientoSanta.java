import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

/**
 * El comportamiento de Santa se encarga de:
 *    - En la primera comunicación con el agente buscador, decidir si es digno o no de recibir el código secreto
 *    - Al recibir la siguiente comunicación responder con sus coordenadas
 */
public class ComportamientoSanta extends Behaviour {

    private AgenteSanta ag;

    public ComportamientoSanta(AgenteSanta a) {
        ag = a;
    }

    @Override
    public void action() {
        // Santa solo puede recibir mensajes del agente buscador
        MessageTemplate mt = MessageTemplate.MatchSender(new AID("AgenteBuscador", AID.ISLOCALNAME));
        ACLMessage msg = ag.blockingReceive(mt);
        System.out.println("Santa ha recibido un mensaje");
       
        String content = msg.getContent();

        System.out.println("Mensaje recibido por Santa: " + content);
        
        String codigo;
        ACLMessage reply = msg.createReply(ACLMessage.INFORM);
        // Primero comprobamos que el mensaje tenga el formato correcto
        if (translated(content)) {
            // Si el mensaje es una propuesta significa que esta viendo a ver si es digno o no
            if(msg.getPerformative() == ACLMessage.PROPOSE){
                Random rand = new Random();
                int randomNumber = rand.nextInt(10);
                boolean trustworthy = randomNumber < 8;
                
               
                if (trustworthy) {
                    codigo = "Hyvää joulua, aquí está el código secreto: '" + ag.getCODIGO_DIGNO() + "' Nähdään pian";
                    System.out.println("Codigo enviado por Santa: " + ag.getCODIGO_DIGNO());
                } else {
                    codigo = "Hyvää joulua, aquí está el código secreto: '11' Nähdään pian";
                    System.out.println("Codigo enviado por Santa: 11");
                }

                
                
                reply.setContent(traducir(codigo));
                reply.addReceiver(msg.getSender());

                myAgent.send(reply);
            }
            // Si el mensaje es una peticion el buscador quiere saber la posición de Santa
            else if(msg.getPerformative() == ACLMessage.REQUEST){
                
                reply.setContent(traducir("Hyvää joulua, aquí tienes mis coordenadas: " + ag.getPosicion().toString() + " Nähdään pian"));
                reply.addReceiver(msg.getSender());

                myAgent.send(reply);

                System.out.println("Mensaje enviado: " + reply.getContent() + "\n\n");

            }
            
            
        } 
        // Si el mensaje no tiene el formato correcto santa no lo entiende y lo rechaza
        else {
           
            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            reply.setContent(traducir("Hyvää joulua, lo siento, no te entiendo Nähdään pian"));

            myAgent.send(reply);

        }
    }

    // Traduce el mensaje enviandoselo al elfo
    private String traducir(String mensaje) {
        // Enviamos el mensaje al elfo para que lo traduzca
        ACLMessage msgToElfo = new ACLMessage(ACLMessage.REQUEST);
        msgToElfo.addReceiver(new AID("AgenteElfo", AID.ISLOCALNAME));
        String convID = "ConversacionElfoSanta";
        msgToElfo.setConversationId(convID);

        msgToElfo.setContent(mensaje);
        myAgent.send(msgToElfo);

        System.out.println("Santa ha enviado un mensaje al elfo");
        
        // Esperamos a recibir la respuesta del elfo y la devolvemos
        MessageTemplate mt = MessageTemplate.MatchSender(new AID("AgenteElfo", AID.ISLOCALNAME));
        ACLMessage msgFromElfo = myAgent.blockingReceive(mt);
        System.out.println("Santa ha recibido un mensaje del elfo");
        
        return msgFromElfo.getContent();
    }

    public boolean translated(String response) {
        return response.toLowerCase().contains("Rakas Joulupukki".toLowerCase()) && response.toLowerCase().contains("Kiitos".toLowerCase());
    }

    @Override
    public boolean done() {
        return false;
    }
}