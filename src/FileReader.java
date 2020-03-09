import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;


public class FileReader {

    public static User findUser(String users) {
        File UserInput = new File(users + ".txt");


        Scanner user = null;
        try {
            user = new Scanner(UserInput);
        } catch (FileNotFoundException e) {
            System.out.println("Sorry I cannot find " + users);
            System.out.println("Make sure you put the file extension at the end e.g. text.txt");
            System.exit(0);
        }
        return FileReader.aUser(user);
    }

    private static User aUser(Scanner user) {

            String userName = user.nextLine();

            Scanner line = new Scanner(userName);
            line.useDelimiter(",");

            String firstNames = line.next();
            String surNames = line.next();

            User theUser = new User(firstNames,surNames);
            FileReader.Session(user,theUser);
            line.close();
            user.close();
            return theUser;
    }

    private static void Session(Scanner sess, User user) {
        while (sess.hasNext()) {
            String Sessions = sess.nextLine();
            Scanner weight = new Scanner(Sessions);
            weight.useDelimiter(",");
            int day = weight.nextInt();
            int month = weight.nextInt();
            int year = weight.nextInt();
            //System.out.print(Sessions);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month -1);
            cal.set(Calendar.DAY_OF_MONTH, day);
            Date date = cal.getTime();
            Session ses = new Session(date);
            user.addSession(ses);
            weight.useDelimiter(";");
            while (weight.hasNext()) {
                String theSession = weight.next();
                Scanner line = new Scanner(theSession);
                line.useDelimiter(",");
                int theWeight = line.nextInt();
                int good = line.nextInt();
                int okay = line.nextInt();
                int bad = line.nextInt();
                int[] form = {good,okay,bad};
                ses.addWeight(theWeight,form);
                line.close();
            }
            weight.close();
        }
    }
}