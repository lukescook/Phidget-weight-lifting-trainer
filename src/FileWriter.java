import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileWriter {

    public static void writeInfo(String userFileName, User a){

        File userPutFile = new File(userFileName);

        PrintWriter userOutput = null;

        try {
            userOutput = new PrintWriter(userPutFile);
        }catch (FileNotFoundException e){
            System.out.println("Sorry I cannot find " + userFileName);
        }
        FileWriter.writeUser(userOutput,a);
        userOutput.close();

    }
    private static void writeUser(PrintWriter user, User u){
        user.println(u.getOTHERNAMES()+","+ u.getSURNAMES());
        ArrayList<Session> userSessions = u.getSessions();
        String list = "";
        for (Session elem : userSessions) {
            Integer[] val = elem.getAllWeightValues();
            if(val.length != 0) {
                list += elem.getDateWrite() + ",";
                for (Integer form : val) {
                    list += form + "," + elem.getFormForWeight(form)[0] + "," + elem.getFormForWeight(form)[1] + "," + elem.getFormForWeight(form)[2] + ";";
                }
                list = list.replaceAll(".$", "\n");
            }
        }
        user.print(list.trim());
    }
}
