package de.life.commands;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import de.life.LiFeBot;
import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetupCommand implements ServerCommand {

	final EventWaiter waiter;

	public SetupCommand() {
		this.waiter = LiFeBot.INSTANCE.getWaiter();
	}

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		frage1(m, channel, message);
	}

	private void frage1(Member m, MessageChannel channel, Message message) {
		message.reply("Herzlich Willkommen in der Einrichtung des LiFeBots.\n"
				+ "Der Bot wird dir jetzt einige Fragen stellen. Die Antwortmöglichkeiten zu diesen stehen in eckigen Klammern hinter der Frage.\n"
				+ "Jede Einstellung kannst du später beliebig verändern.\n"
				+ "Beginnen wir mit einem Newschannel. Dort werden von den Entwicklern neue Funktionen oder Updates anderer Art angekündigt. Möchtest du einen einrichten? [Ja/Nein]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						frage1_1(m, channel, message);
					} else if (response.startsWith("nein")) {
						frage2(m, channel, message);
					} else {
						channel.sendMessage(
								"Du hast dich glaube ich verschrieben. Ich habe die Frage für dich neu gestartet :)")
								.queue();
						frage1(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage1_1(Member m, MessageChannel channel, Message message) {
		message.reply("Soll der Bot automatisch einen Textkanal erstellen? [Ja/Nein]").queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						BotNewsCommand.autoGenerate(m, channel, message);
						frage2(m, channel, message);
					} else if (response.startsWith("nein")) {
						frage1_2(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage1_2(Member m, MessageChannel channel, Message message) {
		message.reply("Bitte gib die ChannelID des Channels an, den du als Newschannel nutzen möchtest. [ChannelID]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					if (!BotNewsCommand.setNewsChannel(m, channel, e.getMessage())) {
						EmbedMessageBuilder.sendMessage("Error",
								"Da hat wohl etwas nicht geklappt. Probier es nochmal.", channel, 10);
						frage1_2(m, channel, message);
						return;
					}
					frage2(m, channel, message);
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage2(Member m, MessageChannel channel, Message message) {
		message.reply(
				"Möchtest du einen Logchannel einrichten, in dem alle Aktionen des Bots auf dieesem Server gespeichert werden? [Ja/Nein]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						frage2_1(m, channel, message);
					} else if (response.startsWith("nein")) {
						frage3(m, channel, message);
					} else {
						channel.sendMessage(
								"Du hast dich glaube ich verschrieben. Ich habe die Frage für dich neu gestartet :)")
								.queue();
						frage2(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage2_1(Member m, MessageChannel channel, Message message) {
		message.reply("Soll der Bot einen Textkanal erstellen? [Ja/Nein]").queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						LogsCommand.autoGenerate(m, channel, message);
						frage3(m, channel, message);
					} else if (response.startsWith("nein")) {
						frage2_2(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage2_2(Member m, MessageChannel channel, Message message) {
		message.reply("Bitte gib die ChannelID des Channels an, den du als Logchannel nutzen möchtest. [ChannelID]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					if (!LogsCommand.setLogsChannel(m, channel, e.getMessage())) {
						EmbedMessageBuilder.sendMessage("Error",
								"Da hat wohl etwas nicht geklappt. Probier es nochmal.", channel, 10);
						frage2_2(m, channel, message);
						return;
					}
					frage3(m, channel, message);
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage3(Member m, MessageChannel channel, Message message) {
		message.reply(
				"Möchtest du ein Voice-Channelhub erstellen, um die privaten Voice Channels des Bots zu aktivieren? [Ja/Nein]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						frage3_1(m, channel, message);
					} else if (response.startsWith("nein")) {
						frage4(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage3_1(Member m, MessageChannel channel, Message message) {
		message.reply("Soll der Bot einen Sprachkanal erstellen? [Ja/Nein]").queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						frage4(m, channel, message);
					} else if (response.startsWith("nein")) {
						frage3_2(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage3_2(Member m, MessageChannel channel, Message message) {
		message.reply("Bitte gib die ChannelID des Channels an, den du als Hubchannel nutzen möchtest. [ChannelID]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					frage4(m, channel, message);
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage4(Member m, MessageChannel channel, Message message) {
		message.reply(
				"Der Bot beinhaltetet ein Verwarnungssystem. Nach welchem Zeitraum soll eine Verwarnung automatisch verfallen? [X y/m/d]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					frage5(m, channel, message);
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage5(Member m, MessageChannel channel, Message message) {
		message.reply(
				"Nach wie vielen Verwarnungen soll ein User automatisch vom Server gebannt werden? Für keine automatischen Bans, gebe bitte 0 an. [X]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					frage6(m, channel, message);
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage6(Member m, MessageChannel channel, Message message) {
		message.reply("Möchtest du einen Zitatechannel festlegen? [Ja/Nein]").queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						frage6_1(m, channel, message);
					} else if (response.startsWith("nein")) {
						frage7(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage6_1(Member m, MessageChannel channel, Message message) {
		message.reply("Soll der Bot einen Textkanal erstellen? [Ja/Nein]").queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						frage7(m, channel, message);
					} else if (response.startsWith("nein")) {
						frage6_2(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage6_2(Member m, MessageChannel channel, Message message) {
		message.reply("Bitte gib die ChannelID des Channels an, den du als Zitatechannel nutzen möchtest. [ChannelID]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					frage6_3(m, channel, message);
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage6_3(Member m, MessageChannel channel, Message message) {
		message.reply(
				"Soll der Bot alle vorhandenen Nachrichten des angegebenen Channels als Zitate importieren? [Ja/Nein]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {

					} else if (response.startsWith("nein")) {

					}
					frage7(m, channel, message);
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage7(Member m, MessageChannel channel, Message message) {
		message.reply(
				"Möchtest du Autotrigger (automatische Nachrichten des Bots, wenn eine Nachricht ein festgelegtes Wort/eine festgelete Phrase enthält) festlegen? [Ja/Nein]")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						frage7_1(m, channel, message);
					} else if (response.startsWith("nein")) {
						endMessage(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage7_1(Member m, MessageChannel channel, Message message) {
		message.reply("Was soll der Trigger sein? [Trigger]").queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					frage7_2(m, channel, message, response);
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage7_2(Member m, MessageChannel channel, Message message, String trigger) {
		message.reply("Was soll die Antwort sein, wenn jemand \"" + trigger + "\" schreibt? [Auto-Antwort]").queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					frage7_3(m, channel, message);
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void frage7_3(Member m, MessageChannel channel, Message message) {
		message.reply("Möchtest du noch einen Trigger festlegen? [Ja/Nein]").queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					final String response = e.getMessage().getContentDisplay().toLowerCase();

					if (response.startsWith("ja")) {
						frage7_1(m, channel, message);
					} else if (response.startsWith("nein")) {
						endMessage(m, channel, message);
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});
	}

	private void endMessage(Member m, MessageChannel channel, Message message) {
		channel.sendMessage("Die Einrichtung des Bots wurde erfolgreich abgeschlossen!").queue();
	}

}