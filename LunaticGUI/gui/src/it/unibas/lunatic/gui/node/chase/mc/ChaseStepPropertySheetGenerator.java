package it.unibas.lunatic.gui.node.chase.mc;

import it.unibas.lunatic.LunaticConstants;
import static it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode.*;
import it.unibas.lunatic.gui.node.utils.StringProperty;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import speedy.model.database.AttributeRef;
import java.lang.reflect.InvocationTargetException;
import java.util.MissingResourceException;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "PROP_CellGroup=Cell group",
    "PROP_ChaseMode=Chase mode",
    "PROP_IsInvalid=Invalid",
    "PROP_IsLeaf=Leaf",
    "PROP_NodeType=Node type",
    "PROP_IsDuplicate=Duplicate",
    "PROP_GroundSolution=Ground solution",
    "PROP_EditedByUser=User node",
    "PROP_IntermediateNode=Intermediate result",
    "PROP_IncompleteSolution=Solution",
    "PROP_UnsatisfiedDependencies=Unsatisfied dependencies",
    "PROP_TreePath=Chase tree path",
    "PSET_AttributeSet=Affected attributes",
    LunaticConstants.CHASE_BACKWARD + "=backward",
    LunaticConstants.CHASE_FORWARD + "=forward",
    LunaticConstants.CHASE_USER + "=user",
    LunaticConstants.CHASE_STEP_TGD + "=tgd",})
public class ChaseStepPropertySheetGenerator {

    public void populateSheet(Sheet sheet, ChaseStepNode chaseStep) {
        Sheet.Set resultSet = createSheetSet(chaseStep);
        sheet.put(resultSet);
    }

    private Sheet.Set createSheetSet(final ChaseStepNode chaseStepNode) {
        final DeltaChaseStep step = chaseStepNode.getChaseStep();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new StringProperty(Bundle.PROP_ChaseMode()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                try {
                    return NbBundle.getMessage(getClass(), step.getChaseMode());
                } catch (MissingResourceException e) {
                    return step.getChaseMode();
                }
            }
        });
        set.put(new PropertySupport.ReadOnly<Boolean>(PROP_INTERMEDIATE_STEP, Boolean.class, Bundle.PROP_IntermediateNode(), Bundle.PROP_IntermediateNode()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return !(step.isSolution() || step.isEditedByUser() || step.isGround());
            }
        });
        set.put(new PropertySupport.ReadOnly<Boolean>(PROP_SOLUTION_STEP, Boolean.class, Bundle.PROP_IncompleteSolution(), Bundle.PROP_IncompleteSolution()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return step.isSolution();
            }
        });
        set.put(new PropertySupport.ReadOnly<Boolean>(PROP_GROUND_SOLUTION_STEP, Boolean.class, Bundle.PROP_GroundSolution(), Bundle.PROP_GroundSolution()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return step.isGround();
            }
        });
        set.put(new PropertySupport.ReadOnly<Boolean>(PROP_EDITED_BY_USER, Boolean.class, Bundle.PROP_EditedByUser(), Bundle.PROP_EditedByUser()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return step.isEditedByUser();
            }
        });
        set.put(new PropertySupport.ReadOnly<Boolean>(PROP_LEAF_STEP, Boolean.class, Bundle.PROP_IsLeaf(), Bundle.PROP_IsLeaf()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return step.isLeaf();
            }
        });
        set.put(new PropertySupport.ReadOnly<Boolean>(PROP_DUPLICATE_STEP, Boolean.class, Bundle.PROP_IsDuplicate(), Bundle.PROP_IsDuplicate()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return step.isDuplicate();
            }
        });
        set.put(new PropertySupport.ReadWrite<Boolean>(PROP_INVALID_STEP, Boolean.class, Bundle.PROP_IsInvalid(), Bundle.PROP_IsInvalid()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return step.isInvalid();
            }

            @Override
            public void setValue(Boolean invalid) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (invalid) {
                    chaseStepNode.setInvalid();
                } else {
                    chaseStepNode.setValid();
                }
            }
        });
        set.put(new StringProperty(Bundle.PROP_TreePath()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return chaseStepNode.getName();
            }

            @Override
            public boolean canWrite() {
                return true;
            }
        });
//        set.put(new PropertySupport.ReadOnly<String[]>("affectedAttributes", String[].class, Bundle.PSET_AttributeSet(), Bundle.PSET_AttributeSet()) {
//            @Override
//            public List getValue() throws IllegalAccessException, InvocationTargetException {
//                List<AttributeRef> affAttributeRefs = step.getAffectedAttributes();
////                List<String> affectedAttributes = new ArrayList<String>();
//                return affAttributeRefs;
//            }
//        });
        return set;
    }

    private Sheet.Set createAffectedAttributeSet(ChaseStepNode chaseStepNode) {
        final DeltaChaseStep chaseStep = chaseStepNode.getChaseStep();
        Sheet.Set set = new Sheet.Set();
        set.setDisplayName(Bundle.PSET_AttributeSet());
        for (final AttributeRef ar : chaseStep.getAffectedAttributes()) {
            set.put(new StringProperty(ar.getTableAlias().toString()) {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return ar.getName();
                }
            });
        }
        return set;
    }
}
