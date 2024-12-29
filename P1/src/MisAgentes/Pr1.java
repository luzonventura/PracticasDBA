/**
 * Autor: Antonio Luzón Ventura
 * DNI: 77448897P
 * Grado: Doble Grado en Ingeniería Informática y Administración y Dirección de Empresas (GIIADE)
 * Descripción: Script para lanzar varios agentes en un contenedor JADE.
 */

package MisAgentes;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Pr1 {
    public static void main(String[] args) {
        
        Runtime rt = Runtime.instance();

        // Configurar contenedor principal
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");

        // Contenedor principal
        ContainerController cc = rt.createMainContainer(profile);

        try {
            // AgenteBasico
            AgentController agenteBasico = cc.createNewAgent(
                    "agenteBasico", "MisAgentes.AgenteBasico", null);
            agenteBasico.start();

            // AgenteMensajeUnico
            AgentController agenteMensajeUnico = cc.createNewAgent(
                    "agenteMensajeUnico", "MisAgentes.AgenteMensajeUnico", null);
            agenteMensajeUnico.start();

            // AgenteMensajePausado
            AgentController agenteMensajePausado = cc.createNewAgent(
                    "agenteMensajePausado", "MisAgentes.AgenteMensajePausado", null);
            agenteMensajePausado.start();

            // AgenteMedia
            AgentController agenteMedia = cc.createNewAgent(
                    "agenteMedia", "MisAgentes.AgenteMedia", null);
            agenteMedia.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
