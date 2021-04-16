package de.life.classes;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class RPSManager {

	private static RPSManager INSTANCE;

	HashMap<Integer, RPSLogic> gamesMap = new HashMap<Integer, RPSLogic>();
	HashMap<User, Integer> playerMap = new HashMap<User, Integer>();

//	PUBLIC RPSMANAGER() {
//		INSTANCE = THIS;
//	}

	public void startGame(User firstPlayer, User secondPlayer, MessageChannel channel) {
		if (hasGame(firstPlayer, secondPlayer)) {
			channel.sendMessage(
					"Einer der beiden Spieler hat bereits ein aktives Schere, Stein, Papier -Spiel. Bitte warte, bis dieses beendet ist.")
					.complete().delete().queueAfter(5, TimeUnit.SECONDS);
			return;
		}

		playerMap.put(firstPlayer, gamesMap.size());
		playerMap.put(secondPlayer, gamesMap.size());
		gamesMap.put(gamesMap.size(), new RPSLogic());
		gamesMap.get(playerMap.get(firstPlayer)).setFirstPlayer(firstPlayer);
		gamesMap.get(playerMap.get(secondPlayer)).setSecondPlayer(secondPlayer);

		Long firstPlayerID = firstPlayer.openPrivateChannel().complete().sendMessage(
				"RPS: Bitte wähle deine Option im Schere, Stein, Papier -Spiel gegen " + secondPlayer.getName() + ".")
				.complete().getIdLong();
		Long secondPlayerID = secondPlayer.openPrivateChannel().complete().sendMessage(
				"RPS: Bitte wähle deine Option im Schere, Stein, Papier -Spiel gegen " + firstPlayer.getName() + ".")
				.complete().getIdLong();

		firstPlayer.openPrivateChannel().complete().addReactionById(firstPlayerID, "✌").queue();
		firstPlayer.openPrivateChannel().complete().addReactionById(firstPlayerID, "✊").queue();
		firstPlayer.openPrivateChannel().complete().addReactionById(firstPlayerID, "🖐").queue();

		secondPlayer.openPrivateChannel().complete().addReactionById(secondPlayerID, "✌").queue();
		secondPlayer.openPrivateChannel().complete().addReactionById(secondPlayerID, "✊").queue();
		secondPlayer.openPrivateChannel().complete().addReactionById(secondPlayerID, "🖐").queue();
	}

	public void enterChoice(User pPlayer, String pChoice) {
		if (!playerMap.containsKey(pPlayer))
			return;
		gamesMap.get(playerMap.get(pPlayer)).setChoice(pPlayer, pChoice);
		updateResult(pPlayer);
	}

	public void updateResult(User pPlayer) {
		if (gamesMap.get(playerMap.get(pPlayer)).getWinner().equals(""))
			return;
		if (gamesMap.get(playerMap.get(pPlayer)).getWinner().equals("draw")) {
			gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().openPrivateChannel().complete()
					.sendMessage("Dein Schere, Stein, Papier -Spiel gegen "
							+ gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().getName()
							+ " endete in einem Unentschieden!")
					.queue();
			gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().openPrivateChannel().complete()
					.sendMessage("Dein Schere, Stein, Papier -Spiel gegen "
							+ gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().getName()
							+ " endete in einem Unentschieden!")
					.queue();
			clearGame(pPlayer);
			return;
		}
		if (gamesMap.get(playerMap.get(pPlayer)).getWinner().equals("firstPlayer")) {
			gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().openPrivateChannel().complete()
					.sendMessage("Du hast das Schere, Stein, Papier -Spiel gegen "
							+ gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().getName() + " gewonnen!")
					.queue();
			gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().openPrivateChannel().complete()
					.sendMessage("Du hast das Schere, Stein, Papier -Spiel gegen "
							+ gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().getName() + " verloren!")
					.queue();
			clearGame(pPlayer);
			return;
		}
		if (gamesMap.get(playerMap.get(pPlayer)).getWinner().equals("secondPlayer")) {
			gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().openPrivateChannel().complete()
					.sendMessage("Du hast das Schere, Stein, Papier -Spiel gegen "
							+ gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().getName() + " verloren!")
					.queue();
			gamesMap.get(playerMap.get(pPlayer)).getSecondPlayer().openPrivateChannel().complete()
					.sendMessage("Du hast das Schere, Stein, Papier -Spiel gegen "
							+ gamesMap.get(playerMap.get(pPlayer)).getFirstPlayer().getName() + " gewonnen!")
					.queue();
			clearGame(pPlayer);
			return;
		}
	}

	public boolean hasGame(User firstPlayer, User secondPlayer) {
		if (playerMap.containsKey(firstPlayer) || playerMap.containsKey(secondPlayer))
			return true;
		return false;
	}

	public void clearGame(User pPlayer) {
		int gameID = playerMap.get(pPlayer);
		playerMap.remove(gamesMap.get(gameID).getFirstPlayer());
		playerMap.remove(gamesMap.get(gameID).getSecondPlayer());
		gamesMap.remove(gameID);

		if (gamesMap.isEmpty())
			INSTANCE = null;
	}

	public static synchronized RPSManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new RPSManager();
		return INSTANCE;
	}
}