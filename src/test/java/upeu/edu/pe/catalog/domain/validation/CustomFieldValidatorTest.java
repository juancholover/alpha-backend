package upeu.edu.pe.catalog.domain.validation;

import org.junit.jupiter.api.Test;
import upeu.edu.pe.catalog.application.dto.CustomFieldDto;
import upeu.edu.pe.catalog.domain.entities.CustomField;
import upeu.edu.pe.catalog.shared.exceptions.BusinessException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomFieldValidatorTest {

    @Test
    public void normalizeFieldName_removesSpacesAndAccents() {
        String input = " Cédula de Estudiante ";
        String normalized = CustomFieldValidator.normalizeFieldName(input);
        assertEquals("cedula_de_estudiante", normalized);
    }

    @Test
    public void validateFieldDefinition_dropdownRequiresChoices() {
        CustomFieldDto dto = CustomFieldDto.builder()
                .fieldName("tipo_sangre")
                .fieldType("dropdown")
                .entityType("student")
                .isRequired(false)
                .build();

        assertThrows(BusinessException.class, () -> CustomFieldValidator.validateFieldDefinition(dto));
    }

    @Test
    public void validateByRules_patternMismatch() {
        CustomField field = new CustomField();
        field.setFieldType("text");
        field.setOptions(null);
        field.setValidationRules("{\"pattern\":\"^[0-9]{3}-[0-9]{7}-[0-9]$\"}");

        boolean ok = CustomFieldValidator.validateByRules(field, "001-1234567-8");
        assertTrue(ok);

        boolean nok = CustomFieldValidator.validateByRules(field, "abc-defg");
        assertFalse(nok);
    }

    @Test
    public void validateByRules_numberMinMax() {
        CustomField field = new CustomField();
        field.setFieldType("number");
        field.setOptions(null);
        field.setValidationRules("{\"min\":1,\"max\":10}");

        assertTrue(CustomFieldValidator.validateByRules(field, 5));
        assertFalse(CustomFieldValidator.validateByRules(field, 0));
        assertFalse(CustomFieldValidator.validateByRules(field, 11));
    }
}
