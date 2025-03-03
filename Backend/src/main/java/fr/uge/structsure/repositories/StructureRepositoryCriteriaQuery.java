package fr.uge.structsure.repositories;

import fr.uge.structsure.dto.structure.AllStructureRequestDTO;
import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.entities.*;
import fr.uge.structsure.utils.OrderEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for structures but using api criteria query
 */
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
        var state = cb.<Integer>selectCase()
                .when(cb.equal(cb.countDistinct(result.get("id")), 0L), State.UNKNOWN.ordinal())
                .when(cb.greaterThan(countNok, 0L), State.NOK.ordinal())
                .when(cb.greaterThan(countDefective, 0L), State.DEFECTIVE.ordinal())
                .otherwise(State.OK.ordinal());

        cq.select(cb.construct(AllStructureResponseDTO.class,
            structure.get("id"),
            structure.get("name"),
            countMeasureChip,
            countPlans,
            state,
            structure.get("note"),
            structure.get("archived")
        ));

        var predicates = new ArrayList<Predicate>();
        predicates.add(cb.like(cb.lower(structure.get("name")), "%" + allStructureRequestDTO.searchByName().toLowerCase() + "%"));

        if (allStructureRequestDTO.archived().isPresent()) {
            predicates.add(cb.equal(structure.get("archived"), true));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        cq.groupBy(structure.get("id"));

        if (allStructureRequestDTO.searchByState().isPresent()) {
            var requestedState = allStructureRequestDTO.searchByState().get();
            Predicate statePredicate;
            if (requestedState == State.UNKNOWN) {
                statePredicate = cb.equal(countMeasureChip, 0L);
            } else if (requestedState == State.DEFECTIVE) {
                statePredicate = cb.greaterThan(countDefective, 0L);
            } else if (requestedState == State.NOK) {
                statePredicate = cb.and(
                        cb.equal(countDefective, 0L),
                        cb.greaterThan(countNok, 0L)
                );
            } else { // State.OK
                statePredicate = cb.and(
                        cb.equal(countDefective, 0L),
                        cb.equal(countNok, 0L),
                        cb.greaterThan(countMeasureChip, 0L)
                );
            }

            cq.having(statePredicate);
        }

        Expression<?> orderExpression;
        switch (AllStructureRequestDTO.OrderByColumn.valueOf(allStructureRequestDTO.orderByColumnName())) {
            case NUMBER_OF_SENSORS -> orderExpression = countMeasureChip;
            case NAME -> orderExpression = structure.get("name");
            case STATE -> orderExpression = cb.selectCase()
                    .when(cb.equal(state, State.NOK.ordinal()), 1)
                    .when(cb.equal(state, State.DEFECTIVE.ordinal()), 2)
                    .when(cb.equal(state, State.UNKNOWN.ordinal()), 3)
                    .when(cb.equal(state, State.OK.ordinal()), 4);
            default -> orderExpression = structure.get("id");
        }
        switch (OrderEnum.valueOf(allStructureRequestDTO.orderType())) {
            case ASC -> cq.orderBy(cb.desc(orderExpression));
            case DESC -> cq.orderBy(cb.asc(orderExpression));
        };
        return em.createQuery(cq).getResultList();
    }

}
