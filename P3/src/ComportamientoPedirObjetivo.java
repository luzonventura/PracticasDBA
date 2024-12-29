import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.lang.acl.MessageTemplate;


public class ComportamientoPedirObjetivo extends Behaviour {

    private AgenteBuscador ag;
    private ACLMessage msgToSanta;
    private ACLMessage msgToRudolf;
    private ACLMessage msgToElfo;
    boolean done = false;

    public ComportamientoPedirObjetivo(AgenteBuscador a, ACLMessage msgToSanta, ACLMessage msgToRudolf, ACLMessage msgToElfo) {
        ag = a;
        this.msgToSanta = msgToSanta;
        this.msgToRudolf = msgToRudolf;
        this.msgToElfo = msgToElfo;
    }

    @Override
    public void action() {
        System.out.println("Buscador pidiendo objetivo");
        // En el caso de que nos falte la posicion de un reno se la pedimos a Rudolf, si este nos informa de que ya estan todos
        // los renos en su posicion, le pedimos a Santa su posicion

        while (msgToRudolf.getPerformative() != ACLMessage.REFUSE) {
            msgToRudolf = msgToRudolf.createReply();
            msgToRudolf.setPerformative(ACLMessage.REQUEST);
            msgToRudolf.setContent("Bro pasame la posicion de un reno En Plan");
    
            ag.send(msgToRudolf);
            System.out.println("Mensaje enviado a Rudolf\n\n");
    
            // Esperamos la respuesta de Rudolf
            MessageTemplate mt = MessageTemplate.MatchSender(new AID("AgenteRudolf", AID.ISLOCALNAME));
            msgToRudolf = this.myAgent.blockingReceive(mt);

            if(msgToRudolf.getPerformative() != ACLMessage.REFUSE) {
                ag.addBehaviour(new ComportamientoMovimiento(ag, msgToRudolf.getContent()));
            }
        }

        System.out.println("Todos los renos en su posicion\n\n");

        // Si ya tenemos la posicion de todos los renos, le pedimos a Santa su posicion, pero primero hay que enviarselo al elfo para que lo traduzca
        msgToElfo = msgToElfo.createReply();
        msgToElfo.setPerformative(ACLMessage.REQUEST);
        msgToElfo.setContent("Bro pasame tu posicion En Plan");
        msgToElfo.addReceiver(msgToElfo.getSender());
        ag.send(msgToElfo);

        // Esperamos la respuesta del elfo
        MessageTemplate mt = MessageTemplate.MatchSender(new AID("AgenteElfo", AID.ISLOCALNAME));
        msgToElfo = this.myAgent.blockingReceive(mt);

        // Enviamos el mensaje a Santa
        msgToSanta = msgToSanta.createReply();
        msgToSanta.setPerformative(ACLMessage.REQUEST);
        msgToSanta.addReceiver(ag.getAID());
        
        msgToSanta.setContent(msgToElfo.getContent());
        ag.send(msgToSanta);
        System.out.println("Mensaje enviado a Santa\n\n");

        // Esperamos la respuesta de Santa
        mt = MessageTemplate.MatchSender(new AID("AgenteSanta", AID.ISLOCALNAME));
        //mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        System.out.println("Esperando respuesta de Santa\n\n");
        msgToSanta = this.myAgent.blockingReceive(mt);
        msgToSanta = this.myAgent.blockingReceive(mt);

        ag.addBehaviour(new ComportamientoMovimiento(ag, msgToSanta.getContent()));
        done = true;
    }

    @Override
    public boolean done() {
        return done;
    }
    
}
