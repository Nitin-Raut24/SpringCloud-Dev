package com.example.graph;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeReposotory extends JpaRepository<Employee, Integer>{

}
