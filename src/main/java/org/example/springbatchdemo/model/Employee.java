package org.example.springbatchdemo.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XStreamAlias("employee")
public class Employee {
    private String empName;
    private Integer empId;
    private String designation;
    private String department;
    private String gender;
    private Long mobileNo;
}
