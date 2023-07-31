package org.lisasp.starters.views.imports;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.starters.data.generator.DataImporter;
import org.lisasp.starters.data.generator.DataUpdater;
import org.lisasp.starters.views.MainLayout;

@Slf4j
@PageTitle("Import")
@Route(value = "import", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ImportView extends Div {

    private final DataImporter importer;
    private final DataUpdater updater;

    private final Button importFile = new Button("Importieren");
    private final Button update = new Button("Aktualisieren");

    public ImportView(DataImporter importer, DataUpdater updater) {
        this.importer = importer;
        this.updater = updater;

        addClassName("import-view");
        setSizeFull();

        importFile.addClickListener(e -> {
            try {
                importer.run();
                Notification.show("Import finished");
                // UI.getCurrent().navigate(TeamView.class);
            } catch (Exception ex) {
                log.warn("Save Team failed", ex);
                Notification.show("An exception happened while trying to store the team details.");
            }
        });
        update.addClickListener(e -> {
            try {
                updater.run();
                Notification.show("Update finished");
                // UI.getCurrent().navigate(TeamView.class);
            } catch (Exception ex) {
                log.warn("Save Team failed", ex);
                Notification.show("An exception happened while trying to store the team details.");
            }
        });

        add(importFile);
        add(update);
    }
}
