package eneseminozbilgin;

import java.util.Date;

import junit.framework.TestCase;

public class ScheduleTest extends TestCase {
	
	private Schedule schedule;

    @Override
    protected void setUp() throws Exception {
        schedule = new Schedule(10);
    }

    public void testAddRendezvous_Success() {
        Patient patient = new Patient("Patient 1", 12345);
        assertTrue(schedule.addRendezvous(patient, new Date()));
    }

    public void testAddRendezvous_MaxReached() {
        Patient patient1 = new Patient("Patient 1", 12345);
        Patient patient2 = new Patient("Patient 2", 67890);
        Patient patient3 = new Patient("Patient 3", 11112);

        assertTrue(schedule.addRendezvous(patient1, new Date()));
        assertTrue(schedule.addRendezvous(patient2, new Date()));
        assertFalse(schedule.addRendezvous(patient3, new Date()));
    }
}
