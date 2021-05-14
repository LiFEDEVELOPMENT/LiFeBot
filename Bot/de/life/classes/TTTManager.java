package de.life.classes;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class TTTManager {

	private static TTTManager INSTANCE;
	private HashMap<Integer, TTTLogic> gamesMap = new HashMap<Integer, TTTLogic>();
	private HashMap<User, Integer> playerMap = new HashMap<User, Integer>();
	private HashMap<User, Long> idMap = new HashMap<User, Long>();
	private final String[] emoteNames = { ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:",
			":eight:", ":nine:" };
	private final String[] unicodeNames = { UnicodeEmotes.ONE.getUnicode(), UnicodeEmotes.TWO.getUnicode(),
			UnicodeEmotes.THREE.getUnicode(), UnicodeEmotes.FOUR.getUnicode(), UnicodeEmotes.FIVE.getUnicode(),
			UnicodeEmotes.SIX.getUnicode(), UnicodeEmotes.SEVEN.getUnicode(), UnicodeEmotes.EIGHT.getUnicode(),
			UnicodeEmotes.NINE.getUnicode() };

	public void clearGame(User pPlayer) {
		int gameID = playerMap.get(pPlayer);
		playerMap.remove(gamesMap.get(gameID).getFirstPlayer());
		playerMap.remove(gamesMap.get(gameID).getSecondPlayer());
		idMap.remove(gamesMap.get(gameID).getFirstPlayer());
		idMap.remove(gamesMap.get(gameID).getSecondPlayer());
		gamesMap.remove(gameID);

		if (gamesMap.isEmpty())
			INSTANCE = null;
	}

	public void enterChoice(User player, int field) {
		if (!playerMap.containsKey(player))
			return;
		gamesMap.get(playerMap.get(player)).enterChoice(player, field);
		updateResult(player);
	}

	public boolean hasGame(User firstPlayer, User secondPlayer) {
		if (playerMap.containsKey(firstPlayer) || playerMap.containsKey(secondPlayer))
			return true;
		return false;
	}

	public void startGame(User firstPlayer, User secondPlayer, MessageChannel channel) {
		if (hasGame(firstPlayer, secondPlayer)) {
			EmbedMessageBuilder.sendMessage("TTT",
					"Einer der beiden Spieler hat bereits ein aktives TicTacToe -Spiel. Bitte warte, bis dieses beendet ist",
					Color.RED, channel, 10);
			return;
		}

		playerMap.put(firstPlayer, gamesMap.size());
		playerMap.put(secondPlayer, gamesMap.size());
		gamesMap.put(gamesMap.size(), new TTTLogic(firstPlayer, secondPlayer));

		int i = 0;
		EmbedBuilder builder = new EmbedBuilder().setTitle("TicTacToe gegen " + secondPlayer.getName());
		for (String s : emoteNames) {
			builder.appendDescription(s);
			if (i == 2 || i == 5)
				builder.appendDescription("\n");
			i++;
		}

		boolean playerOne = new Random().nextBoolean();

		builder.setFooter(playerOne ? firstPlayer.getName() : secondPlayer.getName() + " ist am Zug");

		idMap.put(firstPlayer,
				firstPlayer.openPrivateChannel().complete().sendMessage(builder.build()).complete().getIdLong());
		builder.setTitle("TicTacToe gegen " + firstPlayer.getName());
		idMap.put(secondPlayer,
				secondPlayer.openPrivateChannel().complete().sendMessage(builder.build()).complete().getIdLong());

		if (playerOne) {
			for (String s : unicodeNames) {
				firstPlayer.openPrivateChannel().complete().addReactionById(idMap.get(firstPlayer), s).queue();
			}
			return;
		}
		for (String s : unicodeNames) {
			secondPlayer.openPrivateChannel().complete().addReactionById(idMap.get(secondPlayer), s).queue();
		}
	}

	private void updateResult(User player) {
		User firstPlayer = gamesMap.get(playerMap.get(player)).getFirstPlayer();
		User secondPlayer = gamesMap.get(playerMap.get(player)).getSecondPlayer();

		EmbedBuilder builder = new EmbedBuilder().setTitle("TicTacToe gegen " + secondPlayer.getName());

		if (player.equals(firstPlayer))
			secondPlayer.openPrivateChannel().complete().deleteMessageById(idMap.get(secondPlayer)).queue();
		if (player.equals(secondPlayer))
			firstPlayer.openPrivateChannel().complete().deleteMessageById(idMap.get(firstPlayer)).queue();

		if (gamesMap.get(playerMap.get(player)).getWinner().equals("firstPlayer")) {
			builder.setDescription(firstPlayer.getName() + " gewinnt!");
			clearGame(player);
		} else if (gamesMap.get(playerMap.get(player)).getWinner().equals("secondPlayer")) {
			builder.setDescription(secondPlayer.getName() + " gewinnt!");
			clearGame(player);
		} else if (gamesMap.get(playerMap.get(player)).getWinner().equals("draw")) {
			builder.setDescription("Das Spiel endete in einem Untentschieden!");
			clearGame(player);
		} else {
			for (int i = 0; i < 9; i++) {
				if (gamesMap.get(playerMap.get(player)).getField()[i] == "") {
					builder.appendDescription(emoteNames[i]);
				} else if (gamesMap.get(playerMap.get(player)).getField()[i] == "x") {
					builder.appendDescription(":regional_indicator_x:");
				} else if (gamesMap.get(playerMap.get(player)).getField()[i] == "o") {
					builder.appendDescription(":regional_indicator_o:");
				}
				if (i == 2 || i == 5)
					builder.appendDescription("\n");
			}
		}

		builder.setFooter(firstPlayer.equals(player) ? player.getName() : secondPlayer.getName() + " ist am Zug");

		idMap.put(firstPlayer,
				firstPlayer.openPrivateChannel().complete().sendMessage(builder.build()).complete().getIdLong());
		builder.setTitle("TicTacToe gegen " + firstPlayer.getName());
		idMap.put(secondPlayer,
				secondPlayer.openPrivateChannel().complete().sendMessage(builder.build()).complete().getIdLong());

		if (gamesMap.get(playerMap.get(player)) == null)
			return;

		if (firstPlayer.equals(player)) {
			for (int i = 0; i < 9; i++) {
				if (gamesMap.get(playerMap.get(player)).getFreeFields().contains(i))
					secondPlayer.openPrivateChannel().complete().retrieveMessageById(idMap.get(secondPlayer)).complete()
							.addReaction(unicodeNames[i]).queue();
			}
		} else {
			for (int i = 0; i < 9; i++) {
				if (gamesMap.get(playerMap.get(player)).getFreeFields().contains(i))
					firstPlayer.openPrivateChannel().complete().retrieveMessageById(idMap.get(firstPlayer)).complete()
							.addReaction(unicodeNames[i]).queue();
			}
		}
	}

	public static synchronized TTTManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TTTManager();
		return INSTANCE;
	}
}