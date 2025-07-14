package com.govnet.govnet.controller;

import com.govnet.govnet.dto.AdminUserDTO;
import com.govnet.govnet.dto.UserProfileDTO;
import com.govnet.govnet.entity.MyUser;
import com.govnet.govnet.jwt.JwtUtilityClass;
import com.govnet.govnet.jwt.LoginForm;
import com.govnet.govnet.repo.MyUserRepository;
import com.govnet.govnet.service.FileStorageService;
import com.govnet.govnet.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtilityClass jwtUtilityClass;
    @Autowired
    private MyUserDetailService myUserDetailService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private JavaMailSender mailSender;


    public AccountController(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestPart("user") MyUser user,
                                        @RequestPart(value = "image", required = false) MultipartFile imageFile) {

//        String token = UUID.randomUUID().toString();
//        user.setEmailVerificationToken(token);
//        user.setIsEmailVerified(false);

//        // Send verification email
//        String verificationUrl = "http://localhost:8080/api/verify-email?token=" + token;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(user.getEmail());
//        message.setSubject("Email Verification - Innovation Management System");
//        message.setText("Please verify your email by clicking the link: " + verificationUrl);
//        mailSender.send(message);


        // Check for existing username
        if (myUserRepository.existsByUsername(user.getUsername().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username already taken", "message", "Please choose another username."));
        }

        // Check for existing email
        if (myUserRepository.existsByEmail(user.getEmail().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email already taken", "message", "Please choose another email."));
        }

        // Save profile image or assign default avatar
        String profileImagePath;
        if (imageFile != null && !imageFile.isEmpty()) {
            profileImagePath = fileStorageService.saveProfileImage(imageFile, user.getUsername());
        } else {
            profileImagePath = "E:\\Full Stack GovNet\\attachments\\default_avtar"; // Static path or full URL
        }

        // Set values
        user.setUsername(user.getUsername().toLowerCase());
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProfileImage(profileImagePath);
        user.setCreateDate(LocalDate.now());

        // Save user
        MyUser savedUser = myUserRepository.save(user);

        // Optionally hide password in response
        savedUser.setPassword(null);

        return ResponseEntity.ok(savedUser);
    }

//    @GetMapping("/verify-email")
//    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
//        Optional<MyUser> userOpt = myUserRepository.findByEmailVerificationToken(token);
//
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.badRequest().body("Invalid verification token.");
//        }
//
//        MyUser user = userOpt.get();
//        user.setIsEmailVerified(true);
//        user.setEmailVerificationToken(null); // Clear token after successful verification
//        myUserRepository.save(user);
//
//        return ResponseEntity.ok("Email verified successfully. You can now log in.");
//    }


    // hello comment for checking the project



    // Endpoint to get the profile details of the logged-in user
    //as i think now this api is not needed because now the api: /profile/image/{username} created
    //and also the dto we was created for the user profile also not needed now
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader("Authorization") String token) {
        String email = extractUsernameFromToken(token);
        Optional<MyUser> userOpt = myUserRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(new UserProfileDTO(userOpt.get()));
    }



    @PutMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        String email = extractUsernameFromToken(token);
        Optional<MyUser> userOpt = myUserRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        MyUser user = userOpt.get();

        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete previous profile image if it exists
            if (user.getProfileImage() != null) {
                fileStorageService.deleteFile(user.getProfileImage());
            }

            // Save new image
            String profileImagePath = fileStorageService.saveProfileImage(imageFile, email);
            user.setProfileImage(profileImagePath);
        }

        MyUser updatedUser = myUserRepository.save(user);
        return ResponseEntity.ok(new UserProfileDTO(updatedUser));
    }



    // Endpoint to retrieve user's profile image
    @GetMapping("/profile/image/{username}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String username) {
        Optional<MyUser> userOpt = myUserRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String imagePath = userOpt.get().getProfileImage();
        Path path = Paths.get(imagePath);

        if (!Files.exists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // Adjust based on your image format
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




    private String extractUsernameFromToken(String token) {
        String jwt = token.replace("Bearer ", ""); // Remove "Bearer " prefix
        return jwtUtilityClass.extractUsername(jwt); // Use your actual JWT utility method
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        try {
            // Authenticate using email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginForm.email(), loginForm.password()
                    )
            );

            MyUser user = myUserRepository.findByEmail(loginForm.email())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Check if user is active
            if (!user.getIsActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Inactive user. Please contact admin.");
            }

            // Check if authenticated successfully
            if (authentication.isAuthenticated()) {
                UserDetails userDetails = myUserDetailService.loadUserByUsername(loginForm.email());
                String token = jwtUtilityClass.generateToken(userDetails);
                return ResponseEntity.ok(token);
            }

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }


    @GetMapping("/account")
    public AdminUserDTO getAccount(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String email = jwtUtilityClass.extractUsername(jwt);

        Optional<MyUser> userOpt = Optional.ofNullable(myUserRepository.findByEmail(email)).orElse(null);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        MyUser user = userOpt.get();
        return new AdminUserDTO(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getFathername(),
            user.getNid(),
            user.getPhone(),
            user.getLiteracyLevel(),
            user.getEmail(),
            user.getUsername(),
            user.getRole()
        );
    }

    //activate or de-activate user
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<MyUser> toggleUserActive(@PathVariable Long id) {
        Optional<MyUser> existingUser = myUserRepository.findById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        MyUser user = existingUser.get();
        user.setIsActive(!user.getIsActive()); // Toggle isActive status
        MyUser updatedUser = myUserRepository.save(user);
    
        return ResponseEntity.ok(updatedUser);
   }

}