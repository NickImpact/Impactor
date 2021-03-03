package net.impactdev.impactor.common.event.factory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.kyori.event.method.EventExecutor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

/**
 * An executor factory which uses ASM to create event executors.
 *
 * @param <E> the event type
 * @param <L> the listener type
 */
public final class ImpactorASMExecutorFactory<E, L> implements EventExecutor.Factory<E, L> {

    private static final String PACKAGE = "net.kyori.event.asm.generated";
    private static final String SUPER_NAME = "java/lang/Object";
    private static final String EXECUTE_DESC = "(Ljava/lang/Object;Ljava/lang/Object;)V";
    private static final String[] GENERATED_EVENT_EXECUTOR_NAME = new String[]{Type.getInternalName(EventExecutor.class)};
    private final String session = UUID.randomUUID().toString().substring(26);
    private final AtomicInteger id = new AtomicInteger();
    private final ImpactorASMExecutorFactory.DefiningClassLoader eventExecutorClassLoader;
    private final LoadingCache<Method, Class<? extends EventExecutor<E, L>>> cache;

    public ImpactorASMExecutorFactory() {
        this(ImpactorASMExecutorFactory.class.getClassLoader());
    }

    public ImpactorASMExecutorFactory(final @NonNull ClassLoader parent) {
        this.eventExecutorClassLoader = new ImpactorASMExecutorFactory.DefiningClassLoader(parent);
        this.cache = CacheBuilder.newBuilder()
                .initialCapacity(16)
                .weakValues()
                .build(CacheLoader.from(method -> {
                    requireNonNull(method, "method");
                    final Class<?> listener = method.getDeclaringClass();
                    final String listenerName = Type.getInternalName(listener);
                    final Class<?> parameter = method.getParameterTypes()[0];
                    final String className = this.executorClassName(listener, method, parameter);
                    final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                    cw.visit(V1_8, ACC_PUBLIC | ACC_FINAL, className.replace('.', '/'), null, SUPER_NAME, GENERATED_EVENT_EXECUTOR_NAME);
                    MethodVisitor mv;
                    {
                        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                        mv.visitCode();
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKESPECIAL, SUPER_NAME, "<init>", "()V", false);
                        mv.visitInsn(RETURN);
                        mv.visitMaxs(0, 0);
                        mv.visitEnd();
                    }
                    {
                        mv = cw.visitMethod(ACC_PUBLIC, "invoke", EXECUTE_DESC, null, null);
                        mv.visitCode();
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitTypeInsn(CHECKCAST, listenerName);
                        mv.visitVarInsn(ALOAD, 2);
                        mv.visitTypeInsn(CHECKCAST, Type.getInternalName(parameter));
                        mv.visitMethodInsn(INVOKEVIRTUAL, listenerName, method.getName(), Type.getMethodDescriptor(method), false);
                        mv.visitInsn(RETURN);
                        mv.visitMaxs(0, 0);
                        mv.visitEnd();
                    }
                    cw.visitEnd();
                    return this.eventExecutorClassLoader.defineClass(className, cw.toByteArray());
                }));
    }

    private String executorClassName(final Class<?> listener, final Method method, final Class<?> parameter) {
        return String.format("%s.%s.%s-%s-%s-%d", PACKAGE, this.session, listener.getSimpleName(), method.getName(), parameter.getSimpleName(), this.id.incrementAndGet());
    }

    @Override
    public @NonNull EventExecutor<E, L> create(final @NonNull Object object, final @NonNull Method method) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        if(!Modifier.isPublic(object.getClass().getModifiers())) {
            throw new IllegalArgumentException(String.format("Listener class '%s' must be public", object.getClass().getName()));
        }
        if(!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException(String.format("Subscriber method '%s' must be public", method));
        }
        return this.cache.getUnchecked(method).newInstance();
    }

    // A class loader with a method exposed to define a class.
    private static final class DefiningClassLoader extends ClassLoader {
        private DefiningClassLoader(final ClassLoader parent) {
            super(parent);
        }

        <T> Class<T> defineClass(final String name, final byte[] bytes) {
            return (Class<T>) this.defineClass(name, bytes, 0, bytes.length);
        }
    }
}
