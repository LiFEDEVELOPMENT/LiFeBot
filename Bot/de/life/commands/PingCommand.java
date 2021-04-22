package de.life.commands;

import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PingCommand implements ServerCommand{

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		EmbedMessageBuilder.sendMessage("Pong", m.getJDA().getGatewayPing() + "ms", channel);
	}
}