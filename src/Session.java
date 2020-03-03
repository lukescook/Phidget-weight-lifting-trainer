import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Session {
    private HashMap<Integer, int[]> weights = new HashMap<>();
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatWrite = new SimpleDateFormat("dd,MM,yyyy");
    private Date date;

    public Session(Date d) {
        this.date = d;

    }

    public void addWeight (int weight , int [] form ){
        weights.put(weight,form);
    }

    public Integer[] getAllWeightValues (){
        return weights.keySet().toArray(new Integer[weights.size()]);
    }

    public String getDate() {
        return format.format(date);
    }
    public String getDateWrite() {
        return formatWrite.format(date);
    }

    public int[] getFormForWeight (int i){
        if(weights.containsKey(i)){
            return weights.get(i);
        }
        int[] value = {0,0,0};
        return value ;

    }
    public int getFormForWeightGood (int i){
        if(weights.containsKey(i)){
            return weights.get(i)[0];
        }
        return 0;

    }
    public int getFormForWeightOkay (int i){
        if(weights.containsKey(i)){
            return weights.get(i)[1];
        }
        return 0;

    }
    public int getFormForWeightBad (int i){
        if(weights.containsKey(i)){
            return weights.get(i)[2];
        }
        return 0;

    }
    public void incrementForWeightGood (int i){
        if(weights.containsKey(i)){
            weights.get(i)[0]++;
        }else{
            int[] val = {1,0,0};
            weights.put(i,val);
        }

    }
    public void incrementForWeightOkay (int i){
        if(weights.containsKey(i)){
            weights.get(i)[1]++;
        }else{
            int[] val = {0,1,0};
            weights.put(i,val);
        }

    }
    public void incrementForWeightBad (int i){
        if(weights.containsKey(i)){
            weights.get(i)[2]++;
        }else{
            int[] val = {0,0,1};
            weights.put(i,val);
        }

    }

}
