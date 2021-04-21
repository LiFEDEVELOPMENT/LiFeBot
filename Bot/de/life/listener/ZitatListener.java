package de.life.listener;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.life.GlobalVariables;
import de.life.classes.EmbedMessageBuilder;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ZitatListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		if (event.getJDA().getSelfUser().equals(event.getAuthor())
				|| event.getMessage().getContentDisplay().startsWith(GlobalVariables.prefix)
				|| event.getAuthor().isBot())
			return;

		ResultSet set = SQLite.onQuery(
				"SELECT * FROM channel WHERE channelid = '" + event.getChannel().getIdLong() + "' AND type = 'zitate'");

		try {
			if (set.next()) {
				String zitat = event.getMessage().getContentDisplay();

				zitat = zitat.replaceAll("'", "''");

				SQLite.onUpdate(
						"INSERT INTO zitate (guildid,zitat,time,author) VALUES ('" + event.getGuild().getIdLong()
								+ "','" + zitat + "','" + event.getMessage().getTimeCreated().toString() + "','"
								+ event.getAuthor().getIdLong() + "')");
				EmbedMessageBuilder.sendMessage("Zitat hinzugefügt", zitat, Color.GRAY, event.getChannel(), 10);
			}
		} catch (SQLException e) {
		}
	}
}