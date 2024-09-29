package ru.job4j.di;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {

    /*Карта с объектами. В ней мы будем хранить проинициализированные объекты.*/
    private Map<String, Object> els = new HashMap<String, Object>();

    public void reg(Class cl) {

        /*
            Сначала нужно получить все конструкторы класса. Если их больше 1, то мы не знаем как загружать этот класс и кидаем исключение.
        */
        Constructor[] constructors = cl.getDeclaredConstructors();
        if (constructors.length > 1) {
            throw new IllegalStateException("Class has multiple constructors : " + cl.getCanonicalName());
        }

        /*
            Когда мы нашли конструктор, мы собираем аргументы этого конструктора и ищем уже зарегистрированные объекты, чтобы внедрить их в конструктор.
        */
        Constructor con = constructors[0];
        List<Object> args = new ArrayList<Object>();
        for (Class arg : con.getParameterTypes()) {
            if (!els.containsKey(arg.getCanonicalName())) {
                throw new IllegalStateException("Object doesn't found in context : " + arg.getCanonicalName());
            }
            args.add(els.get(arg.getCanonicalName()));
        }
        try {
            /*Последний этап - это создание объекта и добавление его в карту.*/
            els.put(cl.getCanonicalName(), con.newInstance(args.toArray()));
        } catch (Exception e) {
            throw new IllegalStateException("Coun't create an instance of : " + cl.getCanonicalName(), e);
        }
    }

    /*Метод get возвращает проинициализированный объект.
    * Через рефлексию можно получить имя класса. */
    public <T> T get(Class<T> inst) {
        return (T) els.get(inst.getCanonicalName());
    }
}
