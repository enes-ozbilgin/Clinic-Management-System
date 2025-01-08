package eneseminozbilgin;

public class Doctor extends Person{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int diplomaId;
    private Schedule schedule;

    public Doctor(String name, long nationalId, int diplomaId) {
        super(name, nationalId);
        this.diplomaId = diplomaId;
        this.schedule = new Schedule(10);
    }

	public Schedule getSchedule() {
        return schedule;
    }

	public int getDiplomaId() {
		return diplomaId;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public String toString() {
		return super.toString() + "\nDiploma Id: " + diplomaId;
	}
    
}

