package tst;

import tst.datatypes.*;
import tst.utils.*;

public class Test{

    public static void main(String[] args){
        System.out.println("Main test launched ! ");
        System.out.println("");

        try {
            TestPort.testPort();
            TestKey.testKey();
            TestFileDescription.testFileDescription();
            TestBuffermap.testBuffermap();
            TestFileChecker.testFileChecker();

            System.out.println("All tests passed successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
}