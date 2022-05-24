package net.altirix.chunkmirror.mixin;

import net.altirix.chunkmirror.ChunkMirror;
import net.altirix.chunkmirror.ChunkMirrorServer;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(World.class)
public class WorldMixin {

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(MutableWorldProperties properties, RegistryKey registryRef, RegistryEntry registryEntry,
                      Supplier profiler, boolean isClient, boolean debugWorld, long seed, CallbackInfo ci) {

        ChunkMirrorServer.worlds.put(registryRef.getValue().toString(), (World) (Object) this);
        ChunkMirror.LOGGER.info("Added World Reference to: " + registryRef.getValue());

    }
}
