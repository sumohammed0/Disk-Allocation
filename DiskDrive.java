import java.util.*;
import java.io.*;

public class DiskDrive {
    Block diskData[];

    DiskDrive() {
        diskData = new Block[256]; // 256 blocks of 512 bytes
        diskData[0] = new FileTableBlock(); // file table
        diskData[1] = new BitmapBlock(); // bitmap

        for (int i = 2; i < 256; i++) {
            diskData[i] = new Block();
        }
    }

    public byte[] readBlock(int blockNum) {
        return (diskData[blockNum]).blockContents;
    }

    public void writeToBlock(int blockNum, byte[] dataToWrite) {
        (diskData[blockNum]).writeBlock(dataToWrite);
    }

    public void writeToBlockCH(int blockNum, byte[] dataToWrite, int next) {
        (diskData[blockNum]).writeBlockChained(dataToWrite, next);
    }

    public void deleteBlock(int blockNum) {
        (diskData[blockNum]).deleteBlock();
        ((BitmapBlock) diskData[1]).bitmap.deleteFromBitmap(blockNum);

    }


}
