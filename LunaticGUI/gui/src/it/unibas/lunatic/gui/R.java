package it.unibas.lunatic.gui;

public class R {

    public static class Bean {

        public static final String LOADED_SCENARIO = "loadedScenario";
    }

    public static class BeanProperty {

        public static final String CHASE_RESULT = "chaseResult";
        public static final String CHASE_STATE = "chaseState";
        public static final String PARTIAL_ORDER_SCRIPT = "partialOrderScript";
        public static final String SELECTED_CELL_GROUP_NODE = "cellGroupInfo";
        public static final String STEP_CELL_GROUPS = "stepCellGroups";
    }

    public static final class ActionId {

        public static final String SAVE_CHASE_STEP = "it.unibas.lunatic.gui.action.chase.ActionSaveCells";
        public static final String CLOSE_SCENARIO_FROM_NODE = "it.unibas.lunatic.gui.action.CloseScenarioFromNode";
        public static final String RUN_CHASE = "it.unibas.lunatic.gui.action.Chase";
        public static final String RUN_CHECK_CONFLICTS = "it.unibas.lunatic.gui.action.CheckConflicts";
        public static final String CONTINUE_CHASE = "it.unibas.lunatic.gui.action.RerunChase";
        public static final String LOAD_SCENARIO = "it.unibas.lunatic.gui.action.LoadScenario";
        public static final String SHOW_TABLE = "it.unibas.lunatic.gui.action.ShowTable";
        public static final String SHOW_OCCURRENCE_TUPLE = "it.unibas.lunatic.gui.action.ShowOccurrenceTuple";
        public static final String SHOW_PROVENANCE_TUPLE = "it.unibas.lunatic.gui.action.ShowProvenanceTuple";
        public static final String SHOW_USER_CELL_TUPLE = "it.unibas.lunatic.gui.action.ShowUserCellTuple";
        public static final String SHOW_DEPS = "it.unibas.lunatic.gui.action.ShowDependencies";
        public static final String SHOW_STEP_DEPS = "it.unibas.lunatic.gui.action.ShowStepDependencies";
        public static final String SHOW_DEP_VISUAL = "it.unibas.lunatic.gui.action.ShowDepVisual";
        public static final String SHOW_CELL_GROUP_EDITOR = "it.unibas.lunatic.gui.action.ShowCellGroup";
        public static final String SHOW_CELL_GROUP_DETAILS = "it.unibas.lunatic.gui.action.ShowCellGroupDetails";
        public static final String SHOW_STEP_CELL_GROUPS_ALL = "it.unibas.lunatic.gui.action.ShowAllCellGroupsForStep";
        public static final String EDIT_STEP_CELL_GROUPS = "it.unibas.lunatic.gui.action.EditCellGroupsForStep";
        public static final String SHOW_ORDERING_ATTRIBUTES = "it.unibas.lunatic.gui.action.ShowOrderingAttributes";
        public static final String CREATE_USER_NODE = "it.unibas.lunatic.gui.action.CreateUserNode";
        public static final String INVALID_CHASE_STEP = "it.unibas.lunatic.gui.action.InvalidStep";
        public static final String ENABLE_CHASE_STEP = "it.unibas.lunatic.gui.action.EnableStep";
        public static final String DELETE_USER_NODE = "it.unibas.lunatic.gui.action.DeleteUserNode";
        public static final String EDIT_CELL_GROUP_VALUE = "it.unibas.lunatic.gui.action.EditCellGroupValue";
    }

    public static final class Window {

        public static final String ORDERING_ATTRIBUTES_TABLE = "OrderingAttributesTopComponent";
        public static final String DE_CHASE_RESULT = "DeChaseResultTopComponent";
        public static final String CHASE_RESULT = "ChaseResultTopComponent";
        public static final String MC_CHASE_RESULT = "McChaseResultTopComponent";
        public static final String MC_CHASE_RESULT_RANKED_SOLUTIONS = "MCChaseResultRankedSolutionsTopComponent";
        public static final String SCENARIO = "ScenarioTopComponent";
        public static final String TABLE = "DbTableTopComponent";
        public static final String PAGED_TABLE = "PagedDbTableTopComponent";
        public static final String DEPENDENCIES = "ScenarioDepsTopComponent";
        public static final String UNSATISFIED_DEPENDENCIES = "StepDepsTopComponent";
        public static final String CELL_GROUP_DETAILS = "CellGroupTopComponent";
        public static final String CELL_GROUP_OCCURRENCES = "CellGroupOccurrences";
        public static final String CELL_GROUP_PROVENANCES = "CellGroupProvenances";
        public static final String CELL_GROUP_USER_CELLS = "CellGroupUserCells";
        public static final String CELL_GROUP_ADDITIONAL_CELLS = "CellGroupAdditionalCells";
        public static final String CELL_GROUP_EXPLORER = "CellGroupExplorerTopComponent";
        public static final String VISUAL_DEPENDENCIES = "VisualDepsTopComponent";
    }
}
