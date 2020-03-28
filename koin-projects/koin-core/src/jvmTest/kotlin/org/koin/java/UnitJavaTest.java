package org.koin.java;

import kotlin.Lazy;
import org.junit.Before;
import org.junit.Test;
import org.koin.KoinCoreTest;
import org.koin.core.KoinApplication;
import org.koin.core.context.GlobalContext;
import org.koin.core.scope.Scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.koin.core.context.ContextFunctionsKt.startKoin;
import static org.koin.core.context.ContextFunctionsKt.stopKoin;
import static org.koin.core.qualifier.QualifierKt.named;
import static org.koin.java.KoinJavaComponent.get;
import static org.koin.java.KoinJavaComponent.getKoin;
import static org.koin.java.KoinJavaComponent.inject;
import static org.koin.java.UnitJavaStuffKt.koinModule;

public class UnitJavaTest extends KoinCoreTest {

    @Before
    public void before() {
        KoinApplication koinApp = KoinApplication.init().printLogger().modules(koinModule);

        startKoin(new GlobalContext(), koinApp);
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

        Scope session = getKoin().createScope("mySession", named("Session"), null);

        assertNotNull(session.get(ComponentD.class));

        session.close();
        stopKoin();
    }
}
