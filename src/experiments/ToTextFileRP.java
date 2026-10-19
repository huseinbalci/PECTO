package experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ToTextFileRP implements ResultProcessor{

    String fileName;

    public ToTextFileRP(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void process(String string) {
        try {
            FileWriter fWriter = new FileWriter(fileName, true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);

            bWriter.write(string);
            bWriter.newLine();
            bWriter.close();
            fWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
