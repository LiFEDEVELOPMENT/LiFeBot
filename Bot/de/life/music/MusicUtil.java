package de.life.music;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.life.sql.SQLite;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class MusicUtil {

	public static MessageChannel getMusicChannel(Guild guild) {
		ResultSet set = SQLite
				.onQuery("SELECT * FROM channel WHERE guildid = '" + guild.getIdLong() + "' and type = 'music'");

		try {
			if (set.next()) {
				long channelid = set.getLong("channelid");

				MessageChannel channel;
				if ((channel = guild.getTextChannelById(channelid)) != null)
					return channel;
			}
		} catch (SQLException ex) {
		}
		return null;
	}

	public static void updateChannel(Member m, MessageChannel channel) {

		ResultSet set = SQLite
				.onQuery("SELECT * FROM channel WHERE guildid = '" + m.getGuild().getIdLong() + "' AND type = 'music'");

		try {
			if (set.next()) {
				SQLite.onUpdate("UPDATE channel SET channelid = '" + channel.getIdLong() + "' WHERE guildid = '"
						+ m.getGuild().getIdLong() + "' AND type = 'music'");
			} else {
				SQLite.onUpdate("INSERT INTO channel(guildid, channelid, type) VALUES('" + m.getGuild().getIdLong()
						+ "','" + channel.getIdLong() + "','music')");
			}
		} catch (SQLException ex) {

		}

	}
}