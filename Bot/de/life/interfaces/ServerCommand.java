package de.life.interfaces;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public interface ServerCommand {

	public void performCommand(Member m, MessageChannel channel, Message message);
	
}
