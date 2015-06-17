package org.openiam.bpm.activiti.delegate.entitlements;


import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.openiam.bpm.util.ActivitiConstants;

public class ReplaceSuperiorNotifierDelegate extends AbstractEntitlementsDelegate {

    private FixedValue notifySuperior;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        if (execution.hasVariable("CurrentSuperiorID") && execution.hasVariable("NewSuperiorID")) {
            Object superior = null;
            if ("current".equals(notifySuperior.getExpressionText())) {
                superior = execution.getVariable("CurrentSuperiorID");
                Object newSuperior = execution.getVariable("NewSuperiorID");
                execution.setVariable(ActivitiConstants.ASSOCIATION_ID.getName(), newSuperior);
            } else if ("new".equals(notifySuperior.getExpressionText())) {
                superior = execution.getVariable("NewSuperiorID");
                Object currSuperior = execution.getVariable("CurrentSuperiorID");
                execution.setVariable(ActivitiConstants.ASSOCIATION_ID.getName(), currSuperior);
            }
            execution.setVariable(ActivitiConstants.CARDINALITY_OBJECT.getName(), superior);
            super.execute(execution);
        } else {
            throw new IllegalArgumentException("CurrentSuperiorID and NewSuperiorID must be defined");
        }
    }
}
