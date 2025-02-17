package fr.uge.structsure.repositories;

import fr.uge.structsure.dto.structure.AllStructureRequestDTO;
import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.entities.*;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.StateEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StructureRepositoryCriteriaQuery {

    @PersistenceContext
    EntityManager em;

    /**
     * Returns the list of structures sorted by sortTypeEnum and for each one its state, number of sensors,
     * number of plans and if it's archived
     * @return List<AllStructureResponseDTO> list of the structures
     */
    public List<AllStructureResponseDTO> findAllStructuresWithState(AllStructureRequestDTO allStructureRequestDTO) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(AllStructureResponseDTO.class);
        var structure = cq.from(Structure.class);
        var sensor = structure.join("sensors", JoinType.LEFT);
        var result = sensor.join("results", JoinType.LEFT);
        var plan = structure.join("plans", JoinType.LEFT);

        var countMeasureChip = cb.countDistinct(sensor.get("sensorId").get("measureChip"));
        var countPlans = cb.countDistinct(plan.get("id"));
        var countDefective = cb.sum(cb.<Long>selectCase()
                .when(cb.equal(result.get("state"), State.DEFECTIVE), 1L)
                .otherwise(0L));
        var countNok = cb.sum(cb.<Long>selectCase()
                .when(cb.equal(result.get("state"), State.NOK), 1L)
                .otherwise(0L));
        var state = cb.<String>selectCase()
                .when(cb.equal(cb.countDistinct(sensor.get("sensorId")), 0L), "UNKNOWN")
                .when(cb.greaterThan(countDefective, 0L), "DEFECTIVE")
                .when(cb.greaterThan(countNok, 0L), "NOK")
                .otherwise("OK");

        cq.select(cb.construct(AllStructureResponseDTO.class,
                structure.get("id"),
                structure.get("name"),
                countMeasureChip,
                countPlans,
                state,
                structure.get("archived")
        ));

        var namePredicate = cb.like(cb.lower(structure.get("name")), "%" + allStructureRequestDTO.searchByName().toLowerCase() + "%");
        cq.where(namePredicate);
        cq.groupBy(structure.get("id"));

        Expression<?> orderExpression;
        switch (allStructureRequestDTO.orderByColumnName()) {
            case NUMBER_OF_SENSORS -> orderExpression = countMeasureChip;
            case NAME -> orderExpression = structure.get("name");
            case STATE -> orderExpression = cb.selectCase()
                    .when(cb.equal(structure.get("archived"), true), 4)
                    .when(cb.equal(state, "NOK"), 1)
                    .when(cb.equal(state, "DEFECTIVE"), 2)
                    .when(cb.equal(state, "OK"), 3);
            default -> orderExpression = structure.get("id");
        }
        switch (allStructureRequestDTO.orderType()) {
            case ASC -> cq.orderBy(cb.desc(orderExpression));
            case DESC -> cq.orderBy(cb.asc(orderExpression));
        };
        return em.createQuery(cq).getResultList();
    }

}
