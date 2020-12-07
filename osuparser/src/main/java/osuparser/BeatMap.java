package osuparser;

import java.io.*;
import java.util.Scanner;

public class BeatMap {
    public int fileFormatVersion;

    public String audioFilename;
    public String audioHash;
    public int previewTime;

    public Metadata metadata;
    public DifficultySettings difficulty;

    public static class Metadata {
        public String title;
        public String artist;
        public String creator;
        public String diffName;

        Metadata() {

        }
    }

    public static class DifficultySettings {
        public double hpDrainRate;
        public double circleSize;
        public double overallDifficulty;
        public double sliderMultiplier;
        public double sliderTickRate;

        DifficultySettings() {
        }
    }

    BeatMap(int version) {
        this.fileFormatVersion = version;
        System.out.println(this.fileFormatVersion);
    }

    public static BeatMap parseOsuFile(String filename) {
        BeatMap returnMap = null;

        File osuFile = new File(filename);
        Scanner myReader;
        try {
            myReader = new Scanner(osuFile);

            String versionLine = myReader.nextLine();

            returnMap = new BeatMap(Integer.parseInt(versionLine.replaceAll("[^\\d.]", "")));
            returnMap.metadata = new Metadata();
            returnMap.difficulty = new DifficultySettings();

            String currentSection = "";

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                // Checks if line is blank
                if (data.equals(""))
                    continue;

                // Checks if we are on a header
                if (data.charAt(0) == '[') {
                    String parsedHeader = data.replaceAll("\\[", "");
                    parsedHeader = parsedHeader.replaceAll("\\]", "").strip();

                    currentSection = parsedHeader;

                    // System.out.println(parsedHeader);
                } else {
                    // System.out.println(currentSection);

                    String[] split;

                    switch (currentSection) {
                        case "General":
                            split = data.split(":");

                            if (split[0].equals("AudioFilename")) {
                                returnMap.audioFilename = split[1].strip();
                            } else if (split[0].equals("AudioHash")) {
                                returnMap.audioHash = split[1].strip();
                            } else if (split[0].equals("PreviewTime")) {
                                returnMap.previewTime = Integer.parseInt(split[1].strip());
                            }
                            break;
                        case "Metadata":
                            split = data.split(":");

                            if (split[0].equals("Title")) {
                                returnMap.metadata.title = split[1].strip();
                            } else if (split[0].equals("Artist")) {
                                returnMap.metadata.artist = split[1].strip();
                            } else if (split[0].equals("Creator")) {
                                returnMap.metadata.creator = split[1].strip();
                            } else if (split[0].equals("Version")) {
                                returnMap.metadata.diffName = split[1].strip();
                            }
                            break;
                        case "Difficulty":
                            split = data.split(":");

                            if (split[0].equals("HPDrainRate")) {
                                returnMap.difficulty.hpDrainRate = Double.parseDouble(split[1].strip());
                            } else if (split[0].equals("CircleSize")) {
                                returnMap.difficulty.circleSize = Double.parseDouble(split[1].strip());
                            } else if (split[0].equals("OverallDifficulty")) {
                                returnMap.difficulty.overallDifficulty = Double.parseDouble(split[1].strip());
                            } else if (split[0].equals("SliderMultiplier")) {
                                returnMap.difficulty.sliderMultiplier = Double.parseDouble(split[1].strip());
                            } else if (split[0].equals("SliderTickRate")) {
                                returnMap.difficulty.sliderTickRate = Double.parseDouble(split[1].strip());
                            }
                            break;
                    }
                }

                // System.out.println(data);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return returnMap;
    }
}
