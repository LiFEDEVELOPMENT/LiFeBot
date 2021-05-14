package de.life.commands;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.RPSManager;
import de.life.classes.UnicodeEmotes;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class RPSHerausfordernCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		if (message.getMentionedUsers().size() == 0) {
			message.addReaction(UnicodeEmotes.ROCK.getUnicode()).queue();
			message.addReaction(UnicodeEmotes.PAPER.getUnicode()).queue();
			message.addReaction(UnicodeEmotes.SCISSORS.getUnicode()).queue();
			return;
		}

		User secondPlayer = message.getMentionedUsers().get(0);

		if (secondPlayer.equals(m.getUser()) || secondPlayer.isBot()) {
			EmbedMessageBuilder.sendMessage("RPS",
					"Du kannst gegen diesen Spieler kein Schere, Stein, Papier -Spiel starten", Color.RED, channel, 10);
			return;
		}

		if (RPSManager.getInstance().hasGame(m.getUser(), secondPlayer)) {
			EmbedMessageBuilder.sendMessage("RPS",
					"Einer dieser Spieler hat bereits ein RPS-Spiel. Bitte warte, bis dieses beendet ist", Color.RED,
					channel, 10);
			return;
		}
		
		Long messageID = channel.sendMessage("RPS-Herausforderung: " + secondPlayer.getAsMention() + ", du wurdest von "
				+ m.getAsMention()
				+ " zu einem Spiel Schere, Stein, Papier herausgefordet. Reagiere mit :white_check_mark: für ein Spiel oder mit :x: zum Ablehnen der Herausforderung.")
				.complete().getIdLong();

		channel.deleteMessageById(messageID).queueAfter(1, TimeUnit.MINUTES);
		channel.addReactionById(messageID, UnicodeEmotes.WHITE_CHECK.getUnicode()).queue();
		channel.addReactionById(messageID, UnicodeEmotes.X.getUnicode()).queue();
	}
}