package org.lushplugins.lushtags.util.lamp.response;

import org.lushplugins.chatcolorhandler.paper.PaperColor;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.response.ResponseHandler;

public class StringMessageResponseHandler implements ResponseHandler<BukkitCommandActor, String> {

    @Override
    public void handleResponse(String string, ExecutionContext<BukkitCommandActor> context) {
        PaperColor.handler().sendMessage(context.actor().sender(), string);
    }
}