package com.garden.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface PlantRepository extends JpaRepository<Plant, Long> {

    final String query1 = "SELECT * FROM plant WHERE name LIKE %?1%";
    final String queryGetUsersPlants = "SELECT * FROM plant WHERE user_id=?";
    final String queryPostPlant = "INSERT INTO plant(name, instructions, date, image) VALUES (?,?,?,?)";
    final String queryUpdatePlant = "UPDATE plant SET name=?, instructions=?, date=?, image=?, image_id=? WHERE id=?";
    final String queryUpdatePlantWithoutImage = "UPDATE plant SET name=?, instructions=?, date=? WHERE id=?";
    final String queryFindByImageId = "SELECT * FROM plant WHERE image_id=?";
    final String queryGetImageIdsByUserId = "SELECT image_id FROM plant WHERE user_id=?";
    final String queryDeleteByUserId = "DELETE FROM plant WHERE user_id=?";

    @Query(value = query1, nativeQuery = true)
    List<Plant> getPlantsBytext(String name);

    @Query(value = queryGetUsersPlants, nativeQuery = true)
    public List<Plant> getPlantsByUserId(String user_id);

    @Query(value = queryPostPlant, nativeQuery = true)
    public void addPlant(String name, String instructions, String date, String image);

    @Transactional
    @Modifying
    @Query(value = queryUpdatePlant, nativeQuery = true)
    public void updatePlant(String name, String instructions, String date, String image, String image_id, long id);

    @Transactional
    @Modifying
    @Query(value = queryUpdatePlantWithoutImage, nativeQuery = true)
    public void updatePlantWithoutImage(String name, String instructions, String date, long id);

    @Query(value = queryFindByImageId, nativeQuery = true)
    public Plant findPlantByImageId(String imgId);

    @Query(value = queryGetImageIdsByUserId, nativeQuery = true)
    public List<String> findImageIdsByUserName(String userName);

    @Transactional
    @Modifying
    @Query(value = queryDeleteByUserId, nativeQuery = true)
    public void deleteByUserName(String userName);

}
