package lads.lads_website.service;

import lads.lads_website.domain.LevelingCost;
import lads.lads_website.repository.LevelingCostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelingCostService {

    private LevelingCostRepository levelingCostRepository;

    @Autowired
    public LevelingCostService(LevelingCostRepository levelingCostRepository) {
        this.levelingCostRepository = levelingCostRepository;
    }

    public List<LevelingCost> getLevelingCostFromStartLevelToEndGivenRarity(int startLevel, int endLevel, String rarity) {
        return levelingCostRepository.findAllByLevelGreaterThanAndLevelLessThanEqualAndRarityType(startLevel, endLevel, rarity);
    }
}
