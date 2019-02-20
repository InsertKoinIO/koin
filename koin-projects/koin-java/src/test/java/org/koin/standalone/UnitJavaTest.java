package org.koin.standalone;

import kotlin.Lazy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.koin.core.KoinApplication;
import org.koin.core.context.GlobalContext;
import org.koin.core.scope.ScopeInstance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.koin.core.context.GlobalContext.start;
import static org.koin.core.context.GlobalContext.stop;
import static org.koin.java.KoinJavaComponent.*;
import static org.koin.standalone.UnitJavaStuffKt.koinModule;

public class UnitJavaTest {

    @Before
    public void before() {
        KoinApplication koinApp = KoinApplication.create()
                .printLogger()
                .modules(koinModule);

        start(koinApp);
    }

    @After
    public void after() {
        stop();
    }

    @Test
    public void successful_get() {
        ComponentA a = get(ComponentA.class);
        assertNotNull(a);

        ComponentB b = get(ComponentB.class);
        assertNotNull(b);

        ComponentC c = get(ComponentC.class);
        assertNotNull(c);

        assertEquals(a, b.getComponentA());
        assertEquals(a, c.getA());
    }

    @Test
    public void successful_lazy() {
        Lazy<ComponentA> lazy_a = inject(ComponentA.class);

        Lazy<ComponentB> lazy_b = inject(ComponentB.class);

        Lazy<ComponentC> lazy_c = inject(ComponentC.class);

        assertEquals(lazy_a.getValue(), lazy_b.getValue().getComponentA());
        assertEquals(lazy_a.getValue(), lazy_c.getValue().getA());

        ScopeInstance session = getKoin().createScope("mySession", "Session");

        assertNotNull(get(ComponentD.class, null, session));

        session.close();
        GlobalContext.stop();
    }
}
