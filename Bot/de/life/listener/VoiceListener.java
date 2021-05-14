package de.life.listener;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

import de.life.classes.LogMessanger;
import de.life.music.PlayerManager;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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
				createPrivateChannel(joined, m);

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

				createPrivateChannel(joined, m);

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

	public void createPrivateChannel(VoiceChannel channel, Member m) {
		Role everyone = m.getGuild().getRoles().get((m.getGuild().getRoles().size()) - 1);

		Category cat = channel.getParent();
		VoiceChannel vc = cat.createVoiceChannel("Temp | " + m.getEffectiveName()).complete();
		vc.getManager().setUserLimit(5).queue();
		vc.getGuild().moveVoiceMember(m, vc).queue();

		vc.getManager()
				.putPermissionOverride(everyone, null,
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.VIEW_CHANNEL, Permission.MANAGE_WEBHOOKS,
								Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
								Permission.VOICE_MOVE_OTHERS, Permission.VOICE_USE_VAD, Permission.VOICE_MUTE_OTHERS,
								Permission.VOICE_DEAF_OTHERS, Permission.PRIORITY_SPEAKER))
				.putPermissionOverride(m,
						EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
								Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
								Permission.VOICE_USE_VAD),
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS,
								Permission.VOICE_DEAF_OTHERS))
				.queue();

		SQLite.onUpdate("INSERT INTO channel (guildid,channelid,type) VALUES ('" + m.getGuild().getIdLong() + "','"
				+ vc.getIdLong() + "','temporary')");
	}
}