import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.awt.event.ActionListener;

/**
 * Clase que representa la interfaz gráfica de la aplicación.
 */
public class Interfaz extends JFrame {
    ////////////////////////////////
    /// Mapas y música de fondo ////
    ////////////////////////////////
    private static final String MAPAS_PATH = "maps";
    private static final String IMAGEN_FONDO = "img/menu.png"; // Ruta de la imagen de fondo
    private static final String MAPA_POR_DEFECTO = MAPAS_PATH + "/mapWithoutObstacle.txt"; // Ruta del mapa por
    private static final String MUSICA_FONDO = "sound/pacman_beginning.wav"; // Ruta de la música de fondo

    //////////////////////////////////////////////////////
    /// Atributos de la interfaz /////////////////////////
    /// que tienen relación con la  clase AgenteMovil ////
    //////////////////////////////////////////////////////
    private Mapa mapaSeleccionado;
    private int[] posicionAgente = new int[]{0,0}; // Posición actual del agente
    private int[] posicionObjetivo = new int[]{9,9}; // Posición del objetivo
    private double direccionAgente = 0; // Dirección actual del agente en grados
    private int energiaConsumida = 0; // Energía consumida por el agente
    private List<int[]> CaminoPerfecto = new ArrayList<>(); // Lista de posiciones del camino perfecto
    private List<int[]> CaminoRealizado = new ArrayList<>(); // Lista de posiciones del camino realizado
    private Entorno entorno; // Entorno del agente
    private String Opciones = "Iteracion"; // Permite seleccionar el modo de ejecución del agente, puede ser "Directo" o "Iteracion"
    private List<int[]> posiciones = null; // Lista de posiciones del camino realizado
    private Set<String> posicionesCerradas = null; // Conjunto de posiciones cerradas
    private Nodo nodoActual = null; // Nodo actual del agente
    private List<Nodo> posicionesAbiertas = null;

    ////////////////////////////////////////////////
    /// Paneles y componentes de la interfaz ///////
    ////////////////////////////////////////////////
    private JPanel panelMenu;
    private JPanel panelMapa;
    private Image imagenFondo;
    private Image imgObjetivo;
    private Image imgAgente;
    private JLabel lblEnergia;
    
    //////////////////////////////////////////////////////
    /// Lanzar el agente  y controlar la interfaz ////////
    //////////////////////////////////////////////////////
    private AgentController agentController;
    private AgentContainer mainContainer;
    private Runtime jadeRuntime;

    //////////////////////////////////////////
    /// Controles de la música de fondo //////
    //////////////////////////////////////////
    private Clip clip;
    private FloatControl volumeControl;
    private boolean isMuted = false;
    private JButton btnMute;

    /**
     * Constructor de la clase Interfaz.
     */
    public Interfaz() {
        // Configuración de la ventana
        setTitle("Pac-Agent");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 850); // Tamaño ajustado para incluir el botón sin superposición
        setLocationRelativeTo(null);
        setLayout(new CardLayout());

        // Cargar las imágenes para el fondo, el agente y el objetivo
        imagenFondo = new ImageIcon(IMAGEN_FONDO).getImage();
        imgObjetivo = new ImageIcon("img/objetivo.png").getImage();
        imgAgente = new ImageIcon("img/agente.png").getImage();

        // Inicializar los paneles
        inicializarPanelMenu();
        inicializarPanelMapa();

        // Agregar los paneles a la ventana
        add(panelMenu, "Menu");
        add(panelMapa, "Mapa");

        // Reproducir la música de fondo
        reproducirMusicaFondo();

        // Mostrar el menú inicial
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Menu");
    }

    /**
     * Método para reproducir música de fondo en bucle.
     */
    private void reproducirMusicaFondo() {
        try {
            File archivoMusica = new File(MUSICA_FONDO);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivoMusica);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(-20.0f);
            } else {
                volumeControl = null;
            }
            
            clip.loop(Clip.LOOP_CONTINUOUSLY); 
            clip.start(); 
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la música de fondo: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inicializa el panel del menú con los botones de inicio, selección de mapa y salir.
     */
    private void inicializarPanelMenu() {
        panelMenu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Imagen de fondo
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelMenu.setLayout(new BorderLayout());

        // Panel central para los botones principales
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(4, 1, 10, 10));
        panelBotones.setOpaque(false); // Hacer el panel de botones transparente

        // Botones estilizados
        JButton btnIniciar = crearBoton("Iniciar", e -> iniciarMapaSeleccionado());
        JButton btnElegirMapa = crearBoton("Elegir Mapa", e -> mostrarBotonesMapas());
        JButton btnOpcion = crearBoton("Tipo", e -> mostrarBotonesOpciones());
        JButton btnSalir = crearBoton("Salir", e -> System.exit(0));


        panelBotones.add(btnIniciar);
        panelBotones.add(btnElegirMapa);
        panelBotones.add(btnOpcion);
        panelBotones.add(btnSalir);

        // Añadir el panel de botones centrado
        panelCentral.add(panelBotones, new GridBagConstraints());

        // Botón de mute/unmute en la esquina inferior izquierda
        btnMute = new JButton();
        actualizarIconoMute();
        btnMute.setContentAreaFilled(false);
        btnMute.setBorderPainted(false);
        btnMute.setFocusPainted(false);
        btnMute.addActionListener(e -> toggleMute());
        btnMute.setBackground(new Color(70, 130, 180));
        btnMute.setOpaque(true);

        // Panel inferior para el botón mute
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.add(btnMute, BorderLayout.WEST);

        // Añadir paneles al panelMenu
        panelMenu.add(panelCentral, BorderLayout.CENTER);
        panelMenu.add(panelInferior, BorderLayout.SOUTH);
    }


    /**
     * Método para togglear el mute
     */
    private void toggleMute() {
        isMuted = !isMuted;
        if (volumeControl != null) {
            if (isMuted) {
                clip.stop(); 
            } else {
                clip.start();
            }
        }
        actualizarIconoMute();
    }

    /**
     * Método para actualizar el icono del botón mute
     */
    private void actualizarIconoMute() {
        String iconPath = isMuted ? "img/mute.png" : "img/unmute.png";
        ImageIcon icon = new ImageIcon(iconPath);
        // Escalar la imagen
        Image scaledImage = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        btnMute.setIcon(icon);
    }

    /**
     * Inicializa el panel del mapa donde se visualizará el entorno del agente y el objetivo
     */
    private void inicializarPanelMapa() {
        panelMapa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mapaSeleccionado != null) {
                    int anchoPanel = getWidth();
                    int altoPanel = getHeight() - 50;
                    int filas = mapaSeleccionado.getFilas();
                    int columnas = mapaSeleccionado.getColumnas();

                    // Calcular el tamaño de cada celda en función del tamaño de la pantalla
                    int tamanoCelda = Math.min(anchoPanel / columnas, altoPanel / filas);
                    int margenX = (anchoPanel - (columnas * tamanoCelda)) / 2;
                    int margenY = (altoPanel - (filas * tamanoCelda)) / 2;

                    // Dibujar cada celda del mapa
                    for (int i = 0; i < filas; i++) {
                        for (int j = 0; j < columnas; j++) {
                            int x = margenX + j * tamanoCelda;
                            int y = margenY + i * tamanoCelda;

                            // Dibujar obstáculos, celdas libres, agente y objetivo
                            if (mapaSeleccionado.esCeldaAccesible(i, j)) {
                                g.setColor(Color.BLACK); // Celdas libres en negro
                                g.fillRect(x, y, tamanoCelda, tamanoCelda);
                            } else {
                                g.setColor(Color.BLUE); // Obstáculos en azul
                                g.fillRect(x, y, tamanoCelda, tamanoCelda);
                            }
                            g.setColor(Color.GRAY);
                            g.drawRect(x, y, tamanoCelda, tamanoCelda); // Bordes de celdas
                        }
                    }

                    // Dibujar la posición del agente
                    if (posicionAgente != null) {
                        Graphics2D g2d = (Graphics2D) g.create();

                        // Calcular la posición donde se dibujará el agente
                        int agenteX = margenX + posicionAgente[1] * tamanoCelda;
                        int agenteY = margenY + posicionAgente[0] * tamanoCelda;

                        // Crear una transformación para rotar la imagen
                        AffineTransform at = new AffineTransform();
                        at.translate(agenteX + tamanoCelda / 2, agenteY + tamanoCelda / 2);
                        at.rotate(Math.toRadians(direccionAgente));
                        at.translate(-tamanoCelda / 2, -tamanoCelda / 2);
                        at.scale((double) tamanoCelda / imgAgente.getWidth(null),
                                (double) tamanoCelda / imgAgente.getHeight(null));

                        // Dibujar la imagen rotada
                        g2d.drawImage(imgAgente, at, this);
                        g2d.dispose();
                    }

                    // Dibujar el camino perfecto utilizando la imagen 'fruta.png'
                    if (CaminoPerfecto != null) {
                        Image imgCamino = new ImageIcon("img/fruta.png").getImage();
                        int anchoPunto = tamanoCelda / 4;
                        int altoPunto = tamanoCelda / 4;
                        for (int[] posicion : CaminoPerfecto) {
                            g.drawImage(imgCamino, margenX + posicion[1] * tamanoCelda + (tamanoCelda - anchoPunto) / 2,
                                    margenY + posicion[0] * tamanoCelda + (tamanoCelda - altoPunto) / 2,
                                    anchoPunto, altoPunto, this);
                        }
                    }

                    // Dibujar el camino utilizando la imagen 'punto.png'
                    if (CaminoRealizado != null) {
                        Image imgCamino = new ImageIcon("img/punto.png").getImage();
                        int anchoPunto = tamanoCelda / 4;
                        int altoPunto = tamanoCelda / 4;
                        for (int[] posicion : CaminoRealizado) {
                            g.drawImage(imgCamino, margenX + posicion[1] * tamanoCelda + (tamanoCelda - anchoPunto) / 2,
                                    margenY + posicion[0] * tamanoCelda + (tamanoCelda - altoPunto) / 2,
                                    anchoPunto, altoPunto, this);
                        }
                    }

                    // Dibujar la posición objetivo
                    if (posicionObjetivo != null) {
                        g.drawImage(imgObjetivo, margenX + posicionObjetivo[1] * tamanoCelda,
                                margenY + posicionObjetivo[0] * tamanoCelda, tamanoCelda, tamanoCelda, this);
                    }
                }
            }
        };

        panelMapa.setBackground(new Color(200, 200, 200)); 
    
        
        // Botón para volver al menú principal
        JButton btnVolver = crearBotonEstilizado("Volver al Menú Principal");
        btnVolver.addActionListener(e -> {
            // Terminar el agente y el contenedor de JADE
            terminarAgenteYContenedor();
            mostrarMenu();
        });

        // Crear los botones que tendra el panel 
        JButton btnIterar = crearBoton("Iterar", e-> {launchAgent("Paso");});
        JButton btnAutomatico = crearBoton("Automático", e-> {launchAgent("Auto");});
        JButton btnCambiarPosiciones = crearBoton("Cambiar Posiciones", e -> {cambiarPosiciones();});

        // Etiqueta para mostrar la energía consumida
        lblEnergia = new JLabel("Energía: 0");
        lblEnergia.setForeground(Color.WHITE);
        lblEnergia.setFont(new Font("Arial", Font.BOLD, 16));
        lblEnergia.setHorizontalAlignment(SwingConstants.LEFT); 
        lblEnergia.setVerticalAlignment(SwingConstants.CENTER); 

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(50, 50, 50));

        // Panel para los botones Iterar y Automático
        JPanel panelCentralBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelCentralBotones.setBackground(new Color(50, 50, 50));
        panelCentralBotones.add(btnIterar);
        panelCentralBotones.add(btnAutomatico);
        panelCentralBotones.add(btnCambiarPosiciones);

        // Agregar el label de energía
        JPanel panelIzquierda = new JPanel(new GridBagLayout());
        panelIzquierda.setBackground(new Color(50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panelIzquierda.add(lblEnergia, gbc);

        // Agregar el botón para volver
        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelDerecha.setBackground(new Color(50, 50, 50));
        panelDerecha.add(btnVolver);

        // Añadir subpaneles
        panelInferior.add(panelIzquierda, BorderLayout.WEST);
        panelInferior.add(panelCentralBotones, BorderLayout.CENTER);
        panelInferior.add(panelDerecha, BorderLayout.EAST);

        // Añadir el panel inferior al panelMapa
        panelMapa.setLayout(new BorderLayout());
        panelMapa.add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Muestra el panel del menú.
     */
    private void mostrarMenu() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Menu");
    }

    /**
     * Muestra el panel del mapa.
     */
    private void mostrarMapa() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Mapa");
        panelMapa.repaint();
    }

    /**
     * Inicia el mapa seleccionado o carga el mapa por defecto si no hay selección.
     */
    private void iniciarMapaSeleccionado() {
        try {
            mapaSeleccionado = (mapaSeleccionado != null) ? mapaSeleccionado : new Mapa(MAPA_POR_DEFECTO);
            mostrarMapa();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el mapa: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra botones para seleccionar un mapa.
     */
    private void mostrarBotonesMapas() {
        File directorio = new File(MAPAS_PATH);
        String[] mapas = directorio.list((dir, name) -> name.endsWith(".txt"));

        if (mapas == null || mapas.length == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron mapas en el directorio 'maps'.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear un panel para los botones de los mapas
        JPanel panelMapas = new JPanel();
        panelMapas.setLayout(new GridLayout(mapas.length, 1, 10, 10));
        panelMapas.setBackground(new Color(50, 50, 50));
        panelMapas.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Genera un boton para cada mapa
        for (String mapaNombre : mapas) {
            JButton botonMapa = crearBoton(mapaNombre, e -> seleccionarMapa(mapaNombre));
            panelMapas.add(botonMapa);
        }

        JOptionPane.showMessageDialog(this, panelMapas, "Selecciona un Mapa", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Muestra botones para seleccionar el modo de ejecución del agente
     * "Directo" o "Iteracion"
     */
    private void mostrarBotonesOpciones() {
        // Crea un panel con las dos opciones, "Directo" e "Iteracion"
        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new GridLayout(2, 1, 10, 10));
        panelOpciones.setBackground(new Color(50, 50, 50));
        panelOpciones.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Botones para seleccionar el modo de ejecución
        JToggleButton btnDirecto = crearBotonEstilizadoToggle("Directo");
        JToggleButton btnIteracion = crearBotonEstilizadoToggle("Iteracion");

        // Acciones de los botones
        btnDirecto.addActionListener(e -> {
            Opciones = "Directo";
            btnDirecto.setSelected(true);
            btnIteracion.setSelected(false);
            actualizarBotonEstilo(btnDirecto, btnIteracion);
        });
        
        btnIteracion.addActionListener(e -> {
            Opciones = "Iteracion";
            btnDirecto.setSelected(false);
            btnIteracion.setSelected(true);
            actualizarBotonEstilo(btnDirecto, btnIteracion);
        });

        // Añadir botones al panel
        panelOpciones.add(btnDirecto);
        panelOpciones.add(btnIteracion);

        // Inicializa el estilo de los botones basado en la opción predeterminada
        actualizarBotonEstilo(btnDirecto, btnIteracion);

        // Mostrar el panel en un diálogo
        JOptionPane.showMessageDialog(null, panelOpciones, "Selecciona un Modo", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Método para actualizar el estilo de los botones de opciones.
     * 
     * @param btnDirecto   Botón para el modo "Directo"
     * @param btnIteracion Botón para el modo "Iteracion"
     */
    private void actualizarBotonEstilo(JToggleButton btnDirecto, JToggleButton btnIteracion) {
        Color backgroundSelected = new Color(30, 60, 120);
        Color backgroundDefault = new Color(70, 130, 180);

        if (Opciones.equals("Directo")) {
            btnDirecto.setBackground(backgroundSelected);
            btnIteracion.setBackground(backgroundDefault);
        } else {
            btnDirecto.setBackground(backgroundDefault);
            btnIteracion.setBackground(backgroundSelected);
        }
    }
    /**
     * Método para seleccionar un mapa.
     * 
     * @param mapaNombre Nombre del mapa a seleccionar
     */
    private void seleccionarMapa(String mapaNombre) {
        try {
            mapaSeleccionado = new Mapa(MAPAS_PATH + "/" + mapaNombre);
            JOptionPane.showMessageDialog(this, "Mapa seleccionado: " + mapaNombre, "Mapa Cargado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar el mapa: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para crear un botón con un estilo personalizado.
     * 
     * @param texto Texto del botón
     * @return Botón con estilo personalizado
     */
    private JButton crearBotonEstilizado(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(new Color(70, 130, 180));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(30, 30, 30), 2),
                new EmptyBorder(10, 20, 10, 20)));
        return boton;
    }

    /**
     * Método para crear un botón con un estilo personalizado Toggle.
     * 
     * @param texto Texto del botón
     * @return Botón con estilo personalizado
     */
    private JToggleButton crearBotonEstilizadoToggle(String texto) {
        JToggleButton boton = new JToggleButton(texto);
        boton.setBackground(new Color(70, 130, 180));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(true);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(30, 30, 30), 2),
                new EmptyBorder(10, 20, 10, 20)));

        return boton;
    }
    /**
     * Método para crear un botón con un texto y un ActionListener
     * Usado para simplificar la creación de botones y evitar repetir código
     * 
     * @param texto  Texto del botón
     * @param accion Acción a realizar al hacer clic en el botón
     * @return Botón creado
     */
    private JButton crearBoton(String texto, ActionListener accion) {
        JButton boton = crearBotonEstilizado(texto);
        boton.addActionListener(accion);
        return boton;
    }
    
    /**
     * Método para cambiar la posicion del agente
     * @param posicionAgente Nueva posicion del agente
     */
    public void setPosicionAgente(int[] posicionAgente) {
        this.posicionAgente = posicionAgente;
    }

    /**
     * Método para cambiar la posicion del objetivo
     * @param posicionObjetivo Nueva posicion del objetivo
     */
    public void setPosicionObjetivo(int[] posicionObjetivo) {
        this.posicionObjetivo = posicionObjetivo;
    }

    /**
     * Método para repintar el mapa
     */
    public void repaintMapa() {
        panelMapa.repaint();
    }

    /**
     * Método para añadir una posicion al camino perfecto
     */
    public void pintarCaminoPerfecto(int x, int y) {
        int[] posicion = new int[] { x, y };
        CaminoPerfecto.add(posicion);
    }

    /**
     * Método para añadir una posicion al camino realizado
     */
    public void pintarCaminoRealizado(int x, int y) {

        int[] posicion = new int[] { x, y };
        CaminoRealizado.add(posicion);

    }

    /**
     * Método para limpiar los caminos, tanto el perfecto como el realizado
     */
    public void limpiarCamino() {
        CaminoPerfecto.clear();
        CaminoRealizado.clear();
    }

    /**
     * Método para cambiar la direccion del agente
     */
    public void setDireccionAgente(double direccionAgente) {
        this.direccionAgente = direccionAgente;
    }

    /**
     * Método para actualizar la energía consumida por el agente
     */
    public void actualizarEnergia(int energia) {
        this.energiaConsumida = energia;
        lblEnergia.setText("Energía: " + energiaConsumida);
    }
    
    /**
     * Método para cambiar las posiciones del agente y del objetivo
     */
    private void cambiarPosiciones() {
        if(posicionObjetivo == null ) {
            posicionObjetivo = new int[]{9,9};
        }
        actualizarEnergia(0);
        posiciones = null;
        posicionesCerradas = null;
        nodoActual = null;
        posicionesAbiertas = null;

        // Crear un panel para los campos de texto
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        // Crear los campos de texto
        JTextField txtAgenteX = new JTextField(String.valueOf(posicionAgente[1]));
        JTextField txtAgenteY = new JTextField(String.valueOf(posicionAgente[0]));
        JTextField txtObjetivoX = new JTextField(String.valueOf(posicionObjetivo[1]));
        JTextField txtObjetivoY = new JTextField(String.valueOf(posicionObjetivo[0]));
        
        // Agregar los campos de texto al panel
        panel.add(new JLabel("Agente X:"));
        panel.add(txtAgenteX);
        panel.add(new JLabel("Agente Y:"));
        panel.add(txtAgenteY);
        panel.add(new JLabel("Objetivo X:"));
        panel.add(txtObjetivoX);
        panel.add(new JLabel("Objetivo Y:"));
        panel.add(txtObjetivoY);
        
        // Mostrar el panel en un diálogo
        int resultado = JOptionPane.showConfirmDialog(this, panel, "Cambiar Posiciones", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            try {
                // Obtener las nuevas posiciones
                int xAgente = Integer.parseInt(txtAgenteY.getText());
                int yAgente = Integer.parseInt(txtAgenteX.getText());
                int xObjetivo = Integer.parseInt(txtObjetivoY.getText());
                int yObjetivo = Integer.parseInt(txtObjetivoX.getText());
                
                // Verificar que las posiciones sean válidas y accesibles
                if(xAgente < 0 || xAgente >= mapaSeleccionado.getFilas() || yAgente < 0 || yAgente >= mapaSeleccionado.getColumnas() || xObjetivo < 0 
                    || xObjetivo >= mapaSeleccionado.getFilas() || yObjetivo < 0 || yObjetivo >= mapaSeleccionado.getColumnas() || !mapaSeleccionado.esCeldaAccesible(xAgente, yAgente) 
                    || !mapaSeleccionado.esCeldaAccesible(xObjetivo, yObjetivo)) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingresa posiciones válidas.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Actualizar las posiciones
                limpiarCamino();
                setPosicionAgente(new int[]{xAgente, yAgente});
                setPosicionObjetivo(new int[]{xObjetivo, yObjetivo});
                repaintMapa(); 
    
                JOptionPane.showMessageDialog(this, "Posiciones actualizadas.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Método para obtener la energía consumida por el agente
     * @return Energía consumida por el agente
     */
    public int getEnergiaConsumida() {
        return energiaConsumida;
    }

    /**
     * Método encargado de lanzar el agente con el modo seleccionado
     * @param modo Modo en el que se lanzará el agente
     */
    private void launchAgent(String modo) {
        try {
            if (jadeRuntime == null) {
                // Inicializar el entorno de JADE solo si no está ya inicializado
                jadeRuntime = Runtime.instance();
                jadeRuntime.setCloseVM(false);
            }

            // Crear un perfil para el contenedor principal
            Profile profile = new ProfileImpl(null, 1200, null);
            mainContainer = jadeRuntime.createMainContainer(profile);

            entorno = new Entorno(mapaSeleccionado, posicionAgente);

            // Obtener las posiciones del agente y del objetivo
            int xAgente = posicionAgente[0];
            int yAgente = posicionAgente[1];
            int xObjetivo = posicionObjetivo[0];
            int yObjetivo = posicionObjetivo[1]; 

            // Argumentos para el agente
            Object[] agentArgs = new Object[] { modo, entorno, this, xAgente, yAgente, xObjetivo, yObjetivo, Opciones, posiciones };

            // Crear y lanzar el agente
            agentController = mainContainer.createNewAgent("AgenteMovil", "AgenteMovil", agentArgs);
            agentController.start();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al lanzar el agente: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para terminar el agente y el contenedor de JADE
     */
    private void terminarAgenteYContenedor() {
        try {
            // Detener el agente
            if (agentController != null) {
                agentController = null;
            }

            // Detener el contenedor principal
            if (mainContainer != null) {
                mainContainer.kill();
                mainContainer = null;
            }

            //Reinicia las posiciones limpia el camino y la energía
            setPosicionAgente(new int[]{0,0});
            setPosicionObjetivo(new int[]{9,9});
            limpiarCamino();
            actualizarEnergia(0);
            repaintMapa();
            posiciones = null;
            posicionesCerradas = null;
            nodoActual = null;
            posicionesAbiertas = null;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*******************************************************
     * Metodos de almacen de memoria para iteraciones
     *******************************************************/

    /**
     * Método para guardar las posiciones conocidas
     */
    public void savePosiciones(List<int[]> posiciones) {
        this.posiciones = posiciones;
    }

    /**
     * Método para obtener las posiciones conocidas
     */
    public List<int[]> getPosiciones() {
        return posiciones;
    }

    /**
     * Método para guardar las posiciones cerradas
     */
    public void savePosicionesCerradas(Set<String> posicionesCerradas) {
        this.posicionesCerradas = posicionesCerradas;
    }

    /**
     * Método para obtener las posiciones cerradas
     */
    public Set<String> getPosicionesCerradas() {
        return posicionesCerradas;
    }

    /**
     * Método para guardar el nodo actual
     */
    public void saveNodoActual(Nodo nodoActual) {
        this.nodoActual = nodoActual;
    }

    /**
     * Método para obtener el nodo actual
     */
    public Nodo getNodoActual() {
        return nodoActual;
    }

    /**
     * Método para guardar las posiciones abiertas
     */
    public void savePosicionesAbiertas(List<Nodo> posicionesAbiertas) {
        this.posicionesAbiertas = posicionesAbiertas;
    }
    
    /**
     * Método para obtener las posiciones abiertas
     */
    public List<Nodo> getPosicionesAbiertas() {
        return posicionesAbiertas;
    }
}