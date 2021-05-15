package de.life.listener;

import de.life.GlobalVariables;
import de.life.LiFeBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().startsWith(GlobalVariables.prefix))
            return;
        
        if (!event.getMessage().isFromGuild())
        {
              event.getAuthor().openPrivateChannel()
                      .flatMap(privateChannel -> privateChannel.sendMessage(
                              "Commands gehen nur auf Servern, nicht in DMs"
                      )).queue();

              return;
        }

        String[] args = event.getMessage().getContentDisplay().substring(1).split(" ");
        
        if (args.length <= 0)
            return;
        LiFeBot.INSTANCE.getCmdMan().perform(args[0], event.getMember(), event.getChannel(), event.getMessage());
    }
}