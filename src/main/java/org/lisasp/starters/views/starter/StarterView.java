package org.lisasp.starters.views.starter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.starters.data.Role;
import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.entity.User;
import org.lisasp.starters.data.service.StarterService;
import org.lisasp.starters.security.AuthenticatedUser;
import org.lisasp.starters.views.MainLayout;
import org.springframework.data.domain.PageRequest;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Starter")
@Route(value = "starter/:starterID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed({"USER", "ADMIN"})
@Slf4j
public class StarterView extends Div implements BeforeEnterObserver {

    private final String STARTER_ID = "starterID";
    private final String STARTER_EDIT_ROUTE_TEMPLATE = "starter/%s/edit";

    private Grid<Starter> grid = new Grid<>(Starter.class, false);

    private TextField startnumber;
    private TextField firstName;
    private TextField lastName;
    private IntegerField yearOfBirth;
    private TextField gender;
    private TextField organization;
    private Button cancel = new Button("Abbrechen");
    private Button save = new Button("Speichern");
    private BeanValidationBinder<Starter> binder;
    private Starter starter;

    private final AuthenticatedUser authenticatedUser;
    private final StarterService starterService;

    public StarterView(AuthenticatedUser authenticatedUser, StarterService starterService) {
        this.authenticatedUser = authenticatedUser;
        this.starterService = starterService;

        addClassNames("starter-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("startnumber").setHeader("S#").setAutoWidth(true);
        grid.addColumn("firstName").setHeader("Vorname").setAutoWidth(true);
        grid.addColumn("lastName").setHeader("Nachname").setAutoWidth(true);
        grid.addColumn(Starter::yearOfBirthAsString).setHeader("Jahrgang").setAutoWidth(true);
        grid.addColumn("gender").setHeader("Geschlecht").setAutoWidth(true);
        if (isAdmin()) {
            grid.addColumn("organization").setAutoWidth(true);
        }

        grid.sort(Arrays.asList(new GridSortOrder<>(grid.getColumnByKey("startnumber"), SortDirection.ASCENDING)));
        grid.setItems(query -> {
                          if (!isAuthenticated()) {
                              query.getPage();
                              query.getPageSize();
                              return new ArrayList<Starter>().stream();
                          }
                          User user = authenticatedUser.get().get();
                          if (user.getRoles().contains(Role.ADMIN)) {
                              return starterService.list(
                                              PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                                      .stream();
                          } else if (user.getRoles().contains(Role.USER)) {
                              return starterService.list(user.getName(),
                                                         PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                                      .stream();
                          } else {
                              query.getPage();
                              query.getPageSize();
                              return new ArrayList<Starter>().stream();
                          }
                      }
        );
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().
                addValueChangeListener(event ->
                                       {
                                           if (event.getValue() != null) {
                                               UI.getCurrent().navigate(String.format(STARTER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
                                           } else {
                                               clearForm();
                                               UI.getCurrent().navigate(StarterView.class);
                                           }
                                       });

        // Configure Form
        binder = new BeanValidationBinder<>(Starter.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(lastName).withValidator(name -> name != null && name.trim().length() >= 3, "Der Nachname muss mindestens drei Zeichen lang sein.").bind(
                "lastName");
        binder.forField(firstName).withValidator(name -> name != null && name.trim().length() >= 3, "Der Vorname muss mindestens drei Zeichen lang sein.").bind(
                "firstName");
        binder.forField(yearOfBirth)
                .withValidator(yob -> {
                    if (yob == null) {
                        return true;
                    }
                    return yob <= 2020;
                }, "Der Jahrgang darf nicht über 2020 sein.")
                .withValidator(yob -> {
                    if (yob == null) {
                        return true;
                    }
                    if (yob == 0) {
                        return true;
                    }
                    return yob >= 2000;
                }, "Der Jahrgang darf nicht kleiner als 2000 sein.")
                .bind("yearOfBirth");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e ->
                                {
                                    clearForm();
                                    refreshGrid();
                                });

        save.addClickListener(e ->
                              {
                                  try {
                                      if (this.starter == null) {
                                          this.starter = new Starter();
                                      }
                                      binder.writeBean(this.starter);

                                      starterService.update(this.starter);
                                      clearForm();
                                      refreshGrid();
                                      Notification.show("Starter details stored.");
                                      UI.getCurrent().navigate(StarterView.class);
                                  } catch (ValidationException validationException) {
                                      log.info("Save Starter failed: {}", validationException.getMessage());
                                      Notification.show("An exception happened while trying to store the starter details.");
                                  } catch (Exception ex) {
                                      log.warn("Save Team failed", ex);
                                      Notification.show("An exception happened while trying to store the starter details.");
                                  }
                              });

        populateForm(null);
    }

    private boolean isAdmin() {
        return authenticatedUser.get().map(user -> user.getRoles().contains(Role.ADMIN)).orElse(false);
    }

    private boolean isAuthenticated() {
        return authenticatedUser.get().isPresent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> starterId = event.getRouteParameters().get(STARTER_ID).map(UUID::fromString);
        if (starterId.isPresent()) {
            Optional<Starter> starterFromBackend = starterService.get(starterId.get());
            if (starterFromBackend.isPresent()) {
                populateForm(starterFromBackend.get());
            } else {
                Notification.show(String.format("The requested starter was not found, ID = %s", starterId.get()), 3000,
                                  Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(StarterView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        startnumber = new TextField("S#");
        firstName = new TextField("Vorname");
        lastName = new TextField("Nachname");
        yearOfBirth = new IntegerField("Jahrgang");
        gender = new TextField("Geschlecht");
        organization = new TextField("LV");

        yearOfBirth.setMin(0);
        yearOfBirth.setMax(2020);

        startnumber.setEnabled(false);
        gender.setEnabled(false);
        organization.setEnabled(false);

        Component[] fields = new Component[]{startnumber, gender, firstName, lastName, yearOfBirth, organization};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Starter value) {
        this.starter = value;
        binder.readBean(this.starter);

        save.setEnabled(value != null);
        cancel.setEnabled(value != null);
    }
}
