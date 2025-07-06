package com.govnet.govnet.repo;

import com.govnet.govnet.entity.ForgotPassword;
import com.govnet.govnet.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {

    Optional<ForgotPassword> findFirstByUserAndIsUsedOrderByCreatedDateDesc(MyUser user, Boolean isUsed);
    ForgotPassword findByOtpCode(String otpCode);

}
