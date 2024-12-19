package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.AddSensorAnswerDTO;
import fr.uge.structsure.dto.sensors.AddSensorDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.sort.SortStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final StructureRepository structureRepository;
    private final StructureService structureService;

    public SensorService(SensorRepository sensorRepository, StructureRepository structureRepository, @Autowired StructureService structureService) {
        this.sensorRepository = sensorRepository;
        this.structureRepository = structureRepository;
        this.structureService = structureService;
    }

    public List<SensorDTO> getSensorDTOsByStructure(
            Long structureId,
            String tri,
            String ordre,
            String filtreEtat,
            LocalTime dateInstallationMin,
            LocalTime dateInstallationMax) {

        var sensors = sensorRepository.findByStructureId(structureId);
        if (sensors.isEmpty()) {
            throw new RuntimeException("Ouvrage introuvable");
        }

        // Filtrage par état (archivé ou actif)
        sensors = sensors.stream().toList();

        // Application du tri avec le design pattern Strategy
        var comparator = SortStrategyFactory.getStrategy(tri).getComparator();
        if ("desc".equalsIgnoreCase(ordre)) {
            comparator = comparator.reversed();
        }

        return sensors.stream()
                .sorted(comparator)
                .map(SensorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public AddSensorAnswerDTO addSensor(AddSensorDTO addSensorDTO) throws TraitementException {
        if(!sensorRepository.findAllByName(addSensorDTO.name()).isEmpty()) {
            throw new TraitementException(ErrorIdentifier.NAME_EXISTS);
        }
        if(sensorRepository.countBySensorId(addSensorDTO.sensorId()) > 0){
            throw new TraitementException(ErrorIdentifier.TAGS_EXISTS);
        }
        if(!structureService.checkIfStructureExists(addSensorDTO.structureId())){
            throw new TraitementException(ErrorIdentifier.STRUCTURE_NOT_EXIST);
        }
        if(addSensorDTO.hasNullMembers()){
            throw new TraitementException(ErrorIdentifier.UNDEFINED_FIELD);
        }
        var sensor = new Sensor(addSensorDTO.sensorId().getControlChip(), addSensorDTO.sensorId().getControlChip(), addSensorDTO.name(), addSensorDTO.note(), addSensorDTO.installationDate(), -1d, -1d, false, structureRepository.findById(addSensorDTO.structureId()).orElseThrow());
        var result = sensorRepository.save(sensor);
        return new AddSensorAnswerDTO(result.getSensorId());
    }

    /*public AddSensorAnswerDTO addSensor(AddSensorDTO addSensorDTO){
        var sensor = new Sensor(addSensorDTO.sensorId().getControlChip(), addSensorDTO.sensorId().getControlChip(), addSensorDTO.name(), addSensorDTO.note(), LocalTime.now(), -1d, -1d, false, structureRepository.findById(addSensorDTO.structureId()).orElseThrow());
        var result = sensorRepository.save(sensor);
        return new AddSensorAnswerDTO(result.getSensorId());
    }*/
}
