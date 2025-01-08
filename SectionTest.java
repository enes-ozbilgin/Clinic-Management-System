package eneseminozbilgin;

import junit.framework.TestCase;

public class SectionTest extends TestCase {
	
	private Section section;

    @Override
    protected void setUp() throws Exception {
        section = new Section(1, "Orthopedics");
    }

    public void testAddDoctor() {
        Doctor doctor = new Doctor("Dr. Smith", 12345, 6789);
        section.addDoctor(doctor);
        assertEquals(1, section.getDoctors().size());
    }

    public void testGetDoctor() {
        Doctor doctor = new Doctor("Dr. Smith", 12345, 6789);
        section.addDoctor(doctor);
        assertNotNull(section.getDoctor(6789));
    }
}
