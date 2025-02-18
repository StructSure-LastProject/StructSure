package fr.uge.structsure.repositories;

import fr.uge.structsure.dto.sensors.AllSensorsByStructureRequestDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.entities.Result;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.State;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.StateEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SensorRepositoryCriteriaQuery {

    @PersistenceContext
    EntityManager em;

    public List<SensorDTO> findAllSensorsByStructureId(long structureId, AllSensorsByStructureRequestDTO request) throws TraitementException {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(SensorDTO.class);
        var sensor = cq.from(Sensor.class);
        var result = sensor.join("results", JoinType.LEFT);
        var plan = sensor.join("plan", JoinType.LEFT);

        var predicates = new ArrayList<Predicate>();
        if (request.planFilter() != null && !request.planFilter().isEmpty()) {
            predicates.add(cb.equal(plan.get("name"), request.planFilter()));
        }
        predicates.add(cb.equal(sensor.get("structure").get("id"), structureId));

        Expression<Long> resultCount = cb.count(result);
        Expression<Boolean> isNokPresent = cb.greaterThan(
                cb.count(cb.<Long>selectCase().when(cb.equal(result.get("state"), StateEnum.NOK), 1L)),
                0L
        );
        Expression<Boolean> isDefectivePresent = cb.greaterThan(
                cb.count(cb.<Long>selectCase().when(cb.equal(result.get("state"), StateEnum.DEFECTIVE), 1L)),
                0L
        );
        var caseExpression = cb.<String>selectCase()
                .when(cb.equal(resultCount, 0), "UNKNOWN")
                .when(isNokPresent, "NOK")
                .when(isDefectivePresent, "DEFECTIVE")
                .otherwise("OK");

        cq.select(cb.construct(SensorDTO.class,
                sensor.get("sensorId").get("controlChip"),
                sensor.get("sensorId").get("measureChip"),
                sensor.get("name"),
                sensor.get("note"),
                caseExpression,
                sensor.get("archived"),
                sensor.get("installationDate"),
                sensor.get("x"),
                sensor.get("y")
        ));

        if (request.stateFilter() != null) {
            predicates.add(cb.equal(result.get("state"), request.stateFilter()));
        }

        if (request.minInstallationDate() != null && !request.minInstallationDate().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                predicates.add(cb.greaterThanOrEqualTo(sensor.get("installationDate"), LocalDateTime.parse(request.minInstallationDate(), formatter)));
                if (request.maxInstallationDate() != null && !request.maxInstallationDate().isEmpty()) {
                    predicates.add(cb.lessThanOrEqualTo(sensor.get("installationDate"), LocalDateTime.parse(request.maxInstallationDate(), formatter)));
                }
            } catch (ParseException e) {
                throw new TraitementException(Error.DATE_FORMAT_ERROR);
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.groupBy(sensor.get("sensorId"));
        System.err.println(request.orderByColumn().name() + " " + request.orderType().name());
        Order order = switch (request.orderByColumn()) {
            case NAME -> request.orderType().equals(OrderEnum.ASC) ?
                        cb.asc(sensor.get(request.orderByColumn().getValue())) : cb.desc(sensor.get(request.orderByColumn().getValue()));
            case STATE -> request.orderType().equals(OrderEnum.ASC) ?
                    cb.asc(cb.<Integer>selectCase()
                            .when(cb.equal(caseExpression,  "NOK"), 1)
                            .when(cb.equal(caseExpression, "DEFECTIVE"), 2)
                            .when(cb.equal(caseExpression, "OK"), 3)
                            .when(cb.equal(caseExpression, "UNKNOWN"), 4))
                    : cb.desc(cb.<Integer>selectCase().when(cb.equal(caseExpression,  "NOK"), 1)
                    .when(cb.equal(caseExpression, "DEFECTIVE"), 2)
                    .when(cb.equal(caseExpression, "OK"), 3)
                    .when(cb.equal(caseExpression, "UNKNOWN"), 4));
            case INSTALLATION_DATE -> request.orderType().equals(OrderEnum.ASC) ?
                        cb.asc(sensor.get("installationDate")) : cb.desc(sensor.get("installationDate"));
        };
        cq.orderBy(order);
        var query = em.createQuery(cq);
        query.setFirstResult(request.offset());
        query.setMaxResults(request.limit());
        return query.getResultList();
    }
}
