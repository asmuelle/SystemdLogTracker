package com.tomacheese.SystemdLogTracker;

import java.util.LinkedList;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class Sender extends TimerTask {

    public Sender(String pattern) {
        this.pattern=Pattern.compile(pattern);
    }

    private Pattern pattern;

	@Override
	public void run() {
		if (!Main.queue.isEmpty()) {
			LinkedList<String> messages = new LinkedList<>();
			while (true) {
				String line = Main.queue.poll();
				if (line == null || pattern.matcher(line).matches() )
					break;
				if (!messages.isEmpty() && (String.join("\n", messages).length() + line.length()) >= 1900) {

					Main.sendMessage("```" + String.join("\n", messages) + "```");
					messages.clear();
				}
				if (line.length() >= 1900) {
					if (!messages.isEmpty()) {
						Main.sendMessage("```" + String.join("\n", messages) + "```");
						messages.clear();
					}
					int count = line.length() / 1900;
					for (int i = 0; i <= count; i++) {
						int end = Math.min((i + 1) * 1900, line.length());
						String text = line.substring(i * 1900, end);
						Main.sendMessage("```" + text + "```");
					}
				}
				messages.add(line);
			}
			if (messages.size() > 0) {
				Main.sendMessage("```" + String.join("\n", messages) + "```");
			}
		}
	}
}
