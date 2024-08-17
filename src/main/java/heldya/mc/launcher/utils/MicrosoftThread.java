package heldya.mc.launcher.utils;

import heldya.mc.launcher.Launcher;

public class MicrosoftThread implements Runnable {
    @Override
    public void run() {

    // Espace réservé pour la simulation d'entrée utilisateur ou appel direct.

        String username = "ExampleUser"; // remplace avec le nom utilisateur choisi.
        String uuid = "example-uuid"; // met à jour le UUID quand l'utilisateur met un pseudo.
        String token = "example-token"; // met à jour le token quand l'utilisateur met un pseudo.

        try {
            // authentification géré automatiquement par la génération d'un UUID et d'un token
            // lorsqu'un pseudo est mis en place.
            Launcher.authenticate(username, uuid, token);
        } catch (Exception e) {
            Launcher.getReporter().catchError(e, "Impossible de se connecter.");
        }
    }
}
