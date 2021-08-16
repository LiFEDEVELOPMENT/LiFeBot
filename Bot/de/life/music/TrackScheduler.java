package de.life.music;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

public class TrackScheduler extends AudioEventAdapter {
	private final AudioPlayer player;
	private Long messageID;
	private MessageChannel messageChannel;

	public TrackScheduler(AudioPlayer player) {
		new Queue();
		this.player = player;
	}

	public boolean queue(AudioTrack track) {
		QueueManager.getInstance().getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()));
		if (!player.startTrack(track, true)) {
			QueueManager.getInstance().getQueue(PlayerManager.getInstance().getGuildByPlayerHash(player.hashCode()))
					.add(track);
			return false;
		}
		return true;
	}

	public void nextTrack() {
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
		deleteLatestMessage();

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
		builder.setTitle("Jetzt:");
		builder.setDescription("[" + trackInfo.title + "](" + url + ")");
		builder.addField("Dauer", trackInfo.isStream ? ":red_circle: STREAM"
				: (stunden > 0 ? stunden + "h " : "") + minuten + "m " + sekunden + "s", true);
		builder.addField("Kanal", trackInfo.author, true);

		try {
			if (!set.next())
				return;
			long channelid = set.getLong("channelid");

			if (guild.getTextChannelById(channelid) == null)
				return;

			messageChannel = guild.getTextChannelById(channelid);
			messageID = messageChannel.sendMessageEmbeds(builder.build()).complete().getIdLong();
		} catch (SQLException ex) {
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			nextTrack();
		} else {
			messageID = 0l;
			messageChannel = null;
		}
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

	public void deleteLatestMessage() {
		if (messageChannel != null && messageID != 0l)
			messageChannel.deleteMessageById(messageID).queue();
	}
}