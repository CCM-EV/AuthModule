package com.xdpmhdt.authmodule.repository;

import com.xdpmhdt.authmodule.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    
    @Query("SELECT o FROM OutboxEvent o WHERE o.published = false AND o.retryCount < 5 ORDER BY o.createdAt ASC")
    List<OutboxEvent> findUnpublishedEvents();
}
