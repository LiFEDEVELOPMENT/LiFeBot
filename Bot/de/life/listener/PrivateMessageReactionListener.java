package de.life.listener;

import de.life.classes.RPSManager;
import de.life.classes.TTTManager;
import de.life.classes.UnicodeEmotes;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrivateMessageReactionListener extends ListenerAdapter {

	@Override
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {

		final String[] tttUnicode = { UnicodeEmotes.ONE.getUnicode(), UnicodeEmotes.TWO.getUnicode(),
				UnicodeEmotes.THREE.getUnicode(), UnicodeEmotes.FOUR.getUnicode(), UnicodeEmotes.FIVE.getUnicode(),
				UnicodeEmotes.SIX.getUnicode(), UnicodeEmotes.SEVEN.getUnicode(), UnicodeEmotes.EIGHT.getUnicode(),
				UnicodeEmotes.NINE.getUnicode() };

		final String reactionEmote = event.getReactionEmote().toString().toUpperCase().substring(3);

		if (event.getUser().equals(event.getJDA().getSelfUser()))
			return;

		if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getEmbeds().size() > 0) {
			if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getEmbeds().get(0)
					.getTitle().startsWith("RPS")) {
				if (reactionEmote.equals(UnicodeEmotes.ROCK.getUnicode())) {
					RPSManager.getInstance().enterChoice(event.getUser(), "Rock");
				}
				if (reactionEmote.equals(UnicodeEmotes.PAPER.getUnicode())) {
					RPSManager.getInstance().enterChoice(event.getUser(), "Paper");
				}
				if (reactionEmote.equals(UnicodeEmotes.SCISSORS.getUnicode())) {
					RPSManager.getInstance().enterChoice(event.getUser(), "Scissors");
				}
				return;
			}

			if (event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getEmbeds().get(0)
					.getTitle().startsWith("TicTacToe")) {
				event.getChannel().deleteMessageById(event.getMessageId()).queue();

				for (int i = 1; i < 10; i++) {
					if (event.getReactionEmote().toString().toUpperCase().substring(3).equals(tttUnicode[i - 1])) {
						TTTManager.getInstance().enterChoice(event.getUser(), i);
						return;
					}
				}
			}
		}
	}
}