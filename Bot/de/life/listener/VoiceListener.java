package de.life.listener;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.life.classes.LogMessanger;
import de.life.classes.PrivateVoiceManager;
import de.life.music.PlayerManager;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceListener extends ListenerAdapter {

	public static VoiceListener INSTANCE;

	public VoiceListener() {
		INSTANCE = this;
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		onJoin(event.getChannelJoined(), event.getEntity());
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		onLeave(event.getChannelLeft(), event.getEntity());
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		onMove(event.getChannelJoined(), event.getChannelLeft(), event.getEntity());
	}

	public void onJoin(VoiceChannel joined, Member m) {
		ResultSet set = SQLite
				.onQuery("SELECT * FROM channel WHERE channelid = '" + joined.getIdLong() + "' AND type = 'hub'");

		try {
			if (set.next()) {
				PrivateVoiceManager.createChannel(m.getGuild(), joined.getParent(), m);

				LogMessanger.sendLog(m.getGuild().getIdLong(), "Temp | " + m.getEffectiveName() + ":",
						m.getAsMention() + " ist dem Channel beigetreten!", Color.CYAN);
				return;
			}
		} catch (SQLException e) {
		}
		LogMessanger.sendLog(m.getGuild().getIdLong(), joined.getName() + ":",
				m.getAsMention() + " ist dem Channel beigetreten!", Color.CYAN);
	}

	public void onLeave(VoiceChannel channel, Member m) {
		ResultSet set = SQLite.onQuery(
				"SELECT * FROM channel WHERE channelid = '" + channel.getIdLong() + "' AND type = 'temporary'");

		try {
			if (set.next()) {
				if (channel.getMembers().size() <= 0) {
					ResultSet innerSet = SQLite.onQuery("SELECT * FROM channel WHERE channelid = '"
							+ channel.getIdLong() + "' AND type = 'temporary'");

					try {
						if (innerSet.next()) {
							SQLite.onUpdate("DELETE FROM channel WHERE channelid = '" + channel.getIdLong()
									+ "' AND guildid = '" + m.getGuild().getIdLong() + "' AND type = 'temporary'");
							channel.delete().queue();

						}
					} catch (SQLException e) {
					}
					LogMessanger.sendLog(m.getGuild().getIdLong(), channel.getName() + ":",
							m.getAsMention() + " hat den Kanal verlassen!", Color.CYAN);
				}
			}
		} catch (SQLException e) {
		}
		LogMessanger.sendLog(m.getGuild().getIdLong(), channel.getName() + ":",
				m.getAsMention() + " hat den Kanal verlassen!", Color.CYAN);

		if (channel.getMembers().size() == 1 && channel.getMembers().get(0).equals(m.getGuild().getSelfMember())) {
			PlayerManager.getInstance().getMusicManager(m.getGuild()).player.stopTrack();
			PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.clear();

			m.getGuild().getAudioManager().closeAudioConnection();
		}
	}

	public void onMove(VoiceChannel joined, VoiceChannel left, Member m) {
		if (left.getMembers().size() <= 0) {

			ResultSet innerSet = SQLite.onQuery(
					"SELECT * FROM channel WHERE channelid = '" + left.getIdLong() + "' AND type = 'temporary'");

			try {
				if (innerSet.next()) {

					SQLite.onUpdate("DELETE FROM channel WHERE channelid = '" + left.getIdLong() + "' AND guildid = '"
							+ m.getGuild().getIdLong() + "' AND type = 'temporary'");
					left.delete().queue();

				}
			} catch (SQLException e) {
			}
			LogMessanger.sendLog(m.getGuild().getIdLong(), left.getName() + ":",
					m.getAsMention() + " hat den Kanal verlassen!", Color.CYAN);
		}

		ResultSet outerSet = SQLite
				.onQuery("SELECT * FROM channel WHERE channelid = '" + joined.getIdLong() + "' AND type = 'hub'");

		try {
			if (outerSet.next()) {

				PrivateVoiceManager.createChannel(m.getGuild(), joined.getParent(), m);

				LogMessanger.sendLog(m.getGuild().getIdLong(), left.getName() + "Temp | " + m.getEffectiveName() + ":",
						m.getAsMention() + " hat den Channel gewechselt!", Color.CYAN);

			}
		} catch (SQLException e) {
		}
		LogMessanger.sendLog(m.getGuild().getIdLong(), joined.getName() + ":",
				m.getAsMention() + " ist dem Channel beigetreten!", Color.CYAN);

		if (left.getMembers().size() == 1 && left.getMembers().get(0).equals(m.getGuild().getSelfMember())) {
			PlayerManager.getInstance().getMusicManager(m.getGuild()).player.stopTrack();
			PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.clear();

			m.getGuild().getAudioManager().closeAudioConnection();
		}

	}
}