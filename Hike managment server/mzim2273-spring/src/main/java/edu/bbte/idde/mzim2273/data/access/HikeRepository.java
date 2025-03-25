package edu.bbte.idde.mzim2273.data.access;

import edu.bbte.idde.mzim2273.data.model.Hike;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Profile("jpa")
@Repository
public interface HikeRepository extends JpaRepository<Hike, Long> {
    Collection<Hike> findByNameContaining(String name);
    Collection<Hike> findByPriceLessThan(Double price);

    Collection<Hike> findByDifficultyEquals(Integer difficulty);
}
