package edu.crimpbit;

import com.google.common.collect.ImmutableMap;
import com.thalmic.myo.enums.Arm;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "subject")
public class Subject {

    @Id
    private String id;

    private String name = "";

    private int age = -1;

    private Gender gender = Gender.UNDEFINED;

    private Map<Arm, ArmDetails> armDetails = ImmutableMap.<Arm, ArmDetails>builder().put(
            Arm.ARM_LEFT, new ArmDetails()
    ).put(
            Arm.ARM_RIGHT, new ArmDetails()
    ).build();

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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Map<Arm, ArmDetails> getArmDetails() {
        return armDetails;
    }

    public enum Gender {
        MALE, FEMALE, UNDEFINED;
    }

    public static class ArmDetails {

        private double girth;

        public double getGirth() {
            return girth;
        }

        public void setGirth(double girth) {
            this.girth = girth;
        }

    }

}
