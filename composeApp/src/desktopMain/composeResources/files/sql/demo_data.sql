-- SQL script to populate the database with initial data for the Student Information System.

-- Sample data for students table
INSERT INTO students (first_name, last_name, email, date_of_birth, enrollment_date, status) VALUES
('Jonathan', 'Smith', 'jonathan.smith@university.edu', '2007-05-15', '2023-09-01', 'Active'),
('Elizabeth', 'Johnson', 'elizabeth.johnson@university.edu', '2006-03-22', '2023-09-01', 'Active'),
('Michael', 'Brown', 'michael.brown@university.edu', '2007-11-08', '2023-09-01', 'Active'),
('Samantha', 'Wilson', 'samantha.wilson@university.edu', '2006-07-30', '2023-09-01', 'Active'),
('Daniel', 'Lee', 'daniel.lee@university.edu', '2007-01-17', '2023-09-01', 'Active');

-- Sample data for instructors table
INSERT INTO instructors (first_name, last_name, email, department, hire_date) VALUES
('Robert', 'Anderson', 'robert.anderson@university.edu', 'Computer Science', '2020-01-15'),
('Linda', 'Martinez', 'linda.martinez@university.edu', 'Mathematics', '2019-08-20'),
('James', 'Wilson', 'james.wilson@university.edu', 'English', '2018-06-10'),
('Maria', 'Garcia', 'maria.garcia@university.edu', 'Physics', '2021-01-05'),
('Thomas', 'Brown', 'thomas.brown@university.edu', 'Chemistry', '2020-09-15');

-- Sample data for courses table
INSERT INTO courses (course_code, title, description, credits, instructor_id, max_students, status) VALUES
('CS101', 'Introduction to Computer Programming', 'Fundamental concepts of programming and computer science', 3, 1, 30, 'Active'),
('MATH201', 'Calculus and Linear Algebra', 'Advanced topics in calculus and linear algebra', 4, 2, 25, 'Active'),
('ENG102', 'Academic Writing and Composition', 'Writing and composition techniques', 3, 3, 20, 'Active'),
('PHYS101', 'Introduction to Physics', 'Introduction to classical mechanics and thermodynamics', 4, 4, 30, 'Active'),
('CHEM101', 'General Chemistry', 'Basic principles of chemistry and laboratory techniques', 4, 5, 25, 'Active');

-- Sample data for enrollments table (linking students with courses)
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade, status) VALUES
(1, 1, '2023-09-01', 4.00, 'Enrolled'),
(1, 2, '2023-09-01', 3.33, 'Enrolled'),
(2, 1, '2023-09-01', 3.67, 'Enrolled'),
(2, 3, '2023-09-01', 4.00, 'Enrolled'),
(3, 2, '2023-09-01', 3.00, 'Enrolled'),
(3, 4, '2023-09-01', 3.33, 'Enrolled'),
(4, 3, '2023-09-01', 3.67, 'Enrolled'),
(4, 5, '2023-09-01', 4.00, 'Enrolled'),
(5, 4, '2023-09-01', 3.33, 'Enrolled'),
(5, 5, '2023-09-01', 3.67, 'Enrolled');

-- Sample data for attendance
INSERT INTO attendance (enrollment_id, date, status) VALUES
(1, '2023-09-02', 'Present'),
(2, '2023-09-02', 'Present'),
(3, '2023-09-02', 'Present'),
(4, '2023-09-02', 'Late'),
(5, '2023-09-02', 'Present'),
(6, '2023-09-02', 'Excused'),
(7, '2023-09-02', 'Present'),
(8, '2023-09-02', 'Present'),
(9, '2023-09-02', 'Absent'),
(10, '2023-09-02', 'Present');
