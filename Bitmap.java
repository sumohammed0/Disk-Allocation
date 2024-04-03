import java.util.*;
import java.io.*;

public class Bitmap {
    boolean bitmap[];

    Bitmap() {
        bitmap = new boolean[256];

        Arrays.fill(bitmap, false); // initialize the entire array with false for empty

        bitmap[0] = true;
        bitmap[1] = true;

    }

    public void updateBitmap(int blockNum) {
        //System.out.println("UPDATED BITMAP LOCATION");
        bitmap[blockNum] = true;
    }

    public void deleteFromBitmap(int blockNum) {
        //System.out.println("UPDATED BITMAP LOCATION");
        bitmap[blockNum] = false;
    }

    public void displayBitmap() {
        for (int i = 1; i <= 256; i++) {
            if (!bitmap[i-1]) {
                System.out.print("0");
            }
            else {
                System.out.print("1");
            }

            if ( i % 32 == 0) {
                System.out.println();
            }
        }

        System.out.println();
    }

    // returns the first block num of the space that will fit the file data
    public int getFirstOfBestBlockSpace(int numBlocksNeeded) {
        int start = 2; // start at 2 since the first two spaces are filled with the file table and bitmap
        int count = numBlocksNeeded; // keep track of how many blocks we need, decrements

        for (int i = 2; i < 256; i++) {
            if (!bitmap[i]) { // returns true if bitmap full so if bitmap space not full
                count--;
            }
            // if it is full then increment the start pointer to check space after
            else {
                start++;
            }

            // reached required number of blocks to store file
            if (count == 0) {
                break;
            }
        }

        return start;
    }

    public int[] returnEmptyBlockNums (int numBlocksNeeded) {
        ArrayList<Integer> empties = new ArrayList<>();
        int [] emptyBlocks = new int[numBlocksNeeded];

        // find all the empty blocks and add the nums to the array list
        for (int i = 0; i < 256; i++) {
            if (!bitmap[i]) {
                empties.add(i);
            }
        }

        Random random = new Random();

        for (int i = 0; i < numBlocksNeeded; i++) {
            int n = empties.get(random.ints(0, empties.size()).findFirst().getAsInt());
            emptyBlocks[i] = n;
        }

        return emptyBlocks;
    }

    public boolean getBitmapBlock(int b) {
        return bitmap[b];
    }
}
