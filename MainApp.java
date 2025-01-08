package eneseminozbilgin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings("unused")
public class MainApp implements Runnable {

    private static ArrayList<Section> sections = new ArrayList<>();
    private static Map<Integer, Hospital> hospitals = new HashMap<>();
    private static ArrayList<Schedule> schedules = new ArrayList<>();

    private static Map<Long, Patient> patients = new HashMap<>();
    private static List<Rendezvous> rendezvous = new LinkedList<>();

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("In what mode would you like to run the program? 1 -> Gui 0 -> Console");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            AppConfig.setGuiMode(true);
            System.out.println("Switching to GUI mode.");
            launchGui();
        } else if (choice == 0) {
            AppConfig.setGuiMode(false);
            System.out.println("Switching to Console mode.");
            
            System.out.println("Will multiple users use the console? (yes/no)");
            String multiUser = scanner.nextLine().trim().toLowerCase();
            
            if (multiUser.equals("yes")) {
                AppConfig.setGuiMode(false);
                System.out.println("Starting console in multi-user mode...");

                Thread user1Thread = new Thread(new MainApp(), "User1");

                user1Thread.start();

                try {
                    user1Thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Starting console in single-user mode...");
                AppConfig.setGuiMode(false);
                launchConsole();
            }
        } else {
            System.out.println("Invalid input, exiting program.");
        }
    }
    
    @Override
    public void run() {
        launchConsole();
    }

    private static void launchGui() {
        Map<Long, Patient> patients = new HashMap<>();
        List<Rendezvous> rendezvous = new LinkedList<>();
        CRS crs = new CRS(patients, rendezvous, hospitals);

        JFrame frame = new JFrame("Clinic Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JLabel welcomeLabel = new JLabel("Welcome to the Clinic Management System");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton sectionManagementButton = new JButton("Section Management");
        JButton hospitalManagementButton = new JButton("Hospital Management");
        JButton scheduleManagementButton = new JButton("Schedule Management");
        JButton crsManagementButton = new JButton("CRS Management");
        JButton switchToConsoleButton = new JButton("Switch to Console Mode");
        JButton exitButton = new JButton("Exit");

        sectionManagementButton.addActionListener(e -> openSectionManagement());
        hospitalManagementButton.addActionListener(e -> openHospitalManagement(crs));
        scheduleManagementButton.addActionListener(e -> openScheduleManagement(crs));
        crsManagementButton.addActionListener(e -> openCRSManagement(crs));
        switchToConsoleButton.addActionListener(e -> {
            frame.dispose();
            launchConsole();
        });
        exitButton.addActionListener(e -> System.exit(0));

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.add(welcomeLabel);
        panel.add(sectionManagementButton);
        panel.add(hospitalManagementButton);
        panel.add(scheduleManagementButton);
        panel.add(crsManagementButton);
        panel.add(switchToConsoleButton);
        panel.add(exitButton);

        frame.add(panel);
        frame.setVisible(true);
    }
    
    private static void openSectionManagement() {
        JFrame frame = new JFrame("Section Management");
        frame.setSize(400, 300);

        JLabel label = new JLabel("Section Management");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton addDoctorButton = new JButton("Add Doctor");
        JButton listDoctorsButton = new JButton("List Doctors");
        JButton findDoctorButton = new JButton("Find Doctor");
        JButton createSectionButton = new JButton("Create Section");
        JButton backButton = new JButton("Back");

        addDoctorButton.addActionListener(e -> addDoctorGui());
        listDoctorsButton.addActionListener(e -> listDoctorsGui());
        findDoctorButton.addActionListener(e -> findDoctorGui());
        createSectionButton.addActionListener(e -> createSectionGui());
        backButton.addActionListener(e -> frame.dispose());

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.add(label);
        panel.add(addDoctorButton);
        panel.add(listDoctorsButton);
        panel.add(findDoctorButton);
        panel.add(createSectionButton);
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
    }
    
    private static void createSectionGui() {
        JFrame frame = new JFrame("Create Section");
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel nameLabel = new JLabel("Enter Section Name:");
        JTextField nameField = new JTextField();
        JLabel idLabel = new JLabel("Enter Section ID:");
        JTextField idField = new JTextField();
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String idText = idField.getText().trim();

            if (name.isEmpty() || idText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int id = Integer.parseInt(idText);
                Section section = new Section(id, name);
                sections.add(section);
                JOptionPane.showMessageDialog(frame, "Section created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Section ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(idLabel);
        frame.add(idField);
        frame.add(createButton);
        frame.add(cancelButton);

        frame.setVisible(true);
    }

    private static void findDoctorGui() {
        if (sections.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sections available to find doctors.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = new JFrame("Find Doctor");
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel sectionLabel = new JLabel("Select Section:");
        JComboBox<String> sectionDropdown = new JComboBox<>();
        sections.forEach(section -> sectionDropdown.addItem(section.getName() + " (ID: " + section.getId() + ")"));

        JLabel diplomaIdLabel = new JLabel("Enter Diploma ID:");
        JTextField diplomaIdField = new JTextField();

        JButton findButton = new JButton("Find Doctor");
        JButton cancelButton = new JButton("Cancel");

        findButton.addActionListener(e -> {
            int selectedIndex = sectionDropdown.getSelectedIndex();
            String diplomaIdText = diplomaIdField.getText().trim();

            if (diplomaIdText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a diploma ID!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int diplomaId = Integer.parseInt(diplomaIdText);
                Section selectedSection = sections.get(selectedIndex);
                Doctor doctor = selectedSection.getDoctor(diplomaId);
                if (doctor != null) {
                    JOptionPane.showMessageDialog(frame, "Doctor found: " + doctor, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "No doctor found with Diploma ID: " + diplomaId, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Diploma ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.add(sectionLabel);
        frame.add(sectionDropdown);
        frame.add(diplomaIdLabel);
        frame.add(diplomaIdField);
        frame.add(findButton);
        frame.add(cancelButton);

        frame.setVisible(true);
    }


	private static void listDoctorsGui() {
	    if (sections.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "No sections available to list doctors.", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    JFrame frame = new JFrame("List Doctors");
	    frame.setSize(400, 300);
	    frame.setLayout(new BorderLayout(10, 10));

	    JLabel sectionLabel = new JLabel("Select Section:");
	    JComboBox<String> sectionDropdown = new JComboBox<>();
	    sections.forEach(section -> sectionDropdown.addItem(section.getName() + " (ID: " + section.getId() + ")"));

	    JTextArea doctorListArea = new JTextArea();
	    doctorListArea.setEditable(false);

	    JButton viewButton = new JButton("View Doctors");
	    JButton backButton = new JButton("Back");

	    viewButton.addActionListener(e -> {
	        int selectedIndex = sectionDropdown.getSelectedIndex();
	        Section selectedSection = sections.get(selectedIndex);
	        StringBuilder doctorList = new StringBuilder();
	        selectedSection.getDoctors().forEach(doctor -> 
	            doctorList.append("Name: ").append(doctor.getName())
	                      .append(", National ID: ").append(doctor.getNationalId())
	                      .append(", Diploma ID: ").append(doctor.getDiplomaId()).append("\n")
	        );

	        if (doctorList.length() == 0) {
	            doctorListArea.setText("No doctors found in this section.");
	        } else {
	            doctorListArea.setText(doctorList.toString());
	        }
	    });

	    backButton.addActionListener(e -> frame.dispose());

	    JPanel topPanel = new JPanel(new BorderLayout());
	    topPanel.add(sectionLabel, BorderLayout.NORTH);
	    topPanel.add(sectionDropdown, BorderLayout.CENTER);

	    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
	    buttonPanel.add(viewButton);
	    buttonPanel.add(backButton);

	    frame.add(topPanel, BorderLayout.NORTH);
	    frame.add(new JScrollPane(doctorListArea), BorderLayout.CENTER);
	    frame.add(buttonPanel, BorderLayout.SOUTH);

	    frame.setVisible(true);
	}


	private static void addDoctorGui() {
	    if (sections.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "No sections available. Please create a section first.", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    JFrame frame = new JFrame("Add Doctor");
	    frame.setSize(400, 300);
	    frame.setLayout(new GridLayout(6, 2, 10, 10));

	    JLabel sectionLabel = new JLabel("Select Section:");
	    JComboBox<String> sectionDropdown = new JComboBox<>();
	    sections.forEach(section -> sectionDropdown.addItem(section.getName() + " (ID: " + section.getId() + ")"));

	    JLabel nameLabel = new JLabel("Enter Doctor Name:");
	    JTextField nameField = new JTextField();
	    JLabel nationalIdLabel = new JLabel("Enter National ID:");
	    JTextField nationalIdField = new JTextField();
	    JLabel diplomaIdLabel = new JLabel("Enter Diploma ID:");
	    JTextField diplomaIdField = new JTextField();

	    JButton addButton = new JButton("Add Doctor");
	    JButton cancelButton = new JButton("Cancel");

	    addButton.addActionListener(e -> {
	        String name = nameField.getText().trim();
	        String nationalIdText = nationalIdField.getText().trim();
	        String diplomaIdText = diplomaIdField.getText().trim();
	        int selectedIndex = sectionDropdown.getSelectedIndex();

	        if (name.isEmpty() || nationalIdText.isEmpty() || diplomaIdText.isEmpty()) {
	            JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        try {
	            int nationalId = Integer.parseInt(nationalIdText);
	            int diplomaId = Integer.parseInt(diplomaIdText);
	            Section selectedSection = sections.get(selectedIndex);
	            Doctor doctor = new Doctor(name, nationalId, diplomaId);
	            selectedSection.addDoctor(doctor);
	            JOptionPane.showMessageDialog(frame, "Doctor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	            frame.dispose();
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(frame, "Invalid ID. Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
	        } catch (DuplicateInfoException ex) {
	            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    cancelButton.addActionListener(e -> frame.dispose());

	    frame.add(sectionLabel);
	    frame.add(sectionDropdown);
	    frame.add(nameLabel);
	    frame.add(nameField);
	    frame.add(nationalIdLabel);
	    frame.add(nationalIdField);
	    frame.add(diplomaIdLabel);
	    frame.add(diplomaIdField);
	    frame.add(addButton);
	    frame.add(cancelButton);

	    frame.setVisible(true);
	}


	private static void openHospitalManagement(CRS crs) {
        JFrame frame = new JFrame("Hospital Management");
        frame.setSize(400, 300);

        JLabel label = new JLabel("Hospital Management");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton addSectionButton = new JButton("Add Section");
        JButton findSectionButton = new JButton("Find Section");
        JButton createHospitalButton = new JButton("Create Hospital");
        JButton backButton = new JButton("Back");

        addSectionButton.addActionListener(e -> addSectionGui(crs));
        findSectionButton.addActionListener(e -> findSectionGui());
        createHospitalButton.addActionListener(e -> createHospitalGui(crs));
        backButton.addActionListener(e -> frame.dispose());

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.add(label);
        panel.add(addSectionButton);
        panel.add(findSectionButton);
        panel.add(createHospitalButton);
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
    }

	private static void createHospitalGui(CRS crs) {
	    JFrame frame = new JFrame("Create Hospital");
	    frame.setSize(400, 200);
	    frame.setLayout(new GridLayout(4, 2, 10, 10));

	    JLabel nameLabel = new JLabel("Enter Hospital Name:");
	    JTextField nameField = new JTextField();
	    JLabel idLabel = new JLabel("Enter Hospital ID:");
	    JTextField idField = new JTextField();
	    JButton createButton = new JButton("Create");
	    JButton cancelButton = new JButton("Cancel");

	    createButton.addActionListener(e -> {
	        String name = nameField.getText().trim();
	        String idText = idField.getText().trim();

	        if (name.isEmpty() || idText.isEmpty()) {
	            JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        try {
	            int id = Integer.parseInt(idText);

	            Map<Integer, Hospital> hospitals = crs.getHospitals();
	            if (hospitals.containsKey(id)) {
	                JOptionPane.showMessageDialog(frame, "Hospital ID already exists. Please choose a unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            Hospital hospital = new Hospital(id, name);
	            hospitals.put(id, hospital);
	            crs.setHospitals(hospitals);
	            JOptionPane.showMessageDialog(frame, "Hospital created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	            frame.dispose();
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(frame, "Invalid Hospital ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    cancelButton.addActionListener(e -> frame.dispose());

	    frame.add(nameLabel);
	    frame.add(nameField);
	    frame.add(idLabel);
	    frame.add(idField);
	    frame.add(createButton);
	    frame.add(cancelButton);

	    frame.setVisible(true);
	}



	private static void findSectionGui() {
	    if (hospitals.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "No hospitals available to search sections.", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    JFrame frame = new JFrame("Find Section");
	    frame.setSize(400, 200);
	    frame.setLayout(new GridLayout(4, 2, 10, 10));

	    JLabel hospitalLabel = new JLabel("Select Hospital:");
	    JComboBox<String> hospitalDropdown = new JComboBox<>();
	    hospitals.values().forEach(hospital -> hospitalDropdown.addItem(hospital.getName() + " (ID: " + hospital.getId() + ")"));

	    JLabel sectionIdLabel = new JLabel("Enter Section ID:");
	    JTextField sectionIdField = new JTextField();

	    JButton findButton = new JButton("Find Section");
	    JButton cancelButton = new JButton("Cancel");

	    findButton.addActionListener(e -> {
	        String sectionIdText = sectionIdField.getText().trim();
	        int selectedIndex = hospitalDropdown.getSelectedIndex();

	        if (sectionIdText.isEmpty()) {
	            JOptionPane.showMessageDialog(frame, "Please enter a section ID!", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        try {
	            int sectionId = Integer.parseInt(sectionIdText);
	            Hospital selectedHospital = (Hospital) hospitals.values().toArray()[selectedIndex];
	            Section section = selectedHospital.getSection(sectionId);

	            if (section != null) {
	                JOptionPane.showMessageDialog(frame, "Section found: " + section.getName() + " (ID: " + section.getId() + ")", "Success", JOptionPane.INFORMATION_MESSAGE);
	            } else {
	                JOptionPane.showMessageDialog(frame, "No section found with ID: " + sectionId, "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(frame, "Invalid Section ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    cancelButton.addActionListener(e -> frame.dispose());

	    frame.add(hospitalLabel);
	    frame.add(hospitalDropdown);
	    frame.add(sectionIdLabel);
	    frame.add(sectionIdField);
	    frame.add(findButton);
	    frame.add(cancelButton);

	    frame.setVisible(true);
	}


	private static void addSectionGui(CRS crs) {
	    if (hospitals.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "No hospitals available. Please create a hospital first.", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    JFrame frame = new JFrame("Add Section to Hospital");
	    frame.setSize(400, 400);
	    frame.setLayout(new GridLayout(8, 2, 10, 10)); // Updated to accommodate new fields

	    JLabel hospitalLabel = new JLabel("Select Hospital:");
	    JComboBox<String> hospitalDropdown = new JComboBox<>();
	    hospitals.values().forEach(hospital -> hospitalDropdown.addItem(hospital.getName() + " (ID: " + hospital.getId() + ")"));

	    JLabel sectionLabel = new JLabel("Select Existing Section:");
	    JComboBox<String> sectionDropdown = new JComboBox<>();
	    sections.forEach(section -> sectionDropdown.addItem(section.getName() + " (ID: " + section.getId() + ")"));

	    JLabel createNewSectionLabel = new JLabel("Or Create New Section:");
	    JLabel nameLabel = new JLabel("Enter Section Name:");
	    JTextField newNameField = new JTextField();
	    JLabel idLabel = new JLabel("Enter Section ID:");
	    JTextField newIdField = new JTextField();

	    JButton addButton = new JButton("Add Section");
	    JButton cancelButton = new JButton("Cancel");

	    addButton.addActionListener(e -> {
	        int hospitalIndex = hospitalDropdown.getSelectedIndex();
	        int sectionIndex = sectionDropdown.getSelectedIndex();
	        Hospital selectedHospital = (Hospital) hospitals.values().toArray()[hospitalIndex];

	        if (sectionIndex >= 0) {
	            // Add existing section
	            Section selectedSection = sections.get(sectionIndex);
	            try {
	                selectedHospital.addSection(selectedSection);
	                JOptionPane.showMessageDialog(frame, "Section added successfully to " + selectedHospital.getName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
	                frame.dispose();
	            } catch (DuplicateInfoException ex) {
	                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        } else {
	            // Create and add new section
	            String newName = newNameField.getText().trim();
	            String newIdText = newIdField.getText().trim();

	            if (newName.isEmpty() || newIdText.isEmpty()) {
	                JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            try {
	                int newId = Integer.parseInt(newIdText);
	                Section newSection = new Section(newId, newName);
	                sections.add(newSection);
	                selectedHospital.addSection(newSection);
	                JOptionPane.showMessageDialog(frame, "New section created and added to " + selectedHospital.getName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
	                frame.dispose();
	            } catch (NumberFormatException ex) {
	                JOptionPane.showMessageDialog(frame, "Invalid Section ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
	            } catch (DuplicateInfoException ex) {
	                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    });

	    cancelButton.addActionListener(e -> frame.dispose());

	    frame.add(hospitalLabel);
	    frame.add(hospitalDropdown);
	    frame.add(sectionLabel);
	    frame.add(sectionDropdown);
	    frame.add(createNewSectionLabel);
	    frame.add(new JLabel()); // Empty placeholder for alignment
	    frame.add(nameLabel);
	    frame.add(newNameField);
	    frame.add(idLabel);
	    frame.add(newIdField);
	    frame.add(addButton);
	    frame.add(cancelButton);

	    frame.setVisible(true);
	}

	private static void openScheduleManagement(CRS crs) {
        JFrame frame = new JFrame("Schedule Management");
        frame.setSize(400, 300);

        JLabel label = new JLabel("Schedule Management");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton addRendezvousButton = new JButton("Add Rendezvous");
        JButton addPatientButton = new JButton("Add Patient");
        JButton viewSchedulesButton = new JButton("View Schedules");
        JButton createScheduleButton = new JButton("Create Schedule");
        JButton backButton = new JButton("Back");

        addRendezvousButton.addActionListener(e -> addRendezvousGui(crs));
        addPatientButton.addActionListener(e -> addPatientGui(crs));
        viewSchedulesButton.addActionListener(e -> viewSchedulesGui());
        createScheduleButton.addActionListener(e -> createScheduleGui());
        backButton.addActionListener(e -> frame.dispose());

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.add(label);
        panel.add(addRendezvousButton);
        panel.add(addPatientButton);
        panel.add(viewSchedulesButton);
        panel.add(createScheduleButton);
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
    }

	private static void createScheduleGui() {
	    JFrame frame = new JFrame("Create Schedule");
	    frame.setSize(400, 200);
	    frame.setLayout(new GridLayout(3, 2, 10, 10));

	    JLabel maxPatientsLabel = new JLabel("Enter Max Patients Per Day:");
	    JTextField maxPatientsField = new JTextField();
	    JButton createButton = new JButton("Create");
	    JButton cancelButton = new JButton("Cancel");

	    createButton.addActionListener(e -> {
	        String maxPatientsText = maxPatientsField.getText().trim();

	        if (maxPatientsText.isEmpty()) {
	            JOptionPane.showMessageDialog(frame, "Please fill the field!", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        try {
	            int maxPatients = Integer.parseInt(maxPatientsText);
	            Schedule schedule = new Schedule(maxPatients);
	            schedules.add(schedule);
	            JOptionPane.showMessageDialog(frame, "Schedule created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	            frame.dispose();
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    cancelButton.addActionListener(e -> frame.dispose());

	    frame.add(maxPatientsLabel);
	    frame.add(maxPatientsField);
	    frame.add(createButton);
	    frame.add(cancelButton);

	    frame.setVisible(true);
	}

	private static void viewSchedulesGui() {
	    if (schedules.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "No schedules available.", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    JFrame frame = new JFrame("View Schedules");
	    frame.setSize(400, 300);
	    frame.setLayout(new BorderLayout(10, 10));

	    JTextArea schedulesArea = new JTextArea();
	    schedulesArea.setEditable(false);
	    StringBuilder schedulesText = new StringBuilder();

	    for (Schedule schedule : schedules) {
	        schedulesText.append(schedule).append("\n");
	    }

	    schedulesArea.setText(schedulesText.toString());

	    JButton closeButton = new JButton("Close");
	    closeButton.addActionListener(e -> frame.dispose());

	    frame.add(new JScrollPane(schedulesArea), BorderLayout.CENTER);
	    frame.add(closeButton, BorderLayout.SOUTH);

	    frame.setVisible(true);
	}


	private static void addPatientGui(CRS crs) {
	    JFrame frame = new JFrame("Add Patient");
	    frame.setSize(400, 200);
	    frame.setLayout(new GridLayout(3, 2, 10, 10));

	    JLabel nameLabel = new JLabel("Enter Patient Name:");
	    JTextField nameField = new JTextField();
	    JLabel idLabel = new JLabel("Enter National ID:");
	    JTextField idField = new JTextField();
	    JButton addButton = new JButton("Add Patient");
	    JButton cancelButton = new JButton("Cancel");

	    addButton.addActionListener(e -> {
	        String name = nameField.getText().trim();
	        String idText = idField.getText().trim();

	        if (name.isEmpty() || idText.isEmpty()) {
	            JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        try {
	            long nationalId = Long.parseLong(idText);
	            Patient patient = new Patient(name, nationalId);
	            crs.getPatients().put(nationalId, patient);
	            JOptionPane.showMessageDialog(frame, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	            frame.dispose();
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(frame, "Invalid ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    cancelButton.addActionListener(e -> frame.dispose());

	    frame.add(nameLabel);
	    frame.add(nameField);
	    frame.add(idLabel);
	    frame.add(idField);
	    frame.add(addButton);
	    frame.add(cancelButton);

	    frame.setVisible(true);
	}


	private static void addRendezvousGui(CRS crs) {
	    if (schedules.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "No schedules available. Please create a schedule first.", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    JFrame frame = new JFrame("Add Rendezvous");
	    frame.setSize(400, 300);
	    frame.setLayout(new GridLayout(5, 2, 10, 10));

	    JLabel scheduleLabel = new JLabel("Select Schedule:");
	    JComboBox<String> scheduleDropdown = new JComboBox<>();
	    schedules.forEach(schedule -> scheduleDropdown.addItem(schedule.toString()));

	    JLabel patientIdLabel = new JLabel("Enter Patient National ID:");
	    JTextField patientIdField = new JTextField();
	    JLabel dateLabel = new JLabel("Enter Date (yyyy-MM-dd):");
	    JTextField dateField = new JTextField();

	    JButton addButton = new JButton("Add Rendezvous");
	    JButton cancelButton = new JButton("Cancel");

	    addButton.addActionListener(e -> {
	        String patientIdText = patientIdField.getText().trim();
	        String dateText = dateField.getText().trim();
	        int selectedIndex = scheduleDropdown.getSelectedIndex();

	        if (patientIdText.isEmpty() || dateText.isEmpty()) {
	            JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        try {
	            long patientId = Long.parseLong(patientIdText);
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            Date date = sdf.parse(dateText);

	            // Fetch the patient from the existing CRS object
	            Patient patient = crs.getPatients().get(patientId);

	            if (patient == null) {
	                JOptionPane.showMessageDialog(frame, "Patient not found. Please add the patient first.", "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            Schedule selectedSchedule = schedules.get(selectedIndex);
	            if (selectedSchedule.addRendezvous(patient, date)) {
	            	List<Rendezvous> rendezvous = crs.getRendezvous();
	        	    rendezvous.add(new Rendezvous(patient, date));
	        	    crs.setRendezvous(rendezvous);
	                JOptionPane.showMessageDialog(frame, "Rendezvous added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	                frame.dispose();
	            } else {
	                JOptionPane.showMessageDialog(frame, "Failed to add rendezvous. Max patients reached for this day.", "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(frame, "Invalid Patient ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
	        } catch (ParseException ex) {
	            JOptionPane.showMessageDialog(frame, "Invalid date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });
	    		
	    cancelButton.addActionListener(e -> frame.dispose());

	    frame.add(scheduleLabel);
	    frame.add(scheduleDropdown);
	    frame.add(patientIdLabel);
	    frame.add(patientIdField);
	    frame.add(dateLabel);
	    frame.add(dateField);
	    frame.add(addButton);
	    frame.add(cancelButton);

	    frame.setVisible(true);
	}
	
	private static void openCRSManagement(CRS crs) {
        JFrame frame = new JFrame("CRS Management");
        frame.setSize(400, 300);

        JLabel label = new JLabel("CRS Management");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton makeRendezvousButton = new JButton("Make Rendezvous");
        JButton saveSystemButton = new JButton("Save System Data");
        JButton loadSystemButton = new JButton("Load System Data");
        JButton backButton = new JButton("Back");

        makeRendezvousButton.addActionListener(e -> makeRendezvousGui(crs));
        saveSystemButton.addActionListener(e -> saveSystemDataGui());
        loadSystemButton.addActionListener(e -> loadSystemDataGui());
        backButton.addActionListener(e -> frame.dispose());

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.add(label);
        panel.add(makeRendezvousButton);
        panel.add(saveSystemButton);
        panel.add(loadSystemButton);
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
    }

	private static void loadSystemDataGui() {
	    JFrame frame = new JFrame("Load System Data");
	    frame.setSize(400, 200);
	    frame.setLayout(new GridLayout(2, 1, 10, 10));

	    JLabel messageLabel = new JLabel("Click the button below to load system data:");
	    JButton loadButton = new JButton("Load Data");

	    loadButton.addActionListener(e -> {
	        try {
	            CRS crs = CRS.loadTablesToDisk("SystemData.ser");
	            hospitals = crs.getHospitals();
	            JOptionPane.showMessageDialog(frame, "System data loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	            frame.dispose();
	        } catch (IOException | ClassNotFoundException ex) {
	            JOptionPane.showMessageDialog(frame, "Failed to load system data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    frame.add(messageLabel);
	    frame.add(loadButton);
	    frame.setVisible(true);
	}


    private static void saveSystemDataGui() {
        JFrame frame = new JFrame("Save System Data");
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(2, 1, 10, 10));

        JLabel messageLabel = new JLabel("Click the button below to save system data:");
        JButton saveButton = new JButton("Save Data");

        saveButton.addActionListener(e -> {
            try {
                CRS crs = new CRS(new HashMap<>(), new LinkedList<>(), hospitals);
                crs.saveTablesToDisk("SystemData.ser");
                JOptionPane.showMessageDialog(frame, "System data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Failed to save system data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(messageLabel);
        frame.add(saveButton);
        frame.setVisible(true);
    }


    private static void makeRendezvousGui(CRS crs) {
        if (hospitals.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hospitals available. Please create a hospital first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = new JFrame("Make Rendezvous");
        frame.setSize(500, 400);
        frame.setLayout(new GridLayout(7, 2, 10, 10));

        JLabel patientIdLabel = new JLabel("Enter Patient ID:");
        JTextField patientIdField = new JTextField();
        JLabel hospitalLabel = new JLabel("Select Hospital:");
        JComboBox<String> hospitalDropdown = new JComboBox<>();
        JLabel sectionLabel = new JLabel("Select Section:");
        JComboBox<String> sectionDropdown = new JComboBox<>();
        JLabel doctorLabel = new JLabel("Enter Doctor Diploma ID:");
        JTextField diplomaIdField = new JTextField();
        JLabel dateLabel = new JLabel("Enter Date (yyyy-MM-dd):");
        JTextField dateField = new JTextField();

        // Populate hospitals dropdown
        hospitals.values().forEach(hospital -> hospitalDropdown.addItem(hospital.getName() + " (ID: " + hospital.getId() + ")"));

        hospitalDropdown.addActionListener(e -> {
            sectionDropdown.removeAllItems(); // Clear previous sections
            int hospitalIndex = hospitalDropdown.getSelectedIndex();
            if (hospitalIndex >= 0) {
                Hospital selectedHospital = (Hospital) hospitals.values().toArray()[hospitalIndex];
                selectedHospital.getSections().forEach(section -> sectionDropdown.addItem(section.getName() + " (ID: " + section.getId() + ")"));
            }
        });

        JButton makeRendezvousButton = new JButton("Make Rendezvous");
        JButton cancelButton = new JButton("Cancel");

        makeRendezvousButton.addActionListener(e -> {
            String patientIdText = patientIdField.getText().trim();
            String diplomaIdText = diplomaIdField.getText().trim();
            String dateText = dateField.getText().trim();

            if (patientIdText.isEmpty() || diplomaIdText.isEmpty() || dateText.isEmpty() || sectionDropdown.getItemCount() == 0) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                long patientId = Long.parseLong(patientIdText);
                int diplomaId = Integer.parseInt(diplomaIdText);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(dateText);

                int hospitalIndex = hospitalDropdown.getSelectedIndex();
                int sectionIndex = sectionDropdown.getSelectedIndex();
                Hospital selectedHospital = (Hospital) hospitals.values().toArray()[hospitalIndex];
                Section selectedSection = (Section) selectedHospital.getSections().toArray()[sectionIndex];

                crs.makeRendezvous(patientId, selectedHospital.getId(), selectedSection.getId(), diplomaId, date);
                JOptionPane.showMessageDialog(frame, "Rendezvous successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid ID or Date format. Please check your inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IDException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.add(patientIdLabel);
        frame.add(patientIdField);
        frame.add(hospitalLabel);
        frame.add(hospitalDropdown);
        frame.add(sectionLabel);
        frame.add(sectionDropdown);
        frame.add(doctorLabel);
        frame.add(diplomaIdField);
        frame.add(dateLabel);
        frame.add(dateField);
        frame.add(makeRendezvousButton);
        frame.add(cancelButton);

        frame.setVisible(true);
    }

    private static void launchConsole() {
        Map<Long, Patient> patients = new HashMap<>();
        List<Rendezvous> rendezvous = new LinkedList<>();
        CRS crs = new CRS(patients, rendezvous, hospitals);

        int x = 0;

        System.out.println("Launching console...");

        while (x != 6) { // Updated menu to include switching to GUI mode
            System.out.println("Choose one:");
            System.out.println("Section Management <-- 1");
            System.out.println("Hospital Management <-- 2");
            System.out.println("Schedule Management <-- 3");
            System.out.println("CRS Management <-- 4");
            System.out.println("Switch to GUI mode <-- 5");
            System.out.println("Exit <-- 6");

            x = scanner.nextInt();
            scanner.nextLine();

            switch (x) {
                case 1:
                    section_management();
                    break;
                case 2:
                    hospital_management(crs);
                    break;
                case 3:
                    schedule_management(crs);
                    break;
                case 4:
                    crs_management(crs);
                    break;
                case 5:
                    System.out.println("Switching to GUI mode...");
                    launchGui();
                    return;
                case 6:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        }
    }

    private static void section_management() {
        System.out.println("Section Management Operations:");
        System.out.println("Add Doctor <-- 1");
        System.out.println("List Doctors <-- 2");
        System.out.println("Find Doctor (via Diploma) <-- 3");
        System.out.println("Create new Section <-- 4");
        System.out.println("Return to Main Menu <-- 5");

        int choice = scanner.nextInt();
        scanner.nextLine();
        Section section = null;
        if(choice == 1 || choice == 2 || choice == 3) {
        	if(sections.isEmpty()) {
        		System.out.println("No Sections Avaliable");
        		System.out.println("Creating Section...");
        		section = createSection();
        		sections.add(section);
        	} else {
        		for(Section e : sections) {
        			System.out.println(e);
        		}
        		System.out.println("Enter Id of the section in which the Operation will be carried out:");
            	int Id = scanner.nextInt();
            	for(Section e : sections) {
            		if(e.getId() == Id) {
            			section = e;
            		}
            	}
        	}
        }
        
        switch (choice) {
            case 1:
                System.out.println("Enter doctor name:");
                String name1 = scanner.nextLine();
                scanner.nextLine();
                System.out.println("Enter national ID:");
                int nationalId = scanner.nextInt();
                System.out.println("Enter diploma ID:");
                int diplomaId = scanner.nextInt();
                scanner.nextLine();
                Doctor doctor1 = new Doctor(name1,nationalId,diplomaId);
                try {
                	section.addDoctor(doctor1);
                } catch(Exception e) {
                	e.printStackTrace();
                }
                System.out.println("Doctor " + name1 + " with national ID " + nationalId + " with diploma ID " + diplomaId + " added.");
                break;
            case 2:
            	System.out.println("Listing all doctors...");
                section.listDoctors();
                break;
            case 3:
                System.out.println("Enter diploma ID to find doctor:");
                int diplomaId1 = scanner.nextInt();
                scanner.nextLine();
                Doctor doctor = section.getDoctor(diplomaId1);
                System.out.println("Doctor: " + doctor.getName() + " with national ID " + doctor.getNationalId() + " found.");
                break;
            case 4:
            	Section section1 = createSection();
            	sections.add(section1);
            	break;
            case 5:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("Invalid choice, returning to main menu.");
                break;
        }
    }

    private static Section createSection() {
    	System.out.println("Enter Section name:");
        String section_name = scanner.nextLine();
        System.out.println("Enter Section ID:");
        int section_Id = scanner.nextInt();
        scanner.nextLine(); 
        Section section = new Section(section_Id,section_name);
		return section;
	}

	private static void hospital_management(CRS crs) {
        System.out.println("Hospital Management Operations:");
        System.out.println("Add Section <-- 1");
        System.out.println("Find Section (via ID) <-- 2");
        System.out.println("Create Hospital <-- 3");
        System.out.println("Return to Main Menu <-- 4");
        
        hospitals = crs.getHospitals();
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        Hospital hospital = null;
        if(choice == 1 || choice == 2 || choice == 3) {
        	if(hospitals.isEmpty()) {
        		System.out.println("No Hospitals Avaliable");
        		System.out.println("Creating Hospital...");
        		hospital = createHospital();
        		hospitals.put(hospital.getId(), hospital);
        	} else {
        		for(Hospital e : hospitals.values()) {
        			System.out.println(e);
        		}
        		System.out.println("Enter Id of the hospital in which the Operation will be carried out:");
            	int Id = scanner.nextInt();
            	hospital = hospitals.get(Id);
        	}
        }

        switch (choice) {
            case 1:
                System.out.println("Enter section name:");
                String sectionName = scanner.nextLine();
                scanner.nextLine(); 
                System.out.println("Enter section ID:");
                int sectionId = scanner.nextInt();
                scanner.nextLine();
                Section section = new Section(sectionId,sectionName);
                try {
                	hospital.addSection(section);
                    System.out.println("Section " + sectionName + " with ID " + sectionId + " added.");
                } catch(DuplicateInfoException e) {
                e.printStackTrace();	
                }
                break;
            case 2:
                System.out.println("Enter section ID to find:");
                int findSectionId = scanner.nextInt();
                scanner.nextLine();
                Section section1 = hospital.getSection(findSectionId);
                System.out.println("Section: " + section1.getName() + "With Section Id: " + findSectionId + " found.");
                break;
            case 3:
            	Hospital hospital1 = createHospital();
            	hospitals.put(hospital1.getId(), hospital1);
            	break;
            case 4:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("Invalid choice, returning to main menu.");
                break;
        }
        crs.setHospitals(hospitals);//update
    }

    private static Hospital createHospital() {
    	System.out.println("Enter Hospital name:");
        String hospital_name = scanner.nextLine();
        System.out.println("Enter Hospital ID:");
        int hospital_Id = scanner.nextInt();
        scanner.nextLine(); 
        Hospital hospital = new Hospital(hospital_Id,hospital_name);
		return hospital;
	}

	private static void schedule_management(CRS crs) {
        System.out.println("Schedule Management Operations:");
        System.out.println("Add Rendezvous <-- 1");
        System.out.println("Add Patient <-- 2");
        System.out.println("View Schedules <-- 3");
        System.out.println("Create Schedule <-- 4");
        System.out.println("Return to Main Menu <-- 5");
        
        Map<Long,Patient> patients = crs.getPatients();

        int choice = scanner.nextInt();
        scanner.nextLine();
        
        Schedule schedule = null;
        Patient patient = null;
        if(choice == 1) {
        	if(schedules.isEmpty()) {
        		System.out.println("No Schedules Avaliable");
        		System.out.println("Creating Schedule...");
        		schedule = createSchedule();
        		schedules.add(schedule);
        	} else {
        		for(Schedule e : schedules) {
        			System.out.println(e);
        		}
        		System.out.println("pick a Schedule in which the Operation will be carried out:");
            	int num = scanner.nextInt();
            	scanner.nextLine(); 
            	schedule = schedules.get(num);
        	}
        }
        if( choice == 1 ) {
        	if(patients.isEmpty()) {
        		System.out.println("No Patients Avaliable");
        		System.out.println("Creating Patient...");
        		patient = createPatient();
        		patients.put(patient.getNationalId(), patient);
        	}
        }

        switch (choice) {
            case 1:
                System.out.println("Enter patient national ID:");
                long patientId = scanner.nextLong();
                scanner.nextLine(); 
                patient = patients.get(patientId);
                System.out.println("Enter rendezvous date (e.g., yyyy-MM-dd):");
                String dateStr = scanner.nextLine();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = sdf.parse(dateStr);
                    schedule.addRendezvous(patient, date);
                    System.out.println("Rendezvous added for patient ID " + patientId + " on date " + date + ".");
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                }
                break;
            case 2:
            	patient = createPatient();
            	patients.put(patient.getNationalId(), patient);
            	break;
            case 3:
                System.out.println("Viewing schedules...");
                for(Schedule e : schedules) {
                	System.out.println(e);
                }
                break;
            case 4:
            	Schedule schedule1 = createSchedule();
            	schedules.add(schedule1);
            	break;
            case 5:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("Invalid choice, returning to main menu.");
                break;
        }
    }

	private static Schedule createSchedule() {
    	System.out.println("Enter Max Patients Per Day:");
        int maxPatients = scanner.nextInt();
        scanner.nextLine(); 
        Schedule schedule = new Schedule(maxPatients);
		return schedule;
	}
	
	private static Patient createPatient() {
		System.out.println("Enter Patient name:");
        String patientName = scanner.nextLine();
        System.out.println("Enter National ID:");
        long nationalId = scanner.nextLong();
        scanner.nextLine(); 
        Patient patient = new Patient(patientName,nationalId);
		return patient;
	}

	private static void crs_management(CRS crs) {
        System.out.println("CRS Management Operations:");
        System.out.println("Make Rendezvous <-- 1");
        System.out.println("Save System Data <-- 2");
        System.out.println("Load System Data <-- 3");
        System.out.println("Return to Main Menu <-- 4");

        Map<Long,Patient> patients = crs.getPatients();
    	Map<Integer, Hospital> hospitals = crs.getHospitals();
    	int choice = scanner.nextInt();
        scanner.nextLine(); 
        
        if(choice == 1) {
        	if(patients.isEmpty()) {
        		System.out.println("No Patients Avaliable");
        		System.out.println("Creating Patient...");
        		Patient patient = createPatient();
        		patients.put(patient.getNationalId(), patient);
        	}
        	if(hospitals.isEmpty()) {
        		System.out.println("No Hospitals Avaliable");
        		System.out.println("Creating Hospital...");
        		Hospital hospital = createHospital();
        		hospitals.put(hospital.getId(), hospital);
        	}
        }

        switch (choice) {
        	case 1:
        		for(Patient e : patients.values()) {
        			System.out.println(e);
        		}
        		System.out.println("Enter Patient ID:");
        		long patientId = scanner.nextLong();
        		scanner.nextLine(); 
        		
        		for(Hospital e : hospitals.values()) {
        			System.out.println(e);
        		}
        		System.out.println("Enter Hospital ID:");
        		int hospitalId = scanner.nextInt();
        		scanner.nextLine(); 
        		
        		Hospital hospital = hospitals.get(hospitalId);
        		Section section1 = null;
        		if(hospital.getSections().isEmpty()) {
        			System.out.println("No Sections Avaliable");
            		System.out.println("Creating Section...");
            		section1 = createSection();
            		hospital.addSection(section1);
            		sections.add(section1);
        		}
        		
        		for(Section e : sections) {
        			System.out.println(e);
        		}
        		System.out.println("Enter Section ID:");
        		int sectionId = scanner.nextInt();
        		scanner.nextLine(); 
        		
        		for(Section e : sections) {
        			if( e.getId() == sectionId) {
        				section1 = e;
        			}
        		}
        		if(section1.getDoctors().isEmpty()) {
        			System.out.println("No Doctors Avaliable");
            		System.out.println("Creating Doctor...");
            		System.out.println("Enter doctor name:");
                    String name1 = scanner.nextLine();
                    System.out.println("Enter national ID:");
                    int nationalId = scanner.nextInt();
                    System.out.println("Enter diploma ID:");
                    int diplomaId = scanner.nextInt();
                    scanner.nextLine();
                    Doctor doctor1 = new Doctor(name1,nationalId,diplomaId);
                    section1.addDoctor(doctor1);
        		}
        		
        		section1.listDoctors();
        		System.out.println("Enter Diploma ID:");
        		int diplomaId = scanner.nextInt();
        		scanner.nextLine(); 
        		Doctor doctor = section1.getDoctor(diplomaId);
        		System.out.println("Enter rendezvous date (e.g., yyyy-MM-dd):");
        		String dateStr = scanner.nextLine();
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        		try {
        		    Date date = sdf.parse(dateStr);

        		    if (doctor.getSchedule() == null) {
        		        doctor.setSchedule(new Schedule(10));
        		    }

        		    Schedule schedule = doctor.getSchedule();
        		    if (schedule.addRendezvous(patients.get(patientId), date)) {
        		        System.out.println("Rendezvous successfully added.");
        		    } else {
        		        System.out.println("Rendezvous could not be added. Max patients reached for this day.");
        		    }

        		    crs.makeRendezvous(patientId, hospitalId, sectionId, diplomaId, date);
        		} catch (ParseException e) {
        		    System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        		    e.printStackTrace();
        		} catch (IDException f) {
        		    f.printStackTrace();
        		}
                break;
            case 2:
            	try {
            		crs.saveTablesToDisk("src\\Myfile.ser");
            	} catch (IOException e1) {
            		e1.printStackTrace();
            	}
                System.out.println("System data saved.");
                break;
            case 3:
            	try {
            		CRS.loadTablesToDisk("src\\Myfile.ser");
            	} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
                System.out.println("System data loaded.");
                break;
            case 4:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("Invalid choice, returning to main menu.");
                break;
        }
    }
}
