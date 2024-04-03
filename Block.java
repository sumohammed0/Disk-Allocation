import java.util.*;
import java.io.*;
import java.nio.charset.*;

public class Block {
    byte[] blockContents;
    int nextBlock;

    Block() {
        blockContents = new byte[512]; // each block is 512 bytes
        nextBlock = -1;
    }

    public void writeBlock(byte[] data) {
        blockContents = Arrays.copyOf(data, data.length);
    }

    public void writeBlockChained(byte[] data, int nextBlock) {
        blockContents = Arrays.copyOf(data, data.length);
        this.nextBlock = nextBlock;
    }

    public void deleteBlock() {
        for (int i = 0; i < blockContents.length; i++)
            blockContents[i] = 0; // reset everything to 0 to delete
    }

    public void deleteBlockChained() {
        for (int i = 0; i < blockContents.length; i++) {
            blockContents[i] = 0; // reset everything to 0 to delete
        }
        nextBlock = -1; // remove next block
    }

    public void displayBlock() {
        //System.out.println(Arrays.toString(blockContents));

        for (int i = 0; i < 512; i++){
            System.out.print(blockContents[i]);

            if (i!=0 && i % 32 == 0)
                System.out.println();
        }

        System.out.println();
    }

    public void displayBlockAsString() {

        System.out.print(new String(blockContents, StandardCharsets.UTF_8));
        //System.out.print(text);

    }

    public byte[] getBlockContents() {
        return blockContents;
    }

    public int getNextBlock() {
        return nextBlock;
    }
}
