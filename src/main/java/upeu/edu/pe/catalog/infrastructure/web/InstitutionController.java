package upeu.edu.pe.catalog.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.catalog.application.dto.*;
import upeu.edu.pe.catalog.domain.services.InstitutionService;
import upeu.edu.pe.catalog.shared.response.ApiResponse;

import java.util.List;

@Path("/api/institutions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InstitutionController {

    @Inject
    InstitutionService institutionService;

    @GET
    public Response getAllInstitutions() {
        List<InstitutionResponseDto> institutions = institutionService.getAllInstitutions();
        ApiResponse<List<InstitutionResponseDto>> response = ApiResponse.success(institutions, "Instituciones obtenidas correctamente");
        return Response.ok(response).build();
    }

    @GET
    @Path("/{code}")
    public Response getInstitution(@PathParam("code") String code) {
        InstitutionResponseDto institution = institutionService.getInstitution(code);
        ApiResponse<InstitutionResponseDto> response = ApiResponse.success(institution, "Institución obtenida correctamente");
        return Response.ok(response).build();
    }

    @POST
    public Response createInstitution(@Valid InstitutionRequestDto requestDto) {
        InstitutionResponseDto institution = institutionService.createInstitution(requestDto);
        ApiResponse<InstitutionResponseDto> response = ApiResponse.success(institution, "Institución creada correctamente");
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{code}")
    public Response updateInstitution(@PathParam("code") String code, @Valid InstitutionRequestDto requestDto) {
        InstitutionResponseDto institution = institutionService.updateInstitution(code, requestDto);
        ApiResponse<InstitutionResponseDto> response = ApiResponse.success(institution, "Institución actualizada correctamente");
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{code}")
    public Response deleteInstitution(@PathParam("code") String code) {
        institutionService.deleteInstitution(code);
        ApiResponse<Void> response = ApiResponse.success(null, "Institución eliminada correctamente");
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{code}/activate")
    public Response activateInstitution(@PathParam("code") String code) {
        institutionService.activateInstitution(code);
        ApiResponse<Void> response = ApiResponse.success(null, "Institución activada correctamente");
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{code}/deactivate")
    public Response deactivateInstitution(@PathParam("code") String code) {
        institutionService.deactivateInstitution(code);
        ApiResponse<Void> response = ApiResponse.success(null, "Institución desactivada correctamente");
        return Response.ok(response).build();
    }

    @GET
    @Path("/config/{code}")
    public Response getInstitutionConfig(@PathParam("code") String code) {
        InstitutionConfigDto config = institutionService.getInstitutionConfig(code);
        ApiResponse<InstitutionConfigDto> response = ApiResponse.success(config, "Configuración obtenida correctamente");
        return Response.ok(response).build();
    }

    @GET
    @Path("/login-config")
    public Response getLoginConfig(@QueryParam("code") @DefaultValue("UPEU") String code) {
        LoginConfigDto config = institutionService.getLoginConfig(code);
        ApiResponse<LoginConfigDto> response = ApiResponse.success(config, "Configuración de login obtenida correctamente");
        return Response.ok(response).build();
    }

    // Endpoints para gestionar configuraciones
    @GET
    @Path("/{code}/settings")
    public Response getInstitutionSettings(@PathParam("code") String code) {
        List<InstitutionSettingDto> settings = institutionService.getInstitutionSettings(code);
        ApiResponse<List<InstitutionSettingDto>> response = ApiResponse.success(settings, "Configuraciones obtenidas correctamente");
        return Response.ok(response).build();
    }

    @GET
    @Path("/{code}/settings/{module}/{key}")
    public Response getInstitutionSetting(@PathParam("code") String code,
                                        @PathParam("module") String module,
                                        @PathParam("key") String key) {
        InstitutionSettingDto setting = institutionService.getInstitutionSetting(code, module, key);
        ApiResponse<InstitutionSettingDto> response = ApiResponse.success(setting, "Configuración obtenida correctamente");
        return Response.ok(response).build();
    }

    @POST
    @Path("/{code}/settings")
    public Response createInstitutionSetting(@PathParam("code") String code, @Valid InstitutionSettingDto settingDto) {
        InstitutionSettingDto setting = institutionService.createInstitutionSetting(code, settingDto);
        ApiResponse<InstitutionSettingDto> response = ApiResponse.success(setting, "Configuración creada correctamente");
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/settings/{id}")
    public Response updateInstitutionSetting(@PathParam("id") Long id, @Valid InstitutionSettingDto settingDto) {
        InstitutionSettingDto setting = institutionService.updateInstitutionSetting(id, settingDto);
        ApiResponse<InstitutionSettingDto> response = ApiResponse.success(setting, "Configuración actualizada correctamente");
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/settings/{id}")
    public Response deleteInstitutionSetting(@PathParam("id") Long id) {
        institutionService.deleteInstitutionSetting(id);
        ApiResponse<Void> response = ApiResponse.success(null, "Configuración eliminada correctamente");
        return Response.ok(response).build();
    }
}