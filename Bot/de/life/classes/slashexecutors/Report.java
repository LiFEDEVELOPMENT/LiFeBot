package de.life.classes.slashexecutors;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import de.life.classes.CategoryFinder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

public class Report {

	public Report(User reporter, User target, Guild guild, MessageChannel channel, String reason) {
		Long channelID = CategoryFinder.getReportCategory(guild.getIdLong()).createTextChannel(target.getAsTag())
				.complete().getIdLong();
		MessageChannel reportChannel = guild.getTextChannelById(channelID);
		List<Message> messageHistory = channel.getIterableHistory().complete();
		EmbedBuilder builder = new EmbedBuilder().setTitle(target.getAsTag() + " - #" + channel.getName());

		Integer i = 0;
		Integer ii = 0;
		for (Message curr : messageHistory) {
			if (curr.getAuthor().equals(target)) {
				i++;
				builder.addField(curr.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
						(curr.getContentDisplay().length() > 1014
								? curr.getContentDisplay().substring(0, 1014) + " **[...]**"
								: curr.getContentDisplay()),
						true);
				builder.addBlankField(true);
				builder.addBlankField(true);
			}
			ii++;
			if (ii > 200)
				break;
		}

		builder.setColor(Color.RED);
		if (i == 0)
			builder.setDescription(target.getAsTag()
					+ " entweder in den letzten zwei Wochen oder den letzten 200 Nachrichten dieses Channels keine Nachricht geschickt");
		builder.setFooter("Hier findest du die letzten Nachrichten von " + target.getName()
				+ ". Bitte beachte, dass dies keine zum Zeitpunkt des Reports gelöschten Nachrichten beinhaltet.");

		reportChannel
				.sendMessage(reporter.getAsMention() + " hat " + target.getAsMention() + " in "
						+ guild.getTextChannelById(channel.getIdLong()).getAsMention() + " reportet.\n")
				.complete().getIdLong();
		reportChannel.sendMessageEmbeds(builder.build()).queue();
		reportChannel.sendMessage("Deine Aktionen:")
				.setActionRows(ActionRow.of(Button.success("cancel", "Report stoppen"),
						Button.danger("warning", "Die Person verwarnen!"),
						Button.danger("pun-reporter", "Falscher Report, Reporter verwarnen!"),
						Button.link("https://www.youtube.com/watch?v=dQw4w9WgXcQ", "Chatlog")))
				.queue();
		reportChannel.sendMessage(channel.getHistoryBefore(channel.getLatestMessageId(), 50).complete().toString())
				.queue();
	}
}
