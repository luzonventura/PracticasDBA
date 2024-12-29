/**
 * Autor: Antonio Luzón Ventura
 * DNI: 77448897P
 * Grado: Doble Grado en Ingeniería Informática y Administración y Dirección de Empresas (GIIADE)
 * Ejercicio 1: Crear un Agente básico que muestre un mensaje por consola
 */

package MisAgentes;

import jade.core.Agent;

public class AgenteBasico extends Agent {

    @Override
    protected void setup() {
        System.out.println("¡Hola! Soy un agente básico.");
        doDelete();
    }

    @Override
    protected void takeDown() {
        System.out.println("El agente " + getLocalName() + " ha terminado.");
    }
}
