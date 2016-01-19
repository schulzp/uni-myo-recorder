package edu.crimpbit;

import com.thalmic.myo.enums.Arm;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "subject")
public class Subject {

    @Id
    private String id;

    @NotNull
    private String name = "";

    private int age;

    private ArmDetails leftArm;

    private ArmDetails rightArm;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ArmDetails getArmDetails(Arm arm) {
        if (arm == Arm.ARM_LEFT) {
            return leftArm;
        } else if (arm == Arm.ARM_RIGHT) {
            return rightArm;
        }
        throw new IllegalArgumentException("Unkown arm");
    }

    public static class ArmDetails {

        int girth;

    }

}
