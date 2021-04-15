package de.life.listener;

import de.life.GlobalVariables;
import de.life.LiFeBot;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().startsWith(GlobalVariables.prefix))
            return;

        String[] args = event.getMessage().getContentDisplay().substring(1).split(" ");
        MessageChannel channel = event.getChannel();
        
        if (args.length <= 0)
            return;
        if (!LiFeBot.INSTANCE.getCmdMan().perform(args[0], event.getMember(), channel, event.getMessage()))
            channel.sendMessage("Unknown Command! Try '!commands'").queue();

    }
}