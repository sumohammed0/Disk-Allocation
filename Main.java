import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // get file allocation method from command line
        String FAmethod = args[0];

        UserInterface user = new UserInterface();


        if (FAmethod.equals("contiguous")) {
            user.contiguousAllocation();
        }

        if (FAmethod.equals("chained")) {
            user.chainedAllocation();
        }

        if (FAmethod.equals("indexed")) {
            user.indexedAllocation();
        }
    }
}