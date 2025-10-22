package lads.lads_website.service;

import lads.lads_website.domain.AscensionCost;
import lads.lads_website.repository.AscensionCostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AscensionCostService {
    private AscensionCostRepository ascensionCostRepository;

    @Autowired
    public AscensionCostService(AscensionCostRepository ascensionCostRepository) {
        this.ascensionCostRepository = ascensionCostRepository;
    }

    public List<AscensionCost> getAllAscensionCostsForGivenRarityStartAndEndLevel (String rarity, int start, int end, boolean hasStartAscension, boolean wantsFinalAscension) {
        if (!hasStartAscension && wantsFinalAscension) {
            return ascensionCostRepository.findAllByLevelGreaterThanEqualAndLevelLessThanEqualAndRarityType(start, end, rarity);
        } else if (hasStartAscension && wantsFinalAscension) {
            return ascensionCostRepository.findAllByLevelGreaterThanAndLevelLessThanEqualAndRarityType(start, end, rarity);
        } else if (!hasStartAscension) {
            return ascensionCostRepository.findAllByLevelGreaterThanEqualAndLevelLessThanAndRarityType(start, end, rarity);
        } else {
            return ascensionCostRepository.findAllByLevelGreaterThanAndLevelLessThanAndRarityType(start, end, rarity);
        }
    }
}
