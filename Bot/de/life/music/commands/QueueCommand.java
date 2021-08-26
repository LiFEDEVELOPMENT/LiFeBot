package de.life.music.commands;

import java.awt.Color;
import java.util.Arrays;

import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import de.life.music.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class QueueCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (args.length == 1) {
			// displayQueue(m, channel);
			return;
		}

		switch (args[1]) {
		case "add":
			add(m, channel, message.getContentDisplay());
			break;
		case "clear":
			clearQueue(m, channel);
			break;
		case "empty":
			clearQueue(m, channel);
			break;
		case "jump":
			jumpQueue(m, channel, message);
			break;
		case "list":
			// displayQueue(m, channel);
			break;
		case "shuffle":
			shuffleQueue(m, channel);
			break;
		default:
			add(m, channel, "add " + message.getContentDisplay());
			break;
		}
	}

//	private static void displayQueue(Member m, MessageChannel channel) {
//		int i = 1;
//		EmbedBuilder builder = new EmbedBuilder().setTitle("Es folgt:").setColor(Color.ORANGE);
//
//		if (QueueManager.getInstance().getQueue(m.getGuild()).getQueue() == null) {
//			builder.setDescription("Nichts - Die Queue ist leer");
//			channel.sendMessageEmbeds(builder.build()).queue();
//			return;
//		}
//
//		for (AudioTrack track : QueueManager.getInstance().getQueue(m.getGuild()).getQueue()) {
//			String appString = i + ") " + track.getInfo().author + " - " + track.getInfo().title + "\n(**"
//					+ (track.getInfo().isStream ? ":red_circle: STREAM"
//							: (track.getDuration() / 3600000 > 0 ? track.getDuration() / 3600000 + "h " : "")
//									+ track.getDuration() / 60000 % 60 + "m " + track.getDuration() / 1000 % 60 + "s")
//					+ "**)\n\n";
//			if (builder.length() + appString.length() > 2000) {
//				builder.setFooter("+ " + (QueueManager.getInstance().getQueue(m.getGuild()).getQueue().size() - 1)
//						+ " weitere Titel");
//				break;
//			}
//			builder.appendDescription(appString);
//			i++;
//		}
//
//		if (builder.build().getDescription() == null)
//			builder.setDescription("Nichts - Die Queue ist leer");
//		channel.sendMessageEmbeds(builder.build()).queue();
//	}

	private void add(Member m, MessageChannel channel, String message) {
		PlayCommand.addQueue(m, channel,
				String.join(" ", Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length)));
	}

	private void clearQueue(Member m, MessageChannel channel) {
		PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.clear();
		EmbedMessageBuilder.sendMessage("Musik", "Die Queue wurde geleert", Color.ORANGE, channel, 10);
	}

	private void jumpQueue(Member m, MessageChannel channel, Message message) {
		if (message.getContentDisplay().split(" ").length < 3) {
			EmbedMessageBuilder.sendMessage("Musik", "Bitte gib eine Zahl an", Color.RED, channel, 10);
			return;
		}

		try {
			PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler
					.jump(Integer.parseInt(message.getContentDisplay().split(" ")[2]));
			EmbedMessageBuilder.sendMessage("Musik", Integer.parseInt(message.getContentDisplay().split(" ")[2])
					+ " Songs wurden in der Queue �bersprungen", Color.ORANGE, channel);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Musik", "Bitte gib eine Zahl an", Color.RED, channel, 10);
			return;
		}
	}

	private void shuffleQueue(Member m, MessageChannel channel) {
		PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.shuffle();
		EmbedMessageBuilder.sendMessage("Musik", "Die Queue wurde geshuffled", Color.ORANGE, channel, 10);
	}
}