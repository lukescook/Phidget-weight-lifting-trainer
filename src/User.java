import java.util.ArrayList;
import java.util.Date;

public class User {
    private final String OTHERNAMES; //Name
    private final String SURNAMES; //Last name
    private ArrayList<Session> sessions = new ArrayList<>();

    public User(String OTHERNAMES, String SURNAMES) {
        this.OTHERNAMES = OTHERNAMES;
        this.SURNAMES = SURNAMES;
    }

    public String getOTHERNAMES() {
        return OTHERNAMES;
    }

    public String getSURNAMES() {
        return SURNAMES;
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void addSession(Session s){
        sessions.add(s);

    }
    public Session addNewSession(){
        Date date = new Date();
        Session session = new Session(date);
        sessions.add(session);
        return session;
    }

    public  Session getLast (){
        return sessions.get(sessions.size()-1);
    }
}
