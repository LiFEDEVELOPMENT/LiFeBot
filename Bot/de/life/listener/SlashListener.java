package de.life.listener;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.life.classes.LogMessanger;
import de.life.classes.UnicodeEmotes;
import de.life.classes.slashexecutors.Report;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashListener extends ListenerAdapter {

	private final String[] emoteNames = { ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:",
			":eight:", ":nine:", ":keycap_ten:" };
	private final String[] unicodeNames = { UnicodeEmotes.ONE.getUnicode(), UnicodeEmotes.TWO.getUnicode(),
			UnicodeEmotes.THREE.getUnicode(), UnicodeEmotes.FOUR.getUnicode(), UnicodeEmotes.FIVE.getUnicode(),
			UnicodeEmotes.SIX.getUnicode(), UnicodeEmotes.SEVEN.getUnicode(), UnicodeEmotes.EIGHT.getUnicode(),
			UnicodeEmotes.NINE.getUnicode(), UnicodeEmotes.TEN.getUnicode() };

	public void onSlashCommand(SlashCommandEvent event) {

		if (event.getName().equals("report")) {
			new Report(event.getUser(), event.getOption("user").getAsUser(), event.getGuild(), event.getChannel(),
					(event.getOption("reason") != null ? event.getOption("reason").getAsString() : null));
			event.deferReply(true).complete()
					.sendMessage(event.getOption("user").getAsUser().getAsMention() + " wurde erfolgreich reportet!")
					.queue();
		}

		if (event.getName().equals("poll")) {
			if (event.getSubcommandName().equals("start")) {
				pollStart(event);
			} else if (event.getSubcommandName().equals("stop")) {
				pollStop(event);
			}
		}

	}

	private void pollStart(SlashCommandEvent event) {
		String frage = event.getOption("frage").getAsString();
		String description = "";
		int maxAntworten = 10;

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(frage);
		builder.setFooter("PollID: " + generatePollID().toString() + " - Creator: " + event.getUser().getAsTag());

		for (int i = 1; i <= 10; i++) {
			String aktuelleAntwort = "antwort" + i;
			if (event.getOption(aktuelleAntwort) == null) {
				maxAntworten = i-1;
				break;
			}
			description = description
					+ (":hash:" + emoteNames[i-1] + " " + event.getOption(aktuelleAntwort).getAsString() + " - 0\n");
		}
		builder.setDescription(description);
		Long messageID = event.getChannel().sendMessageEmbeds(builder.build()).complete().getIdLong();
		for (int i = 0; i < maxAntworten; i++) {
			event.getChannel().retrieveMessageById(messageID).complete().addReaction(unicodeNames[i]).queue();
		}
		
		event.deferReply(true).complete().sendMessage("Umfrage erstellt!").queue();

		SQLite.onUpdate("INSERT INTO polls (guildid, channelid, messageid, userid, answercount) VALUES ('"
				+ event.getGuild().getIdLong() + "', '" + event.getChannel().getIdLong() + "', '" + messageID + "', '"
				+ event.getUser().getIdLong() + "', '" + maxAntworten + "')");
		LogMessanger.sendLog(event.getGuild().getIdLong(), "Poll",
				event.getUser().getAsMention() + " hat eine Poll in " + event.getChannel().getName() + " erstellt");
	}

	private void pollStop(SlashCommandEvent event) {

		int pollID = (int) event.getOption("pollid").getAsLong();
		long guildID = 0l;
		long channelID = 0l;
		long messageID = 0l;
		long userID = 0l;
		int answerCount = 0;
		ResultSet set = SQLite.onQuery("SELECT * FROM polls WHERE id = '" + pollID + "'");

		try {
			if (set.next()) {
				guildID = set.getLong("guildid");
				channelID = set.getLong("channelid");
				messageID = set.getLong("messageid");
				userID = set.getLong("userid");
				answerCount = set.getInt("answercount");
			} else {
				event.deferReply(true).complete().sendMessage("Es gibt keine Poll mit dieser ID").queue();
				return;
			}
		} catch (SQLException ex) {
		}

		if (event.getGuild().getIdLong() != guildID || event.getUser().getIdLong() != userID) {
			event.deferReply(true).complete().sendMessage("Du darfst nicht auf diese Umfrage zugreifen").queue();
		}

		if (event.getChannel().getIdLong() != channelID) {
			event.deferReply(true).complete().sendMessage("Diese Umfrage ist nicht in diesem Channel").queue();
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
		for (int i = 0; i < votes.length; i++) {
			if (votes[i] > votes[largest])
				largest = i;
		}
		for (int i = 0; i < votes.length; i++) {
			if (votes[i].equals(votes[largest]) && i != largest) {
				largest = -1;
				break;
			}
		}

		MessageEmbed oldEmbed = event.getChannel().retrieveMessageById(messageID).complete().getEmbeds().get(0);

		EmbedBuilder builder = new EmbedBuilder().setTitle(oldEmbed.getTitle() + " - Ergebnisse");
		builder.setDescription(oldEmbed.getDescription());
		builder.setFooter("Ergebnis: " + ((largest == -1) ? "DRAW!" : ("Option " + largest + " wins!")) + " Creator: "
				+ event.getUser().getAsTag());
		event.getChannel().deleteMessageById(messageID).queue();
		event.getChannel().sendMessageEmbeds(builder.build()).queue();
		event.deferReply(true).complete().sendMessage("Success").complete().delete().queue();

		SQLite.onUpdate("DELETE FROM polls WHERE id = '" + pollID + "'");
		LogMessanger.sendLog(event.getGuild().getIdLong(), "Poll", event.getUser().getAsMention() + " hat die Poll in "
				+ event.getChannel().getName() + " mit der ID " + pollID + " gelöscht");
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
