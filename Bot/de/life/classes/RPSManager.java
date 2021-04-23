package de.life.classes;

import java.awt.Color;
import java.util.HashMap;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public class RPSManager {

	private static RPSManager INSTANCE;
	private HashMap<Integer, RPSLogic> gamesMap = new HashMap<Integer, RPSLogic>();
	private HashMap<User, Integer> playerMap = new HashMap<User, Integer>();

	public void clearGame(User pPlayer) {
		int gameID = playerMap.get(pPlayer);
		playerMap.remove(gamesMap.get(gameID).getFirstPlayer());
		playerMap.remove(gamesMap.get(gameID).getSecondPlayer());
		gamesMap.remove(gameID);

		if (gamesMap.isEmpty())
			INSTANCE = null;
	}

	public void enterChoice(User pPlayer, String pChoice) {
		if (!playerMap.containsKey(pPlayer))
			return;
		gamesMap.get(playerMap.get(pPlayer)).setChoice(pPlayer, pChoice);
		updateResult(pPlayer);
	}

	public boolean hasGame(User firstPlayer, User secondPlayer) {
		if (playerMap.containsKey(firstPlayer) || playerMap.containsKey(secondPlayer))
			return true;
		return false;
	}

	public void startGame(User firstPlayer, User secondPlayer, MessageChannel channel) {
		if (hasGame(firstPlayer, secondPlayer)) {
			EmbedMessageBuilder.sendMessage("RPS",
					"Einer der beiden Spieler hat bereits ein aktives Schere, Stein, Papier -Spiel. Bitte warte, bis dieses beendet ist",
					Color.RED, channel, 10);
			return;
		}

		playerMap.put(firstPlayer, gamesMap.size());
		playerMap.put(secondPlayer, gamesMap.size());
		gamesMap.put(gamesMap.size(), new RPSLogic(firstPlayer, secondPlayer));

		Long firstPlayerID = firstPlayer.openPrivateChannel().complete().sendMessage(
				"RPS: Bitte wähle deine Option im Schere, Stein, Papier -Spiel gegen " + secondPlayer.getName() + ".")
				.complete().getIdLong();
		Long secondPlayerID = secondPlayer.openPrivateChannel().complete().sendMessage(
				"RPS: Bitte wähle deine Option im Schere, Stein, Papier -Spiel gegen " + firstPlayer.getName() + ".")
				.complete().getIdLong();

		firstPlayer.openPrivateChannel().complete().addReactionById(firstPlayerID, "U+270C").queue();
		firstPlayer.openPrivateChannel().complete().addReactionById(firstPlayerID, "U+270A").queue();
		firstPlayer.openPrivateChannel().complete().addReactionById(firstPlayerID, "U+1F590").queue();

		secondPlayer.openPrivateChannel().complete().addReactionById(secondPlayerID, "U+270C").queue();
		secondPlayer.openPrivateChannel().complete().addReactionById(secondPlayerID, "U+270A").queue();
		secondPlayer.openPrivateChannel().complete().addReactionById(secondPlayerID, "U+1F590").queue();
	}

	private void updateResult(User pPlayer) {
		if (gamesMap.get(playerMap.get(pPlayer)).getWinner().equals(""))
			return;

		PrivateChannel firstPlayer = gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().openPrivateChannel()
				.complete();
		PrivateChannel secondPlayer = gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().openPrivateChannel()
				.complete();

		switch (gamesMap.get(playerMap.get(pPlayer)).getWinner()) {
		case "draw":
			firstPlayer.sendMessage("Dein Schere, Stein, Papier -Spiel gegen "
					+ gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().getName()
					+ " endete in einem Unentschieden!").queue();
			secondPlayer.sendMessage("Dein Schere, Stein, Papier -Spiel gegen "
					+ gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().getName()
					+ " endete in einem Unentschieden!").queue();
			clearGame(pPlayer);
			return;
		case "firstPlayer":
			firstPlayer.sendMessage("Du hast das Schere, Stein, Papier -Spiel gegen "
					+ gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().getName() + " gewonnen!").queue();
			secondPlayer.sendMessage("Du hast das Schere, Stein, Papier -Spiel gegen "
					+ gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().getName() + " verloren!").queue();
			clearGame(pPlayer);
			return;
		case "secondPlayer":
			firstPlayer.sendMessage("Du hast das Schere, Stein, Papier -Spiel gegen "
					+ gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().getName() + " verloren!").queue();
			secondPlayer.sendMessage("Du hast das Schere, Stein, Papier -Spiel gegen "
					+ gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().getName() + " gewonnen!").queue();
			clearGame(pPlayer);
			return;
		}
	}

	public static synchronized RPSManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new RPSManager();
		return INSTANCE;
	}
}