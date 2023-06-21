package org.lisasp.starters.views.team;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lisasp.starters.data.Role;
import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.entity.User;
import org.lisasp.starters.data.service.TeamService;
import org.lisasp.starters.security.AuthenticatedUser;
import org.lisasp.starters.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import jakarta.annotation.security.RolesAllowed;

import java.util.*;
import java.util.stream.Stream;

@PageTitle("Team")
@Route(value = "team/:teamID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed({"USER", "ADMIN"})
@Slf4j
public class TeamView extends Div implements BeforeEnterObserver {

    private final String TEAM_ID = "teamID";
    private final String TEAM_EDIT_ROUTE_TEMPLATE = "team/%s/edit";
    private final Binder.Binding<TeamVM, Starter> starter1Binding;
    private final Binder.Binding<TeamVM, Starter> starter2Binding;
    private final Binder.Binding<TeamVM, Starter> starter3Binding;
    private final Binder.Binding<TeamVM, Starter> starter4Binding;
    
    private final Grid<TeamVM> grid = new Grid<>(TeamVM.class, false);

    private TextField discipline;
    private TextField gender;
    private ComboBox<Starter> starter1;
    private ComboBox<Starter> starter2;
    private ComboBox<Starter> starter3;
    private ComboBox<Starter> starter4;
    private TextField organization;
    private TextField startnumber;

    private final Button cancel = new Button("Abbrechen");
    private final Button save = new Button("Speichern");

    private final BeanValidationBinder<TeamVM> binder;

    private TeamVM team;

    private final AuthenticatedUser authenticatedUser;
    private final TeamService teamService;

    @Autowired
    public TeamView(AuthenticatedUser authenticatedUser, TeamService teamService) {
        this.authenticatedUser = authenticatedUser;
        this.teamService = teamService;

        addClassNames("team-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("discipline").setHeader("Disziplin").setAutoWidth(true);
        grid.addColumn("gender").setHeader("Geschlecht").setAutoWidth(true);
        grid.addColumn(t -> starterItemLabelGenerator(t.getStarter1())).setHeader("Starter 1").setAutoWidth(true);
        grid.addColumn(t -> starterItemLabelGenerator(t.getStarter2())).setHeader("Starter 2").setAutoWidth(true);
        grid.addColumn(t -> starterItemLabelGenerator(t.getStarter3())).setHeader("Starter 3").setAutoWidth(true);
        grid.addColumn(t -> starterItemLabelGenerator(t.getStarter4())).setHeader("Starter 4").setAutoWidth(true);
        grid.addColumn(this::roundText).setHeader("Runde").setAutoWidth(true);
        if (isAdmin()) {
            grid.addColumn("organization").setAutoWidth(true);
        }
        grid.setMultiSort(true);
        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("discipline"), SortDirection.ASCENDING), new GridSortOrder<>(grid.getColumnByKey("gender"), SortDirection.ASCENDING)));
        grid.setItems(query -> {
            if (!isAuthenticated()) {
                query.getPage();
                query.getPageSize();
                return Stream.of();
            }
            User user = authenticatedUser.get().get();
            if (user.getRoles().contains(Role.ADMIN)) {
                return teamService.list(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream();
            }
            if (user.getRoles().contains(Role.USER)) {
                return teamService.list(user.getName(), PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream();
            }
            query.getPage();
            query.getPageSize();
            return Stream.of();
        });
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TEAM_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TeamView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(TeamVM.class);

        // Bind fields. This is where you'd define e.g. validation rules
        starter1Binding = binder.forField(starter1).withValidator(starter -> checkGenders(team, starter1.getValue(), starter2.getValue(), starter3.getValue(), starter4.getValue()), "Es müssen zwei weiblich und zwei männliche Schwimmer eingesetzt werden.").bind("starter1");
        starter2Binding = binder.forField(starter2).withValidator(starter -> checkStarter(starter2.getValue(), starter1.getValue()), "Der Schwimmer wird bereits verwendet.").bind("starter2");
        starter3Binding = binder.forField(starter3).withValidator(starter -> checkStarter(starter3.getValue(), starter1.getValue(), starter2.getValue()), "Der Schwimmer wird bereits verwendet.").bind("starter3");
        starter4Binding = binder.forField(starter4).withValidator(starter -> checkStarter(starter4.getValue(), starter1.getValue(), starter2.getValue(), starter3.getValue()), "Der Schwimmer wird bereits verwendet.").bind("starter4");

        binder.bindInstanceFields(this);

        starter1.addValueChangeListener(event -> triggerValidation());
        starter2.addValueChangeListener(event -> triggerValidation());
        starter3.addValueChangeListener(event -> triggerValidation());
        starter4.addValueChangeListener(event -> triggerValidation());

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.team == null) {
                    this.team = new TeamVM();
                }
                binder.writeBean(this.team);

                teamService.update(this.team);
                clearForm();
                refreshGrid();
                Notification.show("Team details stored.");
                UI.getCurrent().navigate(TeamView.class);
            } catch (ValidationException validationException) {
                log.info("Save Team failed: {}", validationException.getMessage());
                Notification.show("An exception happened while trying to store the team details.");
            } catch (Exception ex) {
                log.warn("Save Team failed", ex);
                Notification.show("An exception happened while trying to store the team details.");
            }
        });

        populateForm(null);
    }

    private static final Set<String> DisciplinesWithIntermediateRound = Set.of();

    private String roundText(TeamVM t) {
        if (t == null) {
            return "";
        }
        return switch (t.getRound()) {
            case 0  -> "Vorlauf";
            case 1 -> DisciplinesWithIntermediateRound.contains(t.getDiscipline()) ? "Zwischenlauf" : "Finale";
            default -> "Finale";
        };
    }

    private boolean checkGenders(TeamVM team, Starter... starters) {
        if (team == null) {
            return true;
        }
        if (!"mixed".equalsIgnoreCase(team.getGender())) {
            return true;
        }

        int maleCount = (int) Arrays.stream(starters).filter(s -> s != null && "male".equalsIgnoreCase(s.getGender())).count();
        int femaleCount = (int) Arrays.stream(starters).filter(s -> s != null && "female".equalsIgnoreCase(s.getGender())).count();

        return maleCount <= 2 && femaleCount <= 2;
    }

    private void triggerValidation() {
        starter1Binding.validate();
        starter2Binding.validate();
        starter3Binding.validate();
        starter4Binding.validate();
    }

    private boolean checkStarter(Starter starter, Starter... moreStarters) {
        if (starter == null) {
            return true;
        }
        return Arrays.stream(moreStarters).noneMatch(s -> s != null && s.getStartnumber().equalsIgnoreCase(starter.getStartnumber()));
    }

    private boolean isAdmin() {
        return authenticatedUser.get().map(user -> user.getRoles().contains(Role.ADMIN)).orElse(false);
    }

    private boolean isAuthenticated() {
        return authenticatedUser.get().isPresent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> teamId = event.getRouteParameters().get(TEAM_ID).map(UUID::fromString);
        if (teamId.isPresent()) {
            Optional<TeamVM> teamFromBackend = teamService.get(teamId.get());
            if (teamFromBackend.isPresent()) {
                populateForm(teamFromBackend.get());
            } else {
                Notification.show(String.format("The requested team was not found, ID = %s", teamId.get()), 3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(TeamView.class);
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
        discipline = new TextField("Disziplin");
        gender = new TextField("Geschlecht");
        starter1 = new ComboBox<>("Starter 1");
        starter2 = new ComboBox<>("Starter 2");
        starter3 = new ComboBox<>("Starter 3");
        starter4 = new ComboBox<>("Starter 4");
        organization = new TextField("Organization");
        startnumber = new TextField("Startnumber");

        discipline.setEnabled(false);
        gender.setEnabled(false);
        organization.setEnabled(false);
        startnumber.setEnabled(false);

        starter1.setItemLabelGenerator(this::starterItemLabelGenerator);
        starter2.setItemLabelGenerator(this::starterItemLabelGenerator);
        starter3.setItemLabelGenerator(this::starterItemLabelGenerator);
        starter4.setItemLabelGenerator(this::starterItemLabelGenerator);

        Component[] fields = new Component[]{discipline, gender, starter1, starter2, starter3, starter4, organization, startnumber};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    @NotNull
    private String starterItemLabelGenerator(Starter starter) {
        if (starter == null) {
            return "";
        }
        return String.format("%s %s", starter.getFirstName(), starter.getLastName());
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

    private void populateForm(TeamVM value) {
        this.team = value;

        Starter[] starters = team == null ? new Starter[0] : teamService.getStarters(team.getOrganization(), team.getGender());

        starter1.setItems(starters);
        starter2.setItems(starters);
        starter3.setItems(starters);
        starter4.setItems(starters);

        binder.readBean(this.team);

        boolean hasMoreThan2Competitors = team != null && switch (team.getDiscipline()) {
            case "Board Rescue", "Line Throw" -> false;
            default -> true;
        };

        starter1.setEnabled(value != null);
        starter2.setEnabled(value != null);
        starter3.setEnabled(value != null && hasMoreThan2Competitors);
        starter4.setEnabled(value != null && hasMoreThan2Competitors);

        save.setEnabled(value != null);
        cancel.setEnabled(value != null);

        triggerValidation();
    }
}
