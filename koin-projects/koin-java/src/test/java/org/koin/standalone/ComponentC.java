package org.koin.standalone;

public class ComponentC {
    private ComponentA _a;
    private ComponentB _b;

    public ComponentC(ComponentA a, ComponentB b) {
        _a = a;
        _b = b;
    }

    public ComponentA getA() {
        return _a;
    }

    public ComponentB getB() {
        return _b;
    }
}
