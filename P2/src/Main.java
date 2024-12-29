import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        float probabilidadObstaculo = 0.1f;
        
        for (int i = 0; i < 3; i++) {
            GeneradorMapa.Crear_mover(probabilidadObstaculo);
            probabilidadObstaculo += 0.15f;
        }
        // Inicia la interfaz gráfica
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Crea una nueva instancia de la clase Interfaz
                Interfaz interfaz = new Interfaz();
                interfaz.setVisible(true); // Mostrar la ventana
            }
        });
    }
}