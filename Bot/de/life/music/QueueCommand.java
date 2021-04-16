package de.life.music;

import java.awt.Color;
import java.util.Arrays;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class QueueCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		if (args.length == 1) {
			displayQueue(m, channel, message);
			return;
		}

		switch (args[1]) {
		case "add":
			add(m, channel, message);
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
		case "shuffle":
			shuffleQueue(m, channel);
			break;
		}
	}

	private void displayQueue(Member m, MessageChannel channel, Message message) {
		int i = 1;
		EmbedBuilder builder = new EmbedBuilder().setTitle("Es folgt:").setColor(Color.ORANGE);
		for (AudioTrack track : PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.getQueue()) {
			if (builder.length() + (i + ") " + track.getInfo().author + " - " + track.getInfo().title + " ("
					+ track.getDuration() + ")\n").length() > 2000) {
				builder.setFooter("+ "
						+ (PlayerManager.getInstance().getMusicManager(m.getGuild()).scheduler.getQueue().size() - 1)
						+ " weitere Titel");
				break;
			}
			builder.appendDescription(i + ") " + track.getInfo().author + " - " + track.getInfo().title + " ("
					+ track.getDuration() / 1000 + ")\n");
			i++;
		}

		if (builder.build().getDescription() == null)
			builder.setDescription("Nichts - Die Queue ist leer");
		channel.sendMessage(builder.build()).queue();
	}

	private void add(Member m, MessageChannel channel, Message message) {
		PlayCommand.addQueue(m, channel, String.join(" ", Arrays.copyOfRange(message.getContentDisplay().split(" "), 1,
				message.getContentDisplay().split(" ").length)));
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
			EmbedMessageBuilder.sendMessage("Musik", "Die Queue wurde um "
					+ Integer.parseInt(message.getContentDisplay().split(" ")[2]) + " Songs nach vorne verschoben",
					Color.ORANGE, channel, 10);
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