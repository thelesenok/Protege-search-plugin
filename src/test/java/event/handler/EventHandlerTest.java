package event.handler;

import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.ClassPath;
import org.junit.Test;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

import java.lang.reflect.Method;

/**
 * Created by abarmin on 12.03.17.
 */
public class EventHandlerTest {
    @Test
    public void checkSubscribeMethods() throws Exception {
        final ClassLoader loader = getClass().getClassLoader();
        final ClassPath classPath = ClassPath.from(loader);
        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive("ru.mydesignstudio")) {
            final Class<?> loadedClass = classInfo.load();
            for (Method method : loadedClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Subscribe.class)) {
                    if (method.getExceptionTypes().length > 0) {
                        throw new ApplicationException(String.format(
                                "Event handler %s declares exception!",
                                method
                        ));
                    }
                }
            }
        }
    }
}
