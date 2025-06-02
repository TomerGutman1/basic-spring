package com.handson.basic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.basic.DTO.StudentDetailsOut;
import com.handson.basic.DTO.StudentIn;
import com.handson.basic.DTO.StudentOut;
import com.handson.basic.model.*;
import com.handson.basic.service.AWSService;
import com.handson.basic.service.SmsService;
import com.handson.basic.service.StudentService;
import com.handson.basic.util.FPS;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.handson.basic.util.FPS.aFPS;
import static com.handson.basic.util.FPSCondition.FPSConditionBuilder.aFPSCondition;
import static com.handson.basic.util.FPSField.FPSFieldBuilder.aFPSField;
import static com.handson.basic.util.Strings.likeLowerOrNull;

@RestController
@RequestMapping("/api/students")
public class StudentsController {

    @Autowired
    StudentService studentService;


    @Autowired
    SmsService smsService;


    @Autowired
    AWSService awsService;



    @Autowired
    EntityManager em;

    @Autowired
    ObjectMapper om;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<PaginationAndList> search(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromBirthDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toBirthDate,
            @RequestParam(required = false) Integer fromSatScore,
            @RequestParam(required = false) Integer toSatScore,
            @RequestParam(required = false) Integer fromAvgScore,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") @Min(1) Integer count,
            @RequestParam(defaultValue = "id") StudentSortField sort,
            @RequestParam(defaultValue = "asc") SortDirection sortDirection)

            throws JsonProcessingException {

        var res = FPS.<StudentOut>aFPS().select(List.of(
                        aFPSField().field("s.id").alias("id").build(),
                        aFPSField().field("s.created_at").alias("createdat").build(),
                        aFPSField().field("s.fullname").alias("fullname").build(),
                        aFPSField().field("s.birth_date").alias("birthdate").build(),
                        aFPSField().field("s.sat_score").alias("satscore").build(),
                        aFPSField().field("s.graduation_score").alias("graduationscore").build(),
                        aFPSField().field("s.phone").alias("phone").build(),
                        aFPSField().field("s.profile_picture").alias("profilepicture").build(),
                        aFPSField().field("(select avg(sg.course_score) from student_grade sg where sg.student_id = s.id)").alias("avgscore").build()
                ))
                .from(List.of(" student s"))
                .conditions(List.of(
                        aFPSCondition().condition("( lower(fullname) like :fullName )")
                                .parameterName("fullName").value(likeLowerOrNull(fullName)).build(),
                        aFPSCondition().condition("( s.birth_date >= :fromBirthDate )")
                                .parameterName("fromBirthDate").value(fromBirthDate).build(),
                        aFPSCondition().condition("( s.birth_date <= :toBirthDate )")
                                .parameterName("toBirthDate").value(toBirthDate).build(),
                        aFPSCondition().condition("( sat_score >= :fromSatScore )")
                                .parameterName("fromSatScore").value(fromSatScore).build(),
                        aFPSCondition().condition("( sat_score <= :toSatScore )")
                                .parameterName("toSatScore").value(toSatScore).build(),
                        aFPSCondition().condition("( (select avg(sg.course_score) from student_grade sg where sg.student_id = s.id ) >= :fromAvgScore )")
                                .parameterName("fromAvgScore").value(fromAvgScore).build()
                ))
                .sortField(sort.fieldName)
                .sortDirection(sortDirection)
                .page(page)
                .count(count)
                .itemClass(StudentOut.class)
                .build()
                .exec(em, om);

        return ResponseEntity.ok(res);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOneStudent(@PathVariable Long id) {
        Optional<Student> optional = studentService.findById(id);
        if (optional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

        Student student = optional.get();

        Double avg = student.getStudentGrades().stream()
                .map(StudentGrade::getCourseScore)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        StudentDetailsOut out = new StudentDetailsOut();
        out.setId(student.getId());
        out.setCreatedat(LocalDateTime.ofInstant(student.getCreatedAt(), ZoneId.systemDefault()));
        out.setFullname(student.getFullname());
        out.setBirthdate(student.getBirthDate());
        out.setSatscore(student.getSatScore());
        out.setGraduationscore(student.getGraduationScore());
        out.setPhone(student.getPhone());
        out.setProfilepicture(student.getProfilePicture());
        out.setAvgscore(avg);
        out.setStudentGrades(new ArrayList<>(student.getStudentGrades()));

        return new ResponseEntity<>(out, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> insertStudent(@RequestBody StudentIn studentIn) {
        Student student = studentIn.toStudent();
        student = studentService.save(student);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateStudent(@PathVariable Long id,
                                           @RequestBody StudentIn student) {
        Optional<Student> dbStudent = studentService.findById(id);
        if (dbStudent.isEmpty())
            throw new RuntimeException("Student with id: " + id + " not found");

        student.updateStudent(dbStudent.get());
        Student updatedStudent = studentService.save(dbStudent.get());
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        Optional<Student> dbStudent = studentService.findById(id);
        if (dbStudent.isEmpty())
            throw new RuntimeException("Student with id: " + id + " not found");

        studentService.delete(dbStudent.get());
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }

    @RequestMapping(value = "/highSat", method = RequestMethod.GET)
    public ResponseEntity<?> getHighSatStudents(@RequestParam Integer sat) {
        return new ResponseEntity<>(studentService.getStudentWithSatHigherThan(sat), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/image", method = RequestMethod.PUT,consumes = "multipart/form-data")
    public ResponseEntity<?> uploadStudentImage(@PathVariable Long id,  @RequestParam("image") MultipartFile image)
    {
        Optional<Student> dbStudent = studentService.findById(id);
        if (dbStudent.isEmpty()) throw new RuntimeException("Student with id: " + id + " not found");
        String bucketPath = "tomerbucket123/student-" +  id + ".png" ;
        awsService.putInBucket(image, bucketPath);
        dbStudent.get().setProfilePicture(bucketPath);
        Student updatedStudent = studentService.save(dbStudent.get());
        return new ResponseEntity<>(StudentOut.of(updatedStudent, awsService) , HttpStatus.OK);
    }

    @RequestMapping(value = "/sms/all", method = RequestMethod.POST)
    public ResponseEntity<?> smsAll(@RequestParam String text) {
        new Thread(() -> {
            studentService.all()
                    .forEach(student -> {String phone = student.getPhone();
                        if (phone != null && !phone.trim().isEmpty()) {
                            smsService.send(text, phone);
                        }
                    });
        }).start();
        return new ResponseEntity<>("SENDING", HttpStatus.OK);
    }


}
