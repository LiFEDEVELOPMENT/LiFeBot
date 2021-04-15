package de.life.classes;

import net.dv8tion.jda.api.entities.User;

public class RPSLogic {
	
	User firstPlayer;
	User secondPlayer;
	String firstChoice = "";
	String secondChoice = "";
	String winner = "";

	public RPSLogic() {
		
	}
	
	public void setPlayers(User p1, User p2) {
		firstPlayer = p1;
		secondPlayer = p2;
	}
	
	public void setFirstPlayer(User pPlayer) {
		firstPlayer = pPlayer;
	}
	
	public User getFirstPlayer() {
		return firstPlayer;
	}
	
	public void setSecondPlayer(User pPlayer) {
		secondPlayer = pPlayer;
	}
	
	public User getSecondPlayer() {
		return secondPlayer;
	}
	
	public void setFirstChoice(String pChoice) {
		firstChoice = pChoice;
	}
	
	public String getFirstChoice() {
		return firstChoice;
	}
	
	public void setSecondChoice(String pChoice) {
		secondChoice = pChoice;
	}	
	
	public String getSecondChoice() {
		return secondChoice;
	}
	
	public void setChoice(User pPlayer, String pChoice) {
		if(firstPlayer.equals(pPlayer)) firstChoice = pChoice;
		if(secondPlayer.equals(pPlayer)) secondChoice = pChoice;
	}
		
	public String getWinner() {
		if(firstChoice == "" || secondChoice == "" || firstPlayer == null || secondPlayer == null) return "";
		doLogic();
		
		if(winner.equals("firstPlayer")) return "firstPlayer";
		if(winner.equals("secondPlayer")) return "secondPlayer";
		return "draw";
	}
	
	public void doLogic() {
		if(firstChoice.equals("Scissors") && secondChoice.equals("Scissors")) return;
		if(firstChoice.equals("Scissors") && secondChoice.equals("Rock")) winner = "secondPlayer";
		if(firstChoice.equals("Scissors") && secondChoice.equals("Paper")) winner = "firstPlayer";
		if(firstChoice.equals("Rock") && secondChoice.equals("Scissors")) winner = "firstPlayer";
		if(firstChoice.equals("Rock") && secondChoice.equals("Rock")) return;
		if(firstChoice.equals("Rock") && secondChoice.equals("Paper")) winner = "secondPlayer";
		if(firstChoice.equals("Paper") && secondChoice.equals("Scissors")) winner = "secondPlayer";
		if(firstChoice.equals("Paper") && secondChoice.equals("Rock")) winner = "firstPlayer";
		if(firstChoice.equals("Paper") && secondChoice.equals("Paper")) return;
	}
	
}