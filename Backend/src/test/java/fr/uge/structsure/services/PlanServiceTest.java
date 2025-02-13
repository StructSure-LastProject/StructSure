package fr.uge.structsure.services;

import fr.uge.structsure.dto.plan.AddPlanRequestDTO;
import fr.uge.structsure.exceptions.TraitementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlanServiceTest {

    @Autowired
    private PlanService planService;

    private static final AddPlanRequestDTO REQUEST_DTO = new AddPlanRequestDTO("NameOfThePlan", "");
    private static final MultipartFile MULTIPART = multipartFile("File.png",MediaType.IMAGE_PNG_VALUE, "a".getBytes());

    @Test
    public void testCreatePlanMissingField() {
        /* Structure ID missing */
        assertThrows(TraitementException.class, () ->
            planService.createPlan(null, REQUEST_DTO, MULTIPART));
        /* Name missing */
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, new AddPlanRequestDTO(null, ""), MULTIPART));
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, new AddPlanRequestDTO("", ""), MULTIPART));
        /* Section null instead of empty */
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, new AddPlanRequestDTO("NameOfThePlan", null), MULTIPART));
        /* Missing file */
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, REQUEST_DTO, null));
        /* Empty file */
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, REQUEST_DTO, multipartFile("File.png", MediaType.IMAGE_PNG_VALUE, new byte[0])));
    }

    @Test
    public void testCreatePlanWrongMime() {
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, REQUEST_DTO, multipartFile("File.png", MediaType.IMAGE_GIF_VALUE, "a".getBytes())));
    }

    @Test
    public void testCreatePlanMaxLength() {
        /* Name less than 32 */
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, new AddPlanRequestDTO("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456", ""), MULTIPART));

        /* Section less than 128 */
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, new AddPlanRequestDTO("NameOfThePlan", "A".repeat(129)), MULTIPART));

        /* Section allowed chars */
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, new AddPlanRequestDTO("NameOfThePlan", "_"), MULTIPART));
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, new AddPlanRequestDTO("NameOfThePlan", "é"), MULTIPART));
        assertThrows(TraitementException.class, () ->
            planService.createPlan(0L, new AddPlanRequestDTO("NameOfThePlan", "."), MULTIPART));
    }

    // File downloading not tested here

    private static MockMultipartFile multipartFile(String name, String mime, byte[] data) {
        return new MockMultipartFile(name, name, mime, data);
    }
}
