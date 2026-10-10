package de.lunarlite;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Einstellungsmenue (Standard: Rechte Shift-Taste). */
public class LunarLiteScreen extends Screen {

	public LunarLiteScreen() {
		super(Component.literal("LunarLite"));
	}

	private void toggle(int x, int y, String name, BooleanSupplier get, Consumer<Boolean> set) {
		addRenderableWidget(Button.builder(label(name, get.getAsBoolean()), b -> {
			boolean now = !get.getAsBoolean();
			set.accept(now);
			b.setMessage(label(name, now));
			LunarLiteConfig.save();
		}).bounds(x, y, 150, 20).build());
	}

	private static Component label(String name, boolean on) {
		return Component.literal(name + ": " + (on ? "AN" : "AUS"));
	}

	@Override
	protected void init() {
		LunarLiteConfig c = LunarLiteConfig.INSTANCE;
		int left = this.width / 2 - 155;
		int right = this.width / 2 + 5;
		int top = this.height / 2 - 80;
		int step = 24;

		toggle(left, top, "FPS", () -> c.showFps, v -> c.showFps = v);
		toggle(right, top, "CPS", () -> c.showCps, v -> c.showCps = v);
		toggle(left, top + step, "Ping", () -> c.showPing, v -> c.showPing = v);
		toggle(right, top + step, "Koordinaten", () -> c.showCoords, v -> c.showCoords = v);
		toggle(left, top + 2 * step, "Richtung", () -> c.showDirection, v -> c.showDirection = v);
		toggle(right, top + 2 * step, "Keystrokes", () -> c.showKeystrokes, v -> c.showKeystrokes = v);
		toggle(left, top + 3 * step, "Ruestung", () -> c.showArmor, v -> c.showArmor = v);
		toggle(right, top + 3 * step, "Sprint-Status", () -> c.showSprintStatus, v -> c.showSprintStatus = v);
		toggle(left, top + 4 * step, "Toggle Sprint", () -> c.toggleSprint, v -> c.toggleSprint = v);
		toggle(right, top + 4 * step, "Zoom", () -> c.zoomEnabled, v -> c.zoomEnabled = v);

		addRenderableWidget(Button.builder(Component.literal("Fertig"), b -> onClose())
			.bounds(this.width / 2 - 75, top + 5 * step + 8, 150, 20).build());
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		super.render(g, mouseX, mouseY, delta);
		g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 105, 0xFFFFFFFF);
		g.drawCenteredString(this.font,
			Component.literal("Positionen: config/lunarlite.json  |  Zoom: C  |  Toggle Sprint: R"),
			this.width / 2, this.height / 2 + 75, 0xFFAAAAAA);
	}

	@Override
	public void onClose() {
		LunarLiteConfig.save();
		super.onClose();
	}
}
