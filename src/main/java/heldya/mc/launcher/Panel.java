package heldya.mc.launcher;

import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

import static heldya.mc.launcher.Frame.getBufferedImage;

public class Panel extends JPanel implements SwingerEventListener {
    private JFXPanel fxPanel;
    private MediaPlayer mediaPlayer;
    private STexturedButton play = new STexturedButton(getBufferedImage("play.png"), getBufferedImage("play.png"));
    private STexturedButton connectButton = new STexturedButton(getBufferedImage("account.png"), getBufferedImage("account.png"));
    private JLabel usernameLabel = new JLabel("Compte : non connecté"); // si la personne est pas connectée, alors marquer ceci.
    private String username; // si la personne est connectée, alors marquer ceci "compte : $username".

    public Panel() throws IOException {
        this.setLayout(null);

        fxPanel = new JFXPanel();
        this.add(fxPanel);

        Platform.runLater(() -> {
            initFX(fxPanel);  // Initialiser la vidéo dans le JFXPanel
        });

        // Met en place le bouton "play" à x = 776, Y = 482.
        play.setBounds(776, 482, 100, 50);
        play.addEventListener(this);
        this.add(play);

        // Met en place le bouton "connectButton" à X = 634, Y = 484.
        connectButton.setBounds(634, 484, 100, 50);
        connectButton.addEventListener(e -> handleConnectButton());
        this.add(connectButton);

        this.setComponentZOrder(fxPanel, this.getComponentCount() - 1);

        // Écouteur pour redimensionner le fxPanel et ajuster MediaView en conséquence.
        fxPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Platform.runLater(() -> {
                    adjustMediaViewSize(fxPanel);
                });
            }
        });

        // Position du texte "compte : non connecté" et "compte : $username".
        usernameLabel.setBounds(10, 10, 200, 30);
        this.add(usernameLabel);
    }

    // Background vidéo
    private void initFX(JFXPanel fxPanel) {
        // mettre le chemin absolu de la vidéo.
        String videoPath = Paths.get("src/main/resources/background.mp4").toUri().toString();

        // Charge la vidéo.
        Media media = new Media(videoPath);
        mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        // Ajustement de la taille de la MediaView.
        mediaView.setFitWidth(fxPanel.getWidth());
        mediaView.setFitHeight(fxPanel.getHeight());
        mediaView.setPreserveRatio(false);

        // Création de la scène JavaFX avec la vidéo en arrière-plan.
        Scene scene = new Scene(new javafx.scene.Group(mediaView));
        fxPanel.setScene(scene);

        // Lecture en boucle de la vidéo.
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // fais une lecture en boucle de la vidéo.
        mediaPlayer.play(); // lance la vidéo.
    }

    private void adjustMediaViewSize(JFXPanel fxPanel) {
        MediaView mediaView = (MediaView) ((javafx.scene.Group) fxPanel.getScene().getRoot()).getChildren().get(0);
        mediaView.setFitWidth(fxPanel.getWidth());
        mediaView.setFitHeight(fxPanel.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Met à jour fxPanel pour la taille de la fenêtre.
        fxPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        // Assure que les boutons seront toujours en premier plan par rapport au background.
        this.setComponentZOrder(fxPanel, this.getComponentCount() - 1);
    }

    // Vérification et lancement du jeu
    @Override
    public void onEvent(SwingerEvent swingerEvent) {
        if (swingerEvent.getSource() == play) {
            // Vérification de authInfos
            if (Launcher.getAuthInfos() == null) {
                JOptionPane.showMessageDialog(this, "Veuillez vous authentifier d'abord.");
                return;
            }

            try {
                Launcher.update();
            } catch (Exception e) {
                Launcher.getReporter().catchError(e, "Impossible de mettre à jour le launcher.");
            }

            try {
                Launcher.launch();
            } catch (Exception e) {
                Launcher.getReporter().catchError(e, "Impossible de lancer le jeu.");
            }
        }
    }

    // Fenêtre de mise en place pseudo
    private void handleConnectButton() {
        String inputUsername = JOptionPane.showInputDialog(this, "Entrez votre pseudo:", "Authentification", JOptionPane.PLAIN_MESSAGE);
        if (inputUsername != null && !inputUsername.isEmpty()) {
            username = inputUsername;
            String uuid = generateUUID();
            String token = generateToken();
            Launcher.authenticate(username, uuid, token);

            // Sauvegarde le pseudo choisi.
            Frame.saveUsername(username);

            // Met à jour le "compte : non connecté" en "compte : $username".
            updateUsernameLabel();

            JOptionPane.showMessageDialog(this, "Authentification réussie !\nPseudo: " + username + "\nUUID: " + uuid + "\nToken: " + token);
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un pseudo valide.");
        }
    }

    // Gère la randomisation de UUID quand un pseudo est entré.
    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    // Gère la randomisation du token quand un pseudo est entré.
    private String generateToken() {
        return Long.toHexString(new Random().nextLong());
    }

    // Permet de mettre son pseudo et qu'il soit reconnu en jeu.
    public void setUsername(String username) {
        this.username = username;
        updateUsernameLabel();
    }

    // Texte permettant de voir si on est authentifié ou non.
    private void updateUsernameLabel() {
        if (username != null && !username.isEmpty()) {
            usernameLabel.setText("Compte : " + username);
        } else {
            usernameLabel.setText("Compte : non connecté");
        }
    }
}
