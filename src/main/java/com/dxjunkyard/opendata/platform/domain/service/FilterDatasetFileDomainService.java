package com.dxjunkyard.opendata.platform.domain.service;

import com.dxjunkyard.opendata.platform.domain.model.opendata.DatasetFile;
import com.dxjunkyard.opendata.platform.domain.model.search.condition.SearchCondition;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilterDatasetFileDomainService {

    private static final int MAX_SIZE = 5;

    @NonNull
    public List<DatasetFile> filter(final List<DatasetFile> datasetFiles, final SearchCondition searchCondition) {
        return datasetFiles.stream()
            // nullを最後尾に持っていく
            .sorted(Comparator
                .comparing(DatasetFile::getLastModifiedTimestamp, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(file -> file.getMatchScore(searchCondition), Comparator.nullsFirst(Comparator.naturalOrder()))
                .reversed() // 降順に変更する
            )
            .limit(MAX_SIZE)
            .collect(Collectors.toList());
    }
}
