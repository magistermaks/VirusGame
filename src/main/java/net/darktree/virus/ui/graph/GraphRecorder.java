package net.darktree.virus.ui.graph;

import net.darktree.virus.Main;
import net.darktree.virus.logger.Logger;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Utils;
import processing.data.Table;

import java.util.ArrayList;

public class GraphRecorder {

	private final ArrayList<GraphFrame> frames = new ArrayList<>();


	public void append(GraphFrame frame) {
		frames.add(frame);
	}

	public GraphRecorder dump() {
		if (frames.size() > 0) {
			Table table = new Table();
			addColumns(table);

			int i = 0;
			for (GraphFrame frame : frames) {
				frame.appendToTable(table, i++);
			}

			frames.clear();
			String path = "export/" + Helpers.today() + ".csv";
			Main.applet.saveTable(table, path);
			Logger.info("Saved recorded graph to: '" + path + "'");
		}

		return null;
	}

	private void addColumns(Table table) {
		table.addColumn("id");
		table.addColumn("wastes");
		table.addColumn("viruses");
		table.addColumn("cells");
	}

	public int getSize() {
		return frames.size();
	}

	public int getColor() {
		return (getSize() % 2 == 0) ? Utils.color(255, 0, 0) : Utils.color(100, 0, 0);
	}

}
