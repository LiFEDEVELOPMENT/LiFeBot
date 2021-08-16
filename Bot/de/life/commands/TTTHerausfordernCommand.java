package de.life.commands;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.TTTManager;
import de.life.classes.UnicodeEmotes;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class TTTHerausfordernCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		if (TTTManager.getInstance() == null)
			new TTTManager();

		User secondPlayer = message.getMentionedUsers().get(0);

		if (secondPlayer.equals(m.getUser()) || secondPlayer.isBot()) {
			EmbedMessageBuilder.sendMessage("TTT", "Du kannst gegen diesen Spieler kein TicTacToe -Spiel starten",
					Color.RED, channel, 10);
			return;
		}

		if (TTTManager.getInstance().hasGame(m.getUser(), secondPlayer)) {
			EmbedMessageBuilder.sendMessage("TTT",
					"Einer dieser Spieler hat bereits ein TicTacToe -Spiel. Bitte warte, bis dieses beendet ist",
					Color.RED, channel, 10);
			return;
		}

		Long messageID = channel.sendMessage("TTT-Herausforderung: " + secondPlayer.getAsMention() + ", du wurdest von "
				+ m.getAsMention()
				+ " zu einem TicTacToe -Spiel herausgefordet. Reagiere mit :white_check_mark: f�r ein Spiel oder mit :x: zum Ablehnen der Herausforderung.")
				.complete().getIdLong();

		channel.deleteMessageById(messageID).queueAfter(1, TimeUnit.MINUTES);
		channel.addReactionById(messageID, UnicodeEmotes.WHITE_CHECK.getUnicode()).queue();
		channel.addReactionById(messageID, UnicodeEmotes.X.getUnicode()).queue();
	}
}
