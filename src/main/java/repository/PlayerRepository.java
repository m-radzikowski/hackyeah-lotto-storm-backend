package repository;

import entity.Player;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@Repository
public interface PlayerRepository extends EntityRepository<Player, Long> {

	Optional<Player> findById(Long id);

	Optional<Player> findByUsername(String username);
}
