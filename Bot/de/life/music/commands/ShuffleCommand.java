package de.life.music.commands;

import java.awt.Color;

import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import de.life.music.MusicUtil;
import de.life.music.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ShuffleCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		if (m.getVoiceState().getChannel() != m.getGuild().getSelfMember().getVoiceState().getChannel()
				|| !m.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
			return;

		PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.shuffle();
		EmbedMessageBuilder.sendMessage("Musik", "Die Queue wurde geshuffled", Color.ORANGE,
				MusicUtil.getMusicChannel(m.getGuild()));
	}

}