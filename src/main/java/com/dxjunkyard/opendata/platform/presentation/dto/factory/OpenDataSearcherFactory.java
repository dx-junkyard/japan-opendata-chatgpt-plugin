package com.dxjunkyard.opendata.platform.presentation.dto.factory;

import com.dxjunkyard.opendata.platform.domain.model.opendata.OpenData;
import com.dxjunkyard.opendata.platform.domain.model.search.condition.AreaSearchCondition;
import com.dxjunkyard.opendata.platform.domain.model.search.condition.KeywordSearchCondition;
import com.dxjunkyard.opendata.platform.domain.model.search.condition.SearchCondition;
import com.dxjunkyard.opendata.platform.domain.service.FilterDatasetFileDomainService;
import com.dxjunkyard.opendata.platform.domain.service.RomajiConverterDomainService;
import com.dxjunkyard.opendata.platform.domain.service.UrlBuilderDomainService;
import com.dxjunkyard.opendata.platform.presentation.dto.request.OpenDataSearcherRequest;
import com.dxjunkyard.opendata.platform.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenDataSearcherFactory {

    private final UrlBuilderDomainService urlBuilderDomainService;

    private final FilterDatasetFileDomainService filterDatasetFileDomainService;

    private final RomajiConverterDomainService romajiConverterDomainService;

    @NonNull
    public SearchCondition build(final OpenDataSearcherRequest request) {

        final KeywordSearchCondition keywordSearchCondition = KeywordSearchCondition.of(request.keyword());
        final AreaSearchCondition areaSearchCondition = AreaSearchCondition.of(request.area());

        return SearchCondition.builder()
            .page(request.page())
            .areaSearchCondition(areaSearchCondition)
            .keywordSearchCondition(keywordSearchCondition)
            .formatSet(request.formatSet())
            .language(request.language())
            .build();
    }

    @NonNull
    public OpenDataSearcherResponse build(final OpenData openData, final SearchCondition searchCondition) {
        final List<DatasetResponse> datasetResponse = openData.getDataset().stream()
            .map(dataset -> {
                final var datasetFileResponse = filterDatasetFileDomainService.filter(dataset.getFiles(), searchCondition)
                    .stream()
                    .map(file -> DatasetFileResponse.from(file, searchCondition.isJapanese() ? file.getTitle() : romajiConverterDomainService.convert(file.getTitle())))
                    .toList();

                final var titleEn = searchCondition.isJapanese() ? null : romajiConverterDomainService.convert(dataset.getTitle());

                return DatasetResponse.from(dataset, datasetFileResponse, titleEn);
            }).toList();

        return OpenDataSearcherResponse.builder()
            .searchResultInfo(new SearchResultInfoResponse(openData.getTotal()))
            .dataset(datasetResponse)
            .searchCondition(SearchConditionResponse.from(searchCondition))
            .showMoreUrl(urlBuilderDomainService.build(searchCondition))
            .build();
    }
}
