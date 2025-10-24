package upeu.edu.pe.catalog.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.catalog.application.dto.CustomFieldDto;
import upeu.edu.pe.catalog.domain.services.CustomFieldService;
import upeu.edu.pe.catalog.shared.response.ApiResponse;

import java.util.List;
import java.util.Map;

@Path("/api/v1/institutions/{institutionCode}/custom-fields")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomFieldResource {
    
    private final CustomFieldService customFieldService;
    
    @Inject
    public CustomFieldResource(CustomFieldService customFieldService) {
        this.customFieldService = customFieldService;
    }
    
    @GET
    public Response getAllCustomFields(@PathParam("institutionCode") String institutionCode) {
        List<CustomFieldDto> fields = customFieldService.getAllCustomFieldsByInstitution(institutionCode);
        return Response.ok(
                ApiResponse.success(fields, "Campos personalizados obtenidos correctamente")
        ).build();
    }
    
    @GET
    @Path("/entity-type/{entityType}")
    public Response getCustomFieldsByEntityType(
            @PathParam("institutionCode") String institutionCode,
            @PathParam("entityType") String entityType) {
        List<CustomFieldDto> fields = customFieldService.getCustomFieldsByEntityType(institutionCode, entityType);
        return Response.ok(
                ApiResponse.success(fields, "Campos personalizados obtenidos correctamente")
        ).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getCustomFieldById(@PathParam("id") Long id) {
        CustomFieldDto field = customFieldService.getCustomFieldById(id);
        return Response.ok(
                ApiResponse.success(field, "Campo personalizado obtenido correctamente")
        ).build();
    }
    
    @POST
    public Response createCustomField(
            @PathParam("institutionCode") String institutionCode,
            @Valid CustomFieldDto customFieldDto) {
        CustomFieldDto createdField = customFieldService.createCustomField(institutionCode, customFieldDto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(createdField, "Campo personalizado creado correctamente"))
                .build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCustomField(
            @PathParam("id") Long id,
            @Valid CustomFieldDto customFieldDto) {
        CustomFieldDto updatedField = customFieldService.updateCustomField(id, customFieldDto);
        return Response.ok(
                ApiResponse.success(updatedField, "Campo personalizado actualizado correctamente")
        ).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteCustomField(@PathParam("id") Long id) {
        customFieldService.deleteCustomField(id);
        return Response.ok(
                ApiResponse.success(null, "Campo personalizado eliminado correctamente")
        ).build();
    }
    
    @GET
    @Path("/values/{entityType}/{entityId}")
    public Response getCustomFieldValues(
            @PathParam("institutionCode") String institutionCode,
            @PathParam("entityType") String entityType,
            @PathParam("entityId") Long entityId) {
        Map<String, Object> values = customFieldService.getCustomFieldValuesForEntity(
                institutionCode, entityType, entityId);
        return Response.ok(
                ApiResponse.success(values, "Valores de campos personalizados obtenidos correctamente")
        ).build();
    }
    
    @POST
    @Path("/values/{entityType}/{entityId}")
    public Response saveCustomFieldValues(
            @PathParam("institutionCode") String institutionCode,
            @PathParam("entityType") String entityType,
            @PathParam("entityId") Long entityId,
            Map<String, Object> values) {
        customFieldService.saveCustomFieldValuesForEntity(institutionCode, entityType, entityId, values);
        return Response.ok(
                ApiResponse.success(null, "Valores de campos personalizados guardados correctamente")
        ).build();
    }
}