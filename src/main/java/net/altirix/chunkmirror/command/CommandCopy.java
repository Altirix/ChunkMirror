package net.altirix.chunkmirror.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.altirix.chunkmirror.ChunkMirror;
import net.altirix.chunkmirror.util.ChunkUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

import java.util.UUID;
import java.util.concurrent.ExecutionException;


public class CommandCopy {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literal = CommandManager.literal("copy");
        literal.executes(CommandCopy::execute);
        dispatcher.register(literal);
    }

    public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        UUID playerUuid = player.getUuid();

        ChunkPos cPos = player.getChunkPos();

        try {
            ChunkUtils.replaceChunk(player.world, cPos.x, cPos.z);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        player.sendSystemMessage(Text.of("test"), playerUuid);
        return Command.SINGLE_SUCCESS;
    }
}
