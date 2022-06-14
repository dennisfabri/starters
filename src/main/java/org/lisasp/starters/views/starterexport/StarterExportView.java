package org.lisasp.starters.views.starterexport;

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
import org.lisasp.starters.data.model.ExportType;
import org.lisasp.starters.data.entity.Team;
import org.lisasp.starters.data.model.StarterExport;
import org.lisasp.starters.data.service.ExportService;
import org.lisasp.starters.views.MainLayout;
import org.lisasp.starters.data.model.Discipline;

import javax.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@PageTitle("Starter Export")
@Route(value = "starterexport", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StarterExportView extends Div {

    private final ExportService exportService;

    private final Grid<ExportType> grid = new Grid<>();

    public StarterExportView(ExportService exportService) {
        this.exportService = exportService;

        addClassName("export-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(discipline -> createCard(discipline));
        add(grid);

        grid.setItems(Arrays.asList(ExportType.values()));
    }

    private HorizontalLayout createCard(ExportType discipline) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        String id = discipline.name();

        Anchor anchor = new Anchor(getStreamResource(id + ".csv", discipline), id);
        anchor.getElement().setAttribute("download", true);

        card.add(anchor);
        return card;
    }

    private StreamResource getStreamResource(String filename, ExportType discipline) {
        return new StreamResource(filename,
                                  () -> new ByteArrayInputStream(generateCSV(discipline)));
    }

    private byte[] generateCSV(ExportType discipline) {
        List<StarterExport> teams = exportService.getStarters(discipline);

        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(bos, StandardCharsets.ISO_8859_1);
        ) {
            StatefulBeanToCsv<StarterExport> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withSeparator(';')
                    .build();

            beanToCsv.write(teams);

            writer.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        } catch (CsvDataTypeMismatchException e) {
            throw new RuntimeException(e);
        }
    }
}
