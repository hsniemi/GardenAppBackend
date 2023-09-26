package com.garden.demo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.garden.demo.encoder.PasswordEncoder;

@CrossOrigin
@RequestMapping("/")
@RestController
public class UserController {

    @Autowired
    CloudinaryConfig cloudinaryConfig;

    @Autowired
    UserRepository userRepo;

    @Autowired
    PlantRepository plantRepo;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping(value = "/signup")
    public ResponseEntity<?> addUser(@RequestBody User user) {

        System.out.println(user.getUserName());

        if (user.getUserName() == null) {

            return ResponseEntity.badRequest().body("Missing username!");
        } else if (user.getPassword() == null) {

            return ResponseEntity.badRequest().body("Missing password!");
        } else if (user.getPassword().length() < 6) {

            return ResponseEntity.badRequest().body("Password must have at least six characters!");
        } else if (userRepo.findByUserName(user.getUserName()) != null) {

            return ResponseEntity.badRequest().body("There is already an account by this email!");
        } else if (user.getUserName() != null && user.getPassword() != null
                && userRepo.findByUserName(user.getUserName()) == null) {
            String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepo.save(user);

            return ResponseEntity.ok().body("User created!");
        }

        return ResponseEntity.badRequest().body("Signup failed!");

    }

    @DeleteMapping(value = "/deleteuser")
    public ResponseEntity<?> deleteUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        Cloudinary cloudinary = CloudinaryConfig.configureCloudinary();
        try {
            List<String> imageList = plantRepo.findImageIdsByUserName(userName);
            if (!imageList.isEmpty()) {
                for (String s : imageList) {
                    cloudinary.uploader().destroy(s, ObjectUtils.emptyMap());
                }
            }
            System.out.println(imageList);

            plantRepo.deleteByUserName(userName);
            userRepo.deleteByUserName(userName);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
