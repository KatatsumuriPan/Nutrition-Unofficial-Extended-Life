package ca.wescook.nutrition.utility;

import org.apache.commons.lang3.math.NumberUtils;

public class CompareUtil {

    // From https://stackoverflow.com/questions/58971386/how-to-compare-string-which-contains-integers-in-java
    public static int compareTo(String comp1, String comp2) {
        // If any value has 0 length it means other value is bigger
        if (comp1.length() == 0) {
            if (comp2.length() == 0) {
                return 0;
            }
            return -1;
        } else if (comp2.length() == 0) {
            return 1;
        }
        // Check if first string is digit
        if (NumberUtils.isDigits(comp1)) {
            int val1 = Integer.parseInt(comp1);
            // Check if second string is digit
            if (NumberUtils.isDigits(comp2)) {
                // If both strings are digits then we only need to use Integer compare method
                int val2 = Integer.parseInt(comp2);
                return Integer.compare(val1, val2);
            } else {
                // If only first string is digit we only need to use String compareTo method
                return comp1.compareTo(comp2);
            }

        } else { // If both strings are not digits

            int minVal = Math.min(comp1.length(), comp2.length()), sameCount = 0;

            // Loop through two strings and check how many strings are same
            for (int i = 0; i < minVal; i++) {
                char leftVal = comp1.charAt(i), rightVal = comp2.charAt(i);
                if (leftVal == rightVal) {
                    sameCount++;
                } else {
                    break;
                }
            }
            if (sameCount == 0) {
                // If there's no same letter, then use String compareTo method
                return comp1.compareTo(comp2);
            } else {
                // slice same string from both strings
                String newStr1 = comp1.substring(sameCount), newStr2 = comp2.substring(sameCount);
                if (NumberUtils.isDigits(newStr1) && NumberUtils.isDigits(newStr2)) {
                    // If both sliced strings are digits then use Integer compare method
                    return Integer.compare(Integer.parseInt(newStr1), Integer.parseInt(newStr2));
                } else {
                    // If not, use String compareTo method
                    return comp1.compareTo(comp2);
                }
            }
        }
    }
}
