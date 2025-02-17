package fr.uge.structsure.repositories;

import fr.uge.structsure.dto.structure.AllStructureRequestDTO;
import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StructureRepositoryCriteriaQuery {

    EntityManager em;

    List<AllStructureResponseDTO> findAllStructuresWithStateDesc(AllStructureRequestDTO.SortTypeEnum sortTypeEnum, String containsName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AllStructureResponseDTO> cq = cb.createQuery(AllStructureResponseDTO.class);

        Root<Structure> structure = cq.from(Structure.class);

        Join<Structure, Sensor> sensorJoin = structure.join("sensors", JoinType.LEFT);
        Join<Sensor, Result> resultJoin = sensorJoin.join("results", JoinType.LEFT);
        Join<Structure, Plan> planJoin = structure.join("plans", JoinType.LEFT);

        Expression<Long> countDistinctMeasureChip = cb.countDistinct(sensorJoin.get("sensorId").get("measureChip"));
        Expression<Long> countDistinctPlan = cb.countDistinct(planJoin.get("id"));
        Expression<Long> countDistinctSensor = cb.countDistinct(sensorJoin.get("sensorId"));

        Expression<Integer> defectiveCase = cb.selectCase()
                .when(cb.equal(resultJoin.get("state"), State.DEFECTIVE), 1)
                .otherwise(0);
        Expression<Integer> nokCase = cb.selectCase()
                .when(cb.equal(resultJoin.get("state"), State.NOK), 1)
                .otherwise(0);

        Expression<Long> sumDefective = cb.sum(defectiveCase);
        Expression<Long> sumNok = cb.sum(nokCase);

        CriteriaBuilder.Case<String> stateCase = cb.selectCase()
                .when(cb.equal(countDistinctSensor, 0L), "UNKNOWN")
                .when(cb.greaterThan(sumDefective, 0L), "DEFECTIVE")
                .when(cb.greaterThan(sumNok, 0L), "NOK")
                .otherwise("OK");

        cq.select(cb.construct(
                AllStructureResponseDTO.class,
                structure.get("id"),
                structure.get("name"),
                countDistinctMeasureChip,
                countDistinctPlan,
                stateCase,
                structure.get("archived")
        ));

        ParameterExpression<String> containsNameParam = cb.parameter(String.class, "containsName");
        cq.where(cb.like(
                cb.lower(structure.get("name")),
                cb.concat(cb.concat("%", cb.lower(containsNameParam)), "%")
        ));

        cq.groupBy(structure.get("id"));

        ParameterExpression<String> sortTypeEnumParam = cb.parameter(String.class, "sortTypeEnum");
        cq.orderBy(cb.desc(sortTypeEnumParam));

        TypedQuery<AllStructureResponseDTO> query = em.createQuery(cq);
        query.setParameter("containsName", containsName);
        query.setParameter("sortTypeEnum", sortTypeEnum); // Attention à l'utilisation de ce paramètre pour le tri

        List<AllStructureResponseDTO> resultList = query.getResultList();
    }
}
