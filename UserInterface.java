import java.nio.file.Paths;
import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.*;
import java.nio.ByteBuffer;

public class UserInterface {
    DiskDrive disk = new DiskDrive();

    public void contiguousAllocation() throws IOException {
       // System.out.println("here2");

        boolean exit = false;
        int userChoice = 0;


        do {
            System.out.println("1) Display a file");
            System.out.println("2) Display the file table");
            System.out.println("3) Display the free space bitmap");
            System.out.println("4) Display a disk block");
            System.out.println("5) Copy a file from the simulation to a file on the real system");
            System.out.println("6) Copy a file from the real system to a file in the simulation");
            System.out.println("7) Delete a file");
            System.out.println("8) Exit");

            Scanner input = new Scanner(System.in);
            userChoice = input.nextInt();

            switch (userChoice) {
                case 1: // display file from disk
                    System.out.println("enter file name to display: ");
                    Scanner getnameScan = new Scanner(System.in);
                    String filename = getnameScan.nextLine();

                    int fb = ((FileTableBlock)disk.diskData[0]).fileTable.getFirstBlock(filename);
                    int l = ((FileTableBlock)disk.diskData[0]).fileTable.getLength(filename);

                    //System.out.println("first block: " + fb + "  length: " + l);

                    // iterate through disk array and get block data to display
                    for (int i = fb; i < (fb + l); i++) {
                        (disk.diskData[i]).displayBlockAsString();
                    }
                    System.out.println();
                    break;
                case 2: // display file table
                    ((FileTableBlock)disk.diskData[0]).fileTable.displayFileTable();
                    break;
                case 3: // display bitmap
                    ((BitmapBlock)disk.diskData[1]).bitmap.displayBitmap();;
                    break;
                case 4: // display chosen disk block
                    Scanner getnumScan = new Scanner(System.in);
                    System.out.println("Enter block number: ");
                    int b = getnumScan.nextInt();
                    (disk.diskData[b]).displayBlockAsString();
                    break;
                case 5: // copy file from disk
                    // get filename from user
                    Scanner outputnameScan = new Scanner(System.in);
                    System.out.println("Copy From: ");
                    String inputname = outputnameScan.nextLine();
                    System.out.println("Copy To: ");
                    String outputname = outputnameScan.nextLine();

                    File file1 = new File(outputname);

                    if (file1.createNewFile()) {
                        System.out.println("File created: " + file1.getName());

                        FileWriter Writer = new FileWriter(file1.getName());

                        int fb1 = ((FileTableBlock) disk.diskData[0]).fileTable.getFirstBlock(inputname);
                        int l1 = ((FileTableBlock) disk.diskData[0]).fileTable.getLength(inputname);

                        for (int i = fb1; i < (fb1 + l1); i++) {
                            Writer.write(new String(disk.diskData[i].getBlockContents(), StandardCharsets.UTF_8));
                        }


                        Writer.close();
                        System.out.println("Successfully wrote to " + outputname);

                    }
                    else {
                        System.out.println("File already exists.");
                    }

                    break;
                case 6: // copy file to disk
                    File file = null;
                    String realSysFile = "";
                    String diskFile = "";

                    // get filename from user
                    Scanner inputnameScan = new Scanner(System.in);

                    do {
                        System.out.print("Copy From: ");
                        realSysFile = inputnameScan.nextLine();
                        file = new File(realSysFile); // create file object for given filename
                        System.out.print("Copy To: ");
                        diskFile = inputnameScan.nextLine();
                        //System.out.println(diskFile);

                    }
                    // ensure name is within length
                    while (file.getName().length() > 8 || diskFile.length() > 8);

                    if (((FileTableBlock)disk.diskData[0]).fileTable.fileExist(realSysFile) || ((FileTableBlock)disk.diskData[0]).fileTable.fileExist(diskFile)) {
                        System.out.println("File already exists");
                    }
                    else {
                        copyToDiskCon(realSysFile, diskFile);
                        System.out.println(realSysFile + " copied.");
                    }
                    break;
                case 7: // delete file
                    System.out.println("Enter the file name to delete: ");
                    Scanner getnameDeleteScan = new Scanner(System.in);
                    String f = getnameDeleteScan.nextLine();

                    int sb = ((FileTableBlock)disk.diskData[0]).fileTable.getFirstBlock(f);
                    int len = ((FileTableBlock)disk.diskData[0]).fileTable.getLength(f);

                    //System.out.println("first block: " + fb + "  length: " + l);

                    // iterate through disk array and delete the block
                    for (int i = sb; i < (sb + len); i++) {
                        disk.deleteBlock(i);
                    }

                    // delete from table
                    ((FileTableBlock) disk.diskData[0]).fileTable.deleteTableEntry(f);

                    break;
                case 8:
                    exit = true;
                    break;
            }
        }
        while (!exit);
    }

    public void copyToDiskCon (String realF, String diskF) throws IOException {
        File systemFile = new File(realF);
        Path path = (systemFile).toPath();
        //System.out.println("filepath is: " + path);
        byte[] data = Files.readAllBytes(path);
        //System.out.println("data array is: " + data);

        int blocksNeeded = (int) Math.ceil((double) data.length / 512);
        int startBlock = ((BitmapBlock) disk.diskData[1]).bitmap.getFirstOfBestBlockSpace(blocksNeeded);
//        System.out.println("sb: " + startBlock);
//        System.out.println("blocks needed: " + blocksNeeded);

        Scanner fileReader = new Scanner(systemFile);
        int sizeCount = 0;
        byte[] sArr2;
        byte[] blockArr = new byte[0];

        for (int i = 0; i < blocksNeeded; i++) {

            int blockNumber = startBlock + i;

            int copyStart = i * 512; // start copying every 512 bytes
            // find size of data to be copied
            // check if the size of remaining data is smaller than 512 that way we don't have to create a full block for it
            int length = Math.min(512, data.length - copyStart);

            byte[] blockData = Arrays.copyOfRange(data, copyStart, copyStart + length);

            disk.writeToBlock(blockNumber, blockData);
            ((BitmapBlock) disk.diskData[1]).bitmap.updateBitmap(blockNumber); // set position to true for full
        }

        // add the file to the file table
        ((FileTableBlock)disk.diskData[0]).fileTable.addEntry(diskF, startBlock, blocksNeeded);



    }

    public void chainedAllocation() throws IOException {
        System.out.println("here3");

        boolean exit = false;
        int userChoice = 0;


        do {
            System.out.println("1) Display a file");
            System.out.println("2) Display the file table");
            System.out.println("3) Display the free space bitmap");
            System.out.println("4) Display a disk block");
            System.out.println("5) Copy a file from the simulation to a file on the real system");
            System.out.println("6) Copy a file from the real system to a file in the simulation");
            System.out.println("7) Delete a file");
            System.out.println("8) Exit");

            Scanner input = new Scanner(System.in);
            userChoice = input.nextInt();

            switch (userChoice) {
                case 1: // display file from disk
                    System.out.println("enter file name to display: ");
                    Scanner getnameScan = new Scanner(System.in);
                    String filename = getnameScan.nextLine();

                    int fb = ((FileTableBlock) disk.diskData[0]).fileTable.getFirstBlock(filename);
                    int l = ((FileTableBlock) disk.diskData[0]).fileTable.getLength(filename);

                    //System.out.println("first block: " + fb + "  length: " + l);

                    int nb = fb; // start at the first block

                    // iterate through disk array and get block data to display
                    for (int i = fb; i < (fb + l); i++) {
                        if (nb != -1) {
                            //System.out.println("next block: " + nb);
                            (disk.diskData[nb]).displayBlockAsString();
                            nb = (disk.diskData[i]).getNextBlock();
                        }
                    }
                    System.out.println();
                    break;

                case 2: // display file table
                    ((FileTableBlock) disk.diskData[0]).fileTable.displayFileTable();
                    break;

                case 3: // display bitmap
                    ((BitmapBlock) disk.diskData[1]).bitmap.displayBitmap();
                    break;

                case 4: // display chosen disk block
                    Scanner getnumScan = new Scanner(System.in);
                    System.out.println("Enter block number: ");
                    int b = getnumScan.nextInt();

                    // if the space is full display the block, else output error
                    if ((((BitmapBlock) disk.diskData[1]).bitmap.getBitmapBlock(b))){
                        (disk.diskData[b]).displayBlockAsString();
                    }
                    else {
                        System.out.println("error: block " + b + " is empty");
                    }

                    break;

                case 5: // copy file from disk
                    // get filename from user
                    Scanner outputnameScan = new Scanner(System.in);
                    System.out.println("Copy From: ");
                    String inputname = outputnameScan.nextLine();
                    System.out.println("Copy To: ");
                    String outputname = outputnameScan.nextLine();

                    File file1 = new File(outputname);

                    if (file1.createNewFile()) {
                        System.out.println("File created: " + file1.getName());

                        FileWriter Writer = new FileWriter(file1.getName());

                        int fb1 = ((FileTableBlock) disk.diskData[0]).fileTable.getFirstBlock(inputname);
                        int l1 = ((FileTableBlock) disk.diskData[0]).fileTable.getLength(inputname);

                        //System.out.println("first block: " + fb + "  length: " + l);

                        int nb1 = fb1; // start at the first block

                        // iterate through disk array and get block data to display
                        for (int i = fb1; i < (fb1 + l1); i++) {
                            if (nb1 != -1) {
                                //System.out.println("next block: " + nb);
                                Writer.write(new String(disk.diskData[nb1].getBlockContents(), StandardCharsets.UTF_8));
                                nb1 = (disk.diskData[i]).getNextBlock();
                            }
                        }

                        Writer.close();
                        System.out.println("Successfully wrote to " + outputname);

                    }
                    else {
                        System.out.println("File already exists.");
                    }


                    break;

                case 6: // copy file to disk
                    File file = null;
                    String realSysFile = "";
                    String diskFile = "";

                    // get filename from user
                    Scanner inputnameScan = new Scanner(System.in);

                    do {
                        System.out.print("Copy From: ");
                        realSysFile = inputnameScan.nextLine();
                        file = new File(realSysFile); // create file object for given filename
                        System.out.print("Copy To: ");
                        diskFile = inputnameScan.nextLine();
                        //System.out.println(diskFile);

                    }
                    // ensure name is within length
                    while (file.getName().length() > 8 || diskFile.length() > 8);

                    if (((FileTableBlock) disk.diskData[0]).fileTable.fileExist(realSysFile) || ((FileTableBlock) disk.diskData[0]).fileTable.fileExist(diskFile)) {
                        System.out.println("File already exists");
                    } else {
                        copyToDiskChained(realSysFile, diskFile);
                        System.out.println(realSysFile + " copied.");
                    }
                    break;

                case 7: // delete file
                    System.out.println("Enter the file name to delete: ");
                    Scanner getnameDeleteScan = new Scanner(System.in);
                    String f = getnameDeleteScan.nextLine();

                    int sb = ((FileTableBlock) disk.diskData[0]).fileTable.getFirstBlock(f);
                    int len = ((FileTableBlock) disk.diskData[0]).fileTable.getLength(f);

                    //System.out.println("first block: " + fb + "  length: " + l);

                    int nb1 = sb; // start at the first block

                    // iterate through disk array and get block data to display
                    for (int i = sb; i < (sb + len); i++) {
                        if (nb1 != -1) {
                            nb1 = (disk.diskData[i]).getNextBlock(); // store next block before deletion
                            (disk.diskData[i]).deleteBlock();
                        }
                    }

                    // delete from table
                    ((FileTableBlock) disk.diskData[0]).fileTable.deleteTableEntry(f);

                    break;
                case 8:
                    exit = true;
                    break;
            }
        }
        while (!exit);
    }

    public void copyToDiskChained (String realF, String diskF) throws IOException {
        File systemFile = new File(realF);
        Path path = (systemFile).toPath();
        //System.out.println("filepath is: " + path);
        byte[] data = Files.readAllBytes(path);
        //System.out.println("data array is: " + data);

        int blocksNeeded = (int) Math.ceil((double) data.length / 512);
        int[] emptyblocklist = ((BitmapBlock) disk.diskData[1]).bitmap.returnEmptyBlockNums(blocksNeeded);
//        System.out.println("sb: " + startBlock);
//        System.out.println("blocks needed: " + blocksNeeded);

        Scanner fileReader = new Scanner(systemFile);
        int sizeCount = 0;
        byte[] sArr2;
        byte[] blockArr = new byte[0];

        for (int i = 0; i < emptyblocklist.length; i++) {

            int blockNumber = emptyblocklist[i];

            int copyStart = i * 512; // start copying every 512 bytes
            // find size of data to be copied
            // check if the size of remaining data is smaller than 512 that way we don't have to create a full block for it
            int length = Math.min(512, data.length - copyStart);

            byte[] blockData = Arrays.copyOfRange(data, copyStart, copyStart + length);

            // if it's the last block then put -1 for next block
            if (i+1 == emptyblocklist.length) {
                disk.writeToBlockCH(blockNumber, blockData, -1);
            }
            else {
                disk.writeToBlockCH(blockNumber, blockData, emptyblocklist[i + 1]);
            }

            ((BitmapBlock) disk.diskData[1]).bitmap.updateBitmap(blockNumber); // set position to true for full
        }

        // add the file to the file table
        ((FileTableBlock)disk.diskData[0]).fileTable.addEntry(diskF, emptyblocklist[0], blocksNeeded);



    }

    public void indexedAllocation() {

    }

    public void copyToDiskIndexed(String realF, String diskF) throws IOException {
        File systemFile = new File(realF);
        Path path = (systemFile).toPath();
        //System.out.println("filepath is: " + path);
        byte[] data = Files.readAllBytes(path);
        //System.out.println("data array is: " + data);

        int blocksNeeded = (int) Math.ceil((double) data.length / 512);
        int[] emptyblocklist = ((BitmapBlock) disk.diskData[1]).bitmap.returnEmptyBlockNums(blocksNeeded);
//        System.out.println("sb: " + startBlock);
//        System.out.println("blocks needed: " + blocksNeeded);

        Scanner fileReader = new Scanner(systemFile);
        int sizeCount = 0;
        byte[] sArr2;
        byte[] blockArr = new byte[0];

        for (int i = 0; i < emptyblocklist.length; i++) {

            int blockNumber = emptyblocklist[i];

            int copyStart = i * 512; // start copying every 512 bytes
            // find size of data to be copied
            // check if the size of remaining data is smaller than 512 that way we don't have to create a full block for it
            int length = Math.min(512, data.length - copyStart);

            byte[] blockData = Arrays.copyOfRange(data, copyStart, copyStart + length);

            // if it's the last block then put -1 for next block
            if (i+1 == emptyblocklist.length) {
                disk.writeToBlockCH(blockNumber, blockData, -1);
            }
            else {
                disk.writeToBlockCH(blockNumber, blockData, emptyblocklist[i + 1]);
            }

            ((BitmapBlock) disk.diskData[1]).bitmap.updateBitmap(blockNumber); // set position to true for full
        }

        // add the file to the file table
        ((FileTableBlock)disk.diskData[0]).fileTable.addEntry(diskF, emptyblocklist[0], blocksNeeded);



    }


}
