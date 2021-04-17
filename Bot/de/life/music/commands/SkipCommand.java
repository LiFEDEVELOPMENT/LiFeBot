package de.life.music.commands;

import de.life.interfaces.ServerCommand;
import de.life.music.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class SkipCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		if (!m.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
			return;
		}
		if (!m.getGuild().getSelfMember().getVoiceState().getChannel().equals(m.getVoiceState().getChannel())) {
			return;
		}
		PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.nextTrack();
	}
}