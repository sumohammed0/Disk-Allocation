import java.util.*;
import java.io.*;

public class FileTable {
    FileTableEntry[] table;

    FileTable() {
        table = new FileTableEntry[12]; // will hold 12 entries
    }

    public void addEntry(String name, int firstBlock, int l) {
        for (int i = 0; i < 12; i++)
            if (table[i] == null) {
                table[i] = new FileTableEntry(name, firstBlock, l);
                break;
            }
    }

    public int getFirstBlock(String name) {
        for (int i = 0; i < 12; i++)
            if (table[i] != null && table[i].fileName.equals(name))
                return table[i].startBlock;
        return 0;
    }

    public int getLength(String name) { // Returns length of file.
        for (int i = 0; i < 12; i++)
            if (table[i] != null && table[i].fileName.equals(name))
                return table[i].length;
        return 0;
    }


    public void deleteTableEntry(String fileName) {
        for (int i = 0; i < 12; i++)
            if (table[i].fileName.equals(fileName)) {
                table[i] = null;
                break;
            }
    }

    public void displayFileTable() {
        boolean empty = true;

        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                empty = false;
                System.out.println(table[i].fileName + "\t" + table[i].startBlock + "\t" + table[i].length);
            }
        }

        if (empty) {
            System.out.println("File table is empty.");
        }
    }

    public boolean fileExist(String name) {
        for (int i = 0; i < 12; i++)
            if (table[i] != null && table[i].fileName.equals(name))
                return true;
        return false;
    }
}
