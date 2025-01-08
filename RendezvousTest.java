package eneseminozbilgin;

import java.util.Date;

import junit.framework.TestCase;

public class RendezvousTest extends TestCase {

	private Rendezvous rendezvous;

    @Override
    protected void setUp() throws Exception {
    	Date date = new Date();
    	Patient patient = new Patient("Adam Fury",135);
        rendezvous = new Rendezvous(patient, date);
    }

    public void testGetDate() {
        assertNotNull(rendezvous.getDate());
    }

    public void testGetPatient() {
        assertNotNull(rendezvous.getPatient());
    }
}
