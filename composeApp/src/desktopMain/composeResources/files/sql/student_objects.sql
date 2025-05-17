-- Drop view if exists
DROP VIEW IF EXISTS vw_students;
-- Drop function if exists
DROP FUNCTION IF EXISTS fn_get_student_by_id;
-- Drop procedures if exist
DROP PROCEDURE IF EXISTS sp_add_student;
DROP PROCEDURE IF EXISTS sp_update_student;
DROP PROCEDURE IF EXISTS sp_delete_student;

-- Views
CREATE OR REPLACE VIEW vw_students AS
SELECT 
    id,
    first_name,
    last_name,
    email,
    date_of_birth,
    enrollment_date,
    status
FROM students;



-- Functions
DELIMITER //

CREATE FUNCTION fn_get_student_by_id(p_id INT)
RETURNS TEXT
DETERMINISTIC
BEGIN
    DECLARE student_data TEXT;
    SELECT JSON_OBJECT(
        'id', id,
        'first_name', first_name,
        'last_name', last_name,
        'email', email,
        'date_of_birth', date_of_birth,
        'enrollment_date', enrollment_date,
        'status', status
    ) INTO student_data
    FROM students
    WHERE id = p_id;
    
    RETURN student_data;
END //

-- Stored Procedures
CREATE PROCEDURE sp_add_student(
    IN p_first_name VARCHAR(50),
    IN p_last_name VARCHAR(50),
    IN p_email VARCHAR(100),
    IN p_date_of_birth DATE,
    IN p_enrollment_date DATE,
    IN p_status VARCHAR(20),
    OUT p_id INT
)
BEGIN
    INSERT INTO students (
        first_name,
        last_name,
        email,
        date_of_birth,
        enrollment_date,
        status
    ) VALUES (
        p_first_name,
        p_last_name,
        p_email,
        p_date_of_birth,
        p_enrollment_date,
        COALESCE(p_status, 'Active')
    );
    
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE sp_update_student(
    IN p_id INT,
    IN p_first_name VARCHAR(50),
    IN p_last_name VARCHAR(50),
    IN p_email VARCHAR(100),
    IN p_date_of_birth DATE,
    IN p_enrollment_date DATE,
    IN p_status VARCHAR(20)
)
BEGIN
    UPDATE students 
    SET first_name = p_first_name,
        last_name = p_last_name,
        email = p_email,
        date_of_birth = p_date_of_birth,
        enrollment_date = p_enrollment_date,
        status = COALESCE(p_status, 'Active')
    WHERE id = p_id;
END //

CREATE PROCEDURE sp_delete_student(
    IN p_id INT
)
BEGIN
    DELETE FROM students 
    WHERE id = p_id;
END //

DELIMITER ;
