package queue;

import base.Asserts;
import base.TestCounter;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ReflectionTest {
    private static final boolean DEBUG = false;
    public static final Collector<CharSequence, ?, String> JOINER = Collectors.joining(", ", "(", ")");

    protected final Set<Method> methods = new HashSet<>();

    public ReflectionTest() {
        Asserts.checkAssert(getClass());
    }

    protected void checkResult(final String call, final Object expected, final Object actual) {
        Asserts.assertEquals(call, expected, actual);
    }

    protected <T> T checking(final TestCounter counter, final Class<T> type, final T reference, final T tested) {
        return proxy(type, (proxy, method, args) -> {
            final String call = method.getName() + (args == null ? "()" : args(args));
            if (DEBUG) {
                counter.format("\t%s%n", call);
            }
            methods.add(method);
            final Object expected;
            final Object actual;
            try {
                expected = method.invoke(reference, args);
                actual = method.invoke(tested, args);
            } catch (final InvocationTargetException e) {
                throw e.getCause();
            }
            checkResult(call, expected, actual);
            return actual;
        });
    }

    protected static String args(final Object[] args) {
        return Stream.of(args).map(Objects::toString).collect(JOINER);
    }

    protected static <T> T proxy(final Class<T> type, final InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, handler));
    }


    private interface IMethod {
        Object invoke(final Object instance, final Object[] args) throws InvocationTargetException, IllegalAccessException;
    }


    enum Mode {
        MODULE("Module") {
            @Override
            IMethod lookupMethod(final Class<?> type, final Method method) {
                return findMethod(true, type, method);
            }
        },
        ADT("ADT") {
            @Override
            IMethod lookupMethod(final Class<?> type, final Method method) {
                final Class<?>[] argTypes = Stream.concat(Stream.of(type), Stream.of(method.getParameterTypes()))
                        .toArray(Class<?>[]::new);
                final Method actual = findMethod(true, type, method.getName(), argTypes);

                final Object[] a = new Object[method.getParameterTypes().length + 1];
                return (instance, args) -> {
                    a[0] = instance;
                    if (args != null) {
                        System.arraycopy(args, 0, a, 1, args.length);
                    }
                    return actual.invoke(null, a);
                };
            }
        },
        CLASS("") {
            @Override
            IMethod lookupMethod(final Class<?> type, final Method method) {
                return findMethod(false, type, method);
            }
        };

        private static IMethod findMethod(final boolean isStatic, final Class<?> type, final Method method) {
            return findMethod(isStatic, type, method.getName(), method.getParameterTypes())::invoke;
        }

        private static Method findMethod(final boolean isStatic, final Class<?> type, final String name, final Class<?>[] parameterTypes) {
            final String description = name + args(parameterTypes);
            final Method method;
            try {
                method = type.getMethod(name, parameterTypes);
            } catch (final NoSuchMethodException e) {
                throw Asserts.error("Missing method %s in %s", description, type);
            }
            if (isStatic != Modifier.isStatic(method.getModifiers())) {
                throw Asserts.error("Method %s in %s %s be static", description, type, isStatic ? "must" : "must not");
            }
            return method;
        }

        private final String suffix;

        Mode(final String suffix) {
            this.suffix = suffix;
        }

        abstract IMethod lookupMethod(final Class<?> type, final Method method);

        private Class<?> loadClass(final String baseName) {
            final String className = baseName + suffix;
            try {
                final URL url = Paths.get(".").toUri().toURL();
                return new URLClassLoader(new URL[]{url}).loadClass(className);
            } catch (final MalformedURLException e) {
                throw new AssertionError("Cannot load classes from .", e);
            } catch (final ClassNotFoundException e) {
                throw new AssertionError(String.format("Cannot find class %s: %s", className, e.getMessage()), e);
            }
        }
    }

    protected static class ProxyFactory<T> {
        private final Class<T> type;
        private final Map<Method, IMethod> methods;
        private final Constructor<?> constructor;
        protected final Class<?> implementation;

        public ProxyFactory(final Class<T> type, final Mode mode, final String baseName) {
            implementation = mode.loadClass(baseName);
            try {
                constructor = implementation.getConstructor();
            } catch (final NoSuchMethodException e) {
                throw Asserts.error("%s should have default constructor", implementation.getName());
            }

            this.type = type;
            methods = Stream.of(type.getMethods())
                    .filter(method -> !method.isAnnotationPresent(Ignore.class))
                    .collect(Collectors.toMap(Function.identity(), method -> mode.lookupMethod(implementation, method)));
        }

        public T create() {
            try {
                return wrap(constructor.newInstance());
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new AssertionError("Cannot create " + implementation.getName() + ": " + e.getMessage(), e);
            }
        }

        private T wrap(final Object instance) {
            Asserts.assertEquals("same class", implementation, instance.getClass());

            return proxy(type, (proxy, method, args) -> {
                if (method.getName().equals("toString")) {
                    return instance.toString();
                }
                final Object result;
                try {
                    result = methods.get(method).invoke(instance, args);
                } catch (final InvocationTargetException e) {
                    throw e.getCause();
                }
                if (method.isAnnotationPresent(Wrap.class)) {
                    return wrap(result);
                }
                return result;
            });
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Inherited
    protected @interface Ignore {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Inherited
    protected @interface Wrap {}
}
