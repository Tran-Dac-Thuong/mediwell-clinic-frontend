/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package group2.client.repository;

import group2.client.entities.Doctor;
import group2.client.entities.Rating;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author hokim
 */
public interface RatingByDoctorRepository extends JpaRepository<Rating, Integer> {
     List<Rating> findByDoctorId(Doctor doctor);
}
