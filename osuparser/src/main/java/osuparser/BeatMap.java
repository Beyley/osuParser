package osuparser;

import java.io.*;
import java.util.*;

public class BeatMap {
    public int fileFormatVersion;

    public String audioFilename;
    public String audioHash;
    public int previewTime;

    public Metadata metadata;
    public DifficultySettings difficulty;
    public List<StoryboardEvent> storyboardEvents;
    public List<TimingPoint> timingPoints;

    public static class Metadata {
        public String title;
        public String artist;
        public String creator;
        public String diffName;

        Metadata() {

        }
    }

    public static class TimingPoint {
        public double time;
        public double fourthNoteDelay;
        public double bpm;

        TimingPoint(double time, double fourthNoteDelay) {
            this.time = time;
            this.fourthNoteDelay = fourthNoteDelay;
            this.bpm = (double) 60000.0 / (double) this.fourthNoteDelay;
        }
    }

    public static class StoryboardEvent {
        public int type;
        public int startTime;
        public Object[] parameters;

        public static int imageEvent = 0;
        public static int videoEvent = 1;
        public static int breakEvent = 2;
        public static int backgroundColorEvent = 3;

        StoryboardEvent(int type, int startTime, Object[] parameters) {
            this.type = type;
            this.startTime = startTime;
            this.parameters = parameters;
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

    public static class HitObject {
        public static int circleObjectType = 1;
        public static int sliderObjectType = 2;
        public static int spinnerObjectType = 3;

        public static class Position {
            public int x;
            public int y;

            Position(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        public Position pos;
        public int time;
        public int objectType;
        public int hitSound;
        public char sliderType;
        public List<Position> sliderAnchors;
        public int amountOfReverse;
        public int sliderLength;
        public boolean newCombo;

        HitObject(int x, int y, int time, int objectType, int hitSound) {
            this.pos = new Position(x, y);
            this.time = time;

            if (objectType > 3) {
                this.objectType = objectType - 4;
                this.newCombo = false;
            } else {
                this.objectType = objectType;
                this.newCombo = false;
            }

            this.hitSound = hitSound;
        }

        HitObject(int x, int y, int time, int objectType, int hitSound, char sliderType, List<Position> sliderAnchors,
                int amountOfReverse, int sliderLength) {
            this.pos = new Position(x, y);
            this.time = time;

            if (objectType > 3) {
                this.objectType = objectType - 4;
                this.newCombo = false;
            } else {
                this.objectType = objectType;
                this.newCombo = false;
            }

            this.hitSound = hitSound;
            this.sliderType = sliderType;
            this.sliderAnchors = sliderAnchors;
            this.amountOfReverse = amountOfReverse;
            this.sliderLength = sliderLength;
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
            returnMap.storyboardEvents = new ArrayList<StoryboardEvent>();
            returnMap.timingPoints = new ArrayList<TimingPoint>();

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
                        case "Events":
                            split = data.split(",");

                            if (Integer.parseInt(split[0]) == StoryboardEvent.imageEvent) {
                                String[] imagePath = new String[1];
                                imagePath[0] = split[2].replaceAll("\"", "");

                                returnMap.storyboardEvents.add(new StoryboardEvent(StoryboardEvent.imageEvent,
                                        Integer.parseInt(split[1]), imagePath));
                            } else if (Integer.parseInt(split[0]) == StoryboardEvent.videoEvent) {
                                String[] videoPath = new String[1];
                                videoPath[0] = split[2].replaceAll("\"", "");

                                returnMap.storyboardEvents.add(new StoryboardEvent(StoryboardEvent.videoEvent,
                                        Integer.parseInt(split[1]), videoPath));
                            } else if (Integer.parseInt(split[0]) == StoryboardEvent.breakEvent) {
                                Integer[] endTime = new Integer[1];
                                endTime[0] = Integer.parseInt(split[2]);

                                returnMap.storyboardEvents.add(new StoryboardEvent(StoryboardEvent.breakEvent,
                                        Integer.parseInt(split[1]), endTime));
                            } else if (Integer.parseInt(split[0]) == StoryboardEvent.backgroundColorEvent) {
                                Integer[] rgbValues = new Integer[3];
                                rgbValues[0] = Integer.parseInt(split[2]);
                                rgbValues[1] = Integer.parseInt(split[3]);
                                rgbValues[2] = Integer.parseInt(split[4]);

                                returnMap.storyboardEvents.add(new StoryboardEvent(StoryboardEvent.backgroundColorEvent,
                                        Integer.parseInt(split[1]), rgbValues));
                            }
                            break;
                        case "TimingPoints":
                            split = data.split(",");
                            returnMap.timingPoints
                                    .add(new TimingPoint(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
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
