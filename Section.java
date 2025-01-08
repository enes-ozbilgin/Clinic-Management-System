package eneseminozbilgin;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Section implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int id;
	private String name;
	private List<Doctor> doctors;
	
	public Section(int id, String name) {
		this.id = id;
		this.name = name;
		doctors = new LinkedList<Doctor>();
	}
	 public void listDoctors() {
	    if (doctors.isEmpty()) {
	       System.out.println("No doctors in this section.");
	       return;
	    }
	    int i = 0;
	    for (Doctor e : doctors) {
	        i++;
	        System.out.println(i + ". Doctor\n" + e);
	    }
	}
	public Doctor getDoctor(int diplomaId) {
		for(Doctor e : doctors) {
			if( e.getDiplomaId() == diplomaId) {
				return e;
			}
		}
		System.out.println("Doctor not found");
		return null;
	}
	public List<Doctor> getDoctors() {
		return doctors;
	}
	public void addDoctor(Doctor doctor) throws DuplicateInfoException {
	    	if(doctors.contains(doctor)) {
	    		if (!AppConfig.getGuiMode()) {
	                throw new DuplicateInfoException("This doctor has already been added.");
	            } else {
	                System.out.println("Duplicate doctor detected but ignored in GUI mode.");
	                return;
	            }
	    	} else {
	    		doctors.add(doctor);
	    	}
	}
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	public String toString() {
        return "Section ID: " + this.id + ", Name: " + this.name + ", Doctors: " + this.doctors.size();
    }
}

