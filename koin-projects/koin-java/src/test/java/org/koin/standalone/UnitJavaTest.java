package org.koin.standalone;

import kotlin.Lazy;
import org.junit.Before;
import org.junit.Test;
import org.koin.core.scope.Scope;
import org.koin.test.AutoCloseKoinTest;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.koin.java.standalone.KoinJavaComponent.*;
import static org.koin.java.standalone.KoinJavaStarter.startKoin;
import static org.koin.standalone.UnitJavaStuffKt.koinModule;

public class UnitJavaTest extends AutoCloseKoinTest {

    @Before
    public void before() {
        startKoin(singletonList(koinModule));
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

        Scope session = getKoin().createScope("session");

        assertNotNull(get(ComponentD.class));

        session.close();
    }
}
