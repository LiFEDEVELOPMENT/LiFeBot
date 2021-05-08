package de.life.music.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.hc.core5.http.ParseException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import de.life.GlobalVariables;
import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import de.life.music.MusicUtil;
import de.life.music.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PlayCommand implements ServerCommand {

	private static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(GlobalVariables.spotifyClientID)
			.setClientSecret(GlobalVariables.spotifyClientSecret).build();
	private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		addQueue(m, channel, message.getContentDisplay());
	}

	public static void addQueue(Member m, MessageChannel channel, String message) {
		clientCredentials_Sync();
		String[] args = message.split(" ");

		if (!joinChannel(m)) {
			return;
		}

		args = Arrays.copyOfRange(args, 1, args.length);
		String link = String.join(" ", args);

		if (!isUrl(link) && args.length <= 1) {
			EmbedMessageBuilder.sendMessage("Musik",
					"Zu dieser Suche habe ich leider nichts gefunden - Gib mir bitte noch ein Wort :)", Color.RED,
					MusicUtil.getMusicChannel(m.getGuild()), 10);
			return;
		}

		if (link.startsWith("https://open.spotify.com")) {
			switch (link.charAt(25)) {
			case 't':
				String[] tTemp = args[0].split("track");
				String[] tTemp2 = tTemp[1].split("si");
				String tID = tTemp2[0].substring(1, tTemp2[0].length() - 1);
				link = trackInfo(tID);

				break;
			case 'p':
				String[] pTemp = args[0].split("playlist");
				String[] pTemp2 = pTemp[1].split("si");
				String pID = pTemp2[0].substring(1, pTemp2[0].length() - 1);
				playlistInfo(pID, m, channel);

				break;
			case 'a':
				String[] aTemp = args[0].split("album");
				String[] aTemp2 = aTemp[1].split("si");
				String aID = aTemp2[0].substring(1, aTemp2[0].length() - 1);
				albumInfo(aID, m, channel);

				break;
			}
		}

		if (!isUrl(link)) {
			link = "ytsearch:" + link + " lyric video";
		}

		PlayerManager.getInstance().loadAndPlay(channel, link, m);
		MusicUtil.updateChannel(m, channel);
	}

	private static String trackInfo(String id) {
		String songName = "";
		String artistNames = "";

		try {
			Track track = spotifyApi.getTrack(id).build().execute();

			songName = track.getName();
			ArtistSimplified[] artists = track.getArtists();
			for (ArtistSimplified artist : artists) {
				artistNames.concat(artist.getName() + " ");
			}

		} catch (IOException | SpotifyWebApiException | ParseException e) {
			System.out.println("Error: " + e.getMessage());
		}

		return songName + " " + artistNames;
	}

	private static void playlistInfo(String id, Member m, MessageChannel channel) {
		String songName = "";
		String artistNames = "";

		try {
			for (PlaylistTrack track : spotifyApi.getPlaylistsItems(id).build().execute().getItems()) {
				Track song = spotifyApi.getTrack(track.getTrack().getId()).build().execute();

				songName = song.getName();
				ArtistSimplified[] artists = song.getArtists();
				for (ArtistSimplified artist : artists) {
					artistNames.concat(artist.getName() + " ");
				}

				PlayerManager.getInstance().loadAndPlay(channel,
						"sytsearch: " + songName + " " + artistNames + " lyric video", m);
			}

			MusicUtil.updateChannel(m, channel);
			EmbedMessageBuilder.sendMessage("Musik", "Playlist hinzugefügt", Color.ORANGE, channel);
		} catch (IOException | SpotifyWebApiException | ParseException e) {
		}
	}

	private static void albumInfo(String id, Member m, MessageChannel channel) {
		String songName = "";
		String artistNames = "";

		try {
			for (TrackSimplified track : spotifyApi.getAlbumsTracks(id).build().execute().getItems()) {
				Track song = spotifyApi.getTrack(track.getId()).build().execute();

				songName = song.getName();
				ArtistSimplified[] artists = song.getArtists();
				for (ArtistSimplified artist : artists) {
					artistNames.concat(artist.getName() + " ");
				}

				PlayerManager.getInstance().loadAndPlay(channel,
						"sytsearch: " + songName + " " + artistNames + " lyric video", m);
			}

			MusicUtil.updateChannel(m, channel);
			EmbedMessageBuilder.sendMessage("Musik", "Album hinzugefügt", Color.ORANGE, channel);
		} catch (IOException | SpotifyWebApiException | ParseException e) {
		}
	}

	private static boolean joinChannel(Member m) {
		if (!m.getVoiceState().inVoiceChannel()) {
			return false;
		}

		if (m.getGuild().getSelfMember().getVoiceState().inVoiceChannel()
				&& m.getGuild().getSelfMember().getVoiceState().getChannel() != m.getVoiceState().getChannel()) {
			return false;
		}

		m.getGuild().getAudioManager().openAudioConnection(m.getVoiceState().getChannel());
		return true;
	}

	private static boolean isUrl(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	private static void clientCredentials_Sync() {
		try {
			final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
			spotifyApi.setAccessToken(clientCredentials.getAccessToken());
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}