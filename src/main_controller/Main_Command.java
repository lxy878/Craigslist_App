package main_controller;

import java.util.Scanner;

public class Main_Command {
    private static FileProcessClass filep = new FileProcessClass();

    public static void main(String[] args) {
        Scanner input = null;
        String commandLine;
        String [] strings;
        boolean keepRun = true;
        while(keepRun){
            input = new Scanner(System.in);
            System.out.print("Enter command: ");
            commandLine = input.nextLine();
            strings = commandLine.split(" ");
            switch (strings[0].toLowerCase()) {
                case "-i":  // input command
                    if (strings.length > 1) {    // input with a file name
                        filep.fileReader(strings[1]);
                        filep.search("admin");
                        // save data
                        filep.saveData();
                    }
                    break;
                case "-o":  // output command
                    if (strings.length > 1) {
                        //output data from temp file to a text file
                        filep.fileWriter(strings[1]);
                    } else { // command without file name
                        System.out.println("no file name");
                    }
                    break;
                case "-p":   // input and output
                    if (strings.length > 2) {
                        // get data from input file
                        filep.fileReader(strings[1]);
                        filep.search("admin");
                        // writer data from temp file into a text file
                        filep.fileWriter(strings[2]);
                    } else {
                        System.out.println("missing parameters");
                    }
                    keepRun = false;
                    break;
                case "-q":   // quit command
                    keepRun = false;
                    break;
                case "-users":   // database switch
                    filep.get_hd().print_user();
                    break;
                case "-data":   // database switch
                    filep.get_hd().print_data();
                    break;
                case "-servers":
                    filep.print_servers();
                    break;
                case "-searchtypes":
                    filep.print_searchType();
                    break;
                case "-rebuild":
                    System.out.println("rebuilding data");
                    filep.rebuild();
                    break;
                default:
                    System.out.println("no such command exist");
                    break;
            }
        }
        input.close();
        System.out.println("The program is terminated");
    }
}
