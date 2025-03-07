package com.fennec.fennecgo.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fennec.fennecgo.dto.request.LoginRequest;
import com.fennec.fennecgo.dto.request.SignupRequest;
import com.fennec.fennecgo.dto.response.JwtResponse;
import com.fennec.fennecgo.dto.response.MessageRequestResponse;
import com.fennec.fennecgo.models.ERole;
import com.fennec.fennecgo.models.Role;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.repository.RoleRepository;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.security.jwt.JwtUtils;
import com.fennec.fennecgo.security.services.UserDetailsImpl;
import com.fennec.fennecgo.services.Interface.WalletService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;
  
  @Autowired
  WalletService walletService;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    logger.info("Attempting to authenticate user with phone: {}", loginRequest.getPhone());
    

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getPhone(), loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateJwtToken(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());

      logger.info("User authenticated successfully: {}", userDetails.getPhone());

      return ResponseEntity.ok(new JwtResponse(jwt,
          userDetails.getId(),
          userDetails.getUsername(),
          userDetails.getEmail(),
          userDetails.getPhone(),
          roles));
    } catch (Exception e) {
      logger.error("Authentication failed for phone: {}. Error: {}", loginRequest.getPhone(), e.getMessage());
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Error: Invalid phone or password!"));
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    logger.info("Attempting to register user with phone: {}", signUpRequest.getPhone());

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      logger.warn("Registration failed: Email already in use - {}", signUpRequest.getEmail());
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Error: Email is already in use!"));
    }

    if (userRepository.existsByPhone(signUpRequest.getPhone())) {
      logger.warn("Registration failed: Phone number already in use - {}", signUpRequest.getPhone());
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Error: Phone number is already in use!"));
    }

    User user = new User(
        signUpRequest.getUsername(),
        signUpRequest.getEmail(),
        signUpRequest.getPhone(),
        encoder.encode(signUpRequest.getPassword())
    );

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);
            break;
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
     User saveUser  =userRepository.save(user);
     walletService.createDefaultWalletForUser(saveUser.getId());
    logger.info("User registered successfully with Id: {}", saveUser.getId());
    return ResponseEntity.ok(new MessageRequestResponse("User registered successfully!"));
  }
}
