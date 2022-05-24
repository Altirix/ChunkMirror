package net.altirix.chunkmirror.util;

import net.altirix.chunkmirror.ChunkMirror;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.poi.PointOfInterestStorage;

import java.io.*;
import java.net.Socket;

public class NetUtils {
    public static void sendChunkUpdate(World access, WorldChunk chunk) {
        ServerChunkManager manager = (ServerChunkManager) access.getChunkManager();

        manager.threadedAnvilChunkStorage.getPlayersWatchingChunk(
                chunk.getPos(), false).forEach(player ->
                player.networkHandler.sendPacket(new ChunkDataS2CPacket(
                        chunk, manager.getLightingProvider(), null, null, true)));
    }

    public static ProtoChunk receiveChunk(World access, int x, int z){
        try {
            Socket s = new Socket("localhost",8000);
            InputStream inputStream = s.getInputStream();
            OutputStream outputStream = s.getOutputStream();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            //objectOutputStream.writeObject(access.getRegistryKey());
            objectOutputStream.writeObject(access.getRegistryKey().getValue().toString());
            objectOutputStream.flush();
           // objectOutputStream.writeObject(new ChunkPos(x,z));
            objectOutputStream.writeObject(x);
            objectOutputStream.flush();
            objectOutputStream.writeObject(z);
            objectOutputStream.flush();

            //WorldChunk chunk = (WorldChunk) objectInputStream.readObject();
            //ChunkMirror.LOGGER.info((String) objectInputStream.readObject());
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            String serChunk = (String) objectInputStream.readObject(); //!!! stops here
            ChunkMirror.LOGGER.info(serChunk);
            NbtCompound nbtChunk = NbtHelper.fromNbtProviderString(serChunk);
            ProtoChunk chunk = ChunkSerializer.deserialize((ServerWorld) access,((ServerWorld) access).getPointOfInterestStorage(),new ChunkPos(x,z),nbtChunk);

            s.close();

            return chunk;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
