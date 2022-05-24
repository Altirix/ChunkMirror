package net.altirix.chunkmirror.util;

import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ChunkUtils {

    public static void replaceChunk(World access, int xc, int zc) throws ExecutionException, InterruptedException {
        WorldChunk chunkTarget = access.getChunk(xc, zc);

        CompletableFuture<ProtoChunk> completableFuture = new CompletableFuture<>();

        new Thread(() -> {
            ProtoChunk chunkReplace = NetUtils.receiveChunk(access, xc, zc);
            completableFuture.complete(chunkReplace);
        }).start();

        ProtoChunk chunkReplace = completableFuture.get();

        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++){
                for (int y = chunkTarget.getBottomY(); y < chunkTarget.getTopY(); y++){
                    BlockPos bPos = new BlockPos(x,y,z);
                    chunkTarget.setBlockState(bPos, chunkReplace.getBlockState(bPos),false);
                }
            }
        }

        chunkTarget.setNeedsSaving(true);

        NetUtils.sendChunkUpdate(access, chunkTarget);
    }


}
