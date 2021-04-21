package de.life.music.commands;

import de.life.interfaces.ServerCommand;
import de.life.music.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class LoopCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		if (m.getVoiceState().getChannel() != m.getGuild().getSelfMember().getVoiceState().getChannel()
				|| !m.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
			return;

		if (PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.isLooped()) {
			PlayerManager.getInstance().unloop(channel, m);
		} else {
			PlayerManager.getInstance().loop(channel, m);
		}
	}
}