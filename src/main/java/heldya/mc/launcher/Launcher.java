package heldya.mc.launcher;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.openlauncherlib.util.CrashReporter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

// à modifier si besoin, si vous avez besoin : https://discord.gg/5b5N4h6QjB (shoirakan_.).
public class Launcher {
    private static GameInfos gameInfos = new GameInfos("Heldya Launcher", new GameVersion("1.20", GameType.V1_13_HIGHER_FORGE), new GameTweak[]{GameTweak.FORGE});
    private static Path path = gameInfos.getGameDir();
    public static File crashFile = new File(String.valueOf(path), ".heldya_launcher");
    private static CrashReporter reporter = new CrashReporter(String.valueOf(crashFile), path);
    private static AuthInfos authInfos;

     private static final String USERNAME_KEY = "savedUsername";
    private static final String UUID_KEY = "savedUUID";
    private static final String TOKEN_KEY = "savedToken";

    // Méthode pour authentifier avec un pseudo, UUID et token
    public static void authenticate(String username, String uuid, String token) {
        authInfos = new AuthInfos(username, token, uuid);
        // Sauvegarder les informations d'authentification
        Preferences prefs = Preferences.userRoot().node(Launcher.class.getName());
        prefs.put(USERNAME_KEY, username);
        prefs.put(UUID_KEY, uuid);
        prefs.put(TOKEN_KEY, token);
    }

    // Méthode pour récupérer les informations d'authentification
    public static AuthInfos getAuthInfos() {
        return authInfos;
    }

    // Méthode pour récupérer les informations d'authentification sauvegardées
    public static String getSavedUsername() {
        Preferences prefs = Preferences.userRoot().node(Launcher.class.getName());
        return prefs.get(USERNAME_KEY, null);
    }

    public static String getSavedUUID() {
        Preferences prefs = Preferences.userRoot().node(Launcher.class.getName());
        return prefs.get(UUID_KEY, null);
    }

    public static String getSavedToken() {
        Preferences prefs = Preferences.userRoot().node(Launcher.class.getName());
        return prefs.get(TOKEN_KEY, null);
    }

    // Méthode pour supprimer les informations d'authentification
    public static void deleteAuthInfos() {
        Preferences prefs = Preferences.userRoot().node(Launcher.class.getName());
        prefs.remove(USERNAME_KEY);
        prefs.remove(UUID_KEY);
        prefs.remove(TOKEN_KEY);
    }

    public static void update() throws Exception {
        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder().withName("1.20").build();
        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder().build();

        List<CurseFileInfo> curseFileInfos = new ArrayList<>();
        // curseFileInfos.add(new CurseFileInfo(238222, 4581323));

        AbstractForgeVersion version = new ForgeVersionBuilder(ForgeVersionBuilder.ForgeVersionType.NEW)
                .withCurseMods(curseFileInfos)
                .withForgeVersion("46.0.14")
                .build();

        FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withUpdaterOptions(options)
                .withModLoaderVersion(version)
                .build();
        updater.update(path);
    }

    public static void launch() throws Exception {
        if (authInfos == null) {
            throw new IllegalStateException("Authentication information is not set.");
        }
        NoFramework noFramework = new NoFramework(path, authInfos, GameFolder.FLOW_UPDATER);
        noFramework.launch("1.20", "46.0.14", NoFramework.ModLoader.FORGE);
    }

    public static CrashReporter getReporter() {
        return reporter;
    }
}
