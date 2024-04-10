/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.lib.net;

import buildcraft.core.BCCore;
import buildcraft.api.IBuildCraftMod;
import buildcraft.api.core.BCDebugging;
import buildcraft.api.core.BCLog;
import buildcraft.lib.BCLib;
import buildcraft.lib.BCLibProxy;
import buildcraft.lib.misc.MessageUtil;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MessageManager
{
    public static final boolean DEBUG = BCDebugging.shouldDebugLog("lib.messages");

    // Calen: Thread Safety
//    private static final Map<IBuildCraftMod, PerModHandler> MOD_HANDLERS;
    private static final ConcurrentSkipListMap<IBuildCraftMod, PerModHandler> MOD_HANDLERS;
    //    private static final Map<Class<? extends IMessage>, PerMessageInfo<?>> MESSAGE_HANDLERS = new HashMap<>();
    private static final ConcurrentHashMap<Class<? extends IMessage>, PerMessageInfo<?>> MESSAGE_HANDLERS = new ConcurrentHashMap<>();

    static
    {
        // Calen: Thread Safety -> IllegalArgumentException: NetworkDirection Channel {buildcraftcore:default} already registered
//        MOD_HANDLERS = new TreeMap<>(MessageManager::compareMods);
        MOD_HANDLERS = new ConcurrentSkipListMap<>(MessageManager::compareMods);
    }

    static class PerModHandler
    {
        final IBuildCraftMod module;
        //        final SimpleChannel netWrapper;
        final SimpleChannel netWrapper;
        final SortedMap<Class<? extends IMessage>, PerMessageInfo<?>> knownMessages;

        PerModHandler(IBuildCraftMod module)
        {
            this.module = module;
            this.netWrapper = NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(module.getModId(), "default"),
                    () -> BCCore.MOD_VERSION,
                    (version) -> true,
                    (version) -> true
            );
            knownMessages = new TreeMap<>(Comparator.comparing(Class::getName));
        }
    }

    static class PerMessageInfo<I extends IMessage>
    {
        final PerModHandler modHandler;
        final Class<I> messageClass;

        /**
         * The handler to register, or null if this isn't handled in this physical side.
         */
        @Nullable
        IMessageHandler<I, ?> clientHandler, serverHandler;

        PerMessageInfo(PerModHandler modHandler, Class<I> messageClass)
        {
            this.modHandler = modHandler;
            this.messageClass = messageClass;
        }
    }

    private static int compareMods(IBuildCraftMod modA, IBuildCraftMod modB)
    {
        if (modA instanceof Enum && modB instanceof Enum)
        {
            Enum<?> enumA = (Enum<?>) modA;
            Enum<?> enumB = (Enum<?>) modB;
            if (enumA.getDeclaringClass() == enumB.getDeclaringClass())
            {
                return Integer.compare(enumA.ordinal(), enumB.ordinal());
            }
        }
        return modA.getModId().compareTo(modB.getModId());
    }

    /**
     * Registers a message as one that will not be received, but will be sent.
     */
    public static <I extends IMessage> void registerMessageClass(IBuildCraftMod module, Class<I> clazz, Dist... sides)
    {
        registerMessageClass(module, clazz, null, sides);
    }

    public static <I extends IMessage> void registerMessageClass(IBuildCraftMod module, Class<I> messageClass,
                                                                 IMessageHandler<I, ?> messageHandler, Dist... sides)
    {
        PerModHandler modHandler = MOD_HANDLERS.computeIfAbsent(module, PerModHandler::new);
        PerMessageInfo<I> messageInfo = (PerMessageInfo<I>) modHandler.knownMessages.get(messageClass);
        if (messageInfo == null)
        {
            messageInfo = new PerMessageInfo<>(modHandler, messageClass);
            modHandler.knownMessages.put(messageClass, messageInfo);
            MESSAGE_HANDLERS.put(messageClass, messageInfo);
        }
        String netName = module.getModId();
        if (messageHandler == null)
        {
            if (DEBUG)
            {
                BCLog.logger.info("[lib.messages] Registered message " + messageClass + " for " + netName);
            }
            return;
        }
        Dist specificSide = sides != null && sides.length == 1 ? sides[0] : null;
        if (specificSide == null || specificSide == Dist.CLIENT)
        {
            if (messageInfo.clientHandler != null && DEBUG)
            {
                BCLog.logger.info("[lib.messages] Replacing existing client handler for " + netName + " " + messageClass
                        + " " + messageInfo.clientHandler + " with " + messageHandler);
            }
            messageInfo.clientHandler = messageHandler;
////            modHandler.netWrapper.messageBuilder(messageClass, nextID(), NetworkDirection.PLAY_TO_CLIENT)
////                    .encoder(I::toBytes)
////                    .decoder(I::fromBytes)
////                    .consumer((msg, supplier) -> messageHandler.onMessage(msg, supplier))
////                    .add();
////            modHandler.netWrapper.registerMessage()
//            modHandler.netWrapper.messageBuilder(messageClass, nextID(), NetworkDirection.PLAY_TO_CLIENT)
//                    .encoder(I::toBytes)
//                    .decoder(I::fromBytes)
//                    .consumer((msg, supplier) -> messageHandler.onMessage(msg, supplier))
//                    .add();
        }
        if (specificSide == null || specificSide == Dist.DEDICATED_SERVER)
        {
            if (messageInfo.serverHandler != null && DEBUG)
            {
                BCLog.logger.info("[lib.messages] Replacing existing server handler for " + netName + " " + messageClass
                        + " " + messageInfo.serverHandler + " with " + messageHandler);
            }
            messageInfo.serverHandler = messageHandler;
        }
    }

    /**
     * Sets the handler for the specified handler.
     *
     * @param side The side that the given handler will receive messages on.
     */
    public static <I extends IMessage> void setHandler(Class<I> messageClass, IMessageHandler<I, ?> messageHandler,
                                                       Dist side)
    {
        PerMessageInfo<I> messageInfo = (PerMessageInfo<I>) MESSAGE_HANDLERS.get(messageClass);
        if (messageInfo == null)
        {
            throw new IllegalArgumentException("Cannot set handler for unregistered message: " + messageClass);
        }
        registerMessageClass(messageInfo.modHandler.module, messageClass, messageHandler, side);
    }

    /**
     * Called by {@link BCLib} to finish registering this class.
     */
    public static void fmlPostInit()
    {
        if (DEBUG)
        {
            BCLog.logger.info("[lib.messages] Sorting and registering message classes and orders:");
        }
        for (PerModHandler handler : MOD_HANDLERS.values())
        {
            if (DEBUG)
            {
                BCLog.logger.info("[lib.messages]  - Module: " + handler.module.getModId());
            }
            int wholeId = 0;
            for (PerMessageInfo<?> info : handler.knownMessages.values())
            {
                int id = wholeId++;
                postInitSingle(handler, id, info);
            }
        }
    }

    private static <I extends IMessage> void postInitSingle(PerModHandler handler, int id, PerMessageInfo<I> info)
    {
        boolean cl = info.clientHandler != null;
        boolean sv = info.serverHandler != null;
        if (!(cl | sv))
        {
            if (FMLEnvironment.dist == Dist.CLIENT)
            {
                // the client should *always* be able to handle everything.
                throw new IllegalStateException("Found a registered message " + info.messageClass + " for "
                        + info.modHandler.module.getModId() + " that didn't have any handlers!");
            }
        }

        Class<I> msgClass = info.messageClass;

        // Calen: null -> both directions are ok
        handler.netWrapper.messageBuilder(msgClass, id, null)
                .encoder(I::toBytes)
                .decoder((buf) -> (I) IMessage.staticFromBytes(msgClass, buf))
                .consumer((msg, supplier) ->
                {
                    // Calen: setPacketHandled! -> https://zhuanlan.zhihu.com/p/657227507
                    // or the console will say: Unknown custom packet identifier: buildcraftlib:default
                    NetworkEvent.Context context = supplier.get();
                    // Calen: 1.18.2注册时不要求按方向指定handler 处理时需要判断方向选择handler
                    IMessageHandler messageHandler = null;
                    if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER)
                    {
                        messageHandler = info.serverHandler;
                    }
                    else if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT)
                    {
                        messageHandler = info.clientHandler;
                    }
                    IMessage reply = wrapHandler(messageHandler, msgClass).onMessage(msg, context);
                    context.setPacketHandled(true);
                })
                .add();
        if (DEBUG)
        {
            String sides = cl ? (sv ? "{client, server}" : "{client}") : "{server}";
            BCLog.logger.info("[lib.messages]      " + id + ": " + msgClass + " on sides: " + sides);
        }
    }

//    private static <M> void register(PerModHandler handler, int id, Class<M> messageType, BiConsumer<M, FriendlyByteBuf> encoder,
//                                     Function<FriendlyByteBuf, M> decoder,
//                                     BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer)
//    {
//        handler.netWrapper.registerMessage(id, messageType, encoder, decoder, messageConsumer);
//    }

    private static <I extends IMessage> IMessageHandler<I, ?> wrapHandler(IMessageHandler<I, ?> messageHandler,
                                                                          Class<I> messageClass)
    {
        if (messageHandler == null)
        {
            return (message, context) ->
            {
//                if (context.side == Dist.DEDICATED_SERVER)
                if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER)
                {
                    // Bad/Buggy client
                    Player player = context.getSender();
                    BCLog.logger.warn(
                            "[lib.messages] The client " + player.getName() + " (ID = " + player.getGameProfile().getId()
                                    + ") sent an invalid message " + messageClass + ", when they should only receive them!");
                }
                else
                {
                    throw new Error("Received message " + messageClass
                            + " on the client, when it should only be sent by the client and received on the server!");
                }
                return null;
            };
        }
        else
        {
            return (message, context) ->
            {
                Player player = BCLibProxy.getProxy().getPlayerForContext(context);
                if (player == null || player.level == null)
                {
                    return null;
                }
                BCLibProxy.getProxy().addScheduledTask(player.level, () ->
                {
                    IMessage reply = messageHandler.onMessage(message, context);
                    if (reply != null)
                    {
                        MessageUtil.sendReturnMessage(context, reply);
                    }
                });
                return null;
            };
        }
    }

    private static SimpleChannel getSimpleNetworkWrapper(IMessage message)
    {
        PerMessageInfo<?> info = MESSAGE_HANDLERS.get(message.getClass());
        if (info == null)
        {
            throw new IllegalArgumentException("Cannot send unregistered message " + message.getClass());
        }
        return info.modHandler.netWrapper;
    }

    /**
     * Send this message to everyone. The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     */
    public static void sendToAll(IMessage message)
    {
//        getSimpleNetworkWrapper(message).sendToAll(message);
        getSimpleNetworkWrapper(message).send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * Send this message to the specified player. The {@link IMessageHandler} for this message type should be on the
     * CLIENT side.
     *
     * @param message The message to send
     * @param player  The player to send it to
     */
    public static void sendTo(IMessage message, ServerPlayer player)
    {
//        getSimpleNetworkWrapper(message).sendTo(message, player);
        getSimpleNetworkWrapper(message).send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    /**
     * Send this message to everyone within a certain range of a point. The {@link IMessageHandler} for this message
     * type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param point   The {@link net.minecraftforge.network.PacketDistributor.TargetPoint} around which to
     *                send
     */
    public static void sendToAllAround(IMessage message, PacketDistributor.TargetPoint point)
    {
//        getSimpleNetworkWrapper(message).sendToAllAround(message, point);
        getSimpleNetworkWrapper(message).send(PacketDistributor.NEAR.with(() -> point), message);
    }

    /**
     * Send this message to everyone within the supplied dimension. The {@link IMessageHandler} for this message type
     * should be on the CLIENT side.
     *
     * @param message     The message to send
     * @param dimensionId The dimension id to target
     */
//    public static void sendToDimension(IMessage message, int dimensionId)
    public static void sendToDimension(IMessage message, ResourceKey<Level> dimensionId)
    {
//        getSimpleNetworkWrapper(message).sendToDimension(message, dimensionId);
        getSimpleNetworkWrapper(message).send(PacketDistributor.DIMENSION.with(() -> dimensionId), message);
    }

    /**
     * Send this message to the server. The {@link IMessageHandler} for this message type should be on the SERVER side.
     *
     * @param message The message to send
     */
    public static void sendToServer(IMessage message)
    {
        getSimpleNetworkWrapper(message).sendToServer(message);
    }
}