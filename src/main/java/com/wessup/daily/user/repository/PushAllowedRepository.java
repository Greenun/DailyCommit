package com.wessup.daily.user.repository;

import com.wessup.daily.user.entity.PushAllowed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushAllowedRepository extends JpaRepository<PushAllowed, Long> {
}
