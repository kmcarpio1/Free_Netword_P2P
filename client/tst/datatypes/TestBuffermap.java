package tst.datatypes;

import src.datatypes.BufferMap;
import java.util.Random;

public class TestBuffermap {

    public static void testBuffermap() {
        testBmap2Hex();
        testHex2Bmap();
    }
    
    public static void testBmap2Hex(){
        BufferMap btrue = new BufferMap(8, 1, new boolean[]{true, true, true, true, true, true, true, true});
        String strue = btrue.toString();
        assert btrue.equals(new BufferMap(strue)) : "Buffermap to String to Buffermap breaks equality in : true case";

        BufferMap bfalse = new BufferMap(8, new boolean[]{false, false, false, false, false, false, false, false});
        String sfalse = bfalse.toString();
        assert bfalse.equals(new BufferMap(sfalse)) : "Buffermap to String to Buffermap breaks equality in : false case";

        Random random = new Random();
        for (int run=0 ; run<20 ; run++){
            boolean[] randomArray = new boolean[Math.abs(random.nextInt())%100];
            for (int i = 0; i < randomArray.length; i++) {
                randomArray[i] = random.nextBoolean();
            }
            BufferMap b1 = new BufferMap(randomArray.length, randomArray);
            String s1 = b1.toString();
            assert b1.equals(new BufferMap(s1)) : "Buffermap to String to Buffermap breaks equality in : rand case";
        }
        System.out.println("Bmap2Hex test passed");

    }

    public static void testHex2Bmap(){
        String strue = "255/8";
        BufferMap btrue = new BufferMap(strue);
        assert strue.equals(btrue.toString()) : "String to Buffermap to String breaks equality in : true case";

        String sfalse = "0";
        BufferMap bfalse = new BufferMap(strue);
        assert sfalse.equals(bfalse.toString()) : "String to Buffermap to String breaks equality in : false case";

        Random random = new Random();
        for (int run=0 ; run<20 ; run++){
            String hex = "";
            for (int i=0 ; i<Math.abs(random.nextInt())%10+1 ; i++){
                hex += String.format("%08x", random.nextInt());
            }
            hex += "/" + Integer.parseInt(Integer.toString(hex.length()*4), 16);
            BufferMap bmap = new BufferMap(hex);
            assert hex.equals(bmap.toString());
        }
        System.out.println("Hex2Bmap test passed");

    }
}
