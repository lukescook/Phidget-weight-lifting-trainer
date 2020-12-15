/*import com.sun.deploy.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class FileReaderMain {

    public static final void main(String args[]){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2021);
        cal.set(Calendar.MONTH, 2);
        cal.set(Calendar.DAY_OF_MONTH, 2);
        //Date date = new Date(2020 - 1900 , 3 - 1,2);
        Date date = new Date();
        cal.setTime(date);
        System.out.println(formatter.format(date));
        System.out.println(cal.get(Calendar.YEAR));
        User person = FileReader.findUser("0102389e47.txt");
        System.out.println("///////////////////////////////////////////");
        System.out.println(person.getOTHERNAMES());
        System.out.println(person.getSURNAMES());

        ArrayList<Session> ses = person.getSessions();
        User test = new User("some", "else");
        test.addNewSession();
        //ArrayList<Session> ses = test.getSessions();
        Integer[] tal = ses.get(0).getAllWeightValues();
        System.out.println(tal.length);



        String list = "";
        for (Session elem : ses) {
            Integer[] val = elem.getAllWeightValues();
            if(val.length != 0) {
                list += elem.getDateWrite() + ",";
                for (Integer form : val) {
                    list += form + "," + elem.getFormForWeight(form)[0] + "," + elem.getFormForWeight(form)[1] + "," + elem.getFormForWeight(form)[2] + ";";
                }
                list = list.replaceAll(".$", "\n");
            }
        }
        System.out.println();
        System.out.println(list.trim());
        System.out.println("test");
        FileWriter.writeInfo("01069351f0write.txt",person);
        System.out.println();
        System.out.println();


    }
}
*/