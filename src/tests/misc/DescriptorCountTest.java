package tests.misc;

import foundation.Helpers;

public class DescriptorCountTest {
    public static void main(String[] args) {
        final var str1 = "%d %d%deee_%%d";
        final var count1 = Helpers.formatDescriptorCount(str1);
        System.out.println(count1 == 4);
    }
}
