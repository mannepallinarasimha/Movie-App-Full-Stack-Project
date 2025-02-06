package com.moviemix.MovieApi.auth.repositories;

import com.moviemix.MovieApi.auth.entities.ForgotPassword;
import com.moviemix.MovieApi.auth.entities.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
    @Query("select fp from ForgotPassword fp where fp.otp=?1 and fp.user=?2")
    Optional<ForgotPassword> findByOtpAndEmail(Integer otp, User user);
}
