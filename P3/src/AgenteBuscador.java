import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jade.core.Agent;


/**
 * El Agente buscador es el encargado de realizar el trabajo 
 * 
 * Se comunica usando: "Bro" y "En Plan"
 * Este agente tiene que: 
 * 1. Enviar un mensaje a Santa para ofrecerse como voluntario
 * 2. Esperar la respuesta de Santa
 * 3. Comunicar con rudolf para verificar el codigo
 * 4. Si el codigo es correcto ruldof nos valida
 *  4.1. En la proxima comunicacion con rudolf le pedimos un reno
 *  4.2. Si rudolf nos da un reno y vamos hacia el 
 *  4.3. Repetimos los paso 4.1 y 4.2 hasta que no haya mas renos
 *  4.4. Si no hay mas renos rudolf nos avisara de que no hay mas renos
 *  4.5. Entonces comunicamos a santa que hemos encontrado a todos los renos y este nos envia sus coordenadas
 *  4.6. Vamos hacia santa
 * 5. Si el codigo es incorrecto rudolf nos rechaza
 */

public class AgenteBuscador extends Agent {
    private String codigoSecreto;
    @Override
    protected void setup() {
        System.out.println("El Agente Buscador está listo.\n\n");
        addBehaviour(new ComportamientoInicioBuscador(this));
    }

    /**
     * Obtiene el codigo secreto enviado por santa
     * @param texto texto que contiene el codigo secreto
     * @return el codigo secreto
     */
    public String extraerCodigo(String texto) {
        
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matcher = pattern.matcher(texto);
        
        StringBuilder resultados = new StringBuilder();
        while (matcher.find()) {
            resultados.append(matcher.group(1)).append("\n");
        }
        
        return resultados.toString().trim();
    }

    public String getCodigoSecreto() {
        return codigoSecreto;
    }

    public void setCodigoSecreto(String codigoSecreto) {
        this.codigoSecreto = codigoSecreto;
    }
}
