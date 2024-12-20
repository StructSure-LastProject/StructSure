package fr.uge.structsure.services;

import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.SensorId;
import fr.uge.structsure.entities.State;
import fr.uge.structsure.repositories.ResultRepository;
import fr.uge.structsure.repositories.ScanRepository;
import fr.uge.structsure.repositories.SensorRepository;
import org.springframework.stereotype.Service;

@Service
public class ResultService {

        private final ResultRepository resultRepository;
        private final ScanRepository scanRepository;

        private final SensorRepository sensorRepository;

        public ResultService(ResultRepository resultRepository, ScanRepository scanRepository, SensorRepository sensorRepository) {
            this.resultRepository = resultRepository;
            this.scanRepository = scanRepository;
            this.sensorRepository = sensorRepository;
        }

        public State getLatestStateByChip(String controlCip, String measureChip) {
            var result = resultRepository.findLatestStateBySensor(sensorRepository.findBySensorId(new SensorId(controlCip, measureChip)).getFirst());
            return result.getState();
        }

}
