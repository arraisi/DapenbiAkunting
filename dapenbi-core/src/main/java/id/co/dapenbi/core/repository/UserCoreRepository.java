package id.co.dapenbi.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.co.dapenbi.core.model.User;

public interface UserCoreRepository  extends JpaRepository<User, Long> {

}
