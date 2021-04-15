package de.life.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.life.classes.EmbedMessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

	private static PlayerManager INSTANCE;
	private final Map<Long, GuildMusicManager> musicManagers;
	public final AudioPlayerManager audioPlayerManager;

	public PlayerManager() {
		this.musicManagers = new HashMap<>();
		this.audioPlayerManager = new DefaultAudioPlayerManager();

		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
			final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);

			guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

			return guildMusicManager;
		});
	}

	public void pause(MessageChannel channel, Member m) {
		if (getMusicManager(m.getGuild()).player.isPaused())
			return;

		EmbedMessageBuilder.sendMessage("Music", "Pausiert", Color.ORANGE, channel);
		getMusicManager(m.getGuild()).player.setPaused(true);
	}

	public void resume(MessageChannel channel, Member m) {
		if (!getMusicManager(m.getGuild()).player.isPaused())
			return;

		EmbedMessageBuilder.sendMessage("Music", "Fortgesetzt", Color.ORANGE, channel);
		getMusicManager(m.getGuild()).player.setPaused(false);
	}

	public void loop(MessageChannel channel, Member m) {
		if (getMusicManager(m.getGuild()).scheduler.isLooped())
			return;

		EmbedMessageBuilder.sendMessage("Music", "Dauerschleife aktiviert", Color.ORANGE, channel);
		getMusicManager(m.getGuild()).scheduler.setLooped(true);
	}

	public void unloop(MessageChannel channel, Member m) {
		if (!getMusicManager(m.getGuild()).scheduler.isLooped())
			return;

		EmbedMessageBuilder.sendMessage("Music", "Dauerschleife deaktiviert", Color.ORANGE, channel);
		getMusicManager(m.getGuild()).scheduler.setLooped(false);
	}

	public void loadAndPlay(MessageChannel channel, String trackUrl, Member m) {
		final GuildMusicManager musicManager = this.getMusicManager(m.getGuild());

		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				if (!musicManager.scheduler.queue(track)) {
					EmbedMessageBuilder.sendMessage(
							"Ein Track wurde der Queue hinzugefügt:\n[" + track.getInfo().author + " - "
									+ track.getInfo().title + "](" + track.getInfo().uri + ")",
							Color.decode("#8c14fc"), MusicUtil.getMusicChannel(m.getGuild()));
				}
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {

				if (trackUrl.startsWith("ytsearch:")) {
					if (!musicManager.scheduler.queue(playlist.getTracks().get(0))) {
						EmbedMessageBuilder.sendMessage("Ein Track wurde der Queue hinzugefügt:\n["
								+ playlist.getTracks().get(0).getInfo().author + " - "
								+ playlist.getTracks().get(0).getInfo().title + "]("
								+ playlist.getTracks().get(0).getInfo().uri + ")\n" + "[" + m.getAsMention() + "]",
								Color.decode("#8c14fc"), MusicUtil.getMusicChannel(m.getGuild()));
					}
					return;
				}

				int added = 0;

				for (AudioTrack track : playlist.getTracks()) {
					musicManager.scheduler.queue(track);
					added++;
				}

				EmbedMessageBuilder.sendMessage(added + " Tracks wurden der Queue hinzugefügt", Color.decode("#8c14fc"),
						MusicUtil.getMusicChannel(m.getGuild()));
			}

			@Override
			public void noMatches() {
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				System.out.println(exception.getMessage());
			}
		});

	}

	public Guild getGuildByPlayerHash(int hash) {
		for (GuildMusicManager manager : this.musicManagers.values()) {
			if (manager.getPlayer().hashCode() == hash)
				return manager.getGuild();
		}
		return null;
	}

	public static PlayerManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PlayerManager();
		}

		return INSTANCE;
	}
}