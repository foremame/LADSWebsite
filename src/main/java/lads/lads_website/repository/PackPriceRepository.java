package lads.lads_website.repository;

import lads.lads_website.domain.PackPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackPriceRepository extends JpaRepository<PackPrice, Long> {
}
