package de.life.music;

import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ResumeCommand implements ServerCommand {
	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		if (m.getVoiceState().getChannel() != m.getGuild().getSelfMember().getVoiceState().getChannel()
				|| !m.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
			return;

		PlayerManager.getInstance().resume(channel, m);
	}
}