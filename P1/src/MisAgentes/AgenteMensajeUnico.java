/**
 * Autor: Antonio Luzón Ventura
 * DNI: 77448897P
 * Grado: Doble Grado en Ingeniería Informática y Administración y Dirección de Empresas (GIIADE)
 * Ejercicio 2: Crear un Agente usando comportamientos que muestre un mensaje por consola una única
 * vez
 */

package MisAgentes;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class AgenteMensajeUnico extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("Este mensaje se muestra solo una vez.");
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("El agente " + getLocalName() + " ha terminado.");
    }
}
