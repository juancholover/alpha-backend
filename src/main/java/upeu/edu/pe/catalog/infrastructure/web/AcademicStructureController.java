package upeu.edu.pe.catalog.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.catalog.application.dto.AcademicStructureRequestDto;
import upeu.edu.pe.catalog.application.dto.AcademicStructureResponseDto;
import upeu.edu.pe.catalog.application.services.AcademicStructureService;
import upeu.edu.pe.catalog.shared.response.ApiResponse;

import java.util.List;

@Path("/api/institutions/{institutionCode}/academic-structures")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AcademicStructureController {

    @Inject
    AcademicStructureService academicStructureService;

    @GET
    public Response getAllStructures(@PathParam("institutionCode") String institutionCode) {
        List<AcademicStructureResponseDto> structures =
                academicStructureService.getAllStructuresByInstitution(institutionCode);

        ApiResponse<List<AcademicStructureResponseDto>> response =
                ApiResponse.success(structures, "Estructuras académicas obtenidas correctamente");
        return Response.ok(response).build();
    }

    @GET
    @Path("/active")
    public Response getActiveStructure(@PathParam("institutionCode") String institutionCode) {
        AcademicStructureResponseDto structure = academicStructureService.getActiveStructure(institutionCode);

        ApiResponse<AcademicStructureResponseDto> response =
                ApiResponse.success(structure, "Estructura académica activa obtenida correctamente");
        return Response.ok(response).build();
    }

    @POST
    public Response createStructure(@PathParam("institutionCode") String institutionCode,
                                   @Valid AcademicStructureRequestDto requestDto) {
        AcademicStructureResponseDto structure =
                academicStructureService.createStructure(institutionCode, requestDto);

        ApiResponse<AcademicStructureResponseDto> response =
                ApiResponse.success(structure, "Estructura académica creada correctamente");
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateStructure(@PathParam("id") Long id, @Valid AcademicStructureRequestDto requestDto) {
        AcademicStructureResponseDto structure = academicStructureService.updateStructure(id, requestDto);

        ApiResponse<AcademicStructureResponseDto> response =
                ApiResponse.success(structure, "Estructura académica actualizada correctamente");
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{id}/activate")
    public Response activateStructure(@PathParam("id") Long id) {
        AcademicStructureResponseDto structure = academicStructureService.activateStructure(id);

        ApiResponse<AcademicStructureResponseDto> response =
                ApiResponse.success(structure, "Estructura académica activada correctamente");
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteStructure(@PathParam("id") Long id) {
        academicStructureService.deleteStructure(id);

        ApiResponse<Void> response = ApiResponse.success(null, "Estructura académica eliminada correctamente");
        return Response.ok(response).build();
    }
}