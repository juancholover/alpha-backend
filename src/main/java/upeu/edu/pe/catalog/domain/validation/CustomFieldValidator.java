package upeu.edu.pe.catalog.domain.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import upeu.edu.pe.catalog.application.dto.CustomFieldDto;
import upeu.edu.pe.catalog.domain.entities.CustomField;
import upeu.edu.pe.catalog.shared.exceptions.BusinessException;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class CustomFieldValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CustomFieldValidator() {}

    public static String normalizeFieldName(String name) {
        if (name == null) return null;
        String normalized = name.trim().toLowerCase();
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        normalized = normalized.replaceAll("\\s+", "_");
        normalized = normalized.replaceAll("[^a-z0-9_]", "");
        return normalized;
    }

    public static void validateFieldDefinition(CustomFieldDto dto) {
        String fieldName = dto.getFieldName();
        if (fieldName == null || !fieldName.matches("^[a-z0-9_]+$")) {
            throw new BusinessException("El nombre del campo contiene caracteres inválidos: " + fieldName);
        }

        if (dto.getDisplayOrder() != null && dto.getDisplayOrder() < 0) {
            throw new BusinessException("displayOrder no puede ser negativo");
        }

        if ("dropdown".equals(dto.getFieldType())) {
            Map<String, Object> options = dto.getOptions();
            if (options == null || !(options.get("choices") instanceof List) || ((List<?>) options.get("choices")).isEmpty()) {
                throw new BusinessException("Dropdown debe definir 'choices' en options");
            }
        }

        Map<String, Object> validation = dto.getValidationRules();
        Map<String, Object> options = dto.getOptions();
        Object patternObj = (validation != null ? validation.get("pattern") : null);
        if (patternObj == null && options != null) patternObj = options.get("pattern");
        if (patternObj != null) {
            try {
                Pattern.compile(patternObj.toString());
            } catch (Exception e) {
                throw new BusinessException("Pattern inválido en options/validationRules: " + patternObj);
            }
        }
    }

    public static boolean validateByRules(CustomField field, Object value) {
        if (value == null) return true;
        Map<String, Object> options = new HashMap<>();
        Map<String, Object> rules = new HashMap<>();
        try {
            if (field.getOptions() != null) {
                options = objectMapper.readValue(field.getOptions(), new TypeReference<Map<String, Object>>() {});
            }
            if (field.getValidationRules() != null) {
                rules = objectMapper.readValue(field.getValidationRules(), new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            // ignore parsing errors here; other validations will catch malformed options
        }

        Object patternObj = rules.getOrDefault("pattern", options.get("pattern"));
        if (patternObj != null && value instanceof String) {
            try {
                Pattern p = Pattern.compile(patternObj.toString());
                if (!p.matcher((String) value).matches()) return false;
            } catch (Exception e) {
                return false;
            }
        }

        if (("number".equals(field.getFieldType()) || "decimal".equals(field.getFieldType()))) {
            BigDecimal val;
            try {
                if (value instanceof Number) val = new BigDecimal(((Number) value).toString());
                else val = new BigDecimal(value.toString());
            } catch (Exception e) {
                return false;
            }
            Object min = rules.get("min");
            Object max = rules.get("max");
            try {
                if (min != null) {
                    BigDecimal minVal = new BigDecimal(min.toString());
                    if (val.compareTo(minVal) < 0) return false;
                }
                if (max != null) {
                    BigDecimal maxVal = new BigDecimal(max.toString());
                    if (val.compareTo(maxVal) > 0) return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        if ("dropdown".equals(field.getFieldType())) {
            Object choicesObj = options.get("choices");
            if (choicesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> choices = (List<Object>) choicesObj;
                return choices.contains(value);
            }
        }

        return true;
    }

    public static boolean isNumeric(String s) {
        try {
            new BigDecimal(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
