import java.util.*;
import java.io.*;

public class FileTableEntry {
    String fileName;
    int startBlock;
    int length;

    FileTableEntry(String fn, int sb, int l) {
        this.fileName = fn;
        this.startBlock = sb;
        this.length = l;
    }


}
