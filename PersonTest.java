package eneseminozbilgin;

import junit.framework.TestCase;

public class PersonTest extends TestCase {
	
	private Person person;

    @Override
    protected void setUp() throws Exception {
    	person = new Person("Enes Özbilgin", 1080);
    }

    public void testGetName() {
        assertNotNull(person.getName());
    }

    public void testGetNationalId() {
        assertNotNull(person.getNationalId());
    }
}
