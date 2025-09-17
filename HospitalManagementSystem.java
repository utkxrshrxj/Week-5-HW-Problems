import java.time.LocalDate;
import java.util.*;

final class MedicalRecord {
    private final String recordId;
    private final String patientDNA;
    private final String[] allergies;
    private final String[] medicalHistory;
    private final LocalDate birthDate;
    private final String bloodType;
    
    public MedicalRecord(String recordId, String patientDNA, String[] allergies, 
                        String[] medicalHistory, LocalDate birthDate, String bloodType) {
        if (recordId == null || patientDNA == null || birthDate == null || bloodType == null) {
            throw new IllegalArgumentException("HIPAA compliance: Required medical data cannot be null");
        }
        this.recordId = recordId;
        this.patientDNA = patientDNA;
        this.allergies = allergies != null ? allergies.clone() : new String[0];
        this.medicalHistory = medicalHistory != null ? medicalHistory.clone() : new String[0];
        this.birthDate = birthDate;
        this.bloodType = bloodType;
    }
    
    public String getRecordId() { return recordId; }
    public String getPatientDNA() { return patientDNA; }
    public String[] getAllergies() { return allergies.clone(); }
    public String[] getMedicalHistory() { return medicalHistory.clone(); }
    public LocalDate getBirthDate() { return birthDate; }
    public String getBloodType() { return bloodType; }
    
    public final boolean isAllergicTo(String substance) {
        for (String allergy : allergies) {
            if (allergy.equalsIgnoreCase(substance)) return true;
        }
        return false;
    }
}

class Patient {
    private final String patientId;
    private final MedicalRecord medicalRecord;
    private String currentName;
    private String emergencyContact;
    private String insuranceInfo;
    private int roomNumber;
    private String attendingPhysician;
    
    public Patient(String name) {
        this.patientId = "TEMP-" + System.currentTimeMillis();
        this.currentName = name;
        this.medicalRecord = new MedicalRecord("TEMP-MR", "UNKNOWN", null, null, 
                                             LocalDate.now(), "UNKNOWN");
    }
    
    public Patient(String patientId, String name, String emergencyContact, String insuranceInfo,
                  MedicalRecord medicalRecord) {
        this.patientId = patientId;
        this.currentName = name;
        this.emergencyContact = emergencyContact;
        this.insuranceInfo = insuranceInfo;
        this.medicalRecord = medicalRecord;
    }
  
    public Patient(String patientId, MedicalRecord existingRecord, String name) {
        this.patientId = patientId;
        this.medicalRecord = existingRecord;
        this.currentName = name;
    }
    
    String getBasicInfo() {
        return "Patient ID: " + patientId + ", Name: " + currentName + 
               ", Room: " + roomNumber + ", Doctor: " + attendingPhysician;
    }
    
    public String getPublicInfo() {
        return "Name: " + currentName + ", Room: " + roomNumber;
    }
    
    public String getPatientId() { return patientId; }
    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }
    public void setAttendingPhysician(String physician) { this.attendingPhysician = physician; }
}

class Doctor {
    private final String licenseNumber;
    private final String specialty;
    private final Set<String> certifications;
    
    public Doctor(String licenseNumber, String specialty, Set<String> certifications) {
        this.licenseNumber = licenseNumber;
        this.specialty = specialty;
        this.certifications = new HashSet<>(certifications);
    }
    
    public String getLicenseNumber() { return licenseNumber; }
    public String getSpecialty() { return specialty; }
}

class Nurse {
    private final String nurseId;
    private final String shift;
    private final List<String> qualifications;
    
    public Nurse(String nurseId, String shift, List<String> qualifications) {
        this.nurseId = nurseId;
        this.shift = shift;
        this.qualifications = new ArrayList<>(qualifications);
    }
    
    public String getNurseId() { return nurseId; }
    public String getShift() { return shift; }
}

class Administrator {
    private final String adminId;
    private final List<String> accessPermissions;
    
    public Administrator(String adminId, List<String> accessPermissions) {
        this.adminId = adminId;
        this.accessPermissions = new ArrayList<>(accessPermissions);
    }
    
    public String getAdminId() { return adminId; }
    public List<String> getAccessPermissions() { return new ArrayList<>(accessPermissions); }
}

class HospitalSystem {
    private static final String PRIVACY_POLICY = "HIPAA_COMPLIANT";
    private static final int MAX_CAPACITY = 500;
    
    private final Map<String, Object> patientRegistry = new HashMap<>();
    
    public boolean admitPatient(Object patient, Object staff) {
        if (!(patient instanceof Patient)) return false;
        if (!validateStaffAccess(staff, patient)) return false;
        
        Patient p = (Patient) patient;
        patientRegistry.put(p.getPatientId(), p);
        return true;
    }
    
    private boolean validateStaffAccess(Object staff, Object patient) {
        return staff instanceof Doctor || staff instanceof Nurse || staff instanceof Administrator;
    }
    
    String getPatientInfo(String patientId, Object staff) {
        Object patient = patientRegistry.get(patientId);
        if (patient instanceof Patient && validateStaffAccess(staff, patient)) {
            return ((Patient) patient).getBasicInfo();
        }
        return "Access denied";
    }
}

public class HospitalManagementSystem {
    public static void main(String[] args) {
        MedicalRecord record = new MedicalRecord("MR001", "DNA123", 
                                               new String[]{"Penicillin"}, 
                                               new String[]{"Surgery 2020"}, 
                                               LocalDate.of(1990, 5, 15), "O+");
        
        Patient patient = new Patient("P001", "John Doe", "Jane Doe", "INS123", record);
        patient.setRoomNumber(101);
        
        Doctor doctor = new Doctor("DOC001", "Cardiology", Set.of("Board Certified"));
        Nurse nurse = new Nurse("N001", "Day", List.of("RN", "CPR"));

        HospitalSystem hospital = new HospitalSystem();
        hospital.admitPatient(patient, doctor);
        
        System.out.println("Patient admitted: " + patient.getPublicInfo());
        System.out.println("Allergy check: " + record.isAllergicTo("Penicillin"));
    }
}