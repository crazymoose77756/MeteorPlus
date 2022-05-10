package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.LiteralText;
import olejka.meteorplus.MeteorPlus;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.orbit.EventHandler;
import java.util.Objects;

public class AutoLeave extends Module {
	public AutoLeave() {
		super(MeteorPlus.CATEGORY, "Auto Leave", "Automatically logs out from the server when someone enters your render distance.");
	}
	private final SettingGroup ALSettings = settings.createGroup("Auto Leave Settings");
	private final Setting<Boolean> visualRangeIgnoreFriends = ALSettings.add(new BoolSetting.Builder()
		.name("ignore-friends")
		.description("Ignores friends.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> AutoDisable = ALSettings.add(new BoolSetting.Builder()
		.name("auto-disable")
		.description("Disables function after player detect.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> Command = ALSettings.add(new BoolSetting.Builder()
		.name("command")
		.description("Send command instead of leave.")
		.defaultValue(false)
		.build()
	);

	private final Setting<String> command_str = ALSettings.add(new StringSetting.Builder()
		.name("command:")
		.description("Send command in chat.")
		.defaultValue("/spawn")
		.visible(Command::get)
		.build()
	);

	private final Setting<Boolean> sneak = ALSettings.add(new BoolSetting.Builder()
		.name("Sneak")
		.description("Enable sneak in command mode.")
		.defaultValue(false)
		.build()
	);

	@EventHandler
	public void onEntityAdded(EntityAddedEvent event) {
		if (mc.player == null) return;
		if (visualRangeIgnoreFriends.get()) {
			if (event.entity.isPlayer() && !Friends.get().isFriend((PlayerEntity) event.entity) && !Objects.equals(event.entity.getEntityName(), mc.player.getEntityName()) && !Objects.equals(event.entity.getEntityName(), "FreeCamera")) {
				if (Command.get()) {
					mc.player.sendChatMessage(command_str.get());
					info((String.format("player §c%s§r was detected", event.entity.getEntityName())));
				} else {
					mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText(String.format("[§dAuto Leaeve§r] player %s was detected", event.entity.getEntityName()))));
				}
			if (AutoDisable.get()) this.toggle();
			}
		}
		else if (event.entity.isPlayer()){
				mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText (String.format("[§dAuto Leaeve§r] player %s was detected", event.entity.getEntityName()))));
				if (AutoDisable.get()) this.toggle();
		}
	}

	private void sneak(boolean sneak) {
		ClientPlayerEntity player = mc.player;
		PlayerInputC2SPacket inputC2SPacket = new PlayerInputC2SPacket(player.sidewaysSpeed, player.forwardSpeed, false, true);
	}
}
