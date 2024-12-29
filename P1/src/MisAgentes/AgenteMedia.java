/**
 * Autor: Antonio Luzón Ventura
 * DNI: 77448897P
 * Grado: Doble Grado en Ingeniería Informática y Administración y Dirección de Empresas (GIIADE)
 * Ejercicio 4: Crear un Agente que solicite el número de elementos numéricos a leer, los sume, y calcule
 * su media. Se deberá implementar utilizando varios comportamientos
 */

package MisAgentes;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import java.util.Scanner;

public class AgenteMedia extends Agent {
    private int numeroDeElementos;
    private int suma = 0;
    private int indiceActual = 0;

    @Override
    protected void setup() {
        System.out.println("¡Hola! Soy el agente de la media. Vamos a calcular la media de algunos números.");

        addBehaviour(new SolicitarNumeroComportamiento());

        addBehaviour(new CalcularMediaComportamiento());
    }

    private class SolicitarNumeroComportamiento extends Behaviour {
        private boolean terminado = false;

        @Override
        public void action() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Introduce el número de elementos:");
            numeroDeElementos = scanner.nextInt();
            terminado = true;
        }

        @Override
        public boolean done() {
            return terminado;
        }
    }

    private class CalcularMediaComportamiento extends Behaviour {
        private boolean terminado = false;

        @Override
        public void action() {
            if (indiceActual < numeroDeElementos) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Introduce el número " + (indiceActual + 1) + ":");
                int valor = scanner.nextInt();

                suma += valor;
                indiceActual++;
            } else {
                if (numeroDeElementos > 0) {
                    double media = (double) suma / numeroDeElementos;
                    System.out.println("La suma de los números es: " + suma);
                    System.out.println("La media de los números es: " + media);
                } else {
                    System.out.println("No se introdujeron números.");
                }
                terminado = true;
            }
        }

        @Override
        public boolean done() {
            return terminado;
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("El agente " + getLocalName() + " ha terminado.");
    }
}
