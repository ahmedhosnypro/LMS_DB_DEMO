-- MySQL-compatible schema for Student Information System

-- Students' table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL CHECK (first_name REGEXP '^[A-Za-z-]+$' AND LENGTH(first_name) >= 2),
    last_name VARCHAR(50) NOT NULL CHECK (last_name REGEXP '^[A-Za-z-]+$' AND LENGTH(last_name) >= 2),
    email VARCHAR(100) NOT NULL UNIQUE CHECK (
        email REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$' 
        AND LENGTH(email) >= 5
    ),
    date_of_birth DATE NOT NULL,
    enrollment_date DATE NOT NULL,
    status ENUM('Active', 'Suspended', 'Graduated', 'On Leave', 'Withdrawn') NOT NULL DEFAULT 'Active'
) ENGINE=InnoDB;

-- Instructor table
CREATE TABLE IF NOT EXISTS instructors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL CHECK (first_name REGEXP '^[A-Za-z-]+$' AND LENGTH(first_name) >= 2),
    last_name VARCHAR(50) NOT NULL CHECK (last_name REGEXP '^[A-Za-z-]+$' AND LENGTH(last_name) >= 2),
    email VARCHAR(100) NOT NULL UNIQUE CHECK (
        email REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$' 
        AND LENGTH(email) >= 5
    ),
    department VARCHAR(50) NOT NULL CHECK (LENGTH(department) >= 2),
    hire_date DATE NOT NULL
) ENGINE=InnoDB;

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(10) NOT NULL UNIQUE CHECK (
        course_code REGEXP '^[A-Z]{2,4}[0-9]{3,4}$' 
        AND LENGTH(course_code) >= 5
    ),
    title VARCHAR(100) NOT NULL CHECK (LENGTH(title) >= 4),
    description TEXT,
    credits INT NOT NULL CHECK (credits BETWEEN 1 AND 6),
    instructor_id INT,
    max_students INT CHECK (max_students IS NULL OR (max_students BETWEEN 1 AND 200)),
    status ENUM('Active', 'Inactive', 'Archived') NOT NULL DEFAULT 'Active',
    FOREIGN KEY (instructor_id) REFERENCES instructors(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Enrollment table
CREATE TABLE IF NOT EXISTS enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE NOT NULL,
    grade DECIMAL(4,2) CHECK (grade IS NULL OR (grade BETWEEN 0.00 AND 4.00)),
    status ENUM('Enrolled', 'Dropped', 'Completed', 'Withdrawn') NOT NULL DEFAULT 'Enrolled',
    UNIQUE(student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Attendance table
CREATE TABLE IF NOT EXISTS attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL,
    date DATE NOT NULL,
    status ENUM('Present', 'Absent', 'Late', 'Excused') NOT NULL,
    UNIQUE(enrollment_id, date),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE
) ENGINE=InnoDB;
