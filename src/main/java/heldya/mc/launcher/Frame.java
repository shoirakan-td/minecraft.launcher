package heldya.mc.launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

// NE PAS TOUCHER !!!
public class Frame extends JFrame {
    private static Frame instance;
    private Panel panel;
    private static final String USERNAME_KEY = "savedUsername";

    // paramètres graphique du launcher.
    public Frame() throws IOException {
        instance = this;
        this.setTitle("Heldya Launcher"); // nom de la fenêtre windows
        this.setDefaultCloseOperation(EXIT_ON_CLOSE); // quitte le launcher lorsque X est appuyé.
        this.setSize(960, 600); // rèle la longueur X largeur de la fenêtre.
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);
        this.setIconImage(getImage("logo.png")); // icone du launcher dans la barre des tâches.

        // initialise le panel.
        this.panel = new Panel();
        this.setContentPane(panel);

        // charge le pseudo de l'utilisateur si il à été mis en place à la
        // dernière exécution du programme.
        String savedUsername = getSavedUsername();
        if (savedUsername != null) {
            panel.setUsername(savedUsername);
        }

        this.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        Launcher.crashFile.mkdirs();
        instance = new Frame();
    }

    public static Image getImage(String fileName) throws IOException {
        try (InputStream inputStream = Frame.getInstance().getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found : " + fileName);
            }
            return ImageIO.read(inputStream);
        }
    }

    public static BufferedImage getBufferedImage(String fileName) throws IOException {
        try (InputStream inputStream = Frame.getInstance().getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found : " + fileName);
            }
            return ImageIO.read(inputStream);
        }
    }

    public static Frame getInstance() {
        return instance;
    }

    public Panel getPanel() {
        return this.panel;
    }

    // enregistre  les données utilisateur (token / UUID / pseudo)
    public static void saveUsername(String username) {
        Preferences prefs = Preferences.userRoot().node(Frame.class.getName());
        prefs.put(USERNAME_KEY, username);
    }

    // retrouve les données utilisateur (token / UUID / pseudo).
    public static String getSavedUsername() {
        Preferences prefs = Preferences.userRoot().node(Frame.class.getName());
        return prefs.get(USERNAME_KEY, null);
    }
}
