package eneseminozbilgin;

import junit.framework.TestCase;

public class HospitalTest extends TestCase {
	private Hospital hospital;

    @Override
    protected void setUp() throws Exception {
        hospital = new Hospital(1, "Hospital of Istanbul");
    }

    public void testAddSection() {
        Section section = new Section(1,"Orthopedics");
        hospital.addSection(section);
        assertEquals(1, hospital.getSections().size());
    }

    public void testGetSection() {
    	Section section = new Section(1,"Orthopedics");
        hospital.addSection(section);
        assertNotNull(hospital.getSection(1));
    }
}
