DROP TABLE IF EXISTS students_courses;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS courses;


CREATE TABLE groups 
(
    id SERIAL PRIMARY KEY,
    group_name VARCHAR(128) NOT NULL
);

CREATE TABLE students
(
    id SERIAL PRIMARY KEY,
    group_id INT REFERENCES groups(id) ON DELETE SET NULL,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL
);


CREATE TABLE courses
(
    id SERIAL PRIMARY KEY,
    course_name VARCHAR(32) UNIQUE NOT NULL,
    course_description VARCHAR(500) NOT NULL
);

CREATE TABLE students_courses
(
    student_id INT REFERENCES students(id) ON DELETE CASCADE,
    course_id INT REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE (student_id, course_id)
);