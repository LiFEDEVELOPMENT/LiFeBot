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

		switch (event.getReactionEmote().getName()) {
		case "✌":
			RPSManager.getInstance().enterChoice(event.getUser(), "Scissors");
			break;
		case "✊":
			RPSManager.getInstance().enterChoice(event.getUser(), "Rock");
			break;
		case "�?":
			RPSManager.getInstance().enterChoice(event.getUser(), "Paper");
			break;
		}
	}
}