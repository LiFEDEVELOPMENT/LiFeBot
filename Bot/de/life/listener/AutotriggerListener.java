package de.life.listener;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.life.GlobalVariables;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutotriggerListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		if (event.getJDA().getSelfUser().equals(event.getAuthor()))
			return;

		if (event.getMessage().getContentDisplay().startsWith(GlobalVariables.prefix))
			return;

		ResultSet set = SQLite.onQuery(
				"SELECT * FROM channel WHERE channelid = '" + event.getChannel().getIdLong() + "' AND type = 'zitate'");
		try {
			if (set.next())
				return;
		} catch (SQLException ex) {
		}

		set = SQLite.onQuery(
				"SELECT * FROM channel WHERE channelid = '" + event.getChannel().getIdLong() + "' AND type = 'log'");
		try {
			if (set.next())
				return;
		} catch (SQLException ex) {
		}

		set = SQLite.onQuery("SELECT * FROM autotriggers WHERE guildid = '" + event.getGuild().getIdLong() + "'");

		try {
			while (set.next()) {
				if (event.getMessage().getContentDisplay().toLowerCase().contains(set.getString("trigger"))) {
					event.getChannel().sendMessage(set.getString("text")).queue();
					return;
				}
			}
		} catch (SQLException ex) {
		}
	}
}