package de.life.commands;

import java.util.Random;

import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class CoinflipCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		if (new Random().nextBoolean()) {
			EmbedMessageBuilder.sendMessage(m.getAsMention() + " Zahl", channel, 10);
			return;
		}
		EmbedMessageBuilder.sendMessage(m.getAsMention() + " Kopf", channel, 10);
	}
}