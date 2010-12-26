package roboguice.inject;

import android.content.Context;
import com.google.inject.Singleton;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Singleton
public class ContextObservationManager {

    private final Map<Context, Map<String, Set<ContextObserverMethod>>> mRegistrations;

    public ContextObservationManager() {
        mRegistrations  = new WeakHashMap<Context, Map<String, Set<ContextObserverMethod>>>();
    }

    public boolean isEnabled() {
        return true;
    }

    public void registerObserver(Context context, Object instance, Method method) {
        if (!isEnabled()) return;

        Map<String, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) {
            methods = new HashMap<String, Set<ContextObserverMethod>>();
            mRegistrations.put(context, methods);
        }

        Set<ContextObserverMethod> observers = methods.get(method.getName());
        if (observers == null) {
            observers = new HashSet<ContextObserverMethod>();
            methods.put(method.getName(), observers);
        }

        observers.add(new ContextObserverMethod(instance, method));
    }

    public void clear(Context context) {
        if (!isEnabled()) return;

        final Map<String, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        mRegistrations.remove(context);
        methods.clear();
    }

    public void notify(Context context, String methodName, Object... args) {
        if (!isEnabled()) return;

        final Map<String, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        final Set<ContextObserverMethod> observers = methods.get(methodName);
        if (observers == null) return;

        for (ContextObserverMethod observerMethod : observers) {
            try {
                observerMethod.invoke(null, args);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Object notifyWithResult(Context context, String methodName, Object defaultReturn, Object... args) {
        if (!isEnabled()) return defaultReturn;

        final Map<String, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return defaultReturn;

        final Set<ContextObserverMethod> observers = methods.get(methodName);
        if (observers == null) return defaultReturn;

        for (ContextObserverMethod observerMethod : observers) {
            Object result = null;
            try {
                result = observerMethod.invoke(defaultReturn, args);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (result != null && !result.equals(defaultReturn)) return result;
        }

        return defaultReturn;
    }

    public static class NullContextObservationManager extends ContextObservationManager {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    static class ContextObserverMethod {
        Method method;
        WeakReference<Object> instanceReference;

        public ContextObserverMethod(Object instance, Method method) {
            this.instanceReference = new WeakReference<Object>(instance);
            this.method = method;
        }

        public Object invoke(Object defaultReturn, Object... args) throws InvocationTargetException, IllegalAccessException {
            final Object instance = instanceReference.get();
            if (instance != null) {
                return method.invoke(instance, args);
            }
            return defaultReturn;
        }
    }
}
