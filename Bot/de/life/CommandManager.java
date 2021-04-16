package de.life;

import java.util.concurrent.ConcurrentHashMap;

import de.life.commands.AnnounceCommand;
import de.life.commands.AutotriggerCommand;
import de.life.commands.BanCommand;
import de.life.commands.ClearCommand;
import de.life.commands.CoinflipCommand;
import de.life.commands.ColorCommand;
import de.life.commands.CommandsCommand;
import de.life.commands.HubsCommand;
import de.life.commands.IconUpdateCommand;
import de.life.commands.KickCommand;
import de.life.commands.LogsCommand;
import de.life.commands.MemberInfoCommand;
import de.life.commands.MemesCommand;
import de.life.commands.PollCommand;
import de.life.commands.PrivateChannelCommand;
import de.life.commands.RPSHerausfordernCommand;
import de.life.commands.ReactCommand;
import de.life.commands.ZitateCommand;
import de.life.interfaces.ServerCommand;
import de.life.music.QueueCommand;
import de.life.music.LoopCommand;
import de.life.music.PauseCommand;
import de.life.music.PlayCommand;
import de.life.music.ResumeCommand;
import de.life.music.ShuffleCommand;
import de.life.music.StopCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class CommandManager {

	public ConcurrentHashMap<String, ServerCommand> commands;

	public CommandManager() {
		this.commands = new ConcurrentHashMap<>();

		this.commands.put("announce", new AnnounceCommand());
		this.commands.put("trigger", new AutotriggerCommand());
		this.commands.put("triggers", new AutotriggerCommand());
		this.commands.put("autotrigger", new AutotriggerCommand());
		this.commands.put("autotriggers", new AutotriggerCommand());
		this.commands.put("ban", new BanCommand());
		this.commands.put("bicmac", new BanCommand());
		this.commands.put("clear", new ClearCommand());
		this.commands.put("c", new ClearCommand());
		this.commands.put("coinflip", new CoinflipCommand());
		this.commands.put("flip", new CoinflipCommand());
		this.commands.put("cf", new CoinflipCommand());
		this.commands.put("commands", new CommandsCommand());
		this.commands.put("help", new CommandsCommand());
		this.commands.put("color", new ColorCommand());
		this.commands.put("hub", new HubsCommand());
		this.commands.put("hubs", new HubsCommand());
		this.commands.put("icon", new IconUpdateCommand());
		this.commands.put("kick", new KickCommand());
		this.commands.put("log", new LogsCommand());
		this.commands.put("logs", new LogsCommand());
		this.commands.put("loop", new LoopCommand());
		this.commands.put("memberinfo", new MemberInfoCommand());
		this.commands.put("userinfo", new MemberInfoCommand());
		this.commands.put("meme", new MemesCommand());
		this.commands.put("memes", new MemesCommand());
		this.commands.put("pause", new PauseCommand());
		this.commands.put("play", new PlayCommand());
		this.commands.put("poll", new PollCommand());
		this.commands.put("pv", new PrivateChannelCommand());
		this.commands.put("pvc", new PrivateChannelCommand());
		this.commands.put("privatevoice", new PrivateChannelCommand());
		this.commands.put("privatechannel", new PrivateChannelCommand());
		this.commands.put("privatevoicechannel", new PrivateChannelCommand());
		this.commands.put("q", new QueueCommand());
		this.commands.put("queue", new QueueCommand());
		this.commands.put("react", new ReactCommand());
		this.commands.put("resume", new ResumeCommand());
		this.commands.put("rockpaperscissors", new RPSHerausfordernCommand());
		this.commands.put("rps", new RPSHerausfordernCommand());
		this.commands.put("shuffle", new ShuffleCommand());
		this.commands.put("stop", new StopCommand());
		this.commands.put("zitat", new ZitateCommand());
		this.commands.put("zitate", new ZitateCommand());
	}

	public boolean perform(String command, Member m, MessageChannel channel, Message message) {

		ServerCommand cmd;
		if ((cmd = this.commands.get(command.toLowerCase())) != null) {
			cmd.performCommand(m, channel, message);
			return true;
		}
		return false;
	}

}