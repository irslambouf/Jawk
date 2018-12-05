import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.commons.text.StringEscapeUtils;

public class Main {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java -jar Jawk.jar [delimiter] [print format] [in filename] [output filename]");
            return;
        }

        byte[] delimiter = StringEscapeUtils.unescapeJava(args[0]).getBytes();

        String outFormat = StringEscapeUtils.unescapeJava(args[1]);
        ArrayList<Integer> columns = new ArrayList<>();
        ArrayList<String> inBetween = new ArrayList<>();
        if (outFormat.indexOf('$') != -1) {
            int index, oldIndex = -1, start = 0;
            while ((index = outFormat.indexOf('$', start)) != -1) {
                // At least second Column
                if (oldIndex != -1) {
                    String between = outFormat.substring(oldIndex, index).replaceAll("^\\$\\d+", "");
                    inBetween.add(between);
                }

                start = index + 1;
                String columnNumber;
                if (index + 3 < outFormat.length()) {
                    columnNumber = outFormat.substring(index, index + 3).replaceAll("[^\\d]+", "");
                } else {
                    columnNumber = outFormat.substring(index, outFormat.length()).replaceAll("[^\\d]+", "");
                }
                try {
                    int column = Integer.parseInt(columnNumber);
                    columns.add(column);
                } catch (NumberFormatException e) {
                    System.err.println("Error casting columns number, exiting...");
                    return;
                }

                oldIndex = index;
            }
        } else {
            System.err.println("No column number found, specify column number with a $ (i.e. $1), exiting...");
            return;
        }

        if (columns.size() == 0) {
            System.err.println("Output does not have any column, exiting...");
            return;
        }
        int largestColumn = 0;
        try {
            largestColumn = columns.stream().reduce(Integer::max).get();
        } catch (NoSuchElementException e) {
            System.err.println("Could not determine largest column number, exiting...");
            return;
        }

        ArrayList<String> fileNames = new ArrayList<>();
        for (int i = 2; i < args.length - 1; i++) {
            File in = new File(args[i]);
            if (!in.exists()) {
                System.err.println(in.getAbsolutePath());
                System.err.println("File does not exist, exiting...");
                return;
            }
            fileNames.add(args[i]);
        }

        String outfileName = args[args.length - 1];

        File file = new File(outfileName);
        if (file.exists()) {
            Scanner scanner = new Scanner(System.in);
            String input = null;
            do {
                System.out.print("File already exists, do you want to override (Y/N)? ");
                input = scanner.next();
            } while (input == null || !(input.equals("Y") || input.equals("N")));

            if (input.equalsIgnoreCase("N")) {
                System.out.println("File will not be overwritten, exiting...");
                return;
            }
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);

            for (String inFileName : fileNames) {
                FileAndFolderReaderBinaryV2 readerBinaryV2 = new FileAndFolderReaderBinaryV2(inFileName);

                byte[] line;
                while ((line = readerBinaryV2.readLine()) != null) {
                    UtilBytes uLine = new UtilBytes(line);

                    ArrayList<Integer> indexes = uLine.getAllIndexes(delimiter);

                    if (largestColumn > indexes.size()) {
                        System.err.println("Output column is larger column in line");
                        System.err.println(uLine.toString());
                        continue;
                    }

                    for (int i = 0; i < columns.size(); i++) {
                        int column = columns.get(i) - 1;  // Index is -1 of column number

                        if (column == 0) {
                            fos.write(uLine.getBytesOfRange(0, indexes.get(0)));
                        } else {
                            fos.write(uLine.getBytesOfRange(indexes.get(column - 1) + delimiter.length, indexes.get(column)));
                        }

                        if (i != columns.size() - 1) {
                            fos.write(inBetween.get(i).getBytes());
                        }
                    }
                    fos.write("\n".getBytes());
                    fos.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
