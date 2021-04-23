package de.life.classes;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.User;

public class TTTLogic {

	private final User firstPlayer;
	private final User secondPlayer;
	String[] field = { "", "", "", "", "", "", "", "", "" };
	String winner = "";

	public TTTLogic(User firstPlayer, User secondPlayer) {
		this.firstPlayer = firstPlayer;
		this.secondPlayer = secondPlayer;
	}

	public User getFirstPlayer() {
		return firstPlayer;
	}

	public User getSecondPlayer() {
		return secondPlayer;
	}

	public void enterChoice(User player, int field) {
		if (player.equals(firstPlayer))
			this.field[field - 1] = "x";
		if (player.equals(secondPlayer))
			this.field[field - 1] = "o";
	}

	public String[] getField() {
		return this.field;
	}

	public ArrayList<Integer> getFreeFields() {
		ArrayList<Integer> freeFields = new ArrayList<Integer>();

		for (int i = 0; i < 9; i++) {
			if (this.field[i].equals(""))
				freeFields.add(i);
		}
		return freeFields;
	}

	public String getWinner() {
		doLogic();
		return winner;
	}

	private void doLogic() {
		if ((this.field[0].equals("x") && this.field[1].equals("x") && this.field[2].equals("x"))
				|| (this.field[3].equals("x") && this.field[4].equals("x") && this.field[5].equals("x"))
				|| (this.field[6].equals("x") && this.field[7].equals("x") && this.field[8].equals("x"))
				|| (this.field[0].equals("x") && this.field[3].equals("x") && this.field[6].equals("x"))
				|| (this.field[1].equals("x") && this.field[4].equals("x") && this.field[7].equals("x"))
				|| (this.field[2].equals("x") && this.field[5].equals("x") && this.field[8].equals("x"))
				|| (this.field[0].equals("x") && this.field[4].equals("x") && this.field[8].equals("x"))
				|| (this.field[6].equals("x") && this.field[4].equals("x") && this.field[2].equals("x"))) {
			winner = "firstPlayer";
			return;
		}
		if ((this.field[0].equals("o") && this.field[1].equals("o") && this.field[2].equals("o"))
				|| (this.field[3].equals("o") && this.field[4].equals("o") && this.field[5].equals("o"))
				|| (this.field[6].equals("o") && this.field[7].equals("o") && this.field[8].equals("o"))
				|| (this.field[0].equals("o") && this.field[3].equals("o") && this.field[6].equals("o"))
				|| (this.field[1].equals("o") && this.field[4].equals("o") && this.field[7].equals("o"))
				|| (this.field[2].equals("o") && this.field[5].equals("o") && this.field[8].equals("o"))
				|| (this.field[0].equals("o") && this.field[4].equals("o") && this.field[8].equals("o"))
				|| (this.field[6].equals("o") && this.field[4].equals("o") && this.field[2].equals("o"))) {
			winner = "secondPlayer";
			return;
		}
		if (!this.field[0].equals("") && !this.field[1].equals("") && !this.field[2].equals("")
				&& !this.field[3].equals("") && !this.field[4].equals("") && !this.field[5].equals("")
				&& !this.field[6].equals("") && !this.field[7].equals("") && !this.field[8].equals(""))
			winner = "draw";
	}
}