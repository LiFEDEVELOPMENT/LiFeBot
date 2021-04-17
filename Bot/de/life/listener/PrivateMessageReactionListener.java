 package de.life.listener;

import de.life.classes.RPSManager;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrivateMessageReactionListener extends ListenerAdapter {

	@Override
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {
		if (event.getUser().equals(event.getJDA().getSelfUser()))
			return;
		if (!event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getContentDisplay()
				.startsWith("RPS:"))
			return;
		
		switch (event.getReactionEmote().toString().toUpperCase().substring(3)) {
		case "U+270C":
			RPSManager.getInstance().enterChoice(event.getUser(), "Scissors");
			break;
		case "U+270A":
			RPSManager.getInstance().enterChoice(event.getUser(), "Rock");
			break;
		case "U+1F590":
			RPSManager.getInstance().enterChoice(event.getUser(), "Paper");
			break;
		}
	}
}