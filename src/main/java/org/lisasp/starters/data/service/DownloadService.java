package org.lisasp.starters.data.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.entity.Team;
import org.lisasp.starters.data.model.Discipline;
import org.lisasp.starters.data.model.ExportType;
import org.lisasp.starters.data.model.StarterExport;
import org.lisasp.starters.views.team.TeamVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DownloadService {

    public String[] list(String directory) {
        return new String[]{"a.pdf", "b.pdf"};
    }

    public byte[] serve(String directory, String filename) {
        return new byte[0];
    }
}
