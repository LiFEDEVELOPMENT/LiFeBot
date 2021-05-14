package de.life.commands;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import de.life.classes.BotError;
import de.life.classes.CategoryFinder;
import de.life.classes.EmbedMessageBuilder;
import de.life.classes.UnicodeEmotes;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class ReportCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		String reason = "";

		if (args.length > 2)
			reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

		if (message.getMentionedMembers().size() < 1) {
			EmbedMessageBuilder.sendMessage("Error", BotError.SYNTAX.getError(), Color.RED, channel, 10);
			return;
		}

		List<Message> messageHistory = channel.getIterableHistory().complete();
		User reported = message.getMentionedMembers().get(0).getUser();
		Long channelID = null;

		EmbedBuilder builder = new EmbedBuilder().setTitle(reported.getAsTag() + " - #" + channel.getName());

		channelID = CategoryFinder.getReportCategory(m.getGuild().getIdLong()).createTextChannel(reported.getAsTag())
				.complete().getIdLong();
		MessageChannel reportChannel = m.getGuild().getTextChannelById(channelID);

		Integer i = 0;
		Integer ii = 0;
		for (Message curr : messageHistory) {
			if (curr.getAuthor().equals(reported)) {
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
			builder.setDescription(reported.getAsTag()
					+ " entweder in den letzten zwei Wochen oder den letzten 200 Nachrichten dieses Channels keine Nachricht geschickt");

		Long reportMessageID = reportChannel.sendMessage(m.getUser().getAsMention() + " hat " + reported.getAsMention()
				+ " in " + m.getGuild().getTextChannelById(channel.getIdLong()).getAsMention()
				+ " reportet.\nReagiere mit :exclamation:, um " + reported.getAsMention()
				+ " zu verwarnen, oder mit :free:, um den Report ohne eine Aktion zu schließen.\nWenn du den Reporter verwarnen möchtest, reagiere mit der Uno Reverse Card.\nIn der folgenden Nachricht findest du die letzten Nachrichten von "
				+ reported.getName() + ". Bitte beachte, dass dies keine gelöschten Nachrichten beinhaltet.").complete()
				.getIdLong();
		reportChannel.sendMessage(builder.build()).queue();

		reportChannel.addReactionById(reportMessageID, UnicodeEmotes.X.getUnicode()).queue();
		reportChannel.addReactionById(reportMessageID, UnicodeEmotes.FREE.getUnicode()).queue();
		reportChannel.addReactionById(reportMessageID, UnicodeEmotes.UNO_REVERSE.getUnicode()).queue();
	}
}