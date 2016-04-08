package au.net.metropolis.models;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSV {
    private static final String COMMA = ",";
    private static final String NEW_LINE = "\n";
    private Logger logger = Logger.getLogger(getClass().getName());
    private String name;
    private String[] header;
    private ArrayList<ArrayList<String>> rows;

    public CSV(String name, String[] header, ArrayList<ArrayList<String>> rows) {
        this.name = name;
        this.header = header;
        this.rows = rows;
    }

    public void save() {
        this.save("./");
    }

    public void save(String base) {
        String saveName = base + this.name + ".csv";

        File f = new File(saveName);

        if (f.exists()) {
            Random rand = new Random();
            saveName += String.valueOf(rand.nextFloat());
        }

        try {
            FileWriter writer = new FileWriter(saveName);

            writer.append(StringUtils.join(this.header, COMMA));
            writer.append(NEW_LINE);

            for (ArrayList<String> row : this.rows) {
                writer.append(StringUtils.join(row, COMMA));
                writer.append(NEW_LINE);
            }

            writer.flush();
            writer.close();

            logger.log(Level.INFO, "Saved csv file to " + saveName);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public void show() {
        System.out.println(StringUtils.join(this.header, COMMA));
        for (ArrayList<String> row : this.rows) {
            System.out.println(StringUtils.join(row, ","));
        }
        System.out.println("");
    }
}
