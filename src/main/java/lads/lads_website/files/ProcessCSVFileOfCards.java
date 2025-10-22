package lads.lads_website.files;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProcessCSVFileOfCards {

    public ProcessCSVFileOfCards() {
    }

    public static void readAndWriteLI(Path inputFile, Path outputFile) {
        try {
            BufferedReader br = Files.newBufferedReader(inputFile);
            BufferedWriter bw = Files.newBufferedWriter(outputFile);
            CSVParser records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(br);
            String insertStmt = "";
            for (CSVRecord record : records) {
                String bannerName = record.get(0);
                if (bannerName.equals("XE")) { bannerName = "Xspace Echo"; }
                bannerName = cleanCommas(bannerName);

                String cardName = record.get(1);
                cardName = cleanCommas(cardName);

                String loveInterest = record.get(2);
                if (loveInterest.equals("X")) { loveInterest = "Xavier"; }
                else if (loveInterest.equals("Z")) { loveInterest = "Zayne"; }
                else if (loveInterest.equals("R")) { loveInterest = "Rafayel"; }
                else if (loveInterest.equals("S")) { loveInterest = "Sylus"; }
                else if (loveInterest.equals("C")) { loveInterest = "Caleb"; }

                String rarity = record.get(3);
                if (rarity.length() == 1) { rarity += " Star"; }

                String type = record.get(4);
                if (type.equals("S")) { type = "Solar"; }
                else if (type.equals("L")) { type = "Lunar"; }

                String stella = record.get(5);
                if (stella.equals("R")) { stella = "Ruby"; }
                else if (stella.equals("A")) { stella = "Amber"; }
                else if (stella.equals("E")) { stella = "Emerald"; }
                else if (stella.equals("S")) { stella = "Sapphire"; }
                else if (stella.equals("P")) { stella = "Pearl"; }
                else if (stella.equals("V")) { stella = "Violet"; }

                String stat = record.get(6);
                if (stat.equals("D")) { stat = "Defense"; }
                else if (stat.equals("H")) { stat = "Health"; }
                else if (stat.equals("A")) { stat = "Attack"; }

                insertStmt = "INSERT INTO Card (card_origin_id,[name],love_interest_type,rarity_type,card_type,stellacrum_type,main_stat_type) SELECT co.id, '" + cardName + "', '" + loveInterest + "', '" + rarity + "', '" + type + "', '" + stella + "', '" + stat + "' FROM card_origin co JOIN ";
                if (!bannerName.equals("Xspace Echo") && (rarity.equals("3 Star") || rarity.equals("4 Star"))) {
                    String endOfStmt = "Event e ON co.event_id = e.id WHERE e.name = '" + bannerName + "'";
                    insertStmt += endOfStmt;
                }
                else {
                    String endOfStmt = "Banner b ON co.banner_id = b.id WHERE b.name = '" + bannerName + "'";
                    insertStmt += endOfStmt;
                }
                bw.write(insertStmt + "\n");
            }
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void activityLIPop(Path input, Path output) {
        try {
            BufferedReader br = Files.newBufferedReader(input);
            BufferedWriter bw = Files.newBufferedWriter(output);
            CSVParser records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(br);
            String insertStmt = "";
            for (CSVRecord record : records) {
                String actName = record.get(0);
                actName = cleanCommas(actName);
                String liList = record.get(1);
                String[] lis = liList.split(":");
                String actType = record.get(2);
                if (actType.equals("B")) {
                    insertStmt = "INSERT INTO activity_love_interest (banner_id, activity_run_type, love_interest_type) SELECT id, 'Banner', '";
                    for (String li : lis) {
                        if (li.equals("X")) { li = "Xavier"; }
                        else if (li.equals("Z")) { li = "Zayne"; }
                        else if (li.equals("R")) { li = "Rafayel"; }
                        else if (li.equals("S")) { li = "Sylus"; }
                        else if (li.equals("C")) { li = "Caleb"; }
                        String finalInsert = insertStmt + li + "' FROM Banner WHERE name = '" + actName + "'";
                        bw.write(finalInsert + "\n");
                    }
                }
                else {
                    insertStmt = "INSERT INTO activity_love_interest (event_id, activity_run_type, love_interest_type) SELECT id, 'Event', '";
                    for (String li : lis) {
                        if (li.equals("X")) { li = "Xavier"; }
                        else if (li.equals("Z")) { li = "Zayne"; }
                        else if (li.equals("R")) { li = "Rafayel"; }
                        else if (li.equals("S")) { li = "Sylus"; }
                        else if (li.equals("C")) { li = "Caleb"; }
                        String finalInsert = insertStmt + li + "' FROM Event WHERE name = '" + actName + "'";
                        bw.write(finalInsert + "\n");
                    }
                }
            }
            bw.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void actSchedProp(Path input, Path output) {
        try {
            BufferedReader br = Files.newBufferedReader(input);
            BufferedWriter bw = Files.newBufferedWriter(output);
            CSVParser records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(br);
            for (CSVRecord record : records) {
                String idName = "event_id";
                String tbl = "Event";
                String actName = cleanCommas(record.get(0));
                String type = record.get(1);
                if (type.equals("B")) {
                    idName = "banner_id";
                    tbl = "Banner";
                }
                String start = record.get(2);
                String end = record.get(3);
                String run = record.get(4);
                String insertStmt = "INSERT INTO activity_run_period (" + idName + ", activity_run_type, start_date, end_date, run_num) SELECT id, '" + tbl + "', '" + start + "', '" + end + "', " + run + " FROM " + tbl + " WHERE name = '" + actName + "'";
                bw.write(insertStmt + "\n");
            }
            bw.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String cleanCommas(String str) {
        List<Integer> pos = new ArrayList<Integer>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 39) {
                pos.add(i);
            }
        }
        for (Integer i : pos) {
            str = str.substring(0, i + 1) + "'" + str.substring(i + 1);
        }
        return str;
    }
}
