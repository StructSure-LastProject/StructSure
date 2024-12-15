
package fr.uge.structsure.controllers;

import fr.uge.structsure.entities.*;
import fr.uge.structsure.repositories.*;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;

@RestController
@RequestMapping("")
public class StructsureBackendController {
    private final SensorRepository sensorRepository;
    private final StructureRepository structureRepository;
    private final AccountRepository accountRepository;
    private final ScanRepository scanRepository;
    private final ResultRepository resultRepository;

    public StructsureBackendController(SensorRepository sensorRepository,
                                StructureRepository structureRepository,
                                AccountRepository accountRepository,
                                ScanRepository scanRepository, ResultRepository resultRepository) {
        this.sensorRepository = sensorRepository;
        this.structureRepository = structureRepository;
        this.accountRepository = accountRepository;
        this.scanRepository = scanRepository;
        this.resultRepository = resultRepository;
    }

    /*
    @GetMapping("/demo")
    public void demo() {
        var structure = new Structure("pont de démo1", "ce pont contient 7000 capteurs", false);
        structureRepository.save(structure);
        for(double i = 0; i < 7000;i++){
            var date =  new Time(System.currentTimeMillis());
            var chipcontrol = "chip control " + i;
            var chipmeasure = "chip measure " + i;
            var sensor = new Sensor(chipcontrol, chipmeasure, "name factice", "note de test", date, i, i, false, structure);
            sensorRepository.save(sensor);
        }
        structure = new Structure("pont de démo2", "ce pont contient 7000 capteurs", false);
        structureRepository.save(structure);
        for(double i = 7000; i < 14_000;i++){
            var date =  new Time(System.currentTimeMillis());
            var chipcontrol = "chip control " + i;
            var chipmeasure = "chip measure " + i;
            var sensor = new Sensor(chipcontrol, chipmeasure, "name factice", "note de test", date, i, i, false, structure);
            sensorRepository.save(sensor);
        }
        structure = new Structure("pont de démo3", "ce pont contient 7000 capteurs", false);
        structureRepository.save(structure);
        for(double i = 14_000; i < 21_000;i++){
            var date =  new Time(System.currentTimeMillis());
            var chipcontrol = "chip control " + i;
            var chipmeasure = "chip measure " + i;
            var sensor = new Sensor(chipcontrol, chipmeasure, "name factice", "note de test", date, i, i, false, structure);
            sensorRepository.save(sensor);
        }
        structure = new Structure("pont de démo4", "ce pont contient 7000 capteurs", false);
        structureRepository.save(structure);
        for(double i = 21_000; i < 28_000;i++){
            var date =  new Time(System.currentTimeMillis());
            var chipcontrol = "chip control " + i;
            var chipmeasure = "chip measure " + i;
            var sensor = new Sensor(chipcontrol, chipmeasure, "name factice", "note de test", date, i, i, false, structure);
            sensorRepository.save(sensor);
        }
        structure = new Structure("pont de démo5", "ce pont contient 7000 capteurs", false);
        structureRepository.save(structure);
        for(double i = 28_000; i < 35_000;i++){
            var date =  new Time(System.currentTimeMillis());
            var chipcontrol = "chip control " + i;
            var chipmeasure = "chip measure " + i;
            var sensor = new Sensor(chipcontrol, chipmeasure, "name factice", "note de test", date, i, i, false, structure);
            sensorRepository.save(sensor);
        }
        System.out.println("done");

        // Dans la base il existe 5 structures, id de 1 à 5.

        // Création d'accounts de test
        var account1 = new Account("login", "password", "f", "l", Role.ADMIN, true);
        var account2 = new Account("Jefe", "password", "Baptise", "Venault", Role.RESPONSABLE, true);
        var account3 = new Account("Finkou", "password", "Philippe", "Finkel", Role.OPERATEUR, true);
        var account4 = new Account("Foraxus", "password", "Rémi", "Forax", Role.OPERATEUR, true);
        var account5 = new Account("Revuzinho", "password", "Dominique", "Revuz", Role.OPERATEUR, true);
        accountRepository.save(account1);
        accountRepository.save(account2);
        accountRepository.save(account3);
        accountRepository.save(account4);
        accountRepository.save(account5);

        for (int p = 1; p <= 5; p++) { // Pour chaque pont
            Structure struct = structureRepository.findById(p);
            for (int x = 0; x < 5; x++) { // 5 scans par pont
                var scan = new Scan(struct, new Time(System.currentTimeMillis()), "note de test", account3);
                scanRepository.save(scan);
                for (int i = 0; i < 100; i++) { // 100 résultats par scan
                    var sensor = sensorRepository.findSensorBySensorId_ControlChipAndSensorId_MeasureChip("chip control " + (i + ((p - 1) * 7000)) + ".0",
                            "chip measure " + (i + ((p - 1) * 7000)) + ".0");
                    var result = new Result(State.OK, sensor, scan);
                    resultRepository.save(result);
                }
            }
        }
    }

     */
}
    