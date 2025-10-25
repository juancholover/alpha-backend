package upeu.edu.pe.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadingConfigDto {
    private String name;
    private String code;
    private String logo;
    private String videoUrl;
    private String welcomeMessage;
    private Map<String, Object> colors;
    private List<InfoLinkDto> headerLinks;
    private List<InfoCardDto> infoCards;
    private UniversityRankingDto ranking;
    private Map<String, Object> graphics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfoLinkDto {
        private String title;
        private String url;
        private String icon;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfoCardDto {
        private String title;
        private String description;
        private String icon;
        private String url;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniversityRankingDto {
        private String position;
        private String category;
        private String year;
        private String description;
        private String logoUrl;
    }
}
