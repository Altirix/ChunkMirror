package net.altirix.chunkmirror;

import com.google.gson.Gson;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkMirrorServer {
    public static Map<String, World> worlds = new HashMap<>();

    public void startServer() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(3);

        Runnable serverTask = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(9000);
                ChunkMirror.LOGGER.info("Ready For Clients");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    clientProcessingPool.submit(new ClientTask(clientSocket));
                }
            } catch (IOException e) {
                ChunkMirror.LOGGER.warn("Unable to process client request");
                e.printStackTrace();
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    private record ClientTask(Socket clientSocket) implements Runnable {
        @Override
        public void run() {
            ChunkMirror.LOGGER.info("Client Connected");
            try {
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                String world = (String) objectInputStream.readObject();
                int x = (int) objectInputStream.readObject();
                int z = (int) objectInputStream.readObject();
                ChunkMirror.LOGGER.info("World: "+ world + " x: " + x + " z: " + z);

                WorldAccess access = worlds.get(world);
                Chunk chunk = access.getChunk(x,z);

                NbtCompound serChunk = ChunkSerializer.serialize((ServerWorld) access, chunk);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(serChunk.toString());
                objectOutputStream.flush();

                clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}