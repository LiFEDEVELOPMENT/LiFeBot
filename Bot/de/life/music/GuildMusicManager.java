package de.life.music;

import javax.annotation.Nonnull;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager {
	public final AudioPlayer player;
	public final TrackScheduler scheduler;
	public final Guild guild;

	public GuildMusicManager(@Nonnull AudioPlayerManager manager, @Nonnull Guild pGuild) {
		player = manager.createPlayer();
		scheduler = new TrackScheduler(player);
		player.addListener(scheduler);
		guild = pGuild;
	}

	@Nonnull
	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player);
	}

	@Nonnull
	public Guild getGuild() {
		return guild;
	}

	@Nonnull
	public AudioPlayer getPlayer() {
		return player;
	}
}