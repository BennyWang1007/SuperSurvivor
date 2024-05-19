package event;

import api.EventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class EventDispatcher {
    private final Map<Class<? extends Event>, List<Object[]>> eventHandlers;

    public EventDispatcher() {
        this.eventHandlers = new HashMap<>();
    }

    public void registerEventListener(EventListener listener) throws Exception {
        List<Method> handlerMethods = fetchHandlerMethods(listener);
        for (Method method : handlerMethods) {
            Class<? extends Event> eventType = fetchEventType(method);
            eventHandlers.computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add(new Object[]{listener, method});
        }
    }

    private List<Method> fetchHandlerMethods(EventListener listener) {
        Method[] methods = listener.getClass().getMethods();
        return Arrays.stream(methods)
                .filter(m -> m.getAnnotation(EventHandler.class) != null)
                .toList();
    }

    private Class<? extends Event> fetchEventType(Method method) throws Exception {
        if (method.getParameterCount() != 1)
            throw new Exception("Number of parameter of event handler method must be 1.");
        Parameter parameter = method.getParameters()[0];
        Class<?> eventType = parameter.getType();
        if (eventType.getSuperclass() != Event.class)
            throw new Exception("The parameter type of event handler must extends class Event.");
        return (Class<? extends Event>) eventType;
    }

    public void dispatchEvent(Event event) {
        List<Object[]> handlers = eventHandlers.getOrDefault(event.getClass(), Collections.emptyList());
        handlers.forEach(handler -> invokeEventHandler(handler[0], (Method) handler[1], event));
    }

    private void invokeEventHandler(Object handler, Method method, Event event) {
        try {
            method.invoke(handler, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
