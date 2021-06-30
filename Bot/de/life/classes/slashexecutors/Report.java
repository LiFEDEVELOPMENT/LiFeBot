package de.life.classes.slashexecutors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import de.life.classes.CategoryFinder;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class Report {

	public Report(User reporter, User target, Guild guild, MessageChannel channel, String reason) {
		MessageChannel sameGuildChannel = guild.getTextChannelById(CategoryFinder.getReportCategory(guild.getIdLong())
				.createTextChannel(target.getAsTag()).complete().getIdLong());

		ArrayList<String> messageHistoryList = new ArrayList<String>();
		int i = 0;
		String historyString = "";
		String url = "";

		for (Message m : channel.getIterableHistory()) {
			if (i > 49)
				break;
			if (!m.getAuthor().isBot()) {
				String current = m.getAuthor().getAsTag() + " - "
						+ m.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm"));

				if (m.getAttachments().size() > 0) {
					current = current.concat(" - Nachricht mit Anhang - Link(s):");
					for (Attachment attachment : m.getAttachments()) {
						current = current.concat(" " + attachment.getUrl());
					}
				}
				current = current.concat("\n" + m.getContentDisplay() + "\n\n");
				messageHistoryList.add(current);
				i++;
			}
		}
		Collections.reverse(messageHistoryList);

		for (String s : messageHistoryList) {
			historyString = historyString.concat(s);
		}

		try {
			File chatlog = File.createTempFile("chatlog", ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(chatlog));
			bw.write(historyString);
			bw.close();
			url = guild.getJDA().getGuildById(672454909290872852l).getTextChannelById(859446945470087178l)
					.sendFile(chatlog).complete().getAttachments().get(0).getUrl();
		} catch (IOException e) {
		}

		sameGuildChannel
				.sendMessage(reporter.getAsMention() + " hat " + target.getAsMention() + " in "
						+ guild.getTextChannelById(channel.getIdLong()).getAsMention() + " reportet:")
				.setActionRows(
						ActionRow.of(Button.link(url, "Chatlog"),
								Button.danger("warning", "Die Person (" + target.getAsTag() + ") verwarnen!")),
						ActionRow.of(
								Button.danger("pun-reporter",
										"Falscher Report, Reporter (" + reporter.getAsTag() + ") verwarnen!"),
								Button.primary("cancel", "Report ohne Aktion schlieﬂen")))
				.queue();
	}
}