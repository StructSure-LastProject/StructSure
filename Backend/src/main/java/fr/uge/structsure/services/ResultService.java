package fr.uge.structsure.services;

import fr.uge.structsure.entities.Result;
import fr.uge.structsure.entities.Scan;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.State;
import fr.uge.structsure.repositories.ResultRepository;
import fr.uge.structsure.repositories.ScanRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ResultService {

  private final ResultRepository resultRepository;
  private final ScanRepository scanRepository;

  public ResultService(ResultRepository resultRepository, ScanRepository scanRepository) {
    this.resultRepository = resultRepository;
    this.scanRepository = scanRepository;
  }

  public State getLatestStateByChip(Sensor sensor) {
    var result = resultRepository.findLatestStateBySensor(sensor);
    return result.getState();
  }



  public ScanInfo scanInformations(Long scanId){
    var listResult = resultRepository.findAllByScanId(scanId);
    var scan = scanRepository.findScanById(scanId);
    return new ScanInfo(scan, listResult);
  }
}
