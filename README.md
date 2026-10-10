# LunarLite (Fabric 1.21.11)

Lunar-Client-aehnlicher Client-Mod. Features:

- HUD: FPS, CPS (links | rechts), Ping, Koordinaten, Blickrichtung, Sprint-Status
- Keystrokes (WASD, Maustasten mit CPS, Leertaste)
- Ruestungs-/Item-Anzeige mit Haltbarkeit (rechts oben)
- Zoom (Taste C halten)
- Toggle Sprint (Taste R)
- Einstellungsmenue: Rechte Shift-Taste
- Config: `.minecraft/config/lunarlite.json` (hier auch Positionen aendern)

## Bauen

1. JDK 21 installieren
2. Im Projektordner:
   - Windows: `gradlew build`  (oder `gradle build`, falls Gradle installiert ist)
   - Linux/Mac: `./gradlew build`
3. Die fertige Datei liegt in `build/libs/lunarlite-1.0.0.jar`
4. In den `mods`-Ordner kopieren (zusammen mit Fabric API fuer 1.21.11)

Falls kein `gradlew` im Ordner ist: auf https://fabricmc.net/develop/template/ ein Template
fuer 1.21.11 generieren und den `gradlew`-Teil (gradlew, gradlew.bat, gradle/) hierher kopieren.
Die Versionen in `gradle.properties` bitte mit https://fabricmc.net/develop/ abgleichen.
