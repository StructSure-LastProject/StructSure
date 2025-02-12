package fr.uge.structsure.services;

import fr.uge.structsure.dto.plan.AddPlanRequestDTO;
import fr.uge.structsure.dto.plan.AddPlanResponseDTO;
import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.PlanRepository;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class PlanService {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final PlanRepository planRepository;
    private final StructureService structureService;
    private final String workingDir = System.getProperty("user.dir");

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE
    );

    @Autowired
    public PlanService(PlanRepository planRepository, StructureService structureService) {
        this.planRepository = planRepository;
        this.structureService = structureService;
    }

    public AddPlanResponseDTO createPlan(Long id, AddPlanRequestDTO addPlanRequestDTO, MultipartFile file) throws TraitementException {
        Objects.requireNonNull(addPlanRequestDTO);
        if (id == null) {
            throw new TraitementException(ErrorIdentifier.PLAN_ID_IS_EMPTY);
        }
        var structure = structureService.existStructure(id);
        if (structure.isEmpty()) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_ID_NOT_FOUND);
        }
        if (addPlanRequestDTO.name() == null || addPlanRequestDTO.name().isEmpty()) {
            throw new TraitementException(ErrorIdentifier.PLAN_NAME_IS_EMPTY);
        }
        if (file == null || file.isEmpty()) {
            throw new TraitementException(ErrorIdentifier.PLAN_FILE_IS_EMPTY);
        }
        var mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new TraitementException(ErrorIdentifier.PLAN_FILE_INVALID_FORMAT);
        }
        managedFilesDirectory(workingDir + File.separator + uploadDir);
        managedFilesDirectory(workingDir + File.separator + uploadDir + File.separator + "Ouvrages");
        managedFilesDirectory(workingDir + File.separator + uploadDir + File.separator + "Ouvrages" + File.separator + id);
        managedFilesDirectory(workingDir + File.separator + uploadDir + File.separator + "Ouvrages" + File.separator + id + File.separator + addPlanRequestDTO.section());
        var filePath = workingDir + File.separator + uploadDir + File.separator + "Ouvrages" + File.separator + id + File.separator + addPlanRequestDTO.section() + File.separator + file.getOriginalFilename();
        var dest = new File(filePath);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.severe("IOException when uploading file to server : " + e.getMessage());
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }
        var plan = new Plan(filePath, false, addPlanRequestDTO.name(), addPlanRequestDTO.section(), structure.get());
        Plan savedPlan;
        try {
            savedPlan = planRepository.save(plan);
        } catch (Exception e) {
            log.severe("Exception when uploading plan to bdd : " + e.getMessage());
            var delete = dest.delete();
            if (!delete) {
                log.severe("Exception when removing file to server, please check it");
            }
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }
        return new AddPlanResponseDTO(savedPlan.getId(), new Timestamp(System.currentTimeMillis()).toString());
    }

    private void managedFilesDirectory(String path) throws TraitementException {
        var directory = new File(path);
        var dirCreated = directory.exists();
        if (!dirCreated) {
            log.info(path + " directory not found, creating one ...");
            dirCreated = directory.mkdirs();
        }
        if (!dirCreated) {
            log.warning(path + " directory not created");
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }
    }
}
