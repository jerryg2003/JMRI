package jmri.jmrit.logixng.implementation;

import java.beans.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import jmri.NamedBean;
import jmri.managers.AbstractManager;
import jmri.jmrit.logixng.BaseManager;
import jmri.jmrit.logixng.MaleSocket;

/**
 * Abstract partial implementation for the LogixNG action and expression managers.
 * 
 * @param <E> the type of NamedBean supported by this manager
 * 
 * @author Daniel Bergqvist 2020
 */
public abstract class AbstractBaseManager<E extends NamedBean> extends AbstractManager<E> implements BaseManager<E> {
    
    /**
     * Inform all registered listeners of a vetoable change.If the
 propertyName is "CanDelete" ALL listeners with an interest in the bean
 will throw an exception, which is recorded returned back to the invoking
 method, so that it can be presented back to the user.However if a
 listener decides that the bean can not be deleted then it should throw an
 exception with a property name of "DoNotDelete", this is thrown back up
 to the user and the delete process should be aborted.
     *
     * @param p   The programmatic name of the property that is to be changed.
     *            "CanDelete" will inquire with all listeners if the item can
     *            be deleted. "DoDelete" tells the listener to delete the item.
     * @param old The old value of the property.
     * @param errors if there are errors, the error messages are added to this list
     * @return true if all beans can be deleted, false otherwise
     */
    @OverridingMethodsMustInvokeSuper
    public boolean fireVetoableChange(String p, Object old, List<String> errors) {
        boolean result = true;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, p, old, null);
        if (p.equals("CanDelete")) { // NOI18N
//            StringBuilder message = new StringBuilder();
            for (VetoableChangeListener vc : vetoableChangeSupport.getVetoableChangeListeners()) {
                try {
                    vc.vetoableChange(evt);
                } catch (PropertyVetoException e) {
                    if (e.getPropertyChangeEvent().getPropertyName().equals("DoNotDelete")) { // NOI18N
                        log.info(e.getMessage());
                        result = false;
//                        throw e;
                    }
//                    message.append(e.getMessage()).append("<hr>"); // NOI18N
                    errors.add(e.getMessage());
                }
            }
//            throw new PropertyVetoException(message.toString(), evt);
        } else {
            try {
                vetoableChangeSupport.fireVetoableChange(evt);
            } catch (PropertyVetoException e) {
                log.error("Change vetoed.", e);
                result = false;
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @OverridingMethodsMustInvokeSuper
    public boolean deleteBean(@Nonnull MaleSocket socket, @Nonnull String property, List<String> errors) {
        // throws PropertyVetoException if vetoed
        boolean result = fireVetoableChange(property, (E)socket, errors);
        if (result && property.equals("DoDelete")) { // NOI18N
            deregister((E)socket);
            ((NamedBean)socket).dispose();
        }
        
        return result;
    }
    
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AbstractBaseManager.class);
    
}
