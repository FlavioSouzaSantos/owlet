package br.com.flavio.owlet;

import br.com.flavio.owlet.annotaions.PrefixProperty;
import br.com.flavio.owlet.model.HttpMethod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class PropertiesUtil {

    public static <T> T loadProperties(final Properties properties, Class<T> clazz) {
        if(clazz.isAnnotationPresent(PrefixProperty.class)){
            var annotation = clazz.getAnnotation(PrefixProperty.class);
            var prefixProperty = annotation.value();
            var fields = Stream.concat(Arrays.stream(clazz.getDeclaredFields()), Arrays.stream(clazz.getFields())).toList();
            try {
                var constructor = clazz.getConstructor();
                var instance = constructor.newInstance();
                var hasAnyProperty = false;
                for(Field field : fields){
                    var keyProperty = String.format("%s.%s", prefixProperty, field.getName());
                    if(properties.containsKey(keyProperty)){
                        var method = getSetterMethodForField(clazz, field);
                        var valueProperty = properties.getProperty(keyProperty);
                        switch (field.getType().getTypeName()){
                            case "java.lang.String":
                                method.invoke(instance, valueProperty);
                                hasAnyProperty = true;
                                break;
                            case "java.lang.Integer":
                            case "int":
                                method.invoke(instance, Integer.parseInt(valueProperty));
                                hasAnyProperty = true;
                                break;
                            case "java.lang.Long":
                            case "long":
                                method.invoke(instance, Long.parseLong(valueProperty));
                                hasAnyProperty = true;
                                break;
                            case "java.lang.Boolean":
                            case "boolean":
                                method.invoke(instance, Boolean.parseBoolean(valueProperty));
                                hasAnyProperty = true;
                                break;
                            case "java.net.URI":
                                method.invoke(instance, URI.create(valueProperty));
                                hasAnyProperty = true;
                                break;
                            case "br.com.flavio.owlet.model.HttpMethod":
                                method.invoke(instance, HttpMethod.valueOf(valueProperty));
                                hasAnyProperty = true;
                        }
                    }
                }
                return hasAnyProperty ? instance : null;
            }catch(NoSuchMethodException | InvocationTargetException |
                   InstantiationException | IllegalAccessException ex){}

        }
        return null;
    }

    private static Method getSetterMethodForField(Class clazz, Field field) throws NoSuchMethodException {
        var methodName = String.format("set%s%s", field.getName().substring(0, 1).toUpperCase(), field.getName().substring(1));
        return clazz.getMethod(methodName, field.getType());
    }

    public static List<Properties> spreadByPrefixIndex(final String prefixKey, final Properties properties){
        var list = new ArrayList<Properties>();
        var currentIndex=1;
        do {
            final var keyPrefixName = String.format("%s.%d.", prefixKey, currentIndex);
            final var lengthCharactersOfCurrentIndex = String.valueOf(currentIndex).length() + 2;
            var keys = properties.keySet().stream().map(Object::toString).filter(p -> p.startsWith(keyPrefixName)).toList();
            if(!keys.isEmpty()){
                var property = new Properties();
                keys.forEach(key -> property.setProperty(key.substring(0, prefixKey.length()+1)+key.substring(prefixKey.length()+lengthCharactersOfCurrentIndex), properties.getProperty(key)));
                list.add(property);
                currentIndex++;
            } else currentIndex = 0;
        }while (currentIndex > 0);
        return list;
    }
}
