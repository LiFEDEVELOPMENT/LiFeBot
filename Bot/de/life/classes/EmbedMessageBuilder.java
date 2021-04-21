package de.life.classes;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class EmbedMessageBuilder {
	public static void sendMessage(String description, MessageChannel channel) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setDescription(description);
		channel.sendMessage(messageBuilder.build()).queue();
	}

	public static void sendMessage(String description, Color color, MessageChannel channel) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setDescription(description).setColor(color);
		channel.sendMessage(messageBuilder.build()).queue();
	}

	public static void sendMessage(String description, MessageChannel channel, int deleteTime) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setDescription(description);
		channel.sendMessage(messageBuilder.build()).complete().delete().queueAfter(deleteTime, TimeUnit.SECONDS);
	}

	public static void sendMessage(String description, Color color, MessageChannel channel, int deleteTime) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setDescription(description).setColor(color);
		channel.sendMessage(messageBuilder.build()).complete().delete().queueAfter(deleteTime, TimeUnit.SECONDS);
	}

	public static void sendMessage(String title, String description, MessageChannel channel) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setTitle(title).setDescription(description);
		channel.sendMessage(messageBuilder.build()).queue();
	}

	public static void sendMessage(String title, String description, Color color, MessageChannel channel) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setTitle(title).setDescription(description).setColor(color);
		channel.sendMessage(messageBuilder.build()).queue();
	}

	public static void sendMessage(String title, String description, String footer, MessageChannel channel) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setTitle(title).setDescription(description).setFooter(footer);
		channel.sendMessage(messageBuilder.build()).queue();
	}

	public static void sendMessage(String title, String description, String footer, Color color,
			MessageChannel channel) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setTitle(title).setDescription(description).setFooter(footer)
				.setColor(color);
		channel.sendMessage(messageBuilder.build()).queue();
	}

	public static void sendMessage(String title, String description, MessageChannel channel, int deleteTime) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setTitle(title).setDescription(description);
		channel.sendMessage(messageBuilder.build()).complete().delete().queueAfter(deleteTime, TimeUnit.SECONDS);
	}

	public static void sendMessage(String title, String description, Color color, MessageChannel channel,
			int deleteTime) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setTitle(title).setDescription(description).setColor(color);
		channel.sendMessage(messageBuilder.build()).complete().delete().queueAfter(deleteTime, TimeUnit.SECONDS);
	}

	public static void sendMessage(String title, String description, String footer, MessageChannel channel,
			int deleteTime) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setTitle(title).setDescription(description).setFooter(footer);
		channel.sendMessage(messageBuilder.build()).complete().delete().queueAfter(deleteTime, TimeUnit.SECONDS);
	}

	public static void sendMessage(String title, String description, String footer, Color color, MessageChannel channel,
			int deleteTime) {
		EmbedBuilder messageBuilder = new EmbedBuilder().setTitle(title).setDescription(description).setFooter(footer)
				.setColor(color);
		channel.sendMessage(messageBuilder.build()).complete().delete().queueAfter(deleteTime, TimeUnit.SECONDS);
	}
}