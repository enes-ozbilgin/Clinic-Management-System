package eneseminozbilgin;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Schedule implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Rendezvous> sessions;
    private int maxPatientPerDay;
    private static int counter = -1;

    public Schedule(int maxPatientPerDay) {
        this.maxPatientPerDay = maxPatientPerDay;
        sessions = new LinkedList<Rendezvous>();
    }

    public synchronized boolean addRendezvous(Patient p, Date desired) {
        int count = 0;

        for (Rendezvous e : sessions) {
            if (isSameDay(e.getDate(), desired)) {
                count++;
            }
            if (count >= maxPatientPerDay) {
                return false;
            }
        }

        sessions.add(new Rendezvous(p, desired));
        return true;
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    public String toString() {
    	counter++;
    	return counter + ". Schedule with Max Patient per day of: " + maxPatientPerDay;
    }

	public int getMaxPatientPerDay() {
		return maxPatientPerDay;
	}
    
}

