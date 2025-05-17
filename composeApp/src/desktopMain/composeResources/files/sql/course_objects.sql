DROP VIEW IF EXISTS vw_courses;
-- Drop procedures if exist
DROP PROCEDURE IF EXISTS sp_add_course;
DROP PROCEDURE IF EXISTS sp_update_course;
DROP PROCEDURE IF EXISTS sp_delete_course;
-- Drop function if exists
DROP FUNCTION IF EXISTS fn_get_course_by_id;

-- Views
CREATE OR REPLACE VIEW vw_courses AS
SELECT 
    id,
    course_code,
    title,
    description,
    credits,
    instructor_id,
    max_students,
    status
FROM courses;


-- Functions
DELIMITER //

CREATE FUNCTION fn_get_course_by_id(p_id INT)
RETURNS TEXT
DETERMINISTIC
BEGIN
    DECLARE course_data TEXT;
    SELECT JSON_OBJECT(
        'id', id,
        'course_code', course_code,
        'title', title,
        'description', description,
        'credits', credits,
        'instructor_id', instructor_id,
        'max_students', max_students,
        'status', status
    ) INTO course_data
    FROM courses
    WHERE id = p_id;
    
    RETURN course_data;
END //

-- Stored Procedures
CREATE PROCEDURE sp_add_course(
    IN p_course_code VARCHAR(10),
    IN p_title VARCHAR(100),
    IN p_description TEXT,
    IN p_credits INT,
    IN p_instructor_id INT,
    IN p_max_students INT,
    IN p_status VARCHAR(20),
    OUT p_course_id INT
)
BEGIN
    INSERT INTO courses (
        course_code,
        title,
        description,
        credits,
        instructor_id,
        max_students,
        status
    ) VALUES (
        p_course_code,
        p_title,
        p_description,
        p_credits,
        p_instructor_id,
        p_max_students,
        COALESCE(p_status, 'Active')
    );
    
    SET p_course_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE sp_update_course(
    IN p_course_id INT,
    IN p_course_code VARCHAR(10),
    IN p_title VARCHAR(100),
    IN p_description TEXT,
    IN p_credits INT,
    IN p_instructor_id INT,
    IN p_max_students INT,
    IN p_status VARCHAR(20)
)
BEGIN
    UPDATE courses 
    SET course_code = p_course_code,
        title = p_title,
        description = p_description,
        credits = p_credits,
        instructor_id = p_instructor_id,
        max_students = p_max_students,
        status = COALESCE(p_status, 'Active')
    WHERE id = p_course_id;
END //

CREATE PROCEDURE sp_delete_course(
    IN p_course_id INT
)
BEGIN
    DELETE FROM courses 
    WHERE id = p_course_id;
END //

DELIMITER ;
