package eneseminozbilgin;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class CRSTest extends TestCase {
	private CRS crs;

    @Override
    protected void setUp() throws Exception {
        Map<Long, Patient> patients = new HashMap<>();
        List<Rendezvous> rendezvous = new LinkedList<>();
        Map<Integer, Hospital> hospitals = new HashMap<>();
        crs = new CRS(patients, rendezvous, hospitals);
    }

    public void testAddPatient() {
        Patient patient = new Patient("Test Patient", 12345);
        crs.getPatients().put(patient.getNationalId(), patient);
        assertTrue(crs.getPatients().containsKey(12345L));
    }

    public void testAddRendezvous() {
        Patient patient = new Patient("Test Patient", 12345);
        crs.getPatients().put(patient.getNationalId(), patient);
        Rendezvous rv = new Rendezvous(patient, new Date());
        crs.getRendezvous().add(rv);
        assertTrue(crs.getRendezvous().contains(rv));
    }
}
