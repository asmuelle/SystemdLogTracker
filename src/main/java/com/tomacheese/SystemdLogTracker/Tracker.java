package com.tomacheese.SystemdLogTracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Tracker extends Thread {
	List<String> command;

	public Tracker(List<String> command) {
		this.command = command;
	}

	@Override
	public void run() {
		Process p = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);

			System.out.println("Tracker start.");
			p = pb.start();

			InputStream is = p.getInputStream();
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader buff = new BufferedReader(reader);

			String line;
			while ((line = buff.readLine()) != null) {
				Main.queue.add(line);
			}
			new Sender().run();
			p.waitFor();
			int exitCode = p.exitValue();
			System.out.println("Tracker exited. ExitCode: " + exitCode);
			System.exit(1);
			//System.out.println("Tracker restart...");
			//new Tracker(command).start();
		} catch (Exception e) {
			System.out.println("Tracking failed: " + e.getMessage());
			e.printStackTrace();
			if (p != null && p.isAlive()) {
				p.destroy();
			}
			System.exit(1);
		}
	}
}