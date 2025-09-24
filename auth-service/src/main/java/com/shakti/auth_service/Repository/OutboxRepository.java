package com.shakti.auth_service.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shakti.auth_service.Entity.OutboxEvent;


@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent,Long>{

    // finding all the event which are not publishing
    List<OutboxEvent> findByPublished(boolean published);
}
