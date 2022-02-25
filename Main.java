import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        args = new String[]{"d", "D:\\University\\Term 7\\Analysis and Design of Algorithms\\Projects\\Bonus Project 2\\Bonus Project 1 - 1\\Bonus Project 1 - 1\\6134.5.test_group_2.txt.hc", "1"};
        char mode = args[0].charAt(0);
        String inputPath = args[1];
        int n = Integer.parseInt(args[2]);
        ;

        byte[] inputAsBytes = Files.readAllBytes(Paths.get(inputPath));

        //process path
        int endindex = inputPath.lastIndexOf('\\') + 1;
        String filename = inputPath.substring(endindex);
        inputPath = inputPath.substring(0, endindex);

        if(mode == 'c') {
            //<id>.<n>.abc.exe.hc
            //encoding
            HuffmanBytes encoder = new HuffmanBytes(inputAsBytes, 'c');
            System.out.println(encoder.getEncodingTable());

            List<Byte> outputEncoded = encoder.getEncodedFile();

            //converting outputAsByte from a List<Byte> to a byte[]
            int size = outputEncoded.size();
            byte[] output = new byte[size];

            for (int i = 0; i < size; i++) {
                output[i] = outputEncoded.get(i);
            }

            String opPath = inputPath + "6134." + n + "." + filename + ".hc";

            File file = new File(opPath);
            try {
                // Initialize a pointer
                // in file using OutputStream
                OutputStream os = new FileOutputStream(file);

                // Starts writing the bytes in it
                os.write(output);

                // Close the file
                os.close();
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        } else if(mode == 'd') {
            //decoding
            //extracted.abc.exe
            //<id>.<n>.abc.exe.hc
            int end = filename.lastIndexOf('.');
            String opFileName = filename.substring(0, end);
            int first = opFileName.indexOf('.') + 1;
            opFileName = opFileName.substring(first);

            first = opFileName.indexOf('.') + 1;
            opFileName = opFileName.substring(first);

            opFileName = "extracted." + opFileName;

            HuffmanBytes decoder = new HuffmanBytes(inputAsBytes, 'd');
            String outputDecoded = decoder.getDecodedString();
            System.out.println(outputDecoded);

            String opPath = inputPath + opFileName;

            File file = new File(opPath);
            try {
                // Initialize a pointer
                // in file using OutputStream
                OutputStream os = new FileOutputStream(file);

                // Starts writing the bytes in it
                os.write(Integer.parseInt(outputDecoded));

                // Close the file
                os.close();
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }
    }
}
