package entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class pointEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    private UUID function_id;
    private double x_value;
    private double y_value;

    public UUID getId(){
        return this.id;
    }

    public UUID getFunction_id(){
        return this.function_id;
    }

    public double getX_value(){
        return this.x_value;
    }

    public double getY_value() {
        return this.y_value;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setFunction_id(UUID function_id) {
        this.function_id = function_id;
    }

    public void setX_value(double x_value) {
        this.x_value = x_value;
    }

    public void setY_value(double y_value) {
        this.y_value = y_value;
    }
}
