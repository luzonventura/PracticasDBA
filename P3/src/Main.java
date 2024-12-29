import java.util.ArrayList;
import java.awt.Point;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;

public class Main {
    public static void main(String[] args) {
        // Crea un contenedor JADE para el agente y lo lanza
        // Crea tambien un agente Santa, un agente Rudolf y un agente Elfo

        jade.core.Runtime runtime = jade.core.Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "false");
        AgentContainer container = runtime.createMainContainer(profile);
        ArrayList<Point> puntos = new ArrayList<>();
        puntos.add(new Point(0, 1));
        puntos.add(new Point(0, 2));
        puntos.add(new Point(0, 3));

        try {
            //CREA los agentes y despues los lanza:
            container.createNewAgent("AgenteSanta", "AgenteSanta", new Object[] {new java.awt.Point(0, 0)}).start();
            container.createNewAgent("AgenteRudolf", "AgenteRudolf", null).start();
            container.createNewAgent("AgenteElfo", "AgenteElfo", null).start();
            container.createNewAgent("AgenteBuscador", "AgenteBuscador", null).start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }   


    }
}