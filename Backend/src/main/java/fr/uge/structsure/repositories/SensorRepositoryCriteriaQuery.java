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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SensorRepositoryCriteriaQuery {

    @PersistenceContext
    EntityManager em;


    public List<SensorDTO> findAllSensorsByStructureId(long structureId, AllSensorsByStructureRequestDTO request) throws TraitementException {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(SensorDTO.class);
        var sensor = cq.from(Sensor.class);
        var result = sensor.join("results", JoinType.LEFT);
        var predicates = new ArrayList<Predicate>();
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
        var caseExpression = cb.selectCase()
                .when(cb.equal(resultCount, 0), StateEnum.UNKNOWN)
                .when(isNokPresent, StateEnum.NOK)
                .when(isDefectivePresent, StateEnum.DEFECTIVE)
                .otherwise(StateEnum.OK);

        cq.select(cb.construct(SensorDTO.class, sensor, caseExpression));

        predicates.add(cb.equal(result.get("state"), request.stateFilter()));

        var dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (request.minInstallationDate() != null && !request.minInstallationDate().isEmpty()) {
            try {
                predicates.add(cb.greaterThanOrEqualTo(sensor.get("installationDate"), dateFormat.parse(request.minInstallationDate())));
                if (request.maxInstallationDate() != null && !request.maxInstallationDate().isEmpty()) {
                    predicates.add(cb.lessThanOrEqualTo(sensor.get("installationDate"), dateFormat.parse(request.maxInstallationDate())));
                }
            } catch (ParseException e) {
                throw new TraitementException(Error.DATE_FORMAT_ERROR);
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.groupBy(sensor.get("sensorId"));

        Order order = switch (request.orderByColumn()) {
            case "NAME" -> request.orderType().equals(OrderEnum.ASC) ?
                        cb.asc(sensor.get("name")) : cb.desc(sensor.get("name"));
            case "STATE" -> request.orderType().equals(OrderEnum.ASC) ?
                        cb.asc(result.get("state")) : cb.desc(result.get("state"));
            case "LAST_QUERY_DATE" -> request.orderType().equals(OrderEnum.ASC) ?
                        cb.asc(result.get("scan").get("date")) : cb.desc(result.get("scan").get("date"));
            case "INSTALLATION_DATE" -> request.orderType().equals(OrderEnum.ASC) ?
                        cb.asc(sensor.get("installationDate")) : cb.desc(sensor.get("installationDate"));
            default -> request.orderType().equals(OrderEnum.ASC) ?
                    cb.asc(sensor.get("sensorId")) : cb.desc(sensor.get("sensorId"));
        };
        cq.orderBy(order);
        return em.createQuery(cq).getResultList();
    }
}
