package eneseminozbilgin;

import junit.framework.TestCase;

public class DoctorTest extends TestCase {
	
	private Doctor doctor;

    @Override
    protected void setUp() throws Exception {
        doctor = new Doctor("Dr.Ken",123,321);
    }

    public void testGetSchedule() {
    	assertNotNull(doctor.getSchedule());
    }

    public void testGetDiplomaId() {
        assertNotNull(doctor.getDiplomaId());
    }
}
