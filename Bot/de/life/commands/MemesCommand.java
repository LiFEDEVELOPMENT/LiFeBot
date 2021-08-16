package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.classes.UnicodeEmotes;
import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MemesCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		if (args.length == 1) {
			randomMeme(m, channel, message);
			return;
		}

		switch (args[1].toLowerCase()) {
		case "add":
			addMeme(m, channel, message);
			break;
		case "delete":
			deleteMeme(m, channel, message);
			break;
		case "list":
			listMemes(m, channel, message);
			break;
		case "remove":
			deleteMeme(m, channel, message);
			break;
		}
	}

	private void addMeme(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		String[] meme = Arrays.copyOfRange(args, 2, args.length);

		if (meme.length < 1) {
			channel.sendMessage("Bitte gib ein Meme an.").complete().delete().queueAfter(5, TimeUnit.SECONDS);

			return;
		}

		String memeString = "";
		for (String s : meme) {
			memeString += s + " ";
		}
		memeString = memeString.substring(0, memeString.length() - 1);
		memeString = memeString.replaceAll("'", "''");

		SQLite.onUpdate(
				"INSERT INTO memes (guildid,meme) VALUES ('" + m.getGuild().getIdLong() + "','" + memeString + "')");
		EmbedMessageBuilder.sendMessage("Meme hinzugefügt", memeString, Color.GRAY, channel, 10);
		LogMessanger.sendLog(m.getGuild().getIdLong(), "Meme",
				m.getEffectiveName() + " hat ein Meme hinzugefügt: " + memeString);
	}

	private void deleteMeme(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (args.length <= 2)
			return;

		int id = 0;

		try {
			id = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Meme löschen", "Bitte gib eine gültige Meme-ID an.", Color.GRAY, channel,
					10);
			return;
		}

		ResultSet set = SQLite.onQuery(
				"SELECT * FROM memes WHERE id = '" + id + "' AND guildid = '" + m.getGuild().getIdLong() + "'");

		try {
			if (!set.next()) {
				EmbedMessageBuilder.sendMessage("Meme löschen", "Bitte gib eine gültige Meme-ID an.", Color.GRAY,
						channel, 10);
				return;
			} else {
				EmbedMessageBuilder.sendMessage("Meme gelöscht", set.getString("meme"), Color.GRAY, channel, 10);
			}
		} catch (SQLException ex) {
		}

		SQLite.onUpdate("DELETE FROM memes WHERE id = '" + id + "' AND guildid = '" + m.getGuild().getIdLong() + "'");
		LogMessanger.sendLog(m.getGuild().getIdLong(), "Hub",
				m.getEffectiveName() + " hat das Hub mit folgender ID gelöscht: " + id);
	}

	private void listMemes(Member m, MessageChannel channel, Message message) {
		Long messageID = channel.sendMessage(getMemeEmbed(1, m.getGuild().getIdLong())).complete().getIdLong();

		if (getMemePages(m.getGuild().getIdLong()) > 1) {
			channel.addReactionById(messageID, UnicodeEmotes.ARROW_RIGHT.getUnicode()).queue();
			channel.addReactionById(messageID, UnicodeEmotes.DOUBLE_ARROW_RIGHT.getUnicode()).queue();
		}
	}

	private void randomMeme(Member m, MessageChannel channel, Message message) {
		String result = "";
		Long guildid = m.getGuild().getIdLong();

		ResultSet set = SQLite.onQuery("SELECT * FROM memes WHERE guildid = " + guildid);
		ArrayList<String> memes = new ArrayList<String>();

		try {
			while (set.next()) {
				memes.add(set.getString("meme"));
			}
		} catch (SQLException ex) {
		}

		result = memes.get(new Random().nextInt(memes.size()));

		if (result == "")
			result = "Auf diesem Server gibt es noch keine Memes. Füge eins mit !addmeme <Meme> hinzu!";

		EmbedMessageBuilder.sendMessage(result, Color.GRAY, channel);
	}

	public static MessageEmbed getMemeEmbed(int id, Long guildid) {
		ArrayList<String> memes = new ArrayList<String>();
		ArrayList<Integer> memeIDs = new ArrayList<Integer>();
		ArrayList<MessageEmbed> embeds = new ArrayList<MessageEmbed>();
		String result = "";
		int i = 1;

		ResultSet set = SQLite.onQuery("SELECT * FROM memes WHERE guildid = '" + guildid + "'");

		try {
			while (set.next()) {
				memes.add(set.getString("meme"));
				memeIDs.add(set.getInt("id"));
			}
		} catch (SQLException e) {
		}

		for (int ii = 0; ii < memes.size(); ii++) {
			if ((result + memes.get(ii) + " **(" + memeIDs.get(ii) + ")**\n\n").length() > 2048) {
				embeds.add(new EmbedBuilder().setTitle("Memes").setDescription(result).setFooter("Memes - Page " + i)
						.setColor(Color.YELLOW).build());
				result = "";
				i++;
			}
			result = result + memes.get(ii) + " **(" + memeIDs.get(ii) + ")**\n\n";
		}
		if (result == "")
			result = "Auf diesem Server gibt es noch keine Memes. Füge eins mit !meme add <Meme> hinzu!";
		embeds.add(new EmbedBuilder().setTitle("Memes").setDescription(result).setFooter("Memes - Page " + i)
				.setColor(Color.YELLOW).build());

		return embeds.get(id - 1);
	}

	public static Integer getMemePages(Long guildid) {
		ArrayList<String> memes = new ArrayList<String>();
		ArrayList<Integer> memeIDs = new ArrayList<Integer>();
		ArrayList<MessageEmbed> embeds = new ArrayList<MessageEmbed>();
		String result = "";
		int i = 1;

		ResultSet set = SQLite.onQuery("SELECT * FROM memes WHERE guildid = '" + guildid + "'");

		try {
			while (set.next()) {
				memes.add(set.getString("meme"));
				memeIDs.add(set.getInt("id"));
			}
		} catch (SQLException e) {
		}

		for (int ii = 0; ii < memes.size(); ii++) {
			if ((result + memes.get(ii) + " **(" + memeIDs.get(ii) + ")**\n\n").length() > 2048) {
				embeds.add(new EmbedBuilder().setTitle("Memes").setDescription(result).setFooter("Memes - Page " + i)
						.setColor(Color.CYAN).build());
				result = "";
				i++;
			}
			result = result + memes.get(ii) + " **(" + memeIDs.get(ii) + ")**\n\n";
		}
		embeds.add(new EmbedBuilder().setTitle("Memes").setDescription(result).setFooter("Memes - Page " + i)
				.setColor(Color.CYAN).build());

		return embeds.size();
	}

}