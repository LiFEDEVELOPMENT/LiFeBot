package de.life.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.life.GlobalVariables;
import de.life.classes.LogMessanger;
import de.life.classes.RPSManager;
import de.life.classes.TTTManager;
import de.life.classes.UnicodeEmotes;
import de.life.commands.CommandsCommand;
import de.life.commands.MemesCommand;
import de.life.commands.ZitateCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {

		final String[] pollUnicode = { UnicodeEmotes.ONE.getUnicode(), UnicodeEmotes.TWO.getUnicode(),
				UnicodeEmotes.THREE.getUnicode(), UnicodeEmotes.FOUR.getUnicode(), UnicodeEmotes.FIVE.getUnicode(),
				UnicodeEmotes.SIX.getUnicode(), UnicodeEmotes.SEVEN.getUnicode(), UnicodeEmotes.EIGHT.getUnicode(),
				UnicodeEmotes.NINE.getUnicode(), UnicodeEmotes.TEN.getUnicode() };

		final String reactionEmote = event.getReactionEmote().toString().toUpperCase().substring(3);

//		System.out.println(reactionEmote);
		if (event.getUser().equals(event.getJDA().getSelfUser()))
			return;

		if (event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().size() > 0) {
			MessageEmbed embed = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getEmbeds()
					.get(0);
			String footer = embed.getFooter().getText();
			Long messageID = event.getMessageIdLong();
			MessageEmbed newEmbed = null;
			Long guildID = event.getGuild().getIdLong();

			if (footer.startsWith("Commands") || footer.startsWith("Memes") || footer.startsWith("Zitate")) {
				event.getChannel().retrieveMessageById(messageID).complete().clearReactions().queue();
				Integer pageID = Integer.parseInt(footer.split(" Page ")[1]);

				if (reactionEmote.equals(UnicodeEmotes.DOUBLE_ARROW_LEFT.getUnicode())) {
					pageID = 1;
					if (footer.startsWith("Commands"))
						newEmbed = CommandsCommand.getCommandEmbed(pageID);
					if (footer.startsWith("Memes"))
						newEmbed = MemesCommand.getMemeEmbed(pageID, guildID);
					if (footer.startsWith("Zitate"))
						newEmbed = ZitateCommand.getZitatEmbed(pageID, guildID);
				}

				if (reactionEmote.equals(UnicodeEmotes.ARROW_LEFT.getUnicode())) {
					pageID--;
					if (pageID < 1)
						pageID = 1;
					if (footer.startsWith("Commands")) {
						if (pageID > CommandsCommand.getCommandPages())
							pageID = CommandsCommand.getCommandPages();
						newEmbed = CommandsCommand.getCommandEmbed(pageID);
					}
					if (footer.startsWith("Memes")) {
						if (pageID > MemesCommand.getMemePages(guildID))
							pageID = MemesCommand.getMemePages(guildID);
						newEmbed = MemesCommand.getMemeEmbed(pageID, guildID);
					}
					if (footer.startsWith("Zitate")) {
						if (pageID > ZitateCommand.getZitatePages(guildID))
							pageID = ZitateCommand.getZitatePages(guildID);
						newEmbed = ZitateCommand.getZitatEmbed(pageID, guildID);
					}
				}

				if (reactionEmote.equals(UnicodeEmotes.ARROW_RIGHT.getUnicode())) {
					pageID++;
					if (pageID < 1)
						pageID = 1;
					if (footer.startsWith("Commands")) {
						if (pageID > CommandsCommand.getCommandPages())
							pageID = CommandsCommand.getCommandPages();
						newEmbed = CommandsCommand.getCommandEmbed(pageID);
					}
					if (footer.startsWith("Memes")) {
						if (pageID > MemesCommand.getMemePages(guildID))
							pageID = MemesCommand.getMemePages(guildID);
						newEmbed = MemesCommand.getMemeEmbed(pageID, guildID);
					}
					if (footer.startsWith("Zitate")) {
						if (pageID > ZitateCommand.getZitatePages(guildID))
							pageID = ZitateCommand.getZitatePages(guildID);
						newEmbed = ZitateCommand.getZitatEmbed(pageID, guildID);
					}
				}

				if (reactionEmote.equals(UnicodeEmotes.DOUBLE_ARROW_RIGHT.getUnicode())) {
					if (footer.startsWith("Commands")) {
						pageID = CommandsCommand.getCommandPages();
						newEmbed = CommandsCommand.getCommandEmbed(pageID);
					}
					if (footer.startsWith("Memes")) {
						pageID = MemesCommand.getMemePages(guildID);
						newEmbed = MemesCommand.getMemeEmbed(pageID, guildID);
					}
					if (footer.startsWith("Zitate")) {
						pageID = ZitateCommand.getZitatePages(guildID);
						newEmbed = ZitateCommand.getZitatEmbed(pageID, guildID);
					}
				}

				event.getChannel().editMessageEmbedsById(messageID, newEmbed).queue();
				if (pageID > 1) {
					event.getChannel().addReactionById(messageID, UnicodeEmotes.DOUBLE_ARROW_LEFT.getUnicode()).queue();
					event.getChannel().addReactionById(messageID, UnicodeEmotes.ARROW_LEFT.getUnicode()).queue();
				}
				if (footer.startsWith("Commands")) {
					if (pageID < CommandsCommand.getCommandPages()) {
						event.getChannel().addReactionById(messageID, UnicodeEmotes.ARROW_RIGHT.getUnicode()).queue();
						event.getChannel().addReactionById(messageID, UnicodeEmotes.DOUBLE_ARROW_RIGHT.getUnicode())
								.queue();
					}
				}
				if (footer.startsWith("Memes")) {
					if (pageID < MemesCommand.getMemePages(guildID)) {
						event.getChannel().addReactionById(messageID, UnicodeEmotes.ARROW_RIGHT.getUnicode()).queue();
						event.getChannel().addReactionById(messageID, UnicodeEmotes.DOUBLE_ARROW_RIGHT.getUnicode())
								.queue();
					}
				}
				if (footer.startsWith("Zitate")) {
					if (pageID < ZitateCommand.getZitatePages(guildID)) {
						event.getChannel().addReactionById(messageID, UnicodeEmotes.ARROW_RIGHT.getUnicode()).queue();
						event.getChannel().addReactionById(messageID, UnicodeEmotes.DOUBLE_ARROW_RIGHT.getUnicode())
								.queue();
					}
				}
			}

			if (footer.startsWith("PollID")) {
				Integer pollID = Integer.parseInt(footer.split(" ")[1]);
				Integer answerCount = 0;
				Integer answer = -1;

				ResultSet set = SQLite.onQuery("SELECT * FROM polls WHERE id = '" + pollID + "'");

				try {
					answerCount = set.next() ? set.getInt("answercount") : -1;
				} catch (SQLException e) {
				}

				event.getReaction().removeReaction(event.getUser()).queue();

				for (int i = 1; i < 11; i++) {
					if (reactionEmote.equals(pollUnicode[i - 1]))
						answer = i;
				}

				if (answer > answerCount)
					return;

				SQLite.onUpdate("DELETE FROM pollvotes WHERE userid = '" + event.getUserId() + "' AND pollid = '"
						+ pollID + "'");
				SQLite.onUpdate("INSERT INTO pollvotes (pollid, userid, vote) VALUES ('" + pollID + "', '"
						+ event.getUserId() + "', '" + answer + "')");

				Integer[] votes = new Integer[11];
				Arrays.fill(votes, 0);

				for (int i = 1; i <= answerCount; i++) {
					set = SQLite.onQuery(
							"SELECT COUNT(vote) FROM pollvotes WHERE pollid = '" + pollID + "' AND vote = '" + i + "'");
					try {
						votes[i] = set.next() ? set.getInt(1) : 0;
					} catch (SQLException e) {
					}
				}

				String[] desc = embed.getDescription().split(":hash:");
				for (int i = 1; i < desc.length; i++) {
					String[] parts = desc[i].split("-");
					parts[parts.length - 1] = " " + votes[i].toString();
					desc[i] = "";
					for (String s : parts) {
						desc[i] = desc[i] + "-" + s;
					}
					desc[i] = desc[i].substring(1, desc[i].length());
				}
				String result = "";
				desc = Arrays.copyOfRange(desc, 1, desc.length);
				for (String s : desc) {
					result += ":hash:" + s + "\n";
				}

				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle(embed.getTitle());
				builder.setDescription(result);
				builder.setFooter(footer);
				event.getChannel().editMessageEmbedsById(event.getMessageIdLong(), builder.build()).queue();

				LogMessanger.sendLog(event.getGuild().getIdLong(), "Poll", event.getUser().getAsMention()
						+ " hat in der Poll mit der ID " + pollID + " für Option " + answer + " gestimmt");

				return;
			}

		}

		if (event.getChannel().retrieveMessageById(event.getMessageId()).complete().getContentDisplay()
				.startsWith(GlobalVariables.prefix + "rps"))

		{
			event.getReaction().removeReaction(event.getUser()).queue();
			boolean win = false;

			if (reactionEmote.equals(UnicodeEmotes.ROCK.getUnicode())
					|| reactionEmote.equals(UnicodeEmotes.PAPER.getUnicode())
					|| reactionEmote.equals(UnicodeEmotes.SCISSORS.getUnicode())) {
				if (new Random().nextInt(3) == 0)
					win = true;
			}

			if (win)
				event.getChannel()
						.sendMessage(event.getUser().getAsMention()
								+ ", du hast diese Runde Schere, Stein, Papier gewonnen!")
						.complete().delete().queueAfter(10, TimeUnit.SECONDS);
			if (!win)
				event.getChannel()
						.sendMessage(event.getUser().getAsMention()
								+ ", du hast diese Runde Schere, Stein, Papier leider verloren!")
						.complete().delete().queueAfter(10, TimeUnit.SECONDS);
			return;
		}

		if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getMentionedUsers()
				.size() > 0) {
			if (event.getChannel().retrieveMessageById(event.getMessageId()).complete().getContentDisplay()
					.startsWith("RPS-Herausforderung")) {

				if (reactionEmote.equals(UnicodeEmotes.X.getUnicode())) {
					if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getMentionedUsers()
							.get(0).equals(event.getUser())
							|| event.getChannel().retrieveMessageById(event.getMessageId()).complete()
									.getMentionedUsers().get(1).equals(event.getUser())) {
						event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
					}
				}

				if (reactionEmote.equals(UnicodeEmotes.WHITE_CHECK.getUnicode())) {
					if (!event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getMentionedUsers()
							.get(0).equals(event.getUser()))
						return;
					List<User> playerList = event.getChannel().retrieveMessageById(event.getMessageId()).complete()
							.getMentionedUsers();

					RPSManager.getInstance().startGame(playerList.get(1), playerList.get(0), event.getChannel());
					event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
				}

			} else if (event.getChannel().retrieveMessageById(event.getMessageId()).complete().getContentDisplay()
					.startsWith("TTT-Herausforderung")) {

				if (reactionEmote.equals(UnicodeEmotes.X.getUnicode())) {
					if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getMentionedUsers()
							.get(0).equals(event.getUser())
							|| event.getChannel().retrieveMessageById(event.getMessageId()).complete()
									.getMentionedUsers().get(1).equals(event.getUser())) {
						event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
					}
				}

				if (reactionEmote.equals(UnicodeEmotes.WHITE_CHECK.getUnicode())) {
					if (!event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getMentionedUsers()
							.get(0).equals(event.getUser()))
						return;
					List<User> playerList = event.getChannel().retrieveMessageById(event.getMessageId()).complete()
							.getMentionedUsers();

					TTTManager.getInstance().startGame(playerList.get(1), playerList.get(0), event.getChannel());
					event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
				}
			}
		}
	}
}