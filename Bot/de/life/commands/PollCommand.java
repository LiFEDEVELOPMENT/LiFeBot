package de.life.commands;

import java.util.concurrent.TimeUnit;

import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PollCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		channel.sendMessage(
				"Das funktioniert nicht mehr. Probier doch mal /poll, dort findest du auch noch weitere Commands des LiFeBots!")
				.complete().delete().queueAfter(10, TimeUnit.SECONDS);
	}
}