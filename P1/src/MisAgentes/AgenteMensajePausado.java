/**
 * Autor: Antonio Luzón Ventura
 * DNI: 77448897P
 * Grado: Doble Grado en Ingeniería Informática y Administración y Dirección de Empresas (GIIADE)
 * Ejercicio 3: Crear un Agente básico que muestre un mensaje de forma indefinida, pero esperando 2
 * segundos entre mensaje y mensaje
 */

package MisAgentes;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class AgenteMensajePausado extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {
                System.out.println("¡Hola! Este mensaje se repite cada 2 segundos.");
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("El agente " + getLocalName() + " ha terminado.");
    }
}
