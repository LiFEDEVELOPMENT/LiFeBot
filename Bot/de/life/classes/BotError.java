package de.life.classes;

public enum BotError {
			SYNTAX("Da stimmt wohl etwas mit deiner Commandsyntax nicht, schau doch mal mit !commands nach, wie du den Command richtig benutzt"),
			HIERACHY("Du scheinst nicht mit diesem Member interagieren zu können"),
			HIERACHY_BOT("Der Bot scheint nicht mit diesem Member interagieren zu können"),
			PERMISSION_DEV("Du hast nicht die Berechtigung, dies zu tun. Du musst ein Entwickler dieses Bots sein"),
			PERMISSION_ADMIN("Du hast nicht die Berechtigung, dies zu tun. Du brauchst die Berechtigung 'Permission.ADMINISTRATOR'"),
			PERMISSION_KICK("Du hast nicht die Berechtigung, dies zu tun. Du brauchst die Berechtigung 'Permission.KICK_MEMBERS'"),
			PERMISSION_BAN("Du hast nicht die Berechtigung, dies zu tun. Du brauchst die Berechtigung 'Permission.BAN_MEMBERS'"),
			PERMISSION_MANAGE_SERVER("Du hast nicht die Berechtigung, dies zu tun. Du brauchst die Berechtigung 'Permission.MANAGE_SERVER'"),
			PERMISSION_MANAGE_CHANNEL("Du hast nicht die Berechtigung, dies zu tun. Du brauchst die Berechtigung 'Permission.MANAGE_CHANNEL'"),
			PERMISSION_LINKS("Du hast nicht die Berechtigung, dies zu tun. Du brauchst die Berechtigung 'Permission.MESSAGE_EMBED_LINKS'");
			
			
			private final String error;
			
			BotError(String error) {
				this.error = error;
			}
			
			public String getError() {
				return this.error;
			}
}