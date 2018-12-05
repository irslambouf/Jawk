import java.util.ArrayList;
import java.util.Arrays;

public class UtilBytes {
    private byte[] data;
    private byte[] dataNoEmail = null;
    private byte space = " ".getBytes()[0];
    private byte newLine = "\n".getBytes()[0];
    private byte tab = "\t".getBytes()[0];

    public UtilBytes(byte[] data) {
        this.data = data;
    }

    public void appendToData(byte[] dataToAppend) {
        byte[] newDataBuffer = new byte[data.length + dataToAppend.length];
        System.arraycopy(data, 0, newDataBuffer, 0, data.length);
        System.arraycopy(dataToAppend, 0, newDataBuffer, data.length, dataToAppend.length);
        data = newDataBuffer;
    }

    public int count(byte character) {
        int count = 0;

        for (byte b : data) {
            if (b == character) {
                count++;
            }
        }

        return count;
    }

    public int count(byte character, int start) {
        if (start >= data.length) {
            return 0;
        }

        int count = 0;
        for (int i = start; i < data.length; i++) {
            if (data[i] == character) {
                count++;
            }
        }

        return count;
    }

    public int count(byte[] characters) {
        if (characters.length == 0) {
            return 0;
        }

        if (characters.length == 1) {
            return count(characters[0]);
        }

        int count = 0;

        outer:
        for (int i = 0; i < data.length; i++) {
            if (data[i] == characters[0]) {
                for (int j = 1; j < characters.length; j++) {
                    if (i + j >= data.length || data[i + j] != characters[j]) {
                        i += j - 1; // i++ will trigger
                        continue outer;
                    }
                }

                count++;
                i += characters.length - 1; // i++ will trigger
            }
        }

        return count;
    }

    public int indexOf(byte character){
        for (int index = 0; index < data.length; index++) {
            if (data[index] == character) {
                return index;
            }
        }

        System.err.println("UtilBytes:indexOf - Character:" + character + ", not found in data");
        return -1;
    }

    public int lastIndexOf(byte character) {
        for (int index = data.length - 1; index >= 0; index--) {
            if (data[index] == character) {
                return index;
            }
        }

        return -1;
    }

    public int lastIndexOf(byte[] characters) {
        if (characters.length == 1) {
            return lastIndexOf(characters[0]);
        }
        // Reverse order
        outer:
        for (int i = data.length - 1; i >= 0; i--) {
            if (data[i] == characters[characters.length - 1]){
                int j;
                for (j = 1; j < characters.length; j++) {
                    if (i-j > 0 && data[i-j] != characters[characters.length-1-j]){
                        i -= j + 1;
                        continue outer;
                    }
                }

                return i - j + 1;
            }
        }

        System.err.print("UtilBytes:lastIndexOf - Characters:");
        for (byte b : characters){
            System.err.print("Byte["+b+"]: "+new String(new byte[]{b}));
        }
        System.err.println(", not found in data");
        return -1;
    }

    public byte[] replaceAndGetData(int index, byte newDelimiter) {
        if (data[index] != newDelimiter) {
            data[index] = newDelimiter;
        }

        return data;
    }

    public byte[] replaceAndGetDataExtend(int index, byte newDelimiter) {
        if (data[index] != newDelimiter) {
            data[index] = newDelimiter;
        }

        if (data[data.length - 1] == newLine) {
            data[data.length - 1] = newDelimiter;
        }

        return data;
    }

    public byte[] replaceMultiAndGetDataExtend(ArrayList<Integer> indexes, byte newDelimiter) {
        for (Integer i : indexes) {
            data[i] = newDelimiter;
        }

        if (data[data.length - 1] == newLine) {
            data[data.length - 1] = newDelimiter;
        }

        return data;
    }

    public int trimSpaceTabStart() {
        int newStartIndex;
        for (newStartIndex = 0; newStartIndex < data.length; newStartIndex++) {
            if (data[newStartIndex] != space && data[newStartIndex] != tab) {
                break;
            }
        }

        if (newStartIndex > 0) {
            data = Arrays.copyOfRange(data, newStartIndex, data.length);
        }

        return newStartIndex;
    }

    public void trimSpaceStart() {
        trimCharStart(space);
    }


    public void trimCharStart(byte character) {
        int newStartIndex;
        for (newStartIndex = 0; newStartIndex < data.length; newStartIndex++) {
            if (data[newStartIndex] != character) {
                break;
            }
        }

        if (newStartIndex > 0) {
            data = Arrays.copyOfRange(data, newStartIndex, data.length);
        }
    }

    public int removeAllTabs() {
        int count = 0;

        for (byte b : data) {
            if (b == tab) {
                count++;
            }
        }

        if (count == 0) {
            return 0;
        }

        byte[] cleanData = new byte[data.length - count];
        count = 0;  //   Reuse variable here for index of clearData

        for (byte b : data) {
            if (b != tab) {
                cleanData[count++] = b;
            }
        }

        data = cleanData;
        return count;
    }

    public int findFirstIndex(byte[] toFind){
        if (toFind.length == 1) {
            return indexOf(toFind[0]);
        }

        // Check all characters in data
        outer:
        for (int i = 0; i < data.length; i++) {
            // If first character matches check remaining characters
            if (data[i] == toFind[0]) {
                for (int j = 1; j < toFind.length; j++) {
                    // Fail conditions
                    if (i + j >= data.length || data[i + j] != toFind[j]) {
                        continue outer;
                    }
                }
                // Full data matches
                return i;
            }
        }

        return -1;
    }

    public int findNextIndex(int startIndex, byte character) {
        for (int i = startIndex; i < data.length; i++) {
            if (data[i] == character) {
                return i;
            }
        }

        return -1;
    }

    public int findNextIndex(int startIndex, byte[] characters) {
        if (characters.length == 0) {
            return -1;
        }

        if (characters.length == 1) {
            return findNextIndex(startIndex, characters[0]);
        }

        if (startIndex < 0){
            return -1;
        }

        outer:
        for (int i = startIndex; i < data.length; i++) {
            if (data[i] == characters[0]) {
                for (int j = 1; j < characters.length; j++) {
                    if (i + j >= data.length || data[i + j] != characters[j]) {
                        continue outer;
                    }
                }

                return i;
            }
        }

        return -1;
    }

    public ArrayList<Integer> getAllIndexes(byte character) {
        ArrayList<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            if (data[i] == character) {
                indexes.add(i);
            }
        }

        return indexes;
    }

    public ArrayList<Integer> getAllIndexesExceptInvalid(byte character, ArrayList<Integer> invalidIndexes) {
        ArrayList<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            if (data[i] == character && !invalidIndexes.contains(i)) {
                indexes.add(i);
            }
        }

        return indexes;
    }

    public ArrayList<Integer> getAllIndexesExceptInvalid(byte[] characters, ArrayList<Integer> invalidIndexes) {
        if (characters.length == 1) {
            return getAllIndexesExceptInvalid(characters[0], invalidIndexes);
        }

        ArrayList<Integer> indexes = new ArrayList<>();

        outer:
        for (int i = 0; i < data.length; i++) {
            if (data[i] == characters[0] && !invalidIndexes.contains(i)) {
                for (int j = 1; j < characters.length; j++) {
                    if (i + j >= data.length || data[i + j] != characters[j]) {
                        continue outer;
                    }
                }

                indexes.add(i);
            }
        }

        return indexes;
    }

    public ArrayList<Integer> getAllIndexes(byte[] characters) {
        if (characters.length == 0) {
            return new ArrayList<>();
        }

        if (characters.length == 1) {
            return getAllIndexes(characters[0]);
        }

        ArrayList<Integer> indexes = new ArrayList<>();

        outer:
        for (int i = 0; i < data.length; i++) {
            if (data[i] == characters[0]) {
                for (int j = 1; j < characters.length; j++) {
                    if (i + j >= data.length || data[i + j] != characters[j]) {
                        i += j - 1;  // i++ will trigger
                        continue outer;
                    }
                }
                indexes.add(i);
                i += characters.length - 1; // i++ will trigger
            }
        }
        return indexes;
    }

    public byte[] getData() {
        return data;
    }

    public byte getByte(int index) {
        if (index < data.length) {
            return data[index];
        } else {
            return (byte) 0x0;
        }
    }


    public int size() {
        return data.length;
    }

    public byte[] getBytesOfRange(int start, int end) {
        if (start >= 0 && end <= data.length) {
            return Arrays.copyOfRange(data, start, end);
        } else {
            return null;
        }
    }

    public byte[] getBytesOfRangeWithEndNewline(int start, int end) {
        if (start >= 0 && end <= data.length) {
            byte[] dataOfRangeWithNewLine = new byte[end - start + 1];
            dataOfRangeWithNewLine[dataOfRangeWithNewLine.length - 1] = "\n".getBytes()[0];
            System.arraycopy(data, start, dataOfRangeWithNewLine, 0, end - start);
            return dataOfRangeWithNewLine;
        } else {
            return null;
        }
    }

    public int getContinuousCharacterCount(int startIndex, byte character){
        if (startIndex < 0) {
            return 0;
        }

        int count = 0;
        for (int i = startIndex; i < data.length; i++) {
            if (data[i] == character) {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    public int getContinuousCharacterCount(int startIndex, byte[] characters){
        if (characters.length == 1) {
            return getContinuousCharacterCount(startIndex, characters[0]);
        }

        int count = 0;
        outer:
        for (int i = startIndex; i < data.length; i++) {
            if (data[i] == characters[0]) {
                for (int j = 1; j < characters.length; j++) {
                    if (i + j >= data.length || data[i + j] != characters[j]) {
                        continue outer;
                    }
                }

                count++;
            } else {
                break;
            }
        }

        return count;
    }

    public boolean isEmpty() {
        return data != null && data.length > 0;
    }

    public int getLastIndexNotNewline() {
        return data.length - 2; // Newline is always last
    }

    public void remove(int index) {
        byte[] temp = new byte[data.length - 1];
        System.arraycopy(data, 0, temp, 0, index);
        System.arraycopy(data, index + 1, temp, index, data.length - ++index);
        data = temp;
    }

    public void removeBytes(int startIndex, int length) {
        if (startIndex > data.length - 1) {
            System.err.println("UtilBytes:removeBytes - startIndex is larger than data length");
            return;
        }

        if (startIndex + length > data.length) {
            System.err.println("UtilBytes:removeBytes - length parameter is to large adjusted to max length of data");
            length = data.length - startIndex;
        }

        if (startIndex < 0) {
            System.err.println("UtilByres:removeBytes - startIndex is smaller than 0");
            return;
        }

        byte[] temp = new byte[data.length - length];

        System.arraycopy(data, 0, temp, 0, startIndex);
        System.arraycopy(data, startIndex + length, temp, startIndex, data.length - (startIndex + length));

        data = temp;
    }

    public void removeInvalidIndexes(ArrayList<Integer> invalidIndexes) {
        // Do in reverse order se we dont have to change index values
        for (int i = invalidIndexes.size() - 1; i >= 0; i--) {
            this.remove(invalidIndexes.get(i));
        }
    }

    public boolean contains(byte[] subData, int startIndex) {
        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < data.length; i++) {
            if (data[i] == subData[0]) {
                boolean totalMatch = true;
                for (int subIndex = 1; subIndex < subData.length; subIndex++) {
                    if (i + subIndex >= data.length || data[i + subIndex] != subData[subIndex]) {
                        totalMatch = false;
                        break;
                    }
                }

                if (totalMatch) {
                    return true;
                }
            }
        }
        return false;
    }

    // Makes sure there is a character repeated x times and
    // that the character following is not: the same or end of data aka \n
    public boolean containsRepeatingStrict(byte repeatedCharacter, int numberOfTimes) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == repeatedCharacter) {
                if (i + numberOfTimes - 1 < data.length) {
                    for (int j = 1; j < numberOfTimes; j++) {
                        if (data[i + j] != repeatedCharacter) {
                            return false;
                        }
                    }

                    if (i + numberOfTimes < data.length) {
                        return data[i + numberOfTimes] != repeatedCharacter;
                        // REFACTOR
//                        if (data[i + numberOfTimes] != repeatedCharacter) {
//                            return true;
//                        } else {
//                            return false;
//                        }
                    } else {
                        // End of the byte array
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }   // END for loop

        return false;
    }

    public boolean containsRepeatingStrict(byte repeatedCharacter, int nmbrOfTimes, int start) {
        if (start < 0) {
            start = 0;
        }

        if (start > data.length) {
            return false;
        }

        outer:
        for (int i = start; i < data.length; i++) {
            // possible match
            if (data[i] == repeatedCharacter) {
                if (i + nmbrOfTimes - 1 < data.length) {
                    // Check if repeats enough times
                    for (int j = 1; j < nmbrOfTimes; j++) {
                        if (data[i + j] != repeatedCharacter) {
                            continue outer;
                        }
                    }

                    // Check that next char is not the same
                    if (i + nmbrOfTimes < data.length) {
                        return data[i + nmbrOfTimes] != repeatedCharacter;
                    } else {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return new String(data);
    }

    public byte[] getDataNoEmail() {
        return dataNoEmail;
    }


    public String toStringNoInsertSQL() {
        final byte[] SQL_START = "INSERT INTO user VALUES(".getBytes();
        final byte[] SQL_END = ");".getBytes();

        if (this.contains(SQL_START, 0) && this.contains(SQL_END, data.length - 4)) {  // -4, because SQL_END is 3 bytes and length is +1 of last index

            return new String(this.getBytesOfRangeWithEndNewline(SQL_START.length, data.length - SQL_END.length - 1));
        }

        return null;
    }

    public boolean removeSQLInsert() {
        final byte[] SQL_START = "INSERT INTO user VALUES(".getBytes();
        final byte[] SQL_END = ");".getBytes();

        if (this.contains(SQL_START, 0) && this.contains(SQL_END, data.length - 4)) {
            data = this.getBytesOfRangeWithEndNewline(SQL_START.length, data.length - 3);
            return true;
        }
        return false;
    }

    public void replaceByte(int index, byte newByte) {
        data[index] = newByte;
    }

    public UtilBytes clone(){
        return new UtilBytes(this.data);
    }
}
