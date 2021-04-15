package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class CommandsCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		Long messageID = channel.sendMessage(getCommandEmbed(1)).complete().getIdLong();

		if (getCommandPages() > 1)
			channel.addReactionById(messageID, "▶").queue();
	}

	public static MessageEmbed getCommandEmbed(int id) {
		ArrayList<String> commands = new ArrayList<String>();
		ArrayList<MessageEmbed> embeds = new ArrayList<MessageEmbed>();
		String result = "";
		Integer i = 1;

		ResultSet set = SQLite.onQuery("SELECT * FROM commands");

		try {
			while (set.next()) {
				commands.add(set.getString("command"));
			}
		} catch (SQLException e) {
		}

		for (String s : commands) {
			if ((result + s + "\n\n").length() > 2048) {
				embeds.add(new EmbedBuilder().setTitle("Commands").setDescription(result)
						.setFooter("Commands - Page " + i).setColor(Color.CYAN).build());
				result = "";
				i++;
			}
			result = result + s + "\n\n";
		}
		embeds.add(new EmbedBuilder().setTitle("Commands").setDescription(result).setFooter("Commands - Page " + i)
				.setColor(Color.CYAN).build());

		return embeds.get(id - 1);
	}

	public static Integer getCommandPages() {
		ArrayList<String> commands = new ArrayList<String>();
		ArrayList<MessageEmbed> embeds = new ArrayList<MessageEmbed>();
		String result = "";
		Integer i = 1;

		ResultSet set = SQLite.onQuery("SELECT * FROM commands");

		try {
			while (set.next()) {
				commands.add(set.getString("command"));
			}
		} catch (SQLException e) {
		}

		for (String s : commands) {
			if ((result + s + "\n\n").length() > 2048) {
				embeds.add(new EmbedBuilder().setTitle("Commands").setDescription(result)
						.setFooter("Commands - Page " + i).build());
				result = "";
				i++;
			}
			result = result + s + "\n\n";
		}
		embeds.add(new EmbedBuilder().setTitle("Commands").setDescription(result).setFooter("Commands - Page " + i)
				.build());

		return embeds.size();
	}

}