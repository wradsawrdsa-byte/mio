package nick;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.lang.reflect.Field;

public final class Plugin implements PreLaunchEntrypoint {
    static {
        try {
            final ClassLoader loader = Plugin.class.getClassLoader();
            final Field _delegate = loader.getClass().getDeclaredField("delegate");
            _delegate.setAccessible(true);
            final Object delegate = _delegate.get(loader);
            final Field _original = loader.getClass().getDeclaredField("originalLoader");
            _original.setAccessible(true);
            final ClassLoader original = (ClassLoader) _original.get(loader);
            final Field _parent = delegate.getClass().getDeclaredField("parentClassLoader");
            _parent.setAccessible(true);
            final ClassLoader parent = (ClassLoader) _parent.get(delegate);
            final MioClassLoader mio = new MioClassLoader(parent, original);
            _original.set(loader, mio);
            _parent.set(delegate, mio);
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
        }
    }

    @Override
    public void onPreLaunch() {}
}