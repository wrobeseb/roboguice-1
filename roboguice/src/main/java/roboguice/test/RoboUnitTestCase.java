package roboguice.test;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.app.Instrumentation;
import com.google.inject.Injector;
import roboguice.application.RoboApplication;
import roboguice.inject.ContextScope;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

/**
 * Use RoboUnitTestCase when you'd like to make simple unit tests that
 * may depend on Context resources, but do not depend on Activities,
 * Services, ContentProviders, or other Contexts directly.
 *
 * An example:
 *
 * <code>
 * public class MyTest extends RoboUnitTestCase<MyApplication> {
 *
 *  // Make sure you use one of the @*Test annotations AND begin
 *  // your testcase's name with "test"
 *  \@MediumTest
 *  public void test01() {
 *      // Make sure you're using com.mydomain.R, not com.mydomain.test.R
 *      assertEquals("Hello World, Lop!", getContext().getString(com.mydomain.R.string.hello));
 *  }
 *
 * }
 * </code>
 *
 * Also, see the notes about your Application class below.
 *
 * @param <AppType> The type of your Application class.  This class must have a
 *                  constructor that accepts a Context argument and calls
 *                  {@link android.app.Application#attachBaseContext(android.content.Context)}
 */
@SuppressWarnings({"UnusedDeclaration"})
public class RoboUnitTestCase<AppType extends RoboApplication> extends InstrumentationTestCase {
    protected Injector injector;

    @Override
    protected void runTest() throws Throwable {
        final Instrumentation instrumentation = getInstrumentation();
        final Context context = instrumentation.getTargetContext();
        final Constructor constructor = applicationType().getConstructor(Instrumentation.class);
        final RoboApplication app = (RoboApplication)constructor.newInstance(instrumentation);
        injector = app.getInjector();
        final ContextScope scope = injector.getInstance(ContextScope.class);

        try {
            scope.enter(context);
            super.runTest();
        } finally {
            scope.exit(context);
        }
    }

    protected Injector getInjector() {
        return injector;
    }

    protected Class<? extends RoboApplication> applicationType() {
        final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<? extends RoboApplication>) parameterizedType.getActualTypeArguments()[0];
    }

}
