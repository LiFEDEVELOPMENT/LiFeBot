package de.life.music;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Nonnull;

import de.life.sql.SQLite;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class MusicUtil {

	/**
	 * 
	 * @param guild The {@link net.dv8tion.jda.api.entities.Guild Guild} you want to
	 *              retrieve the {@link net.dv8tion.jda.api.entities.MessageChannel
	 *              Music-Channel} of.
	 * @return The {@link net.dv8tion.jda.api.entities.MessageChannel Music-Channel}
	 *         of the given {@link net.dv8tion.jda.api.entities.Guild Guild}.
	 *         Returns null, if there is none.
	 */
	public static MessageChannel getMusicChannel(@Nonnull Guild guild) {
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

	/**
	 * 
	 * @param m       The {@link net.dv8tion.jda.api.entities.Member Member} who
	 *                used the command resulting in the change of the
	 *                {@link net.dv8tion.jda.api.entities.MessageChannel
	 *                Music-Channel}.
	 * @param channel The {@link net.dv8tion.jda.api.entities.MessageChannel
	 *                Channel} the command resulting in the change of the
	 *                {@link net.dv8tion.jda.api.entities.MessageChannel
	 *                Music-Channel} got executed in.
	 */
	public static void updateChannel(@Nonnull Member m, @Nonnull MessageChannel channel) {
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