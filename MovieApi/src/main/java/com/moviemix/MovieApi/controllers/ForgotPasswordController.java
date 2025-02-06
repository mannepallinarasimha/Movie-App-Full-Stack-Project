package com.moviemix.MovieApi.controllers;

import com.moviemix.MovieApi.auth.entities.ForgotPassword;
import com.moviemix.MovieApi.auth.entities.User;
import com.moviemix.MovieApi.auth.repositories.ForgotPasswordRepository;
import com.moviemix.MovieApi.auth.repositories.UserRepository;
import com.moviemix.MovieApi.auth.utils.ChangePassword;
import com.moviemix.MovieApi.entity.MailBody;
import com.moviemix.MovieApi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping(path="api/v1/forgotpassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    //send email for verification
    @PostMapping(path="verifyEmail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Provide valid email"));
        Integer otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .textBody("This is The OTP for your forgot password request: "+otp)
                .subject("OTP for forgot password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 20 * 100000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);
        return ResponseEntity.ok("Email Sent for verification");
    }


    @PostMapping(path="verifyOTP/{otp}/{email}")
    public ResponseEntity<String> verifyOTP(@PathVariable Integer otp, @PathVariable String email){
        //check user exist or not
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Provide valid email"));
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndEmail(otp, user).orElseThrow(() -> new RuntimeException("Invalid otp for Email: " + email));
        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpId());
            return new ResponseEntity<>("OTP has expired..", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok("OTP verified...");
    }


    @PostMapping(path="changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email){
    // check both passords are same or not
        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>("Please enter password again...", HttpStatus.EXPECTATION_FAILED);
        }
        String encodePassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodePassword);
        return ResponseEntity.ok("Password Has been updated Successfully...");
    }
    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

}
