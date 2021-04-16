package de.life.commands;

import java.util.concurrent.TimeUnit;

import de.life.classes.RPSManager;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class RPSHerausfordernCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		if (message.getMentionedUsers().size() == 0) {
			message.addReaction("✌").queue();
			message.addReaction("✊").queue();
			message.addReaction("🖐").queue();
			return;
		}

		if (RPSManager.getInstance() == null)
			new RPSManager();

		User secondPlayer = message.getMentionedUsers().get(0);

		if (secondPlayer.equals(m.getUser()) || secondPlayer.isBot()) {
			channel.sendMessage("Du kannst gegen diesen Spieler kein Schere, Stein, Papier -Spiel starten.").queue();
			return;
		}

		if (RPSManager.getInstance().hasGame(m.getUser(), secondPlayer)) {
			channel.sendMessage(
					"Einer dieser Spieler hat bereits ein Schere, Stein, Papier -Spiel. Bitte warte, bis dieses beendet ist.")
					.queue();
			return;
		}
		Long messageID = channel.sendMessage("RPS-Herausforderung: " + secondPlayer.getAsMention() + ", du wurdest von "
				+ m.getAsMention()
				+ " zu einem Spiel Schere, Stein, Papier herausgefordet. Reagiere mit :white_check_mark: für ein Spiel oder mit :x: zum Ablehnen der Herausforderung.")
				.complete().getIdLong();

		channel.deleteMessageById(messageID).queueAfter(1, TimeUnit.MINUTES);
		channel.addReactionById(messageID, "✅").queue();
		channel.addReactionById(messageID, "❌").queue();
	}
}