package tst.datatypes;

import src.datatypes.Key;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestKey {

    public static void testKey() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        testKeyMd5();
        //testKeyConstructorWithInput(); //TODO reparer ce test
        testKeyCompareMethod();
        System.out.println("testKey Passed [✔]");
    }

    public static void testKeyConstructorWithInput() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String input = "test";
        String expectedHash = "098f6bcd4621d373cade4e832627b4f6"; // MD5 for "test"
        Key key = new Key(input);

        // Accessing private field _hash using reflection
        Method hashGetter = Key.class.getDeclaredMethod("getHash");
        hashGetter.setAccessible(true);
        String hash = (String) hashGetter.invoke(key);

        assert hash.equals(expectedHash) : "Key constructor failed to generate or store the correct MD5 hash for input 'test'.";
        System.out.println("testKeyConstructorWithInput Passed");
    }

    public static void testKeyCompareMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Key key1 = new Key("test");
        Key key2 = new Key("test");
        Key key3 = new Key("diff");

        // Test for equality
        assert Key.compare(key1, key2) : "Key.compare failed to identify equal hashes.";

        // Test for inequality
        assert !Key.compare(key1, key3) : "Key.compare failed to identify unequal hashes.";

        System.out.println("testKeyCompareMethod Passed");
    }

    public static void testKeyMd5() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        testMd5HashWithKnownValues();
        testMd5HashWithEmptyString();
        testMd5HashWithNull();

        System.out.println("testKeyMd5 Passed");
    }

    public static void testMd5HashWithKnownValues() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Key key = new Key(""); // Dummy Key object to access the method

        String input = "hello";
        String expectedHash = "5d41402abc4b2a76b9719d911017c592"; // MD5 for "hello"
        assert invokeMd5HashMethod(key, input).equals(expectedHash) : "MD5 hash mismatch for known value 'hello'.";

        input = "world";
        expectedHash = "7d793037a0760186574b0282f2f435e7"; // MD5 for "world"
        assert invokeMd5HashMethod(key, input).equals(expectedHash) : "MD5 hash mismatch for known value 'world'.";
    }

    public static void testMd5HashWithEmptyString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Key key = new Key(""); // Dummy Key object to access the method

        String input = "";
        String expectedHash = "d41d8cd98f00b204e9800998ecf8427e"; // MD5 for empty string
        assert invokeMd5HashMethod(key, input).equals(expectedHash) : "MD5 hash mismatch for empty string.";
    }

    public static void testMd5HashWithNull() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Key key = new Key(""); // Dummy Key object to access the method

        try {
            invokeMd5HashMethod(key, null);
            assert false : "MD5 hash method did not handle null input as expected.";
        } catch (Exception e) {
            // Expected an exception or null handling
            assert true; // Pass the test if an exception is thrown or null is handled
        }
    }

    private static String invokeMd5HashMethod(Key key, String input) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method md5HashMethod = Key.class.getDeclaredMethod("md5Hash", String.class);
        md5HashMethod.setAccessible(true);
        return (String) md5HashMethod.invoke(key, input);
    }
}
