package net.darktree.virus.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class ClipboardHelper {

    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static void put(String string) {
        StringSelection selection = new StringSelection(string);
        clipboard.setContents(selection, selection);
    }

    public static String get() {
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        }catch(Exception e) {
            return "";
        }
    }

}
