package de.lunarlite;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class LunarLiteClient implements ClientModInitializer {
	public static KeyMapping menuKey;
	public static KeyMapping zoomKey;
	public static KeyMapping sprintToggleKey;

	/** FOV vor dem Zoomen, null = nicht am Zoomen. */
	private static Integer savedFov = null;

	@Override
	public void onInitializeClient() {
		LunarLiteConfig.load();

		menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.lunarlite.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));
		zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.lunarlite.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, KeyMapping.Category.MISC));
		sprintToggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.lunarlite.togglesprint", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KeyMapping.Category.MISC));

		HudRenderer.register();

		ClientTickEvents.END_CLIENT_TICK.register(LunarLiteClient::onTick);
	}

	private static void onTick(Minecraft mc) {
		LunarLiteConfig cfg = LunarLiteConfig.INSTANCE;

		while (menuKey.consumeClick()) {
			mc.setScreen(new LunarLiteScreen());
		}
		while (sprintToggleKey.consumeClick()) {
			cfg.toggleSprint = !cfg.toggleSprint;
			LunarLiteConfig.save();
		}

		if (mc.player == null) {
			savedFov = null;
			return;
		}

		// Toggle Sprint: rennt automatisch, solange man vorwaerts laeuft
		if (cfg.toggleSprint && mc.screen == null && mc.options.keyUp.isDown()) {
			mc.options.keySprint.setDown(true);
		}

		// Zoom: FOV waehrend die Taste gehalten wird absenken, danach wiederherstellen
		boolean zooming = cfg.zoomEnabled && zoomKey.isDown() && mc.screen == null;
		if (zooming) {
			if (savedFov == null) savedFov = mc.options.fov().get();
			mc.options.fov().set(Math.max(30, Math.min(110, cfg.zoomFov)));
		} else if (savedFov != null) {
			mc.options.fov().set(savedFov);
			savedFov = null;
		}
	}
}
