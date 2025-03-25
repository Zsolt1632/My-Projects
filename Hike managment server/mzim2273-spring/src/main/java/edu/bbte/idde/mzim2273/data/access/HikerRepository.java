package edu.bbte.idde.mzim2273.data.access;

import edu.bbte.idde.mzim2273.data.model.Hiker;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface HikerRepository extends JpaRepository<Hiker,Long> {
}
