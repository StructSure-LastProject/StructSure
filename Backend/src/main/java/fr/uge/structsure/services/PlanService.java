package fr.uge.structsure.services;

import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.repositories.PlanRepository;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class PlanService {
    private final PlanRepository planRepository;
    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public List<File> findAll(Long StructureId) {
        var plans = planRepository.findAllByStructureId(StructureId);
        return plans.stream().map(Plan::getFile).toList();
    }
}
