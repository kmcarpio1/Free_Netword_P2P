package tst.datatypes;

import java.lang.reflect.Field;
import java.util.Arrays;

import src.datatypes.IpPort;

public class TestPort {
    public static void testPort() throws NoSuchFieldException, IllegalAccessException, Exception {
        System.out.println("Test constructor IpPort...");
        try{
            IpPort ipPort = new IpPort("255.255.10.102:1234");

            // Accessing package-private fields using reflection
            Field ipField = IpPort.class.getDeclaredField("ip");
            ipField.setAccessible(true);
            byte[] ip = (byte[]) ipField.get(ipPort);

            assert Arrays.equals(ip, new byte[]{-1, -1, 10, 102});

            Field portField = IpPort.class.getDeclaredField("port");
            portField.setAccessible(true);
            int port = (int) portField.get(ipPort);

            assert port == 10;

            ipPort = new IpPort("255.255.10.102", 2222);
            port = (int) portField.get(ipPort);
            assert port == 2222;

            try {
                ipPort = new IpPort("255.255.10.102.255:1234");
                // Continuez ici avec le code si aucune exception n'est levée
            } catch (IllegalArgumentException e) {
                System.out.println("Une NullPointerException a été attrapée : " + e.getMessage());
                try {
                    ipPort = new IpPort("255.255.10:1234");
                    // Continuez ici avec le code si aucune exception n'est levée
                } catch (IllegalArgumentException d) {
                    System.out.println("Une NullPointerException a été attrapée : " + d.getMessage());
                    System.out.println("[✔]");
                    System.out.println("");
                }           
            }

            
        } catch(Exception e){
            System.out.println("On a une erreur dans un des appels de fonction");
            System.out.println("");
        }
    }
}
