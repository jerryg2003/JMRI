package jmri.jmrit.beantable.routetable;

import jmri.*;
import jmri.implementation.DefaultConditional;
import jmri.implementation.DefaultLogix;
import jmri.implementation.DefaultRoute;
import jmri.util.JUnitUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for jmri.jmrit.beantable.routtable.RouteExportToLogix
 *
 * @author Paul Bender Copyright (C) 2020
 */
class RouteExportToLogixTest {

    private LogixManager lm;
    private ConditionalManager cm;
    private RouteManager rm;
    private Logix l;

    @BeforeEach
    void setUp() {
        JUnitUtil.setUpLoggingAndCommonProperties();
        lm = Mockito.mock(LogixManager.class);
        Mockito.when(lm.getSystemNamePrefix()).thenReturn("IL");
        Mockito.when(lm.createNewLogix(Mockito.anyString(),Mockito.anyString())).thenAnswer(i -> l = new DefaultLogix(i.getArgument(0),i.getArgument(1)));
        cm = Mockito.mock(ConditionalManager.class);
        Mockito.when(cm.createNewConditional(Mockito.anyString(),Mockito.anyString())).thenAnswer(i -> new DefaultConditional(i.getArgument(0),i.getArgument(1)));
        rm = Mockito.mock(RouteManager.class);
    }

    @AfterEach
    void tearDown() {
        lm = null;
        rm = null;
        cm = null;
        if(l!=null){
            l.deActivateLogix();
            l.dispose();
        }
        l = null;
        JUnitUtil.tearDown();
    }

    private Turnout createMockTurnout(String systemName, String userName){
        Turnout t = Mockito.mock(Turnout.class);
        Mockito.when(t.getSystemName()).thenReturn(systemName);
        Mockito.when(t.getUserName()).thenReturn(userName);
        Mockito.when(t.getDisplayName()).thenReturn(userName);
        return t;
    }

    private Sensor createMockSensor(String systemName,String userName){
        Sensor s = Mockito.mock(Sensor.class);
        Mockito.when(s.getSystemName()).thenReturn(systemName);
        Mockito.when(s.getUserName()).thenReturn(userName);
        Mockito.when(s.getDisplayName()).thenReturn(userName);
        return s;
    }

    private void addRouteSensorToRoute(Sensor s,Route r,int sensorMode,int index){
        Mockito.when(r.getRouteSensor(index)).thenReturn(s);
        Mockito.when(r.getRouteSensorMode(index)).thenReturn(sensorMode);
        String displayName = s.getDisplayName();
        Mockito.when(r.getRouteSensorName(index)).thenReturn(displayName);
    }

    private void addControlTurnoutToRoute(Turnout t,Route r,int turnoutMode){
        Mockito.when(r.getCtlTurnout()).thenReturn(t);
        Mockito.when(r.getControlTurnoutState()).thenReturn(turnoutMode);
        String displayName = t.getDisplayName();
        Mockito.when(r.getControlTurnout()).thenReturn(displayName);
    }

    private void addOutputTurnoutToRoute(Turnout t,Route r,int turnoutMode,int index){
        Mockito.when(r.getOutputTurnout(index)).thenReturn(t);
        String displayName = t.getDisplayName();
        Mockito.when(r.getOutputTurnoutByIndex(index)).thenReturn(displayName);
        Mockito.when(r.getOutputTurnoutState(index)).thenReturn(turnoutMode);
        String systemName = t.getSystemName();
        Mockito.when(r.getOutputTurnoutSetState(systemName)).thenReturn(turnoutMode);
    }

    private void addOutputSensorToRoute(Sensor s,Route r,int turnoutMode,int index){
        Mockito.when(r.getOutputSensor(index)).thenReturn(s);
        String displayName = s.getDisplayName();
        Mockito.when(r.getOutputSensorByIndex(index)).thenReturn(displayName);
        Mockito.when(r.getOutputSensorState(index)).thenReturn(turnoutMode);
        String systemName = s.getSystemName();
        Mockito.when(r.getOutputSensorSetState(systemName)).thenReturn(turnoutMode);
    }

    @Test
    void whenAnEmptyRouteIsExported_ThenAnEmptyLogixIsCreated_AndTheRouteIsDeleted() {
        Route r = new DefaultRoute("IO12345","Hello World");
        Mockito.when(rm.getBySystemName(Mockito.anyString())).thenReturn(r);
        Mockito.when(rm.getByUserName(Mockito.anyString())).thenReturn(r);
        new RouteExportToLogix("IO12345",rm,lm,cm).export();
        Mockito.verify(lm).createNewLogix("IL:RTX:IO12345","Hello World");
        Mockito.verify(rm).deleteRoute(r);
    }

    @Test
    void whenRouteWithOneTurnoutIsExported_ThenALogixIsCreatedWithAConditionalAction_AndTheRouteIsDeleted() {
        Route r = Mockito.mock(Route.class);
        Mockito.when(r.getSystemName()).thenReturn("IO12345");
        Mockito.when(r.getUserName()).thenReturn("Hello World");
        Mockito.when(r.getDisplayName()).thenReturn("Hello World");
        Turnout t = createMockTurnout("IT1","Turnout");
        Turnout t1 = createMockTurnout("IT2","Turnout2");
        addOutputTurnoutToRoute(t,r,Turnout.THROWN,0);
        addControlTurnoutToRoute(t,r,Turnout.CLOSED);
        Sensor s = createMockSensor("IS1","Sensor");
        Mockito.when(rm.getBySystemName(Mockito.anyString())).thenReturn(r);
        Mockito.when(rm.getByUserName(Mockito.anyString())).thenReturn(r);
        new RouteExportToLogix("IO12345",rm,lm,cm).export();
        Mockito.verify(cm).createNewConditional(Mockito.anyString(),Mockito.anyString());
        Mockito.verify(lm).createNewLogix("IL:RTX:IO12345","Hello World");
        Mockito.verify(rm).deleteRoute(r);
        assertThat(l).isNotNull();
    }
}
