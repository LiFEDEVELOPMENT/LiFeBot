package de.life.music.commands;

import java.awt.Color;

import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import de.life.music.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class StopCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		if (m.getGuild().getSelfMember().getVoiceState().getChannel() == null) {
			EmbedMessageBuilder.sendMessage("Musik", "Der Bot ist in keinem Channel", Color.RED, channel, 10);
			return;
		}

		if (m.getGuild().getSelfMember().getVoiceState().getChannel() != m.getVoiceState().getChannel()) {
			EmbedMessageBuilder.sendMessage("Musik", "Du musst im gleichen Voice Channel sein wie der Bot", Color.RED,
					channel, 10); 
			return;
		}
		
		PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.deleteLatestMessage();
		PlayerManager.getInstance().getMusicManager(m.getGuild()).player.stopTrack();
		PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.clear();

		m.getGuild().getAudioManager().closeAudioConnection();
	}

}