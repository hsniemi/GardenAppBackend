package com.garden.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "PLANT")
public class Plant {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    private String image;
    private String image_id;
    private String date;
    private String user_id;

    public Plant() {

    }

    public Plant(long id, String name, String instructions, String image, String image_id, String date,
            String user_id) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.image = image;
        this.image_id = image_id;
        this.date = date;
        this.user_id = user_id;
    }

    public Plant(long id, String name, String instructions, String date, String image, String image_id) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.date = date;
        this.image = image;
        this.image_id = image_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return String.format("Plant [id=%d, name=%s, instructions=%s, date=%s, image=%s]", id, name, instructions, date,
                image);
    }

}
