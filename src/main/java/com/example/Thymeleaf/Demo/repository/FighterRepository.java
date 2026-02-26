package com.example.Thymeleaf.Demo.repository;

import com.example.Thymeleaf.Demo.Model.Fighter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FighterRepository extends JpaRepository<Fighter, Integer> {

}
