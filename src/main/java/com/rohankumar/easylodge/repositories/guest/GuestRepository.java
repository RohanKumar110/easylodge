package com.rohankumar.easylodge.repositories.guest;

import com.rohankumar.easylodge.entities.guest.Guest;
import com.rohankumar.easylodge.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {

    List<Guest> findByUser(User user);
}
