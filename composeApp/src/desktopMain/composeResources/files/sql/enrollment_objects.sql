-- Drop view if exists
DROP VIEW IF EXISTS vw_enrollments;
-- Drop functions if exist
DROP FUNCTION IF EXISTS fn_get_enrollments_for_student;
DROP FUNCTION IF EXISTS fn_get_enrollments_for_course;
DROP FUNCTION IF EXISTS fn_get_enrollment_by_id;
-- Drop procedures if exist
DROP PROCEDURE IF EXISTS sp_enroll_student;
DROP PROCEDURE IF EXISTS sp_update_enrollment_grade;
DROP PROCEDURE IF EXISTS sp_unenroll_student;

-- Views
CREATE OR REPLACE VIEW vw_enrollments AS
SELECT 
    e.id,
    e.student_id,
    CONCAT(s.first_name, ' ', s.last_name) AS student_name,
    e.course_id,
    c.title AS course_name,
    e.enrollment_date,
    e.grade,
    e.status
FROM enrollments e
JOIN students s ON e.student_id = s.id
JOIN courses c ON e.course_id = c.id;

-- Functions
DELIMITER //

CREATE FUNCTION fn_get_enrollments_for_student(p_student_id INT)
RETURNS TEXT
DETERMINISTIC
BEGIN
    DECLARE enrollments_json TEXT;
    SELECT JSON_ARRAYAGG(
        JSON_OBJECT(
            'id', e.id,
            'student_id', e.student_id,
            'student_name', CONCAT(s.first_name, ' ', s.last_name),
            'course_id', e.course_id,
            'course_name', c.title,
            'enrollment_date', e.enrollment_date,
            'grade', e.grade,
            'status', e.status
        )
    ) INTO enrollments_json
    FROM enrollments e
    JOIN students s ON e.student_id = s.id
    JOIN courses c ON e.course_id = c.id
    WHERE e.student_id = p_student_id;
    RETURN enrollments_json;
END //

CREATE FUNCTION fn_get_enrollments_for_course(p_course_id INT)
RETURNS TEXT
DETERMINISTIC
BEGIN
    DECLARE enrollments_json TEXT;
    SELECT JSON_ARRAYAGG(
        JSON_OBJECT(
            'enrollment_id', e.enrollment_id,
            'student_id', e.student_id,
            'student_name', CONCAT(s.first_name, ' ', s.last_name),
            'course_id', e.course_id,
            'course_name', c.title,
            'enrollment_date', e.enrollment_date,
            'grade', e.grade,
            'status', e.status
        )
    ) INTO enrollments_json
    FROM enrollments e
    JOIN students s ON e.student_id = s.id
    JOIN courses c ON e.course_id = c.id
    WHERE e.course_id = p_course_id;
    RETURN enrollments_json;
END //

CREATE FUNCTION fn_get_enrollment_by_id(p_enrollment_id INT)
RETURNS TEXT
DETERMINISTIC
BEGIN
    DECLARE enrollment_json TEXT;
    SELECT JSON_OBJECT(
        'enrollment_id', e.enrollment_id,
        'student_id', e.student_id,
        'student_name', CONCAT(s.first_name, ' ', s.last_name),
        'course_id', e.course_id,
        'course_name', c.title,
        'enrollment_date', e.enrollment_date,
        'grade', e.grade,
        'status', e.status
    ) INTO enrollment_json
    FROM enrollments e
    JOIN students s ON e.student_id = s.id
    JOIN courses c ON e.course_id = c.id
    WHERE e.enrollment_id = p_enrollment_id;
    RETURN enrollment_json;
END //



-- Stored Procedures
CREATE PROCEDURE sp_enroll_student(
    IN p_student_id INT,
    IN p_course_id INT,
    IN p_enrollment_date DATE,
    IN p_grade DECIMAL(4,2),
    IN p_status VARCHAR(20),
    OUT p_enrollment_id INT
)
BEGIN
    INSERT INTO enrollments (
        student_id,
        course_id,
        enrollment_date,
        grade,
        status
    ) VALUES (
        p_student_id,
        p_course_id,
        p_enrollment_date,
        p_grade,
        COALESCE(p_status, 'Enrolled')
    );
    SET p_enrollment_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE sp_update_enrollment_grade(
    IN p_enrollment_id INT,
    IN p_grade DECIMAL(4,2)
)
BEGIN
    UPDATE enrollments
    SET grade = p_grade
    WHERE enrollment_id = p_enrollment_id;
END //

CREATE PROCEDURE sp_unenroll_student(
    IN p_enrollment_id INT
)
BEGIN
    DELETE FROM enrollments
    WHERE enrollment_id = p_enrollment_id;
END //

DELIMITER ;
