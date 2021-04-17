package de.life.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PollCommand implements ServerCommand {

	private final String[] emoteNames = { ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:",
			":eight:", ":nine:", ":keycap_ten:" };
	private final String[] unicodeNames = { "U+0031", "U+0032", "U+0033", "U+0034", "U+0035", "U+0036", "U+0037", "U+0038", "U+0039", "U+1F51F" };
	private final String splitter = "\\";

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		switch (args[1].toLowerCase()) {
		case "start":
			startPoll(m, channel, message);
			break;
		case "close":
			closePoll(m, channel, message);
			break;
		case "end":
			closePoll(m, channel, message);
			break;
		case "stop":
			closePoll(m, channel, message);
			break;
		default:
			channel.sendMessage(
					"Ohoh, da scheint etwas mit deinem Command nicht zu stimmen. Schau doch mal unter !commands nach, wie man diesen Command richtig benutzt!")
					.complete().delete().queueAfter(5, TimeUnit.SECONDS);
			break;
		}
	}

	private void startPoll(Member m, MessageChannel channel, Message message) {
		String poll = "";
		String description = "";
		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 3) {
			channel.sendMessage(
					"Ohoh, da scheint etwas mit deiner Nachricht nicht zu stimmen. Schau doch mal unter !commands nach, wie man eine Poll richtig startet!")
					.complete().delete().queueAfter(5, TimeUnit.SECONDS);
			return;
		}

		args = Arrays.copyOfRange(args, 2, args.length);
		for (String s : args) {
			poll = poll + " " + s;
		}

		String frage = poll.split(Pattern.quote(splitter))[0];
		String[] antworten = poll.split(Pattern.quote(splitter));
		antworten = Arrays.copyOfRange(antworten, 1, antworten.length);

		if (antworten.length < 2) {
			channel.sendMessage("Du musst mindestens zwei Antwortmöglichkeiten angeben LUL").complete().delete()
					.queueAfter(5, TimeUnit.SECONDS);
			return;
		}

		if (frage.length() > 243) {
			channel.sendMessage("Die Frage kann maximal 242 Zeichen lang sein!").complete().delete().queueAfter(5,
					TimeUnit.SECONDS);
			return;
		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(frage);
		builder.setFooter("PollID: " + generatePollID().toString() + " - Creator: " + m.getEffectiveName());

		for (int i = 0; i < antworten.length; i++) {
			description = description + (":hash:" + emoteNames[i] + " " + antworten[i] + " - 0\n");
		}
		description = description.substring(0, description.length() - 1);
		builder.setDescription(description);

		Long messageID = channel.sendMessage(builder.build()).complete().getIdLong();
		for (int i = 0; i < antworten.length; i++) {
			channel.retrieveMessageById(messageID).complete().addReaction(unicodeNames[i]).queue();
		}

		SQLite.onUpdate("INSERT INTO polls (guildid, channelid, messageid, userid, answercount) VALUES ('"
				+ message.getGuild().getIdLong() + "', '" + channel.getIdLong() + "', '" + messageID + "', '"
				+ m.getUser().getIdLong() + "', '" + antworten.length + "')");
	}

	private void closePoll(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		Integer pollID;
		Long guildID = 0l;
		Long channelID = 0l;
		Long messageID = 0l;
		Long userID = 0l;
		Integer answerCount = 0;

		try {
			pollID = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			channel.sendMessage(
					"Ohoh, da scheint etwas mit deiner Nachricht nicht zu stimmen. Schau doch mal unter !commands nach, wie man eine Poll richtig schließt!")
					.complete().delete().queueAfter(5, TimeUnit.SECONDS);
			return;
		}

		ResultSet set = SQLite.onQuery("SELECT * FROM polls WHERE id = '" + pollID + "'");

		try {
			if (set.next()) {
				guildID = set.getLong("guildid");
				channelID = set.getLong("channelid");
				messageID = set.getLong("messageid");
				userID = set.getLong("userid");
				answerCount = set.getInt("answercount");
			} else {
				channel.sendMessage("Es gibt keine Poll mit dieser ID").complete().delete().queueAfter(5,
						TimeUnit.SECONDS);
				return;
			}
		} catch (SQLException ex) {
		}

		if (guildID != m.getGuild().getIdLong() || userID != m.getUser().getIdLong()) {
			channel.sendMessage("Du darfst nicht auf diese Poll zugreifen!").complete().delete().queueAfter(5,
					TimeUnit.SECONDS);
			return;
		}

		if (channelID != channel.getIdLong()) {
			channel.sendMessage("Diese Poll ist nicht in diesem Channel!").complete().delete().queueAfter(5,
					TimeUnit.SECONDS);
			return;
		}

		Integer[] votes = new Integer[11];
		for (int i = 0; i < 11; i++) {
			votes[i] = 0;
		}

		for (int i = 1; i <= answerCount; i++) {
			set = SQLite.onQuery(
					"SELECT COUNT(vote) FROM pollvotes WHERE pollid = '" + pollID + "' AND vote = '" + i + "'");
			try {
				votes[i] = set.next() ? set.getInt(1) : 0;
			} catch (SQLException e) {
			}
		}

		Integer largest = 0;
		for (int i = 1; i < votes.length; i++) {
			for (int ii = 0; ii < votes.length; ii++) {
				if (votes[i] > votes[ii])
					largest = i;
			}
		}
		for (int i = 0; i < votes.length; i++) {
			if (votes[i].equals(votes[largest]) && i != largest) {
				largest = -1;
				break;
			}
		}

		MessageEmbed oldEmbed = channel.retrieveMessageById(messageID).complete().getEmbeds().get(0);

		EmbedBuilder builder = new EmbedBuilder().setTitle(oldEmbed.getTitle() + " - Ergebnisse");
		builder.setDescription(oldEmbed.getDescription());
		builder.setFooter("Ergebnis: " + ((largest == -1) ? "DRAW!" : ("Option " + largest + " wins!")) + " Creator: "
				+ m.getEffectiveName());
		channel.deleteMessageById(messageID).queue();
		channel.sendMessage(builder.build()).queue();

		SQLite.onUpdate("DELETE FROM polls WHERE id = '" + pollID + "'");
	}

	private Integer generatePollID() {
		ResultSet set = SQLite.onQuery("SELECT * FROM sqlite_sequence WHERE name = 'polls'");

		try {
			return set.next() ? (set.getInt("seq") + 1) : 1;
		} catch (SQLException e) {
		}

		return -1;
	}
}