package de.life.listener;

import de.life.classes.RPSManager;
import de.life.classes.TTTManager;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrivateMessageReactionListener extends ListenerAdapter {

	@Override
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {
		if (event.getUser().equals(event.getJDA().getSelfUser()))
			return;

		if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getEmbeds().size() > 0) {
			if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getEmbeds().get(0)
					.getTitle().startsWith("RPS")) {
				switch (event.getReactionEmote().toString().toUpperCase().substring(3)) {
				case "U+270C":
					RPSManager.getInstance().enterChoice(event.getUser(), "Scissors");
					return;
				case "U+270A":
					RPSManager.getInstance().enterChoice(event.getUser(), "Rock");
					return;
				case "U+1F590":
					RPSManager.getInstance().enterChoice(event.getUser(), "Paper");
					return;
				}
				return;
			}
			if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getEmbeds().get(0)
					.getTitle().startsWith("TicTacToe")) {
				event.getChannel().deleteMessageById(event.getMessageId()).queue();
				switch (event.getReactionEmote().toString().toUpperCase().substring(3)) {
				case "U+31U+FE0FU+20E3":
					TTTManager.getInstance().enterChoice(event.getUser(), 1);
					return;
				case "U+32U+FE0FU+20E3":
					TTTManager.getInstance().enterChoice(event.getUser(), 2);
					return;
				case "U+33U+FE0FU+20E3":
					TTTManager.getInstance().enterChoice(event.getUser(), 3);
					return;
				case "U+34U+FE0FU+20E3":
					TTTManager.getInstance().enterChoice(event.getUser(), 4);
					return;
				case "U+35U+FE0FU+20E3":
					TTTManager.getInstance().enterChoice(event.getUser(), 5);
					return;
				case "U+36U+FE0FU+20E3":
					TTTManager.getInstance().enterChoice(event.getUser(), 6);
					return;
				case "U+37U+FE0FU+20E3":
					TTTManager.getInstance().enterChoice(event.getUser(), 7);
					return;
				case "U+38U+FE0FU+20E3":
					TTTManager.getInstance().enterChoice(event.getUser(), 8);
					return;
				case "U+39U+FE0FU+20E3":
					TTTManager.getInstance().enterChoice(event.getUser(), 9);
					return;
				}
			}
		}
	}
}