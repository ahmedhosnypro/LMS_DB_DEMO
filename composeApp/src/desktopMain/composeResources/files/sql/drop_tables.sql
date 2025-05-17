-- Drop tables in reverse order of creation to handle foreign key constraints
DROP TABLE IF EXISTS attendance;

DROP TABLE IF EXISTS enrollments;
DROP VIEW IF EXISTS vw_enrollments;
DROP FUNCTION IF EXISTS fn_get_enrollments_for_student;
DROP FUNCTION IF EXISTS fn_get_enrollments_for_course;
DROP FUNCTION IF EXISTS fn_get_enrollment_by_id;
DROP PROCEDURE IF EXISTS sp_enroll_student;
DROP PROCEDURE IF EXISTS sp_update_enrollment_grade;
DROP PROCEDURE IF EXISTS sp_unenroll_student;

DROP TABLE IF EXISTS courses;
DROP VIEW IF EXISTS vw_courses;
DROP PROCEDURE IF EXISTS sp_add_course;
DROP PROCEDURE IF EXISTS sp_update_course;
DROP PROCEDURE IF EXISTS sp_delete_course;
DROP FUNCTION IF EXISTS fn_get_course_by_id;

DROP TABLE IF EXISTS instructors;

DROP TABLE IF EXISTS students;
DROP VIEW IF EXISTS vw_students;
DROP FUNCTION IF EXISTS fn_get_student_by_id;
DROP PROCEDURE IF EXISTS sp_add_student;
DROP PROCEDURE IF EXISTS sp_update_student;
DROP PROCEDURE IF EXISTS sp_delete_student;




