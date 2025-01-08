package eneseminozbilgin;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CRS implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Long,Patient> patients = new HashMap<Long,Patient>();
	private List<Rendezvous> rendezvous = new LinkedList<Rendezvous>();
	private Map<Integer,Hospital> hospitals = new HashMap<Integer,Hospital>();
    
	public CRS(Map<Long, Patient> patients, List<Rendezvous> rendezvous, Map<Integer, Hospital> hospitals) {
		this.patients = patients;
		this.rendezvous = rendezvous;
		this.hospitals = hospitals;
	}


	public synchronized boolean makeRendezvous(long patientId, int hospitalId, int sectionId, int diplomaId, Date desiredDate) throws IDException {
	    if (!patients.containsKey(patientId)) {
	    	if (!AppConfig.getGuiMode()) {
	    		throw new IDException("Patient ID not found: " + patientId);
            } else {
                System.out.println("Patient ID not found but ignored in GUI mode.");  
            }
	    }

	    if (!hospitals.containsKey(hospitalId)) {
	    	if (!AppConfig.getGuiMode()) {
	    		throw new IDException("Hospital ID not found: " + hospitalId);
            } else {
                System.out.println("Hospital ID not found but ignored in GUI mode.");  
            }
	    }

	    Hospital hospital = hospitals.get(hospitalId);
	    Section section = hospital.getSection(sectionId);
	    Patient patient = patients.get(patientId);
	    
	    if (section == null) {
	    	if (!AppConfig.getGuiMode()) {
	    		throw new IDException("Section ID not found in hospital: " + sectionId);
            } else {
                System.out.println("Section ID not found in hospital but ignored in GUI mode.");  
            }
	    }
	    
	    Doctor doctor = section.getDoctor(diplomaId);
	    
	    if (doctor == null) {
	    	if (!AppConfig.getGuiMode()) {
	    		throw new IDException("Doctor ID not found in section: " + diplomaId);
            } else {
                System.out.println("Doctor ID not found in section but ignored in GUI mode.");  
            }
	    }
	    
	    Schedule schedule = doctor.getSchedule();
	    boolean isAdded = schedule.addRendezvous(patient,desiredDate);

	    if (isAdded) {
	        Rendezvous rendezvousEntry = new Rendezvous(patient,desiredDate);
	        rendezvous.add(rendezvousEntry);
	    }

	    return isAdded;
	}


    public void saveTablesToDisk(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    public static CRS loadTablesToDisk(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (CRS) ois.readObject();
        }
    }


	public Map<Long, Patient> getPatients() {
		return patients;
	}


	public void setPatients(Map<Long, Patient> patients) {
		this.patients = patients;
	}


	public List<Rendezvous> getRendezvous() {
		return rendezvous;
	}


	public void setRendezvous(List<Rendezvous> rendezvous) {
		this.rendezvous = rendezvous;
	}


	public Map<Integer, Hospital> getHospitals() {
		return hospitals;
	}


	public void setHospitals(Map<Integer, Hospital> hospitals) {
		this.hospitals = hospitals;
	}
}
