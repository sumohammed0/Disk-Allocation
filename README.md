# Disk-Allocation
This project demonstrates two different file allocation methods: contiguous and chained

The project consists of three main objects:  
User interface, File System, and Disk Drive.

The disk stores its contents in an array of 256 blocks of 512 bytes each. The first block is for the file allocation table. The second block is a bitmap for free space management.  The remaining blocks hold data for the files. The disk object only knows how to read and write blocks by block number (array index).  The file system object stores and retrieves files on the disk using the file allocation method chosen by the user. Space is allocated using the first available approach.  The user interface object provides a menu as shown below and invokes methods to support the choices  

 1) Display a file  
 2) Display the file table  
 3) Display the free space bitmap  
 4) Display a disk block  
 5) Copy a file from the simulation to a file on the real system  
 6) Copy a file from the real system to a file in the simulation  
 7) Delete a file  
 8) Exit  

Compile: Javac *.java
Run:  Java Main "contiguous"  or Java Main "chained"

note: 
the filename entered for case 6 can only be up to 8 characters in length including the .txt extension 

