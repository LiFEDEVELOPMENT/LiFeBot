package de.life.music.commands;

import de.life.interfaces.ServerCommand;
import de.life.music.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ResumeCommand implements ServerCommand {
	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		if (m.getVoiceState().getChannel() != m.getGuild().getSelfMember().getVoiceState().getChannel()
				|| !m.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
			return;

		PlayerManager.getInstance().resume(channel, m);
	}
}