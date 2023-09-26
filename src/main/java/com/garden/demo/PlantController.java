package com.garden.demo;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@RestController
@CrossOrigin
@RequestMapping
public class PlantController {
    @Autowired
    CloudinaryConfig cloudinaryConfig;

    Cloudinary cloudinary = CloudinaryConfig.configureCloudinary();

    @Autowired
    private PlantRepository repository;

    @GetMapping(value = "/plants", produces = { "application/json" })
    public ResponseEntity<Collection<Plant>> getPlants() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<Plant> plants = ((Collection<Plant>) repository.getPlantsByUserId(userName))
                .stream()
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(plants);
    }

    @GetMapping(value = "plants/{id}", produces = { "application/json" })
    public ResponseEntity<Plant> getPlantById(
            @PathVariable long id) {
        System.out.println("get plant by id: " + id);
        Plant res = repository.findById(id).get();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping(value = "/plants/addplant", consumes = { "application/json" })
    public ResponseEntity<?> addPlant(@RequestBody Plant plant) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        plant.setUser_id(userName);

        try {
            repository.save(plant);
            URI uri = URI.create("/plants/addplant/" + plant.getId());

            return ResponseEntity.created(uri).body(plant);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not save plant!");
        }

    }

    @PostMapping(value = "/plants/addimage", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> addImage(@RequestParam("file") MultipartFile mFile,
            @RequestParam("img_id") String imgId) {

        String imageUrl = "";
        String imageId = "";

        try {
            Map<?, ?> map = cloudinary.uploader().upload(mFile.getBytes(),
                    ObjectUtils.asMap("folder", "garden", "public_id", imgId, "overwrite", true));

            imageUrl = (String) map.get("url");
            imageId = (String) map.get("public_id");
            System.out.println(map.get("url"));
            System.out.println(map.get("public_id"));
            Map<String, String> imageJson = Map.of("img_url", imageUrl, "img_id",
                    imageId);

            return new ResponseEntity<>(imageJson, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());

            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping(value = "/plant/{name}", produces = { "application/json" })
    public ResponseEntity<List<Plant>> getByName(@PathVariable String name) {
        List<Plant> res = repository.getPlantsBytext(name);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping(value = "/plants/addplant/{id}", consumes = { "application/json" })
    public ResponseEntity<Plant> updatePlant(@PathVariable long id, @RequestBody Plant plant) {
        System.out.println("update plant");
        boolean itemExists = repository.existsById(id);

        if (itemExists) {
            if (plant.getImage() != null) {
                repository.updatePlant(plant.getName(), plant.getInstructions(), plant.getDate(), plant.getImage(),
                        plant.getImage_id(), id);
            } else {
                repository.updatePlantWithoutImage(plant.getName(), plant.getInstructions(), plant.getDate(), id);
            }

            return new ResponseEntity<Plant>(plant, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/plants/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable long id) {
        Plant plant = repository.findById(id).get();
        String image_id = plant.getImage_id();

        if (image_id.isEmpty()) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            try {
                cloudinary.uploader().destroy(image_id, ObjectUtils.emptyMap());
                repository.deleteById(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                System.out.println(e);
                return ResponseEntity.notFound().build();
            }
        }
    }
}
