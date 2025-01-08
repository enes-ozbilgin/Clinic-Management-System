package eneseminozbilgin;

import java.io.Serializable;
import java.util.Date;

public class Rendezvous implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Patient patient;
    private Date dateTime;

    public Rendezvous(Patient patient, Date date) {
        this.patient = patient;
        this.dateTime = date;
    }

    public Date getDate() {
        return dateTime;
    }

    public Patient getPatient() {
        return patient;
    }
}


