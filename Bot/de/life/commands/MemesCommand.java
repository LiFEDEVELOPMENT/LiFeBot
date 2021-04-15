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
import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

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
		String result = "";
		String foo;
		Long guildid = m.getGuild().getIdLong();

		ResultSet set = SQLite.onQuery("SELECT * FROM memes WHERE guildid = " + guildid);
		ArrayList<String> memes = new ArrayList<String>();
		ArrayList<Integer> memeIDs = new ArrayList<Integer>();

		try {
			while (set.next()) {
				memes.add(set.getString("meme"));
				memeIDs.add(set.getInt("id"));
			}
		} catch (SQLException ex) {
		}

		int i = 0;
		for (String meme : memes) {
			if ((foo = result + meme + " **(" + memeIDs.get(i) + ")**\n\n").length() > 2048) {
				EmbedMessageBuilder.sendMessage(result, Color.GRAY, channel);
				result = "";
			}
			result = result + meme + " **(" + memeIDs.get(i) + ")**\n\n";
			i++;
		}

		if (result == "")
			result = "Auf diesem Server gibt es noch keine Memes. Füge eins mit !meme add <Meme> hinzu!";

		EmbedMessageBuilder.sendMessage(result, Color.GRAY, channel);
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

}