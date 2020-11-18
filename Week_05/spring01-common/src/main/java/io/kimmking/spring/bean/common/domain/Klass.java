package io.kimmking.spring.bean.common.domain;

import java.util.List;
import lombok.Data;

@Data
public class Klass { 
    private Integer age = 1;

    private List<Student> students;

    public Klass() {
        System.out.println("construct");
    }
    
    public void dong(){
        System.out.println(this.getStudents());
    }
    
}
