package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.TourniquetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Repository
public interface TourniquetHistoryRepo extends JpaRepository<TourniquetHistory,Integer> {
    List<TourniquetHistory> findAllByExitedAtBetween(Timestamp fromDate, Timestamp toDate);

    Collection<? extends TourniquetHistory> findAllByEnteredAtBetween(Timestamp fromDate, Timestamp toDate);

}
