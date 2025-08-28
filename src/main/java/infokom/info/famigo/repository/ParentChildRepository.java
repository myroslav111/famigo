package infokom.info.famigo.repository;

import infokom.info.famigo.entity.ParentChild;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentChildRepository extends JpaRepository<ParentChild, Long> {
    List<ParentChild> findByChildId(Long childId);
}
