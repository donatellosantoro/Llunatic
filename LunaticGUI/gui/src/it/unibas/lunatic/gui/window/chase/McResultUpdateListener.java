/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.chase;

import it.unibas.lunatic.AbstractListener;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.McChaseResult;

/**
 *
 * @author Antonio Galotta
 */
public class McResultUpdateListener extends AbstractListener<IChaseResult> {

    private McResultView tc;

    @Override
    public void onChange(IModel ls, IChaseResult cs) {
        if (ls != null && cs != null && !cs.IsDataExchange()) {
            McChaseResult result = (McChaseResult) cs;
            tc.onChaseResultUpdate(result);
        }else{
            tc.onChaseResultClose();
        }
    }

    public void register(McResultView tc) {
        this.tc = tc;
        super.registerBean(R.Bean.LOADED_SCENARIO, R.BeanProperty.CHASE_RESULT, IChaseResult.class);
    }
}
