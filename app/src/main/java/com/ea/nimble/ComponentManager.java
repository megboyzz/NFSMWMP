/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Component;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.Utility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

class ComponentManager
implements LogSource {
    private Map<String, Component> m_components = new LinkedHashMap<String, Component>();
    private Stage m_stage = Stage.CREATE;

    ComponentManager() {
    }

    void cleanup() {
        ListIterator<Component> listIterator = new ArrayList<Component>(this.m_components.values()).listIterator(this.m_components.size());
        while (true) {
            if (!listIterator.hasPrevious()) {
                this.m_stage = Stage.CREATE;
                return;
            }
            listIterator.previous().cleanup();
        }
    }

    Component getComponent(String string2) {
        return this.m_components.get(string2);
    }

    Component[] getComponentList(String string2) {
        ArrayList<Component> arrayList = new ArrayList<Component>(this.m_components.size());
        Iterator<Map.Entry<String, Component>> iterator = this.m_components.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Component> entry = iterator.next();
            if (!entry.getKey().startsWith(string2)) continue;
            arrayList.add(entry.getValue());
        }
        return arrayList.toArray(new Component[arrayList.size()]);
    }

    @Override
    public String getLogSourceTitle() {
        return "Component";
    }

    void registerComponent(Component component, String string2) {
        if (!Utility.validString(string2)) {
            Log.Helper.LOGF(this, "Cannot register component without valid componentId", new Object[0]);
            return;
        }
        if (component == null) {
            Log.Helper.LOGF(this, "Try to register invalid component with id: " + string2, new Object[0]);
            return;
        }
        Component component2 = this.m_components.get(string2);
        if (component2 == null) {
            Log.Helper.LOGI(this, "Register module: " + string2, new Object[0]);
        } else {
            Log.Helper.LOGI(this, "Register module(overwrite): " + string2, new Object[0]);
        }
        this.m_components.put(string2, component);
        if (this.m_stage.compareTo(Stage.SETUP) < 0) return;
        if (component2 != null) {
            if (this.m_stage.compareTo(Stage.SETUP) >= 0) {
                if (this.m_stage.compareTo(Stage.SUSPEND) >= 0) {
                    component2.resume();
                }
                component2.cleanup();
            }
            component2.teardown();
        }
        component.setup();
        if (this.m_stage.compareTo(Stage.READY) < 0) return;
        component.restore();
        if (this.m_stage.compareTo(Stage.SUSPEND) < 0) return;
        component.suspend();
    }

    void restore() {
        Iterator<Component> iterator = this.m_components.values().iterator();
        while (true) {
            if (!iterator.hasNext()) {
                this.m_stage = Stage.READY;
                return;
            }
            iterator.next().restore();
        }
    }

    void resume() {
        Iterator<Component> iterator = this.m_components.values().iterator();
        while (true) {
            if (!iterator.hasNext()) {
                this.m_stage = Stage.READY;
                return;
            }
            iterator.next().resume();
        }
    }

    void setup() {
        Iterator<Component> iterator = this.m_components.values().iterator();
        while (true) {
            if (!iterator.hasNext()) {
                this.m_stage = Stage.SETUP;
                return;
            }
            iterator.next().setup();
        }
    }

    void suspend() {
        ListIterator<Component> listIterator = new ArrayList<Component>(this.m_components.values()).listIterator(this.m_components.size());
        while (true) {
            if (!listIterator.hasPrevious()) {
                this.m_stage = Stage.SUSPEND;
                return;
            }
            listIterator.previous().suspend();
        }
    }

    void teardown() {
        ListIterator<Component> listIterator = new ArrayList<Component>(this.m_components.values()).listIterator(this.m_components.size());
        while (true) {
            if (!listIterator.hasPrevious()) {
                this.m_stage = Stage.CREATE;
                return;
            }
            listIterator.previous().teardown();
        }
    }

    private static enum Stage {
        CREATE,
        SETUP,
        READY,
        SUSPEND;

    }
}

