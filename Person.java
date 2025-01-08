package eneseminozbilgin;

import java.io.Serializable;

public class Person implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private final long nationalId;
	
	public Person(String name, long nationalId) {
		this.name = name;
		this.nationalId = nationalId;
	}
	
	public String toString() {
	    return "Name: " + (this.name == null || this.name.isEmpty() ? "N/A" : this.name) +
	           "\nNational ID: " + this.nationalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getNationalId() {
		return nationalId;
	}
	
}
