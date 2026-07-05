package DyProSo2023.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {

    @Override
    List<Attendee> findAll();

    Attendee findByResetPasswordToken(String token);

    Attendee findByVerificationCode(String verificationCode);
}
