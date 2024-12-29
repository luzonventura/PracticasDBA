import jade.core.behaviours.Behaviour;

public class ComportamientoMovimiento extends Behaviour {
    
    private AgenteBuscador ag;
    private String posicion;
    
    public ComportamientoMovimiento(AgenteBuscador a, String posicion) {
        ag = a;
        this.posicion = posicion;
    }

    @Override
    public void action() {
        System.out.println("Buscador moviendose hacia la posicion: " + posicion);
        // Movemos al buscador hacia la posicion del reno
    }

    @Override
    public boolean done() {
        return true;
    }
}
