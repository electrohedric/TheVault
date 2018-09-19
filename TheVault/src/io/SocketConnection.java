package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import guis.SetupScreen;

public class SocketConnection implements Runnable {
	
	public static boolean started = false;
	public static ReceiveMode receiving;
	public static String gameID;
	
	public static void connect() {
		if(!started) {
			started = true;
			receiving = ReceiveMode.GAME_ID;
			gameID = "";
			new Thread(new SocketConnection()).start();
		}
	}
	
	public void onFail() {
		Log.err("Socket crashed!"); // TODO allow retry button
		started = false;
	}
	
	public static HttpURLConnection sendRequest(String file, String msg) throws IOException {
		URL url = new URL("HTTP", "localhost", 4096, file);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true); // POST mode
		con.setConnectTimeout(5);
		con.setReadTimeout(0);
		con.setRequestProperty("Accept", "text/event-stream");
		if(msg != null) {
			OutputStream output = con.getOutputStream();
			output.write(msg.getBytes("UTF-8"));
		}
		con.connect();
		return con;
	}
	
	public static HttpURLConnection sendRequest(String file) throws IOException {
		return sendRequest(file, null);
	}
	
	@Override
	public void run() {
		HttpURLConnection con = null;
		BufferedReader in = null;
		try {
			Log.log("initializing connection...");
			con = sendRequest("/add-game-client");
			int status = con.getResponseCode();
			if(status != 200) {
				onFail();
				return;
			}
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while(started) {
				String inputLine;
				while((inputLine = in.readLine()) == null && started) {
					Thread.sleep(200);
				}
				if(!started) {
					Log.log("SOCKET > quit loop");
					break;
				}
				int dataIndex = inputLine.indexOf("data:");
				if(dataIndex == -1)  // we only care about 'data' lines
					continue;
				String data = inputLine.substring(dataIndex + 5).trim();
				switch(receiving) {
				case GAME_ID:
					gameID = data;
					Log.log("My game ID is '" + gameID); // TODO display this number
					receiving = ReceiveMode.DEVICES;
					break;
				case DEVICES:
					String[] elements = data.split("\\|"); // get the id and color. split on '|' (escaped)
					if(elements.length == 2) {
						String clientID = elements[0];
						String clientColor = elements[1];
						Log.log("ID=" + clientID + " wants color " + clientColor);
						HttpURLConnection permitCon = sendRequest("/permit-client", String.format("client-id=%s&game-id=%s", clientID, gameID)); // permit this client
						if(permitCon.getResponseCode() == 200) {
							SetupScreen.getInstance().choosePawn(clientColor.toLowerCase());
							// FIXME link the client with their pawn to send the proper updates
						}
						permitCon.disconnect();
					} // otherwise ignore
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			onFail();
		} catch (IOException e) {
			e.printStackTrace();
			onFail();
		} catch (InterruptedException e) {
			e.printStackTrace();
			onFail();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(con != null)
				con.disconnect();
			started = false;
			Log.log("Socket disconnected");
		}
	}
	
	private static enum ReceiveMode {
		GAME_ID, DEVICES
	}
}
