package com.xx.platform.core.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import com.xx.platform.core.SearchContext;

public class StartPortListener implements Runnable {
	Logger log=Logger.getLogger("com.xx.platform.core.rpc.StartPortListener");
	private int port = SearchContext.SERVER_FLAG_PORT;

	public StartPortListener() {
	}

	public void run() {
		PrintWriter writer = null;
		Socket socket = null;
		BufferedReader br = null;
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			log.info("¿ªÆô¼àÌý¶Ë¿Ú  "+port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SearchContext.server8432 = server;
		while (true) {
			try {
				if(server.isClosed()){
					break;
				}
				socket = server.accept();
				if(socket.isClosed()){
					writer = new PrintWriter(socket.getOutputStream());
					writer.println(SearchContext.socketInfoPre);
					br = new BufferedReader(new InputStreamReader(socket
							.getInputStream()));
					writer.flush();
					br.readLine();
				}
			} catch (IOException e) {
//				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
//						e.printStackTrace();
					}
				}
				if (writer != null) {
					writer.close();
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
//						e.printStackTrace();
					}
				}
			}

		}
	}
}
