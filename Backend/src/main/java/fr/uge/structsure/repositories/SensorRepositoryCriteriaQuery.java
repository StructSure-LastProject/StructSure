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

/**
 * The sensor repository using the api criteria query
 */
@Repository
public class SensorRepositoryCriteriaQuery {

    @PersistenceContext
    EntityManager em;

    /**
     * Finds all the sensors that are present in the structure
     * @param structureId the structure id
     * @param request the request containing data like filter by and order by
     * @return List<SensorDTO> the list of the sensors
     * @throws TraitementException error with code DATE_FORMAT_ERROR if there is an error while converting date
     */
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
        Expression<Boolean> isNokPresent = checkIsNokPresent(cb, result);
        Expression<Boolean> isDefectivePresent = checkIsDefectivePresent(cb, result);

        Expression<Integer> state = getState(cb, resultCount, isNokPresent, isDefectivePresent);

        cq.select(cb.construct(SensorDTO.class,
                sensor.get("sensorId").get("controlChip"),
                sensor.get("sensorId").get("measureChip"),
                sensor.get("name"),
                sensor.get("note"),
                state,
                sensor.get("archived"),
                sensor.get("installationDate"),
                sensor.get("x"),
                sensor.get("y")
        ));


        if (request.archivedFilter() != null) {
            predicates.add(cb.equal(sensor.get("archived"), request.archivedFilter()));
        }

        addMinAndMaxInstallationDatePredicate(request, predicates, cb, sensor);

        cq.where(predicates.toArray(new Predicate[0]));
        cq.groupBy(sensor.get("sensorId"));

        if (request.stateFilter() != null) {
            var stateFilterEnum = State.valueOf(request.stateFilter());
            cq.having(cb.equal(state, stateFilterEnum.ordinal()));
        }
        var orderByColumn = AllSensorsByStructureRequestDTO.OrderByColumn.valueOf(request.orderByColumn());
        var orderType = OrderEnum.valueOf(request.orderType());
        Order order = switch (orderByColumn) {
            case NAME -> orderType.equals(OrderEnum.ASC) ?
                        cb.asc(sensor.get(orderByColumn.getValue())) : cb.desc(sensor.get(orderByColumn.getValue()));
            case STATE -> orderType.equals(OrderEnum.ASC) ? cb.asc(getWhen(cb, state)) : cb.desc(getWhen(cb, state));
            case INSTALLATION_DATE -> orderType.equals(OrderEnum.ASC) ?
                        cb.asc(sensor.get("installationDate")) : cb.desc(sensor.get("installationDate"));
        };
        cq.orderBy(order);
        var query = em.createQuery(cq);
        query.setFirstResult(request.offset());
        query.setMaxResults(request.limit());
        return query.getResultList();
    }

    private static void addMinAndMaxInstallationDatePredicate(AllSensorsByStructureRequestDTO request, ArrayList<Predicate> predicates, CriteriaBuilder cb, Root<Sensor> sensor) throws TraitementException {
        if (request.minInstallationDate() != null && !request.minInstallationDate().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                predicates.add(cb.greaterThanOrEqualTo(sensor.get("installationDate"), LocalDateTime.parse(request.minInstallationDate(), formatter)));
                if (request.maxInstallationDate() != null && !request.maxInstallationDate().isEmpty()) {
                    predicates.add(cb.lessThanOrEqualTo(sensor.get("installationDate"), LocalDateTime.parse(request.maxInstallationDate(), formatter)));
                }
            } catch (DateTimeParseException e) {
                throw new TraitementException(Error.DATE_FORMAT_ERROR);
            }
        }
    }

    private static Predicate checkIsDefectivePresent(CriteriaBuilder cb, Join<Object, Object> result) {
        return cb.greaterThan(
                cb.count(cb.<Long>selectCase().when(cb.equal(result.get("state"), StateEnum.DEFECTIVE), 1L)),
                0L
        );
    }

    private static Predicate checkIsNokPresent(CriteriaBuilder cb, Join<Object, Object> result) {
        return cb.greaterThan(
                cb.count(cb.<Long>selectCase().when(cb.equal(result.get("state"), StateEnum.NOK), 1L)),
                0L
        );
    }

    private static Expression<Integer> getState(CriteriaBuilder cb, Expression<Long> resultCount, Expression<Boolean> isNokPresent, Expression<Boolean> isDefectivePresent) {
        return cb.<Integer>selectCase()
                .when(cb.equal(resultCount, 0), State.UNKNOWN.ordinal())
                .when(isNokPresent, State.NOK.ordinal())
                .when(isDefectivePresent, State.DEFECTIVE.ordinal())
                .otherwise(State.OK.ordinal());
    }

    private static CriteriaBuilder.Case<Integer> getWhen(CriteriaBuilder cb, Expression<Integer> caseExpression) {
        return cb.<Integer>selectCase()
                .when(cb.equal(caseExpression, State.NOK.ordinal()), 1)
                .when(cb.equal(caseExpression, State.DEFECTIVE.ordinal()), 2)
                .when(cb.equal(caseExpression, State.OK.ordinal()), 3)
                .when(cb.equal(caseExpression, State.UNKNOWN.ordinal()), 4);
    }
}
