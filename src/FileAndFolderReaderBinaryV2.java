import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileAndFolderReaderBinaryV2 {
    private final String FILE_OR_FOLDER;

    private boolean isDirectory = false;
    private File[] fileList = null;
    private int fileListIndex = 0;

    private FileInputStream fileInputStream = null;
    private int start = 0;
    private int end = 0;
    private final int DEFAULT_BUFFER_SIZE = 4096;
    private int readBufferSize = 4096;
    private byte[] readBuffer = new byte[readBufferSize];
    private byte[] remainder = null;
    private int lastStartIncrease = 0;

    private byte newLine = "\n".getBytes()[0];
    private byte carriageReturn = "\r".getBytes()[0];

    public FileAndFolderReaderBinaryV2(String fileOrFolder) {
        FILE_OR_FOLDER = fileOrFolder;
        init();
    }

    private void init() {
        File fileOrFolder = new File(FILE_OR_FOLDER);

        if (fileOrFolder.exists()) {
            isDirectory = fileOrFolder.isDirectory();

            if (isDirectory) {
                fileList = fileOrFolder.listFiles();
            } else {
                fileList = new File[]{fileOrFolder};
            }
        } else {
            System.err.println("File does not exist");
        }
    }

    private FileInputStream getFileInputStream() {
        // Error with file or folder passed to constructor
        if (fileList == null) {
            return null;
        }

        // We have finished reading all files passes
        if (fileListIndex == fileList.length) {
            return null;
        }
        try {
            System.out.println("Reading: " + fileList[fileListIndex].toString());
            return new FileInputStream(fileList[fileListIndex++]);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public byte[] readLine() {
        if (fileInputStream == null) {
            fileInputStream = getFileInputStream();

            // No more files to read
            if (fileInputStream == null) {
                return null;
            }
        }

        return getByteline();
    }

    private byte[] getByteline() {
        while ((end = findEndOfLine()) == -1) {
            // We have reached end of file need to change FileInputStream to next file
            if (readToBuffer() <= 0) {
                break;
            }
        }

        // We reached end of file
        if (end == -1) {
            start = 0;
            readBuffer = new byte[DEFAULT_BUFFER_SIZE];
            fileInputStream = getFileInputStream();

            if (fileInputStream == null) {
                return null;
            }

            return getByteline();
        }

        byte[] returnBytes;
        int destPosition = 0;

        if (remainder != null) {
            // We have data from previous read buffer
            returnBytes = new byte[remainder.length + (end - start + 1)];
            System.arraycopy(remainder, 0, returnBytes, 0, remainder.length);
            destPosition = remainder.length;
            remainder = null;
        } else {
            returnBytes = new byte[end - start + 1];
        }

        System.arraycopy(readBuffer, start, returnBytes, destPosition, end - start + 1);
        returnBytes[returnBytes.length - 1] = newLine;  // Override the last char to newline

        if (readBuffer[end] == carriageReturn) {
            start = end + 2;
            lastStartIncrease = 2;
        } else {
            start = end + 1;
            lastStartIncrease = 1;
        }

        // Empty line read next one
        if (returnBytes.length == 1 && returnBytes[0] == newLine){
            return getByteline();
        }

        return returnBytes;
    }

    private int findEndOfLine() {
        for (int i = start; i < readBufferSize; i++) {
            if (readBuffer[i] == carriageReturn || readBuffer[i] == newLine) {
                return i;
            }
        }

        // Didn't find end /r or /n in readBuffer
        return -1;
    }

    private int readToBuffer() {
        int count = 0;
        try {
            // First read into the buffer
            if (readBuffer.length == DEFAULT_BUFFER_SIZE && readBufferSize == DEFAULT_BUFFER_SIZE && start == 0) {
                count = fileInputStream.read(readBuffer);

                // Save the size of the buffer without resizing it
                if (count < readBufferSize) {
                    readBufferSize = count;
                }
            } else {
                // We have a buffer that contains data but still has space for more data
                if (readBufferSize < readBuffer.length && readBuffer.length == DEFAULT_BUFFER_SIZE) {
                    count = fileInputStream.read(readBuffer, readBufferSize, readBuffer.length - readBufferSize);
                } else {
                    // We need to read more data
                    if (readBufferSize - start > 0) {
                        remainder = new byte[readBufferSize - start];
                        System.arraycopy(readBuffer, start, remainder, 0, readBufferSize - start);
                        start = 0;
                    } else {
                        if (readBufferSize - start < 0 && lastStartIncrease == 2) {
                            start = 1;
                        }else{
                            start = 0;
                        }
                    }
                    readBuffer = new byte[DEFAULT_BUFFER_SIZE];
                    readBufferSize = 0;
                    count = fileInputStream.read(readBuffer);
                }

                if (count >= 0) {
                    readBufferSize += count;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    public String getCurrentFile() {
        return fileList[fileListIndex - 1].toString();
    }

}
