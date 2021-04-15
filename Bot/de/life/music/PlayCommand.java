package de.life.music;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PlayCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (!joinChannel(m)) {
			return;
		}

		if (args.length < 2) {
			return;
		}

		args = Arrays.copyOfRange(args, 1, args.length);
		String link = String.join(" ", args);

		if (!isUrl(link)) {
			link = "ytsearch:" + link;
		}
		PlayerManager.getInstance().loadAndPlay(channel, link, m);
		MusicUtil.updateChannel(m, channel);
	}

	private boolean joinChannel(Member m) {

		if (!m.getVoiceState().inVoiceChannel()) {
			return false;
		}

		if (m.getGuild().getSelfMember().getVoiceState().inVoiceChannel()
				&& m.getGuild().getSelfMember().getVoiceState().getChannel() != m.getVoiceState().getChannel()) {
			return false;
		}

		m.getGuild().getAudioManager().openAudioConnection(m.getVoiceState().getChannel());
		return true;
	}

	private boolean isUrl(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

}