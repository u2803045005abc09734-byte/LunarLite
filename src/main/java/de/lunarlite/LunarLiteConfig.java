package de.lunarlite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Einfache JSON-Config (config/lunarlite.json). Positionen (x/y) kannst du dort auch per Hand aendern. */
public class LunarLiteConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("lunarlite.json");

	public static LunarLiteConfig INSTANCE = new LunarLiteConfig();

	// Module an/aus
	public boolean showFps = true;
	public boolean showCps = true;
	public boolean showPing = true;
	public boolean showCoords = true;
	public boolean showDirection = true;
	public boolean showKeystrokes = true;
	public boolean showArmor = true;
	public boolean showSprintStatus = true;

	// Funktionen
	public boolean toggleSprint = false;
	public boolean zoomEnabled = true;
	public int zoomFov = 30;

	// Positionen (Pixel vom linken/oberen Rand bzw. rechten fuer Armor)
	public int infoX = 4;
	public int infoY = 4;
	public int keysX = 4;
	public int keysY = 90;
	public int armorX = 4;   // Abstand vom rechten Rand
	public int armorY = 4;

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				LunarLiteConfig cfg = GSON.fromJson(Files.readString(FILE), LunarLiteConfig.class);
				if (cfg != null) INSTANCE = cfg;
			}
		} catch (Exception e) {
			System.err.println("[LunarLite] Config konnte nicht geladen werden: " + e);
		}
		save();
	}

	public static void save() {
		try {
			Files.createDirectories(FILE.getParent());
			Files.writeString(FILE, GSON.toJson(INSTANCE));
		} catch (IOException e) {
			System.err.println("[LunarLite] Config konnte nicht gespeichert werden: " + e);
		}
	}
}
