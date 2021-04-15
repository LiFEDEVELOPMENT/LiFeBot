package de.life.classes;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class DeleteMessages {

	public void delete(MessageChannel channel, int amount) {
		List<Message> messages = new ArrayList<>();
		int i = amount;

		for (Message message : channel.getIterableHistory().cache(false)) {
			if (!message.isPinned()) {
				messages.add(message);
			} else {
				i++;
			}
			if (--i <= 0)
				break;
		}

		channel.purgeMessages(messages);
	}
	
	public void delete(MessageChannel channel, int amount, Member member) {
		List<Message> messages = new ArrayList<>();
		int i = amount;
		
		for(Message message : channel.getIterableHistory().cache(false)) {
			if (!message.isPinned()) {
				if(message.getAuthor().equals(member.getUser())) {
					messages.add(message);
				} else {
					i++;
				}
			} else {
				i++;
			}
			if (--i <= 0) {
				break;
			}
			if(i >= 1000) {
				break;
			}
		}
	}
}