package au.net.metropolis;

import au.net.metropolis.models.CSV;
import au.net.metropolis.models.CompressFile;
import au.net.metropolis.models.XML;
import org.apache.commons.cli.*;

import java.util.Iterator;
import java.util.Map;

public class Processor {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("f")
                .argName("input_files")
                .hasArgs()
                .desc("input file list")
                .build());
        options.addOption("show", false, "show csv content");
        options.addOption("save", false, "save csv file");
        options.addOption("help", false, "show help");

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help") || args.length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("processor", options);
                return;
            }

            if (cmd.hasOption("f")) {
                String[] inputFiles = cmd.getOptionValues("f");
                for (String inputFile : inputFiles) {
                    if (!inputFile.startsWith("-")) {
                        CompressFile compressFile = new CompressFile(inputFile);
                        Iterator iter = compressFile.getFileMap().entrySet().iterator();

                        while (iter.hasNext()) {
                            @SuppressWarnings("unchecked")
                            Map.Entry<String, String> pair = (Map.Entry<String, String>) iter.next();
                            XML xml = new XML(pair.getKey(), pair.getValue());

                            System.out.println("---Processing: " + pair.getKey() + " in " + compressFile.getPath());

                            CSV csv = xml.toCSV();
                            if (cmd.hasOption("show"))
                                csv.show();
                            if (cmd.hasOption("save"))
                                csv.save();

                            iter.remove();
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
