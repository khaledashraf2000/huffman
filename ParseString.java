import java.util.ArrayList;
import java.util.List;

/*
 this class is to convert output encoded string from huffman algorithm
 into bytes instead of characters
 */
public class ParseString {
    //TODO need to test ParseString.parseString more
    public static void main(String[] args) {
        //List<Byte> c = parseString("1110");

        //System.out.println(c);
        byte[] b = {-96, 4};
        System.out.println(bitsArrayToString(b));
    }

    /**
     * Converts a string representation of a binary number to its equivalent byte representation
     * while last byte is number of concatenated zeroes
     */
    public static List<Byte> parseString(String s) {
        int concatenatedZeros = 0;
        if (s.length() % 8 != 0)
            concatenatedZeros = 8 - s.length() % 8;
        String zeros = "";
        for (int i = 0; i < concatenatedZeros; i++) {
            zeros = zeros.concat("0");
        }
        s = s.concat(zeros);
        int sizeOfInputs = s.length() / 8;
        String[] inputs = new String[sizeOfInputs];
        int[] inputsIntegers = new int[sizeOfInputs];
        List<Byte> inputsBytes = new ArrayList<>();

        //substrings
        for (int i = 0; i < sizeOfInputs; i++) {
            inputs[i] = s.substring(8 * i, 8 * i + 8);
        }


        //converting from binary to decimal, integer values
        int temp, sum;
        for (int i = 0; i < sizeOfInputs; i++) {
            if (inputs[i].compareTo("10000000") == 0) {
                inputsIntegers[i] = -128;
                continue;
            }
            sum = 0;
            for (int j = 7; j > 0; j--) {

                //need to improve complexity, chatAt(j) takes O(n)
                temp = Character.getNumericValue(inputs[i].charAt(j));
                sum = sum + (int) (temp * Math.pow(2, 7 - j));
            }
            //assigning sign
            //TODO this creates a problem where: 0, 00, 000,... and 1, 10, 100, 1000... have the same
            //representation of 0, the number of zeroes concatenated are different however
            //we can't differentiate between them
            if (inputs[i].charAt(0) == '1')
                sum = sum * -1;
            inputsIntegers[i] = sum;

        }

        for (int i = 0; i < sizeOfInputs; i++) {
            inputsBytes.add((byte) inputsIntegers[i]);
        }

        inputsBytes.add((byte) concatenatedZeros);

        return inputsBytes;
    }

    /**
     * converts list of bytes to its equivalent string
     *
     * @param bytes: list of bytes
     * @return stringFinal: string representation of list of bytes
     */
    public static String bitsListToString(List<Byte> bytes) {
        //from stackoverflow.com
        int addedZeroes = bytes.remove(bytes.size() - 1);
        String stringFinal = "";
        String convertedByte = "";
        int y;
        for (Byte b : bytes) {
            if (b == -128) {
                stringFinal = stringFinal.concat("10000000");
            } else {
                int x = (int) b;
                if (x < 0) {
                    stringFinal = stringFinal.concat("1");
                    x = -1 * x;
                } else
                    stringFinal = stringFinal.concat("0");
                for (int i = 0; i < 7; i++) {
                    y = x % 2;
                    x = x / 2;
                    String s = Integer.toString(y);
                    convertedByte = s.concat(convertedByte);
                }
                stringFinal = stringFinal.concat(convertedByte);
            }

        }
        int len = stringFinal.length() - addedZeroes;
        stringFinal = stringFinal.substring(0, len);

        return stringFinal;
    }

    /**
     * converts array of bytes to its equivalent string
     *
     * @param bytes: array of bytes
     * @return stringFinal: string representation of array of bytes
     */
    public static String bitsArrayToString(byte[] bytes) {
        int addedZeroes = bytes[bytes.length - 1];
        String stringFinal = "";
        String convertedByte = "";
        int y;
        for (int i = 0; i < bytes.length - 1; i++) {
            byte b = bytes[i];
            if (b == -128) {
                stringFinal = stringFinal.concat("10000000");
            } else {
                int x = (int) b;
                if (x < 0) {
                    stringFinal = stringFinal.concat("1");
                    x = -1 * x;
                } else {
                    stringFinal = stringFinal.concat("0");
                }
                for (int j = 0; j < 7; j++) {
                    y = x % 2;
                    x = x / 2;
                    String s = Integer.toString(y);
                    convertedByte = s.concat(convertedByte);
                }
                stringFinal = stringFinal.concat(convertedByte);
            }

        }
        int len = stringFinal.length() - addedZeroes;
        stringFinal = stringFinal.substring(0, len);

        return stringFinal;
    }
}