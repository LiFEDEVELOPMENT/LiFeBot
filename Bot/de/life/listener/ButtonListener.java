package de.life.listener;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonListener extends ListenerAdapter {

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (event.getButton().getId().equals("cancel")) {
			if (event.getTextChannel().getParent() != null
					&& event.getTextChannel().getParent().getChannels().size() == 1)
				event.getTextChannel().getParent().delete().queue();
			event.getTextChannel().delete().queue();
		} else if (event.getButton().getId().startsWith("llul")) {
			
		}
	}
}