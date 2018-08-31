package org.koin.standalone;

import kotlin.Lazy;
import org.junit.Before;
import org.junit.Test;
import org.koin.test.AutoCloseKoinTest;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
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
    }

    @Test
    public void successful_release_module() {
        ComponentB b = get(ComponentB.class);

        ComponentD d_1 = get(ComponentD.class);

        assertEquals(b, d_1.getComponentB());

        release("anotherModule");

        ComponentD d_2 = get(ComponentD.class);

        assertEquals(b, d_2.getComponentB());

        assertEquals(d_1, d_2);
    }
}
