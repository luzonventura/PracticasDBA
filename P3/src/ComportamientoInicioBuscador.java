import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;

/**
 * El comportamiento de inicio del buscador se encarga de:
 *     - Enviar un mensaje a Santa para ofrecerse como voluntario
 *     - Esperar la respuesta de Santa
 *     - Comunicar con Rudolf para verificar el código
 *     - Si el código es correcto, Rudolf nos valida y llamamos a el siguiente comportamiento
 *     - Si el código es incorrecto, Rudolf nos rechaza y finalizamos el agente
 */

public class ComportamientoInicioBuscador extends OneShotBehaviour {
    private AgenteBuscador ag;

    public ComportamientoInicioBuscador(AgenteBuscador a) {
        ag = a;
    }

    @Override
    public void action() {
        //Primero le envio el mensaje al elfo para que me lo traduzca y luego se lo envio a santa ya traducido
        ACLMessage msgToElfo = new ACLMessage(ACLMessage.REQUEST);
        msgToElfo.setContent("Bro me ofrezco como voluntario En Plan");
        System.out.println("Agente buscador se ofrece como voluntario");
        
        //Enviamos el mensaje al elfo para traducirlo
        msgToElfo.addReceiver(new AID("AgenteElfo", AID.ISLOCALNAME));
        String convID_elfo = "ConversacionElfoAgente";
        msgToElfo.setConversationId(convID_elfo);
        ag.send(msgToElfo);
        System.out.println("Mensaje enviado al elfo");


        //Esperamos la respuesta del elfo y la enviamos a santa
        MessageTemplate mt = MessageTemplate.MatchSender(new AID("AgenteElfo", AID.ISLOCALNAME));
        msgToElfo = this.myAgent.blockingReceive(mt);

        // Enviamos el mensaje a Santa
        ACLMessage msgToSanta = new ACLMessage(ACLMessage.PROPOSE);
        msgToSanta.addReceiver(new AID("AgenteSanta", AID.ISLOCALNAME));
        String convID = "ConversacionSanta";

        msgToSanta.setContent(msgToElfo.getContent());
        msgToSanta.setConversationId(convID);
        ag.send(msgToSanta);
        
        // Esperamos a la respuesta de Santa y extraemos el codigo
        mt = MessageTemplate.MatchSender(new AID("AgenteSanta", AID.ISLOCALNAME));
        msgToSanta = this.myAgent.blockingReceive(mt);

        System.out.println("Respuesta de Santa: " + msgToSanta.getContent() + "\n\n");
        
        String codigoRudolf = ag.extraerCodigo(msgToSanta.getContent());
        System.out.println("Codigo secreto: " + codigoRudolf);
        ag.setCodigoSecreto(codigoRudolf);
        
        // Enviamos a Rudolf un mensaje de tipo PROPOSE con el codigo secreto
        ACLMessage msgToRudolf = new ACLMessage(ACLMessage.PROPOSE);
        System.out.println("Enviando mensaje a Rudolf");
        msgToRudolf.addReceiver(new AID("AgenteRudolf", AID.ISLOCALNAME));
        
        msgToRudolf.setContent("Bro autenticame En Plan");
        msgToRudolf.setConversationId(ag.getCodigoSecreto());

        ag.send(msgToRudolf);

        // Esperamos la respuesta de Rudolf y si somos aceptados llamamos al siguiente comportamiento sino finalizamos
        mt = MessageTemplate.MatchSender(new AID("AgenteRudolf", AID.ISLOCALNAME));
        msgToRudolf = this.myAgent.blockingReceive(mt);
        
        if(msgToRudolf.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
            System.out.print("Bro soy digno En Plan\n\n");
            ag.addBehaviour(new ComportamientoPedirObjetivo(ag, msgToSanta, msgToRudolf, msgToElfo));
        }else if(msgToRudolf.getPerformative() == ACLMessage.REJECT_PROPOSAL){
            System.out.print("Bro no soy digno En Plan");
            ag.doDelete();
        }
    }

    
}
