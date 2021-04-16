package de.life.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.life.GlobalVariables;
import de.life.classes.RPSManager;
import de.life.commands.CommandsCommand;
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
		if (event.getUser().equals(event.getJDA().getSelfUser()))
			return;

		if (event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().size() > 0) {
			event.getReaction().removeReaction(event.getUser()).queue();

			MessageEmbed embed = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getEmbeds()
					.get(0);
			String footer = embed.getFooter().getText();

			if (footer.startsWith("Commands")) {
				Integer pageID = Integer.parseInt(footer.split(" Page ")[1]);
				if (pageID == CommandsCommand.getCommandPages())
					pageID = (CommandsCommand.getCommandPages() - 1);

				switch (event.getReactionEmote().getName()) {
				case "◀":
					if (pageID <= 1)
						pageID = 2;
					if (pageID == 2)
						event.getReaction().removeReaction().queue();

					Long messageID = event.getChannel()
							.editMessageById(event.getMessageId(), CommandsCommand.getCommandEmbed(pageID - 1))
							.complete().getIdLong();

					if (pageID - 1 > 1)
						event.getChannel().addReactionById(messageID, "◀").queue();
					event.getChannel().addReactionById(messageID, "▶").queue();

					break;
				case "▶":

					if (pageID == 1 || pageID == (CommandsCommand.getCommandPages() - 1))
						event.getReaction().removeReaction().queue();

					messageID = event.getChannel()
							.editMessageById(event.getMessageId(), CommandsCommand.getCommandEmbed(pageID + 1))
							.complete().getIdLong();

					event.getChannel().addReactionById(messageID, "◀").queue();
					if (pageID + 1 < CommandsCommand.getCommandPages())
						event.getChannel().addReactionById(messageID, "▶").queue();

					break;
				}
				return;
			}

			if (footer.startsWith("Zitate")) {
				Integer pageID = Integer.parseInt(footer.split(" Page ")[1]);
				if (pageID == ZitateCommand.getZitatePages(event.getGuild().getIdLong()))
					pageID = (ZitateCommand.getZitatePages(event.getGuild().getIdLong()) - 1);

				switch (event.getReactionEmote().getName()) {
				case "◀":
					if (pageID <= 1)
						pageID = 2;
					if (pageID == 2)
						event.getReaction().removeReaction().queue();

					Long messageID = event.getChannel()
							.editMessageById(event.getMessageId(),
									ZitateCommand.getZitatEmbed(pageID - 1, event.getGuild().getIdLong()))
							.complete().getIdLong();

					if (pageID - 1 > 1)
						event.getChannel().addReactionById(messageID, "◀").queue();
					event.getChannel().addReactionById(messageID, "▶").queue();

					break;
				case "▶":

					if (pageID == 1 || pageID == (ZitateCommand.getZitatePages(event.getGuild().getIdLong()) - 1))
						event.getReaction().removeReaction().queue();

					messageID = event.getChannel()
							.editMessageById(event.getMessageId(),
									ZitateCommand.getZitatEmbed(pageID + 1, event.getGuild().getIdLong()))
							.complete().getIdLong();

					event.getChannel().addReactionById(messageID, "◀").queue();
					if (pageID + 1 < ZitateCommand.getZitatePages(event.getGuild().getIdLong()))
						event.getChannel().addReactionById(messageID, "▶").queue();

					break;
				}
				return;
			}

			if (footer.startsWith("PollID")) {
				Integer pollID = Integer.parseInt(footer.split(" ")[1]);
				Integer answerCount = 0;
				Integer answer = 0;

				ResultSet set = SQLite.onQuery("SELECT * FROM polls WHERE id = '" + pollID + "'");

				try {
					answerCount = set.next() ? set.getInt("answercount") : -1;
				} catch (SQLException e) {
				}

				switch (event.getReactionEmote().getName()) {
				case "1�?⃣":
					answer = 1;
					break;
				case "2�?⃣":
					answer = 2;
					break;
				case "3�?⃣":
					answer = 3;
					break;
				case "4�?⃣":
					answer = 4;
					break;
				case "5�?⃣":
					answer = 5;
					break;
				case "6�?⃣":
					answer = 6;
					break;
				case "7�?⃣":
					answer = 7;
					break;
				case "8�?⃣":
					answer = 8;
					break;
				case "9�?⃣":
					answer = 9;
					break;
				case "🔟":
					answer = 10;
					break;
				default:
					answer = -1;
					break;
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
				event.getChannel().editMessageById(event.getMessageIdLong(), builder.build()).queue();
				return;
			}

		}

		if (event.getChannel().retrieveMessageById(event.getMessageId()).complete().getContentDisplay()
				.startsWith(GlobalVariables.prefix + "rps")) {
			event.getReaction().removeReaction(event.getUser()).queue();
			boolean win = false;
			switch (event.getReactionEmote().getName()) {
			case "✌":
				if (new Random().nextInt(3) == 0)
					win = true;

				break;
			case "✊":
				if (new Random().nextInt(3) == 0)
					win = true;
				break;
			case "🖐":
				if (new Random().nextInt(3) == 0)
					win = true;
				break;
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
			switch (event.getReactionEmote().getName()) {
			case "❌":
				if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getMentionedUsers()
						.get(0).equals(event.getUser())
						|| event.getChannel().retrieveMessageById(event.getMessageId()).complete().getMentionedUsers()
								.get(1).equals(event.getUser())) {
					event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
				}
				break;
			case "✅":
				if (!event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getMentionedUsers()
						.get(0).equals(event.getUser()))
					return;
				List<User> playerList = event.getChannel().retrieveMessageById(event.getMessageId()).complete()
						.getMentionedUsers();

				RPSManager.getInstance().startGame(playerList.get(1), playerList.get(0), event.getChannel());
				event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
			}
		}
	}
}