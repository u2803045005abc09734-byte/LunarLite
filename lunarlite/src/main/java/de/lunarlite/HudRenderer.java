package de.lunarlite;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Locale;

public class HudRenderer {
	private static final int WHITE = 0xFFFFFFFF;
	private static final int GRAY = 0xFFAAAAAA;
	private static final int BOX_BG = 0x66000000;
	private static final int BOX_BG_ON = 0xAAFFFFFF;

	// FPS-Zaehler (eigene Messung, unabhaengig von Minecraft-Internals)
	private static int frames = 0;
	private static int fps = 0;
	private static long lastFpsTime = System.currentTimeMillis();

	// CPS: Zeitstempel der Klicks in den letzten 1000 ms
	private static final ArrayDeque<Long> leftClicks = new ArrayDeque<>();
	private static final ArrayDeque<Long> rightClicks = new ArrayDeque<>();
	private static boolean leftWasDown = false;
	private static boolean rightWasDown = false;

	public static void register() {
		HudRenderCallback.EVENT.register(HudRenderer::render);
	}

	private static void pollClicks() {
		long window = GLFW.glfwGetCurrentContext();
		if (window == 0L) return;
		long now = System.currentTimeMillis();

		boolean left = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		boolean right = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
		if (left && !leftWasDown) leftClicks.addLast(now);
		if (right && !rightWasDown) rightClicks.addLast(now);
		leftWasDown = left;
		rightWasDown = right;

		while (!leftClicks.isEmpty() && now - leftClicks.peekFirst() > 1000) leftClicks.pollFirst();
		while (!rightClicks.isEmpty() && now - rightClicks.peekFirst() > 1000) rightClicks.pollFirst();
	}

	private static void render(GuiGraphics g, DeltaTracker delta) {
		Minecraft mc = Minecraft.getInstance();

		// FPS und Klicks immer mitzaehlen
		frames++;
		long nowMs = System.currentTimeMillis();
		if (nowMs - lastFpsTime >= 1000) {
			fps = frames;
			frames = 0;
			lastFpsTime = nowMs;
		}
		pollClicks();

		if (mc.player == null || mc.options.hideGui) return;
		LunarLiteConfig cfg = LunarLiteConfig.INSTANCE;

		// ---- Info-Block (links oben) ----
		int x = cfg.infoX;
		int y = cfg.infoY;
		int line = 11;

		if (cfg.showFps) {
			text(g, mc, "FPS: " + fps, x, y);
			y += line;
		}
		if (cfg.showCps) {
			text(g, mc, "CPS: " + leftClicks.size() + " | " + rightClicks.size(), x, y);
			y += line;
		}
		if (cfg.showPing && mc.getConnection() != null) {
			PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
			if (info != null) {
				text(g, mc, "Ping: " + info.getLatency() + " ms", x, y);
				y += line;
			}
		}
		if (cfg.showCoords) {
			text(g, mc, String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f",
				mc.player.getX(), mc.player.getY(), mc.player.getZ()), x, y);
			y += line;
		}
		if (cfg.showDirection) {
			String dir = mc.player.getDirection().getName();
			text(g, mc, "Richtung: " + dir.substring(0, 1).toUpperCase(Locale.ROOT) + dir.substring(1), x, y);
			y += line;
		}
		if (cfg.showSprintStatus) {
			if (cfg.toggleSprint) {
				text(g, mc, "[Sprint: Toggled]", x, y);
			} else if (mc.options.keySprint.isDown()) {
				text(g, mc, "[Sprint: Gehalten]", x, y);
			}
		}

		// ---- Keystrokes ----
		if (cfg.showKeystrokes) {
			renderKeystrokes(g, mc, cfg.keysX, cfg.keysY);
		}

		// ---- Ruestungsanzeige (rechts oben) ----
		if (cfg.showArmor) {
			renderArmor(g, mc, mc.getWindow().getGuiScaledWidth() - 20 - cfg.armorX, cfg.armorY);
		}
	}

	private static void text(GuiGraphics g, Minecraft mc, String s, int x, int y) {
		g.drawString(mc.font, s, x, y, WHITE, true);
	}

	private static void keyBox(GuiGraphics g, Minecraft mc, int x, int y, int w, int h, String label, boolean down) {
		g.fill(x, y, x + w, y + h, down ? BOX_BG_ON : BOX_BG);
		int tw = mc.font.width(label);
		g.drawString(mc.font, label, x + (w - tw) / 2, y + (h - 8) / 2, down ? 0xFF000000 : WHITE, false);
	}

	private static void renderKeystrokes(GuiGraphics g, Minecraft mc, int x, int y) {
		int s = 20; // Tastengroesse
		int gap = 2;
		KeyMapping w = mc.options.keyUp, a = mc.options.keyLeft, sKey = mc.options.keyDown, d = mc.options.keyRight;

		keyBox(g, mc, x + s + gap, y, s, s, "W", w.isDown());
		keyBox(g, mc, x, y + s + gap, s, s, "A", a.isDown());
		keyBox(g, mc, x + s + gap, y + s + gap, s, s, "S", sKey.isDown());
		keyBox(g, mc, x + 2 * (s + gap), y + s + gap, s, s, "D", d.isDown());

		int mouseY = y + 2 * (s + gap);
		int mw = (3 * s + 2 * gap - gap) / 2;
		keyBox(g, mc, x, mouseY, mw, s, "LMB " + leftClicks.size(), leftWasDown);
		keyBox(g, mc, x + mw + gap, mouseY, mw, s, "RMB " + rightClicks.size(), rightWasDown);

		int spaceY = mouseY + s + gap;
		keyBox(g, mc, x, spaceY, 3 * s + 2 * gap, 12, "-----", mc.options.keyJump.isDown());
	}

	private static void renderArmor(GuiGraphics g, Minecraft mc, int x, int y) {
		EquipmentSlot[] slots = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
		};
		for (EquipmentSlot slot : slots) {
			ItemStack stack = mc.player.getItemBySlot(slot);
			if (stack.isEmpty()) continue;
			g.renderItem(stack, x, y);
			g.renderItemDecorations(mc.font, stack, x, y);
			if (stack.isDamageableItem()) {
				int left = stack.getMaxDamage() - stack.getDamageValue();
				g.drawString(mc.font, String.valueOf(left), x - mc.font.width(String.valueOf(left)) - 2, y + 4, GRAY, true);
			}
			y += 18;
		}
	}
}
