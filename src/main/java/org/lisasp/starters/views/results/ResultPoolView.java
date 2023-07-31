package org.lisasp.starters.views.results;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.lisasp.starters.data.model.ExportType;
import org.lisasp.starters.data.model.StarterExport;
import org.lisasp.starters.data.service.DownloadService;
import org.lisasp.starters.data.service.ExportService;
import org.lisasp.starters.views.MainLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@PageTitle("Results Pool")
@Route(value = "results/pool", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ResultPoolView extends Div {

    private final DownloadService downloadService;

    private final Grid<String> grid = new Grid<>();

    public ResultPoolView(DownloadService downloadService) {
        this.downloadService = downloadService;

        addClassName("export-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(discipline -> createCard(discipline));
        add(grid);

        grid.setItems(downloadService.list("pool"));
    }

    private HorizontalLayout createCard(String discipline) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        String id = discipline;

        Anchor anchor = new Anchor(getStreamResource(id), id);
        anchor.getElement().setAttribute("download", true);

        card.add(anchor);
        return card;
    }

    private StreamResource getStreamResource(String filename) {
        return new StreamResource(filename,
                                  () -> new ByteArrayInputStream(downloadService.serve("pool", filename)));
    }
}
