package net.altirix.chunkmirror;

import net.altirix.chunkmirror.command.CommandCopy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.world.ServerChunkManager;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunkMirror implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ChunkMirror");

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> CommandCopy.register(dispatcher));
		new ChunkMirrorServer().startServer();
	}

}

