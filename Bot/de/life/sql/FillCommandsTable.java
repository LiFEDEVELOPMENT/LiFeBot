package de.life.sql;

public class FillCommandsTable {

	public static void fillTable() {
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Announce:**\r\n"
				+ "!announce <Nachricht> - Announced die Nachricht\r\n"
				+ "!announce <@Rolle> <Nachricht> - Announced die Nachricht an @Rolle')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Autotriggers:**\r\n"
				+ "!trigger add <trigger\\\\response> - Fügt den angegebenen Autotrigger hinzu\r\n"
				+ "!trigger delete <Trigger-ID> - Löscht den Autotrigger mit der angegebenen ID\r\n"
				+ "!trigger list - Zeigt eine Liste aller Autotrigger\r\n"
				+ "Alias: !triggers / !autotrigger / !autotriggers')");
		SQLite.onUpdate(
				"INSERT INTO commands (command) VALUES ('**Ban:**\r\n" + "!ban <@User> - Bannt den User vom Server\r\n"
						+ "!ban <@User> <Reason> - Bannt den User vom Server mit der angegebenen Reason\r\n"
						+ "Alias: !bicmac <@User>')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Clear:**\r\n"
				+ "!clear <Anzahl> [@User] - Löscht x Nachrichten im aktuellen Channel. Wird ein User angegeben, werden x Nachrichten von diesem User gelöscht\r\n"
				+ "Alias: !c')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Coinflip:**\r\n"
				+ "!coinflip - Wirft eine Münze für dich\r\n" + "Alias: !cf / !flip')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Color:**\r\n"
				+ "!color <#RRGGBB> - Gibt eine Box mit der angegebenen Farbe zurück')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Commands:**\r\n"
				+ "!commands - Zeigt diese Liste\r\n" + "Alias: !help')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Hub-Channels:**\r\n"
				+ "!hub add <Channel-ID> - Fügt den angegebenen Channel als Hub-Channel hinzu\r\n"
				+ "!hub delete <Channel-ID> - Löscht den Hub-Channel mit der angegebenen ID\r\n"
				+ "!hub list - Zeigt eine Liste aller Hubchannels\r\n" + "Alias: !hubs')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Kick:**\r\n"
				+ "!kick <@User> - Kickt den User vom Server\r\n"
				+ "!kick <@User> <Reason> - Kickt den User mit der angegebenen Reason vom Server')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Log-Channels:**\r\n"
				+ "!log add <Channel-ID> - Fügt den angegebenen Channel als Log-Channel hinzu\r\n"
				+ "!log delete <Channel-ID> - Löscht den Log-Channel mit der angegebenen ID\r\n"
				+ "!log list - Zeigt eine Liste aller Log-Channels\r\n" + "Alias: !logs')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Memberinfo:**\r\n"
				+ "!memberinfo <@User> - Zeigt zusätzliche Infos zu dem angegebenen User\r\n"
				+ "Alias: !userinfo <@User>')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Memes:**\r\n"
				+ "!meme - Zeigt ein zufälliges Meme\r\n" + "!meme add <Meme> - Fügt ein Meme hinzu\r\n"
				+ "!meme delete <ID> - Löscht das Meme mit der ID\r\n"
				+ "!meme list - Zeigt eine Liste aller Memes mit ihren IDs\r\n" + "Alias: !memes')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Musik:**\r\n"
				+ "!play <YouTube-Link/Spotify-Link> - Spielt das angegebene Lied bzw. fügt es zur Queue hinzu\r\n"
				+ "!play <Youtube-Playlistlink/Spotfy-Playlistlink> - Spielt die angegebene Playlist bzw. fügt sie zur Queue hinzu\r\n"
				+ "!play <Spotify-Albumlink> - Spielt das angegebene Album bzw. fügt es der Queue hinzu\r\n"
				+ "!play <Lied/Interpret> - Spielt das angegebene Lied bzw. fügt es zur Queue hinzu\r\n\n"
				+ "!pause - Pausiert die Wiedergabe\r\n" + "!resume - Startet die Wiedergabe erneut\r\n\n"
				+ "!queue - Zeigt die Queue\r\n"
				+ "!queue <YouTube-Link/Spotify-Link> - Spielt das angegebene Lied bzw. fügt es zur Queue hinzu\r\n"
				+ "!queue <Youtube-Playlistlink/Spotfy-Playlistlink> - Spielt die angegebene Playlist bzw. fügt sie zur Queue hinzu\r\n"
				+ "!queue <Spotify-Albumlink> - Spielt das angegebene Album bzw. fügt es der Queue hinzu\r\n"
				+ "!queue <Lied/Interpret> - Spielt das angegebene Lied bzw. fügt es zur Queue hinzu\r\n"
				+ "!queue add <YouTube-Link/Spotify-Link> - Spielt das angegebene Lied bzw. fügt es zur Queue hinzu\r\n"
				+ "!queue add <Youtube-Playlistlink/Spotfy-Playlistlink> - Spielt die angegebene Playlist bzw. fügt sie zur Queue hinzu\r\n"
				+ "!queue add <Spotify-Albumlink> - Spielt das angegebene Album bzw. fügt es der Queue hinzu\r\n"
				+ "!queue add <Lied/Interpret> - Spielt das angegebene Lied bzw. fügt es zur Queue hinzu\r\n"
				+ "!queue clear - Leert die Queue\r\n" + "Alias: !queue empty\r\n"
				+ "!queue jump <Anzahl> - Springt x Lieder in der Queue nach vorne\r\n"
				+ "!queue list - Zeigt die Queue\r\n" + "!queue shuffle - Shuffled die Queue\r\n" + "Alias: !q\r\n\n"
				+ "!skip - Springt zum nächsten Lied in der Queue\r\n" + "!stop - Stoppt den Bot\r\n" + "Alias: !leave"
				+ "')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Minigames:**\r\n"
				+ "!rps <@Spieler>: Spiele ''Schere, Stein, Papier'' gegen einen anderen Spieler\r\n"
				+ "!rps <Schere/Stein/Papier>: Spiele ''Schere, Stein, Papier'' gegen den Computer\r\n"
				+ "Alias: !rockpaperscissors\r\n\n"
				+ "!ttt <@Spieler>: Spiele ''TicTacToe'' gegen einen anderen Spieler')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**React:**\r\n"
				+ "!react <Nachrichten-ID> <Emote> - Reagiert auf die angegebenene Nachricht mit dem angegebenen Emote\r\n"
				+ "!react <Nachrichten-ID> Abstimmung - Reagiert auf die angegebene Nachricht mit 👍 und 👎')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Polls:**\r\n"
				+ "!poll start <Frage\\AW1\\AW2\\[AW3]\\...\\\\[AW10]> - Startet eine Umfrage mit den angegebenen Werten.\r\n"
				+ "!poll close <Poll-ID> - Stoppt die angegebene Umfrage')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Ping:**\r\n"
				+ "!ping - Gibt den Bot-Ping zu Discord zurück')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Private Voice Channels:**\r\n"
				+ "!pv lock <@User> [<@User>] [<@User>] - Sperrt bis zu drei User gleichzeitig von deinem privaten Kanal. Jeder dieser User wird aus deinem privaten Kanal entfernt\r\n"
				+ "!pv lockall - Sperrt alle User von deinem privaten Kanal. Jeder wird aus deinem privaten Kanal entfernt\r\n"
				+ "!pv unlock <@User> [<@User>] [<@User>] - Gibt deinen privaten Kanal für bis zu drei User gleichzeitig frei\r\n"
				+ "!pv unlockall - Gibt deinen privaten Kanal für alle User frei\r\n"
				+ "!pv userlimit <Limit> - Setzt dein Userlimit auf einen Wert zwischen 1 und 99. 0 entfernt das Userlimit\r\n"
				+ "Alias: !pvc / !privatevoice / !privatechannel / !privatevoicechannel')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Roles:**\r\n"
				+ "!role add <@User> <@Role> - Fügt dem angegebenen User die Rolle hinzu\r\n"
				+ "!role remove <@User> <@Role> - Entfernt dem angegebenen User die Rolle\r\n"
				+ "Alias: !roles add/remove <@User> <@Role>')");
		SQLite.onUpdate("INSERT INTO commands (command) VALUES ('**Zitate:**\r\n"
				+ "!zitat - Zeigt ein zufälliges Zitat\r\n" + "!zitat add <Zitat> - Fügt ein Zitat hinzu\r\n"
				+ "!zitat delete <ID> - Löscht das Zitat mit der ID\r\n"
				+ "!zitat list - Zeigt eine Liste aller Zitate mit ihren IDs\r\n"
				+ "!zitat channels add <Channel-ID> - Fügt den angegebenen Zitatechannel hinzu\r\n"
				+ "!zitat channels delete <Channel-ID> - Löscht den Zitatechannel mit der ID\r\n"
				+ "!zitat channels list - Zeigt eine List aller Zitate Channels\r\n" + "Alias: !zitate')");
	}
}