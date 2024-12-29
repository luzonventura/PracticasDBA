import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;

/**
 * El comportamiento de Rudolf se encarga de:
 *     - En la primera comunicación con el agente buscador, comprobar que el código sea el correcto
 *     Responder con la verififcación de este o con un mensaje indicando que no es digno
 * 
 *      - En las siguientes comunicaciones, responder con la posición de los renos
 */

public class ComportamientoRudolf extends Behaviour {
    private AgenteRudolf ag;

    public ComportamientoRudolf(AgenteRudolf a) {
        ag = a;
    }

    @Override
    public void action() {
        // Recibe un mensaje de cualquier tipo de el agente buscador
        MessageTemplate mt = MessageTemplate.MatchSender(new AID("AgenteBuscador", AID.ISLOCALNAME));
        ACLMessage msg = this.myAgent.blockingReceive(mt);
        ACLMessage reply = msg.createReply();
        
        // Primero comprobamos que el mensaje tenga el codigo correcto
        if(!msg.getConversationId().equals(ag.getCODIGO_DIGNO())){
            System.out.println("Rudolph recibió un mensaje con un código incorrecto");

            // Si el código es incorrecto, rechazamos la propuesta y enviamos un mensaje de rechazo
           
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            reply.setContent("Bro No eres digno En Plan");
            this.myAgent.send(reply);
        }
        // En el caso de que no sea un mensaje con el codigo incorrecto hay dos posibilidades,
        // que se valide el agente o que se pida la posición de los renos
        else{
            String content = msg.getContent();
            System.out.println("Rudolph recibió un mensaje con un código correcto");

            // En el caso de que el mensaje sea para aceptar la propuesta, enviamos un mensaje de aceptación
            // Es decir validamos al agente
            if( msg.getPerformative() == ACLMessage.PROPOSE && translated(content)){
                System.out.println("Rudolph recibió un mensaje de propuesta");
                
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                
                reply.setContent("Bro Aceptado En Plan");
                this.myAgent.send(reply);
            }
            // En el caso de que el mensaje sea una petición de información, enviamos la posición de los renos
            else if(msg.getPerformative() == ACLMessage.REQUEST && translated(content)){
                System.out.println("Rudolph recibió un mensaje de petición de información");

                // Si no se han encontrado todos los renos, enviamos la posición de un reno
                if(!ag.todosRenosEncontrados()){
                    System.out.println("Rudolf va a enviar la posición de un reno sin encontrar");

                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Bro " + ag.getRenoPos(ag.getRenosEncontrados()) + " En Plan");

                    ag.send(reply);
                    ag.incrementarRenosEncontrados();
                }
                // Si ya se han encontrado todos los renos le informamos al agente buscador
                else{
                    System.out.println("Rudolf no encuentra más renos, ya estan todos");

                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("Bro Ya no hay más renos En Plan");
                    
                    ag.send(reply);
                    ag.doDelete();
                }
                
                
            }
        }
    }
    
    public boolean translated(String response) {
        return response.contains("Bro") && response.contains("En Plan");
    }

    @Override
    public boolean done() {
        return false;
    }
}