package de.life.commands;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import de.life.LiFeBot;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetupCommand implements ServerCommand {

	final EventWaiter waiter;

	public SetupCommand() {
		this.waiter = LiFeBot.INSTANCE.getWaiter();
	}

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {

		message.reply("Möchtest du ein Channelhub für diesen Server festlegen?").queue();

		waiter.waitForEvent(MessageReceivedEvent.class,
				e -> e.getAuthor().equals(m.getUser()) && e.getChannel().equals(channel)
						&& !e.getMessage().equals(message),
				e -> e.getMessage().reply("Ok").queue(), 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

}
