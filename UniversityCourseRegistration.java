import java.time.LocalDate;
import java.util.*;

final class AcademicRecord {
    private final String studentId;
    private final String major;
    private final LocalDate enrollmentDate;
    private final Map<String, String> completedCourses;
    private final double cumulativeGPA;
    private final String[] academicHonors;

    public AcademicRecord(String studentId, String major, LocalDate enrollmentDate,
                          Map<String, String> completedCourses, double cumulativeGPA, String[] academicHonors) {
        this.studentId = studentId;
        this.major = major;
        this.enrollmentDate = enrollmentDate;
        this.completedCourses = new HashMap<>(completedCourses != null ? completedCourses : new HashMap<>());
        this.cumulativeGPA = cumulativeGPA;
        this.academicHonors = academicHonors != null ? academicHonors.clone() : new String[0];
    }

    public String getStudentId() { return studentId; }
    public String getMajor() { return major; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public Map<String, String> getCompletedCourses() { return new HashMap<>(completedCourses); }
    public double getCumulativeGPA() { return cumulativeGPA; }
    public String[] getAcademicHonors() { return academicHonors.clone(); }

    public final boolean meetsPrerequisites(String courseCode) {
        String[] prerequisites = getPrerequisitesForCourse(courseCode);
        for (String prereq : prerequisites) {
            if (!completedCourses.containsKey(prereq)) return false;
        }
        return true;
    }

    private String[] getPrerequisitesForCourse(String courseCode) {
        if (courseCode.equals("CS201")) return new String[]{"CS101"};
        if (courseCode.equals("MATH301")) return new String[]{"MATH201", "MATH202"};
        return new String[0];
    }
}

class Student {
    private final String studentId;
    private final AcademicRecord academicRecord;
    private String currentName;
    private String email;
    private String phoneNumber;
    private String currentAddress;
    private String emergencyContact;

    public Student(String studentId, String name, String major) {
        this.studentId = studentId;
        this.currentName = name;
        this.academicRecord = new AcademicRecord(studentId, major, LocalDate.now(), null, 0.0, null);
    }

    public Student(String studentId, String name, Map<String, String> transferCredits, double gpa) {
        this.studentId = studentId;
        this.currentName = name;
        this.academicRecord = new AcademicRecord(studentId, "Undeclared", LocalDate.now(), transferCredits, gpa, null);
    }

    public Student(String studentId, String name, String major, AcademicRecord undergraduateRecord) {
        this.studentId = studentId;
        this.currentName = name;
        this.academicRecord = new AcademicRecord(
                studentId,
                major,
                LocalDate.now(),
                undergraduateRecord.getCompletedCourses(),
                undergraduateRecord.getCumulativeGPA(),
                null
        );
    }

    String getAcademicStanding() {
        double gpa = academicRecord.getCumulativeGPA();
        if (gpa >= 3.5) return "Dean's List";
        if (gpa >= 2.0) return "Good Standing";
        return "Academic Probation";
    }

    public String getContactInfo() {
        return "Name: " + currentName + ", Email: " + email + ", Phone: " + phoneNumber;
    }

    public String getStudentId() { return studentId; }
    public AcademicRecord getAcademicRecord() { return academicRecord; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setCurrentAddress(String address) { this.currentAddress = address; }
}

class Course {
    private final String courseCode;
    private final String title;
    private final int creditHours;
    private final String[] prerequisites;

    public Course(String courseCode, String title, int creditHours, String[] prerequisites) {
        this.courseCode = courseCode;
        this.title = title;
        this.creditHours = creditHours;
        this.prerequisites = prerequisites != null ? prerequisites.clone() : new String[0];
    }

    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public int getCreditHours() { return creditHours; }
    public String[] getPrerequisites() { return prerequisites.clone(); }
}

class Professor {
    private final String facultyId;
    private final String department;
    private final List<String> qualifications;

    public Professor(String facultyId, String department, List<String> qualifications) {
        this.facultyId = facultyId;
        this.department = department;
        this.qualifications = new ArrayList<>(qualifications);
    }

    public String getFacultyId() { return facultyId; }
    public String getDepartment() { return department; }
}

class Classroom {
    private final String roomNumber;
    private final int capacity;
    private final String[] equipment;

    public Classroom(String roomNumber, int capacity, String[] equipment) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.equipment = equipment != null ? equipment.clone() : new String[0];
    }

    public String getRoomNumber() { return roomNumber; }
    public int getCapacity() { return capacity; }
}

class RegistrationSystem {
    private static final int MAX_CREDITS_PER_SEMESTER = 18;
    private static final double MIN_GPA_FOR_ENROLLMENT = 2.0;

    private final Map<String, Object> enrolledStudents = new HashMap<>();

    public boolean enrollStudent(Object student, Object course) {
        if (!(student instanceof Student) || !(course instanceof Course)) return false;
        if (!validatePrerequisites(student, course)) return false;

        Student s = (Student) student;
        enrolledStudents.put(s.getStudentId(), s);
        return true;
    }

    private boolean validatePrerequisites(Object student, Object course) {
        if (!(student instanceof Student) || !(course instanceof Course)) return false;

        Student s = (Student) student;
        Course c = (Course) course;

        return s.getAcademicRecord().meetsPrerequisites(c.getCourseCode()) &&
               s.getAcademicRecord().getCumulativeGPA() >= MIN_GPA_FOR_ENROLLMENT;
    }

    String getEnrollmentReport(String studentId) {
        Object student = enrolledStudents.get(studentId);
        if (student instanceof Student) {
            Student s = (Student) student;
            return "Student: " + s.getContactInfo() + ", Standing: " + s.getAcademicStanding();
        }
        return "Student not found";
    }
}

public class UniversityCourseRegistration {
    public static void main(String[] args) {
        Course cs101 = new Course("CS101", "Intro to Programming", 3, null);
        Course cs201 = new Course("CS201", "Data Structures", 3, new String[]{"CS101"});

        Map<String, String> completedCourses = new HashMap<>();
        completedCourses.put("CS101", "A");

        // Corrected constructor call: (studentId, name, transferCredits, gpa)
        Student student = new Student("S001", "Aarav", completedCourses, 3.8);
        student.setEmail("student@university.edu");

        // Use Arrays.asList for Java 8 compatibility
        Professor prof = new Professor("P001", "Computer Science",
                Arrays.asList("PhD", "Industry Experience"));

        RegistrationSystem system = new RegistrationSystem();
        boolean enrolled = system.enrollStudent(student, cs201);

        System.out.println("Enrollment successful: " + enrolled);
        System.out.println("Prerequisites met: " + student.getAcademicRecord().meetsPrerequisites("CS201"));
        System.out.println("Academic standing: " + student.getAcademicStanding());
    }
}
