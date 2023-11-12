package org.example;
import java.util.Arrays;
public class GoogleAnalyticsV2 {
    public static void main(String[] args) {
        String[] newArgs = args;
        boolean allValidInputs = true;
        if (args.length == 0) {
            allValidInputs = false;
            return;
        }
        if (args[0].matches("-ta")) {
            caseTa(newArgs);
            return;
        }
        if(args[0].matches("-ttpp")) {
            caseTTPP(getSortedEvents(Arrays.copyOfRange(args, 1, args.length)));
            return;
        }
        if(args[0].matches("-dppp") || args[0].matches("-dppd")) {
            caseDPPP_DPPD(getSortedEvents(Arrays.copyOfRange(args, 1, args.length)), args[0]);
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if (args.length == 1) {
                if (isValidEvent(args[0])) {
                    System.out.println(args[0]);
                    return;
                }
                return;
            }
            String input = args[i];
            if (!(isValidEvent(input))) {
                allValidInputs = false;
            }
        }
        if (allValidInputs) {
            getSortedEvents(args);
            System.out.println(args[0]);
            for (int j = 1; j < args.length - 2; j++) {
                String before = args[j - 1];
                String arg1 = args[j];
                String eventBefore = before.substring(0, before.indexOf(':'));
                String eventOne = arg1.substring(0, arg1.indexOf(':'));
                if (eventOne.equals(eventBefore)) {
                    System.out.println(args[j]);
                } else {
                    System.out.println();
                    System.out.println(args[j]);
                }
            }
            System.out.println();
            System.out.println(args[args.length - 2]);
            System.out.println(args[args.length - 1]);
        }
    }

    public static boolean isValidEvent(String event) {
        if (event == null) {
            System.out.println("null event");
            return false;
        }
        String path;
        String time = null;
        String amount = null;
        String acquisition = null;
        String[] eventArray = event.split(":");
        if (event.startsWith(":")) {
            System.out.println("Event is missing at least one component: " + event);
            return false;
        }
        if (eventArray.length == 4) {
            path = eventArray[0];
            time = eventArray[1];
            amount = eventArray[2];
            acquisition = eventArray[3];
        } else {
            System.out.println("Event is missing at least one component: " + event);
            return false;
        }
        if ((isValidAcquisition(acquisition) && (isValidConversionValue(amount)) && (isValidTimeOnPage(time)) && (isValidPath(path)))) {
            return true;
        }
        return false;
    }

    public static boolean isValidAcquisition(String acquisition) {
        if (acquisition == null) {
            System.out.println("null acquisition");
            return false;
        } else if (!(acquisition.matches("|search|direct|referral|"))) {
            System.out.println("Acquisition must be one of search, direct, or referral. Invalid acquisition: " + acquisition);
            return false;
        }
        return (acquisition.matches("|search|direct|referral|"));
    }

    public static boolean isValidConversionValue(String value) {
        if (value == null || getValidDollarAmount(value) == -1) {
            System.out.println("The conversion value must be a non-negative dollar amount. Invalid conversion value: " + value);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidTimeOnPage(String time) {
        if (time == null || toPositiveInt(time) == -1) {
            System.out.println("Invalid time on page: " + time);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidPath(String path) {
        if (path == null) {
            System.out.println("null path");
            return false;
        }
        String lastFive = path.substring(path.length() - 5);
        String beginning = path.substring(0, path.length() - 5);
        String lastFiveLowercase = lastFive.toLowerCase();
        String html = ".html";
        if (!lastFiveLowercase.equals(html)) {
            System.out.println("Paths must end with \".html\". Invalid path: " + path);
            return false;
        } else {
            for (int i = 0; i < beginning.length(); i++) {
                String currentChar = String.valueOf(beginning.charAt(i));
                if (!currentChar.matches("[a-zA-Z1-9\\-\\/\\.\\\\]")) {
                    System.out.println("Paths may only be made of letters, numbers, dashes, periods, and slashes. Invalid path: " + path);
                    return false;
                }
            }
        }
        return true;
    }

    public static int toPositiveInt(String num) {
        try {
            int number = Integer.parseInt(num);
            if (number > 0) {
                return number;
            }
        } catch (NumberFormatException e) {
        }
        return -1;
    }

    public static double getValidDollarAmount(String dollars) {
        if (dollars.startsWith("$")) {
            String amountString = dollars.substring(1);
            try {
                double strAmount = Double.parseDouble(amountString);
                if (strAmount > 0 && amountString.matches("^[0-9]+(\\.)?(\\d{1,2})?$")) {
                    return strAmount;
                }
            } catch (NumberFormatException e) {
            }
        }
        return -1;
    }

    public static String[] getSortedEvents(String[] events) {
        for (int i = 0; i < events.length - 1; i++) {
            for (int j = 0; j < events.length - 1; j++) {
                String eventOne = events[j].substring(0, events[j].indexOf(':'));
                String eventTwo = events[j + 1].substring(0, events[j + 1].indexOf(':'));
                if (eventOne.compareTo(eventTwo) > 0) {
                    String temp = events[j];
                    events[j] = events[j + 1];
                    events[j + 1] = temp;
                }
            }
        }
        return events;
    }

    private static void caseTa(String[] args) {
        boolean allValidInputs = true;
        for (int i = 1; i < args.length; i++) {
            String input = args[i];
            if (!(isValidEvent(input))) {
                allValidInputs = false;
            }
        }
        if (!allValidInputs) {
            return;
        }
        //if all valid inputs true
        String[] acquisitions = new String[args.length - 1];
        if (args.length == 1) {
            System.out.println(args[0]);
            return;
        }
        for (int i = 1; i < args.length; i++) {
            String input = args[i];
            String[] eventArray = input.split(":");
            acquisitions[i - 1] = eventArray[3];
        }
        int numSearch = 0;
        int numReferral = 0;
        int numDirect = 0;
        for (int i = 0; i < acquisitions.length; i++) {
            if (acquisitions[i].matches("search")) {
                numSearch++;
            } else if (acquisitions[i].matches("referral")) {
                numReferral++;
            } else {
                numDirect++;
            }
        }
        if (numDirect != 0) {
            System.out.println("direct: " + numDirect);
        }
        if (numReferral != 0) {
            System.out.println("referral: " + numReferral);
        }
        if (numSearch != 0) {
            System.out.println("search: " + numSearch);
        }
    }

    private static void caseTTPP(String[] args) {
        boolean allValidInputs = true;
        for (int i = 0; i < args.length; i++) {
            String input = args[i];
            if (!(isValidEvent(input))) {
                allValidInputs = false;
            }
        }
        if (!allValidInputs) {
            return;
        }
        int uniqueEvents = 1;
        for (int j = 0; j < args.length - 1; j++) {
            String eventOne = args[j].substring(0, args[j].indexOf(':'));
            String eventTwo = args[j + 1].substring(0, args[j + 1].indexOf(':'));
            if (eventOne.compareTo(eventTwo) != 0) {
                uniqueEvents++;
            }
        }
        String[][] uniqueEventArray = new String[uniqueEvents][2];
        String[] eventArray = args[0].split(":");
        uniqueEventArray[0][0] = eventArray[0];
        uniqueEventArray[0][1] = eventArray[1];
        int newUniqueEvents = 0;
        for(int i = 1; i < args.length; i++){
            String[] currentEventArray = args[i].split(":");
            String currentEventPath = args[i].substring(0, args[i].indexOf(':'));
            String previousEventPath = args[i - 1].substring(0, args[i - 1].indexOf(':'));
            if(currentEventPath.equals(previousEventPath)){
                uniqueEventArray[newUniqueEvents][1] = String.valueOf(Integer.parseInt(uniqueEventArray[newUniqueEvents][1]) + Integer.parseInt(currentEventArray[1]));
            }else {
                newUniqueEvents += 1;
                uniqueEventArray[newUniqueEvents][0] = currentEventArray[0];
                uniqueEventArray[newUniqueEvents][1] = currentEventArray[1];
            }
        }
        for (int i = 0; i < uniqueEventArray.length; i++) {
            System.out.println("PATH: " + uniqueEventArray[i][0] + "\t" + "TIME: " + uniqueEventArray[i][1]);
        }
    }

    private static void caseDPPP_DPPD(String[] args, String firstArg){
        boolean allValidInputs = true;
        for (int i = 0; i < args.length; i++) {
            String input = args[i];
            if (!(isValidEvent(input))) {
                allValidInputs = false;
            }
        }
        if (!allValidInputs) {
            return;
        }
        int uniqueEvents = 1;
        for (int j = 0; j < args.length - 1; j++) {
            String eventOne = args[j].substring(0, args[j].indexOf(':'));
            String eventTwo = args[j + 1].substring(0, args[j + 1].indexOf(':'));
            if (eventOne.compareTo(eventTwo) != 0) {
                uniqueEvents++;
            }
        }
        String[][] uniqueEventArray = new String[uniqueEvents][2];
        String[] eventArray = args[0].split(":");
        uniqueEventArray[0][0] = eventArray[0];
        uniqueEventArray[0][1] = String.valueOf(getValidDollarAmountDppp(eventArray[2]));
        int newUniqueEvents = 0;
        for(int i = 1; i < args.length; i++){
            String[] currentEventArray = args[i].split(":");
            currentEventArray[2] = String.valueOf(getValidDollarAmountDppp(currentEventArray[2]));
            System.out.println(currentEventArray[2]);
            String currentEventPath = args[i].substring(0, args[i].indexOf(':'));
            String previousEventPath = args[i - 1].substring(0, args[i - 1].indexOf(':'));
            if(currentEventPath.equals(previousEventPath)){
                double eventAmount1 = getValidDollarAmountDppp(uniqueEventArray[newUniqueEvents][1]);
                double eventAmount2 = Double.parseDouble(currentEventArray[2]);
                String combined = String.valueOf(eventAmount1 + eventAmount2);
                uniqueEventArray[newUniqueEvents][1] = String.valueOf(combined);

            }else {
                newUniqueEvents += 1;
                uniqueEventArray[newUniqueEvents][0] = currentEventArray[0];
                uniqueEventArray[newUniqueEvents][1] = currentEventArray[2];
            }
        }
        if(firstArg.matches("-dppp")) {
            System.out.println(firstArg);
            for (int i = 0; i < uniqueEventArray.length; i++) {
                System.out.println("PATH: " + uniqueEventArray[i][0] + "\t" + "DOLLARS: $" + uniqueEventArray[i][1]);
            }
        } else if (firstArg.matches("-dppd")) {
            for (int i = 0; i < uniqueEventArray.length - 1; i++) {
                for (int j = 0; j < uniqueEventArray.length - 1; j++) {
                    double eventOne = Double.parseDouble(uniqueEventArray[j][1]);
                    double eventTwo = Double.parseDouble(uniqueEventArray[j+1][1]);
                    if (eventOne > eventTwo) {
                        String temp = uniqueEventArray[j][1];
                        uniqueEventArray[j][1] = uniqueEventArray[j+1][1];
                        uniqueEventArray[j+1][1] = temp;
                    }
                }
            }
            System.out.println(firstArg);
            for (int i = 0; i < uniqueEventArray.length; i++) {
                System.out.println("PATH: " + uniqueEventArray[i][0] + "\t" + "DOLLARS: $" + uniqueEventArray[i][1]);
            }
        }
    }

    public static double getValidDollarAmountDppp(String dollars) {
        if (dollars.startsWith("$")) {
            String amountString = dollars.substring(1);
            try {
                double strAmount = Double.parseDouble(amountString);
                if (strAmount > 0 && amountString.matches("^[0-9]+(\\.)?(\\d{1,2})?$")) {
                    return strAmount;
                }
            } catch (NumberFormatException e) {
            }
        }
        return Double.parseDouble(dollars);
    }
}