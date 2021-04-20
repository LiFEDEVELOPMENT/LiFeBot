package de.life.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.life.classes.EmbedMessageBuilder;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This class schedules tracks for the audio player. It contains the queue of
 * tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
	private final AudioPlayer player;
	private final Queue queue = new Queue();

	/**
	 * @param player The audio player this scheduler uses
	 */
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
	}

	/**
	 * Add the next track to queue or play right away if nothing is in the queue.
	 *
	 * @param track The track to play or add to queue.
	 */
	public boolean queue(AudioTrack track) {
		QueueManager.getInstance().getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()));
		if (!player.startTrack(track, true)) {
			QueueManager.getInstance().getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()))
					.add(track);
			return false;
		}
		return true;
	}

	/**
	 * Start the next track, stopping the current one if it is playing.
	 */
	public void nextTrack() {
		// Start the next track, regardless of if something is already playing or not.
		// In case queue was empty, we are
		// giving null to startTrack, which is a valid argument and will simply stop the
		// player.
		if (!player.startTrack(QueueManager.getInstance()
				.getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode())).next(), false)) {
			PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()).getAudioManager()
					.closeAudioConnection();
			EmbedMessageBuilder.sendMessage("Musik", "Die Queue ist leer\nStarte mit !play einen neuen Song!",
					Color.ORANGE,
					MusicUtil.getMusicChannel(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode())));
		}
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		Guild guild = PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode());
		AudioTrackInfo trackInfo = track.getInfo();
		String url = trackInfo.uri;
		ResultSet set = SQLite
				.onQuery("SELECT * FROM channel WHERE guildid = '" + guild.getIdLong() + "' AND type = 'music'");

		long sekunden = trackInfo.length / 1000;
		long minuten = sekunden / 60;
		long stunden = minuten / 60;
		minuten %= 60;
		sekunden %= 60;

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.GREEN);
		builder.setTitle("Jetzt l�uft:");
		builder.setDescription("[" + trackInfo.title + "](" + url + ")");
		builder.addField("L�nge", trackInfo.isStream ? ":red_circle: STREAM"
				: (stunden > 0 ? "h " : "") + minuten + "m " + sekunden + "s", true);
		builder.addField("Kanal", trackInfo.author, true);

		try {
			if (!set.next())
				return;
			long channelid = set.getLong("channelid");

			if (guild.getTextChannelById(channelid) == null)
				return;

			MessageChannel channel = guild.getTextChannelById(channelid);
			channel.sendMessage(builder.build()).complete().delete().queueAfter(trackInfo.length / 1000,
					TimeUnit.SECONDS);
		} catch (SQLException ex) {
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Only start the next track if the end reason is suitable for it (FINISHED or
		// LOAD_FAILED)
		if (endReason.mayStartNext) {
			nextTrack();
		}
	}

	public boolean isLooped() {
		return this.queue.isLooped();
	}

	public void setLooped(boolean looped) {
		QueueManager.getInstance().getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()))
				.setLooped(looped);
	}

	public void shuffle() {
		QueueManager.getInstance().getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()))
				.shuffle();
	}

	public ArrayList<AudioTrack> getQueue() {
		return QueueManager.getInstance().getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()))
				.getQueue();
	}

	public void clear() {
		if (QueueManager.getInstance()
				.getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode())) != null)
			QueueManager.getInstance().getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()))
					.clear();
	}

	public void jump(int amount) {
		QueueManager.getInstance().getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()))
				.jump(amount);
	}
}