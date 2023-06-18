package org.lisasp.starters.views.export;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.*;
import java.util.List;
import jakarta.annotation.security.RolesAllowed;

import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.lisasp.starters.data.entity.Team;
import org.lisasp.starters.data.model.Discipline;
import org.lisasp.starters.data.service.ExportService;
import org.lisasp.starters.views.MainLayout;
import org.springframework.data.domain.PageRequest;

@PageTitle("Export")
@Route(value = "export/team", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TeamExportView extends Div {

    private final ExportService exportService;

    private final Grid<Discipline> grid = new Grid<>();

    public TeamExportView(ExportService exportService) {
        this.exportService = exportService;

        addClassName("export-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(discipline -> createCard(discipline));
        add(grid);

        grid.setItems(query ->
                           exportService.listDisciplines(
                                          PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                                  .stream()
        );
    }

    private HorizontalLayout createCard(Discipline discipline) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        String id = String.format("%s-%s.csv", discipline.getName().replace(' ', '-'), discipline.getGender());

        Anchor anchor = new Anchor(getStreamResource(id, discipline), String.format("%s %s", discipline.getName(), discipline.getGender()));
        anchor.getElement().setAttribute("download",true);

        card.add(anchor);
        return card;
    }

    private StreamResource getStreamResource(String filename, Discipline discipline) {
        return new StreamResource(filename,
                                  () -> new ByteArrayInputStream(generateCSV(discipline)));
    }

    private byte[] generateCSV(Discipline discipline) {
        List<Team> teams= exportService.listTeamsForDiscipline(discipline);

        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(bos);
        ) {
            StatefulBeanToCsv<Team> beanToCsv = new StatefulBeanToCsvBuilder(writer)
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
